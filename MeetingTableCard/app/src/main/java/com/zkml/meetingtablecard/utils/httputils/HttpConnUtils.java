package com.zkml.meetingtablecard.utils.httputils;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;

import com.zkml.meetingtablecard.application.MyApplication;
import com.zkml.meetingtablecard.constant.CommonStatusType;
import com.zkml.meetingtablecard.constant.Constant;
import com.zkml.meetingtablecard.constant.IPConfig;
import com.zkml.meetingtablecard.utils.ActivityUtils;
import com.zkml.meetingtablecard.utils.DateUtils;
import com.zkml.meetingtablecard.utils.JsonUtil;
import com.zkml.meetingtablecard.utils.LogUtil;
import com.zkml.meetingtablecard.utils.cache.StringUtils;
import com.zkml.meetingtablecard.utils.httputils.cache.CacheMode;
import com.zkml.meetingtablecard.utils.httputils.cookie.CookieJarImpl;
import com.zkml.meetingtablecard.utils.httputils.cookie.store.CookieStore;
import com.zkml.meetingtablecard.utils.httputils.cookie.store.MemoryCookieStore;
import com.zkml.meetingtablecard.utils.httputils.https.HttpsUtils;
import com.zkml.meetingtablecard.utils.httputils.interceptor.LoggerInterceptor;
import com.zkml.meetingtablecard.utils.httputils.model.HttpHeaders;
import com.zkml.meetingtablecard.utils.httputils.model.HttpParams;
import com.zkml.meetingtablecard.utils.httputils.request.DeleteRequest;
import com.zkml.meetingtablecard.utils.httputils.request.GetRequest;
import com.zkml.meetingtablecard.utils.httputils.request.HeadRequest;
import com.zkml.meetingtablecard.utils.httputils.request.OptionsRequest;
import com.zkml.meetingtablecard.utils.httputils.request.PostRequest;
import com.zkml.meetingtablecard.utils.httputils.request.PutRequest;
import com.zkml.meetingtablecard.utils.httputils.utils.AESUtils;

import java.io.File;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLSession;

import okhttp3.Call;
import okhttp3.Headers;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Response;
import okio.Buffer;

import static com.zkml.meetingtablecard.utils.CarEasyUtil.getNonce;


/**
 * Created by lenovo on 2016/11/2.
 */
public class HttpConnUtils {

    public static final int DEFAULT_MILLISECONDS = 200000; //默认的超时时间
    //异常
    public static String[] error = {"ClientProtocolException", "IOException", "ParseException", "UnknownHostException", "HttpHostConnectException", "404", "500", "IllegalStateException"};
    private static HttpConnUtils mInstance;                 //单例
    private static Application context;                   //全局上下文
    private Handler mDelivery;                            //用于在主线程执行的调度器
    private OkHttpClient.Builder okHttpClientBuilder;     //ok请求的客户端
    private HttpParams mCommonParams;                     //全局公共请求参数
    private HttpHeaders mCommonHeaders;                   //全局公共请求头
    private CacheMode mCacheMode;                         //全局缓存模式
    private CookieJarImpl cookieJar;                      //全局 Cookie 实例
    private static SharedPreferences sharedPreferences;   //全局的sharepreference
    private static Map<String, Object> apiMap;               //全局的map
    private static boolean authSwitch = false;//是否需要鉴权
    public static final int HUGEUPGRADEVERSION = 1; //大版本升级，就可用这个字段，跟后台交互

    private HttpConnUtils() {
        okHttpClientBuilder = new OkHttpClient.Builder();
        //允许cookie的自动化管理，默认内存管理
        cookieJar = new CookieJarImpl(new MemoryCookieStore());
        okHttpClientBuilder.cookieJar(cookieJar);
        okHttpClientBuilder.hostnameVerifier(new DefaultHostnameVerifier());
        mDelivery = new Handler(Looper.getMainLooper());
    }

    public static HttpConnUtils getInstance() {
        if (mInstance == null) {
            synchronized (HttpConnUtils.class) {
                if (mInstance == null) {
                    mInstance = new HttpConnUtils();
                }
            }
        }
        return mInstance;
    }

    /**
     * 必须在全局Application先调用，获取context上下文，否则缓存无法使用
     */
    public static void init(Application app) {
        context = app;
    }

    /**
     * 获取全局上下文
     */
    public static Context getContext() {
        if (context == null)
            throw new IllegalStateException("请先在全局Application中调用 OkHttpUtils.init() 初始化！");
        return context;
    }

    /**
     * get请求
     */
    public static GetRequest get(String url) {
        return new GetRequest(url);
    }

    //post请求
    public static Map<String, Object> commPostReqToServer(String req_path, Map<String, String> map, Context context) {
        return getReqToServer(req_path, map, context, CommonStatusType.POST, "");

    }

    //post请求 json数据
    public static Map<String, Object> commPostJsonReqToServer(String req_path, Map<String, String> map, Context context, String json) {
        return getReqToServer(req_path, map, context, CommonStatusType.POST, json);
    }

    //get请求
    public static Map<String, Object> commGetReqToServer(String req_path, Map<String, String> map, Context context) {
        return getReqToServer(req_path, map, context, CommonStatusType.GET, "");
    }

    //put请求
    public static Map<String, Object> commPutReqToServer(String req_path, Map<String, String> map, Context context) {
        return getReqToServer(req_path, map, context, CommonStatusType.PUT, "");
    }

    //delete请求
    public static Map<String, Object> commDeleteReqToServer(String req_path, Map<String, String> map, Context context) {
        return getReqToServer(req_path, map, context, CommonStatusType.DELETE, "");
    }

    /**
     * post请求
     */
    public static PostRequest post(String url) {
        return new PostRequest(url);
    }

    /**
     * post请求
     *
     * @param url  url
     * @param json json 数据
     * @return
     */
    public static PostRequest postJson(String url, String json) {
        return new PostRequest(url).postJson(json);
    }

    /**
     * put请求
     */
    public static PutRequest put(String url) {
        return new PutRequest(url);
    }

    /**
     * head请求
     */
    public static HeadRequest head(String url) {
        return new HeadRequest(url);
    }

    /**
     * delete请求
     */
    public static DeleteRequest delete(String url) {
        return new DeleteRequest(url);
    }

    /**
     * patch请求
     */
    public static OptionsRequest options(String url) {
        return new OptionsRequest(url);
    }

    //0系统异常，1，服务器连接失败
    public static int handException(String excp) {
        for (int i = 0; i < error.length; i++) {
            if (excp.equals(error[3]) || excp.equals(error[4])) {//服务器连接失败
                return 1;
            } else if (excp.equals(error[5])) {//404
                return 2;
            } else if (excp.equals(error[0]) || excp.equals(error[1])) {//ioException
                return 0;
            } else if (excp.equals(error[2])) {//解析出错
                return 3;
            } else if (excp.equals(error[6])) {//500
                return 4;
            } else if (excp.equals(error[7])) {//IllegalStateException
                return 5;
            }
        }
        return 0;
    }

    //获取ip和端口号
    public static String getIP(String url) {
        //使用正则表达式过滤，
        String re = "((http|ftp|https)://)(([a-zA-Z0-9._-]+)|([0-9]{1,3}.[0-9]{1,3}.[0-9]{1,3}.[0-9]{1,3}))(([a-zA-Z]{2,6})|(:[0-9]{1,5})?)";
        String str = "";
        // 编译正则表达式
        Pattern pattern = Pattern.compile(re);
        // 忽略大小写的写法
        Matcher matcher = pattern.matcher(url);
        if (matcher.matches()) {
            str = url;
        } else {
            String[] split2 = url.split(re);
            if (split2.length > 1) {
                String substring = url.substring(0, url.length() - split2[1].length());
                str = substring;
            } else {
                str = split2[0];
            }
        }
        return str;
    }

    //统一截取请求地址
    public static String interceptUrl(String req_path) {
        String url = "";
        String deploySign = sharedPreferences.getString("deploySign", "");
        // 智慧后勤接口拦截，登录不需要，其他会议室需要
        if (!TextUtils.isEmpty(req_path) && req_path.contains(IPConfig.IP_PROJECT)
                && !req_path.contains("/mlyun-auth/server/auth/login")) {
            url = req_path.replace(IPConfig.IP_PROJECT, IPConfig.IP_PROJECT + "/meeting");
        } else if (!TextUtils.isEmpty(req_path) && req_path.contains(IPConfig.IP_TEST)) {// 经过网关的接口(测试环境)
            if (!StringUtils.isStrEmpty(deploySign)) {
                url = req_path.replace(IPConfig.IP_TEST,
                        IPConfig.IP_TEST + "/meeting_v2/" + deploySign + "/meeting-test");
//                        IPConfig.IP_TEST + "/meeting/" + deploySign + "/meeting");
            } else {
                url = req_path;
            }
        } else if (!TextUtils.isEmpty(req_path) && req_path.contains(IPConfig.IP_FORMAL)) {
            if (!StringUtils.isStrEmpty(deploySign)) {
                url = req_path.replace(IPConfig.IPCONFIG,
                        IPConfig.IPCONFIG + "/meeting_v2_saas_online/" + deploySign + "/meeting");
            } else {
                url = req_path;
            }
        } else {
            url = req_path;
        }
        return url;
    }

    /**
     * 机关事务不需要systemParam这个参数，所以需要加上这个变量去控制
     */
    private static boolean isWorkbench = false;

    /**
     * 网络请求
     *
     * @param req_path 请求地址
     * @param map      请求参数
     * @param context  context
     * @param reqType  请求的类型 GET,POST,PUT,DELETE
     * @return rsultMap
     */
    private static Map<String, Object> getReqToServer(String req_path, Map<String, String> map, Context context, String reqType, String json) {
        try {
            sharedPreferences = ActivityUtils.selSharedPreferencesData(context, MyApplication.USER_INFO_NAME);
            String token = sharedPreferences.getString("token", "");
            String appChannel = sharedPreferences.getString("appChannel", "");
            String authId = sharedPreferences.getString("auth_id", "");
            LogUtil.i("zzz1", "token http " + token);
            String image = "";
            String pay = "";
            if (map != null) {
                pay = map.get("payParameter");
                image = map.get("image");
            } else {
                map = new HashMap<>();
            }
            authSwitch = false;
            isWorkbench = false;
            LogUtil.d(req_path);
            req_path = interceptUrl(req_path);
            LogUtil.i("zzz1", "interceptUrl-req_path " + req_path);
//            LogUtil.i("zzz1", "getIP-req_path " + getIP(req_path));

            String jsonStr;
            AESUtils aesUtils = AESUtils.getInstance();
            if (!TextUtils.isEmpty(image)) {
                map.remove("image");
            }
            if (authSwitch) {
                //网关鉴权
                String businessParam = JsonUtil.toJson(map);//将已有业务参数转换为json字符串
                map.clear();
                map.put("businessParam", aesUtils.encrypt(businessParam));
                Map<String, String> systemMap = new HashMap<>();
                systemMap.put("hugeUpgradeVersion", HUGEUPGRADEVERSION + "");
                systemMap.put("authId", authId);
                systemMap.put("terminalType", "ANDROID");
                systemMap.put("phoneSystem", android.os.Build.VERSION.RELEASE);
                if (!TextUtils.isEmpty(req_path) && !req_path.contains("/mobileTerminal/login")) {//登录
                    systemMap.put("token", token);
                }
                systemMap.put("appPackageId", Constant.APP_PACKAGENAME);
                systemMap.put("appChannel", appChannel);
                String systemParam = JsonUtil.toJson(systemMap);//将系统级参数转换为json字符串
                if (!isWorkbench) {
                    map.put("systemParam", systemParam);
                }
                for (String key : map.keySet()) {
                    LogUtil.i("mzkml", "-----------请求参数------------" + key + "=" + map.get(key));
                }
            } else {
                //系统级参数
                map.put("terminalType", "ANDROID");
                if (!TextUtils.isEmpty(req_path) && req_path.contains("/mobileTerminal/login")) {//登录
                    LogUtil.i("mzkml", "-----------登录------------");
                    map.put("token", token);
                    map.put("phoneSystem", android.os.Build.VERSION.RELEASE);
                    map.put("appPackageId", Constant.APP_PACKAGENAME);
                    //系统级参数
                    Map<String, String> systemMap = new HashMap<>();
                    systemMap.put("authId", authId);
                    systemMap.put("hugeUpgradeVersion", HUGEUPGRADEVERSION + "");
                    systemMap.put("terminalType", "ANDROID");
                    systemMap.put("phoneSystem", android.os.Build.VERSION.RELEASE);
                    if (!TextUtils.isEmpty(req_path) && !req_path.contains("/mobileTerminal/login")) {//登录
                        systemMap.put("token", token);
                    }
                    systemMap.put("appPackageId", Constant.APP_PACKAGENAME);
                    systemMap.put("appChannel", appChannel);
                    String systemParam = JsonUtil.toJson(systemMap);//将系统级参数转换为json字符串
                    if (!isWorkbench) {
                        map.put("systemParam", systemParam);
                    }
                    for (String key : map.keySet()) {
                        LogUtil.i("mzkml", "-----------请求参数------------" + key + "=" + map.get(key));
                    }
                }

            }
            //请求头信息
            HttpHeaders headers = new HttpHeaders();
            headers.put("Authorization", token);
            if (authSwitch) {
                //修改 head传值，只有需要鉴权的时候head才有值
                String timestamp = DateUtils.getCurrentTimeMillis();//获取当前时间毫秒数
                String nonce = getNonce(timestamp);//获取nonce
                headers = new HttpHeaders();
                headers.put("version", MyApplication.getVersionName(context));
                headers.put("appChannel", appChannel);
                headers.put("timestamp", timestamp);
                headers.put("nonce", nonce);
                headers.put("signature", aesUtils.encrypt(nonce + "&" + timestamp));
                ConcurrentHashMap<String, String> headersMap = headers.headersMap;
                for (String key : headersMap.keySet()) {
                    LogUtil.i("headers", "-----------请求参数------------" + key + "=" + headersMap.get(key));
                }
            }

            Response response = null;
            if (!TextUtils.isEmpty(image)) {
                File file = new File(image);
                if (StringUtils.getTypeState(reqType, CommonStatusType.GET)) {
                    response = getInstance().get(req_path).tag(context).params("image", file).params(map).headers(headers).execute();
                } else if (StringUtils.getTypeState(reqType, CommonStatusType.POST)) {
                    response = getInstance().post(req_path).tag(context).params("image", file).params(map).headers(headers).execute();
                } else if (StringUtils.getTypeState(reqType, CommonStatusType.PUT)) {
                    response = getInstance().put(req_path).tag(context).params("image", file).params(map).headers(headers).execute();
                } else if (StringUtils.getTypeState(reqType, CommonStatusType.DELETE)) {
                    response = getInstance().delete(req_path).tag(context).params("image", file).params(map).headers(headers).execute();
                }
            } else {
                if (StringUtils.getTypeState(reqType, CommonStatusType.GET)) {
                    response = getInstance().get(req_path).tag(context).params(map).headers(headers).execute();
                } else if (StringUtils.getTypeState(reqType, CommonStatusType.POST)) {
                    if (!StringUtils.isStrEmpty(json)) {
                        response = getInstance().postJson(req_path, json).tag(context).headers(headers).execute();
                    } else {
                        response = getInstance().post(req_path).tag(context).params(map).headers(headers).execute();
                    }
                } else if (StringUtils.getTypeState(reqType, CommonStatusType.PUT)) {
                    response = getInstance().put(req_path).tag(context).params(map).headers(headers).execute();
                } else if (StringUtils.getTypeState(reqType, CommonStatusType.DELETE)) {
                    response = getInstance().delete(req_path).tag(context).params(map).headers(headers).execute();
                }
            }
            jsonStr = response.body().string();
            Headers reponseHeaders = response.headers();
            LogUtil.d("header: " + reponseHeaders);
            List<String> cookies = reponseHeaders.values("Set-Cookie");
            if (cookies != null && cookies.size() != 0) {
                String session = cookies.get(0);
                LogUtil.d("session: " + session);
                MyApplication.getApp().setSessionId(session.substring(0, session.indexOf(";")));
            }

            LogUtil.iJsonFormat("mzkml", jsonStr);
            LogUtil.iJsonFormat("req_path", req_path);
            LogUtil.iJsonFormat("complete_path", req_path);
            LogUtil.i("okhttp", jsonStr);
            //是否抛出异常
            for (int i = 0; i < error.length; i++) {
                if (jsonStr.equals(error[i])) {
                    Map<String, Object> excMap = new HashMap<String, Object>();
                    excMap.put("exception", error[i]);
                    return excMap;
                }
            }

            //判断session失效,跳转到登录页面
            if (jsonStr.indexOf("sessionfail") > -1) {//重新
                Map<String, Object> reqmap = new HashMap<String, Object>();
                reqmap.put("reLogin", "true");
                return reqmap;
            }
            //判断权限
            if (jsonStr.indexOf("no_auth") > -1) {
                Map<String, Object> reqmap = new HashMap<String, Object>();
                reqmap.put("no_auth", "true");
                return reqmap;
            }
            //在别处登录了
            if (jsonStr.indexOf("sessionagain") > -1) {
                Map<String, Object> reqmap = new HashMap<String, Object>();
                reqmap.put("sessionagain", "true");
                return reqmap;
            }

            if (!TextUtils.isEmpty(pay) && "payParameter".equals(pay)) {
                Map<String, Object> resultMap = new HashMap<>();
                resultMap.put("payParameter", jsonStr);
                return resultMap;
            } else if (!TextUtils.isEmpty(jsonStr) && !"".equals(jsonStr)) {
                Map<String, Object> resultMap = StringUtils.transResultJsonToMap(jsonStr);
                return resultMap;
            }
            return null;
        } catch (Exception e) {
            Map<String, Object> excMap = new HashMap<String, Object>();
            excMap.put("exception", error[7]);
            return excMap;
        }
    }

    public Handler getDelivery() {
        return mDelivery;
    }

    public OkHttpClient getOkHttpClient() {
        return okHttpClientBuilder.build();
    }

    /**
     * 调试模式
     */
    public HttpConnUtils debug(String tag) {
        okHttpClientBuilder.addInterceptor(new LoggerInterceptor(tag, true));
        return this;
    }

    /**
     * https的全局访问规则
     */
    public HttpConnUtils setHostnameVerifier(HostnameVerifier hostnameVerifier) {
        okHttpClientBuilder.hostnameVerifier(hostnameVerifier);
        return this;
    }

    /**
     * https的全局自签名证书
     */
    public HttpConnUtils setCertificates(InputStream... certificates) {
        okHttpClientBuilder.sslSocketFactory(HttpsUtils.getSslSocketFactory(certificates, null, null));
        return this;
    }

    /**
     * https的全局自签名证书
     */
    public HttpConnUtils setCertificates(String... certificates) {
        for (String certificate : certificates) {
            InputStream inputStream = new Buffer().writeUtf8(certificate).inputStream();
            setCertificates(inputStream);
        }
        return this;
    }

    /**
     * 全局cookie存取规则
     */
    public HttpConnUtils setCookieStore(CookieStore cookieStore) {
        cookieJar = new CookieJarImpl(cookieStore);
        okHttpClientBuilder.cookieJar(cookieJar);
        return this;
    }

    /**
     * 获取全局的cookie实例
     */
    public CookieJarImpl getCookieJar() {
        return cookieJar;
    }

    /**
     * 全局读取超时时间
     */
    public HttpConnUtils setReadTimeOut(int readTimeOut) {
        okHttpClientBuilder.readTimeout(readTimeOut, TimeUnit.MILLISECONDS);
        return this;
    }

    /**
     * 全局写入超时时间
     */
    public HttpConnUtils setWriteTimeOut(int writeTimeout) {
        okHttpClientBuilder.writeTimeout(writeTimeout, TimeUnit.MILLISECONDS);
        return this;
    }

    /**
     * 全局连接超时时间
     */
    public HttpConnUtils setConnectTimeout(int connectTimeout) {
        okHttpClientBuilder.connectTimeout(connectTimeout, TimeUnit.MILLISECONDS);
        return this;
    }

    /**
     * 获取全局的缓存模式
     */
    public CacheMode getCacheMode() {
        return mCacheMode;
    }

    /**
     * 全局的缓存模式
     */
    public HttpConnUtils setCacheMode(CacheMode cacheMode) {
        mCacheMode = cacheMode;
        return this;
    }

    /**
     * 获取全局公共请求参数
     */
    public HttpParams getCommonParams() {
        return mCommonParams;
    }

    /**
     * 添加全局公共请求参数
     */
    public HttpConnUtils addCommonParams(HttpParams commonParams) {
        if (mCommonParams == null) mCommonParams = new HttpParams();
        mCommonParams.put(commonParams);
        return this;
    }

    /**
     * 获取全局公共请求头
     */
    public HttpHeaders getCommonHeaders() {
        return mCommonHeaders;
    }

    /**
     * 添加全局公共请求参数
     */
    public HttpConnUtils addCommonHeaders(HttpHeaders commonHeaders) {
        if (mCommonHeaders == null) mCommonHeaders = new HttpHeaders();
        mCommonHeaders.put(commonHeaders);
        return this;
    }

    /**
     * 添加全局拦截器
     */
    public HttpConnUtils addInterceptor(Interceptor interceptor) {
        okHttpClientBuilder.addInterceptor(interceptor);
        return this;
    }

    /**
     * 根据Tag取消请求
     */
    public void cancelTag(Object tag) {
        for (Call call : getOkHttpClient().dispatcher().queuedCalls()) {
            if (tag.equals(call.request().tag())) {
                call.cancel();
            }
        }
        for (Call call : getOkHttpClient().dispatcher().runningCalls()) {
            if (tag.equals(call.request().tag())) {
                call.cancel();
            }
        }
    }

    /**
     * 此类是用于主机名验证的基接口。 在握手期间，如果 URL 的主机名和服务器的标识主机名不匹配，
     * 则验证机制可以回调此接口的实现程序来确定是否应该允许此连接。策略可以是基于证书的或依赖于其他验证方案。
     * 当验证 URL 主机名使用的默认规则失败时使用这些回调。如果主机名是可接受的，则返回 true
     */
    public class DefaultHostnameVerifier implements HostnameVerifier {
        @Override
        public boolean verify(String hostname, SSLSession session) {
            return true;
        }
    }


}
