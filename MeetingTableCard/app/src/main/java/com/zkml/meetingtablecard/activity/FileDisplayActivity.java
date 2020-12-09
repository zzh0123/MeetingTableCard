package com.zkml.meetingtablecard.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.text.TextUtils;
import android.view.Window;
import android.view.WindowManager;

import com.zkml.meetingtablecard.R;
import com.zkml.meetingtablecard.utils.ActivityManager;
import com.zkml.meetingtablecard.utils.LogUtil;
import com.zkml.meetingtablecard.utils.ToastUtils;
import com.zkml.meetingtablecard.view.FileView.Md5Tool;
import com.zkml.meetingtablecard.view.FileView.SuperFileView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

/**
 * @author: zzh
 * data : 2020/12/07
 * description：展示资料
 */
public class FileDisplayActivity extends AppCompatActivity {


    private OkHttpClient mOkHttpClient;
    private String TAG = "FileDisplayActivity";
    private SuperFileView mSuperFileView;
    private String filePath;
    private String fileUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //设置没有标题
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        //设置全屏
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_file_display);
        ActivityManager.getInstance().addActivity(this);
        initView();
    }


    public void initView() {
        mOkHttpClient = new OkHttpClient();

        mSuperFileView = (SuperFileView) findViewById(R.id.superFileView);
        mSuperFileView.setOnGetFilePathListener(new SuperFileView.OnGetFilePathListener() {
            @Override
            public void onGetFilePath(SuperFileView superFileView) {
                getFilePathAndShowFile(superFileView);
            }
        });

        Intent intent = this.getIntent();
//        String path = (String) intent.getSerializableExtra("path");
        fileUrl = getIntent().getStringExtra("fileUrl");

        if (!TextUtils.isEmpty(fileUrl)) {
            LogUtil.i("zzz1","文件path:" + fileUrl);
            setFilePath(fileUrl);
        }
        mSuperFileView.show();

    }


    private void getFilePathAndShowFile(SuperFileView superFileView) {


        if (getFilePath().contains("http")) {//网络地址要先下载

            downLoadFromNet(getFilePath(),superFileView);

        } else {
            superFileView.displayFile(new File(getFilePath()));
        }
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        LogUtil.i("zzz1","FileDisplayActivity-->onDestroy");
        if (mSuperFileView != null) {
            mSuperFileView.onStopDisplay();
        }
    }


    public static void show(Context context, String url) {
        Intent intent = new Intent(context, FileDisplayActivity.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable("path", url);
        intent.putExtras(bundle);
        context.startActivity(intent);

    }

    public void setFilePath(String fileUrl) {
        this.filePath = fileUrl;
    }

    private String getFilePath() {
        return filePath;
    }

    private void downLoadFromNet(final String url,final SuperFileView superFileView) {

        //1.网络下载、存储路径、
        File cacheFile = getCacheFile(url);
        if (cacheFile.exists()) {
            if (cacheFile.length() <= 0) {
                LogUtil.i("zzz1", "删除空文件！！");
                cacheFile.delete();
                return;
            }
        }

        final Request request = new Request.Builder().url(fileUrl).build();
        mOkHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                showCustomToast(getString(R.string.download_fail));
                LogUtil.i("zzz1", "文件下载失败");
                File file = getCacheFile(url);
                if (!file.exists()) {
                    LogUtil.i("zzz1", "删除下载失败文件");
                    file.delete();
                }
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                LogUtil.i("zzz1", "下载文件-->onResponse");
                boolean flag;
                InputStream is = null;
                byte[] buf = new byte[2048];
                int len = 0;
                FileOutputStream fos = null;
                try {
                    ResponseBody responseBody = response.body();
                    is = responseBody.byteStream();
                    long total = responseBody.contentLength();

                    File file1 = getCacheDir(url);
                    if (!file1.exists()) {
                        file1.mkdirs();
                        LogUtil.i("zzz1", "创建缓存目录： " + file1.toString());
                    }

                    //fileN : /storage/emulated/0/pdf/kauibao20170821040512.pdf
                    File fileN = getCacheFile(url);//new File(getCacheDir(url), getFileName(url))

                    LogUtil.i("zzz1", "创建缓存文件： " + fileN.toString());
                    if (!fileN.exists()) {
                        boolean mkdir = fileN.createNewFile();
                    }
                    fos = new FileOutputStream(fileN);
                    long sum = 0;
                    while ((len = is.read(buf)) != -1) {
                        fos.write(buf, 0, len);
                        sum += len;
                        int progress = (int) (sum * 1.0f / total * 100);
                        LogUtil.i("zzz1", "写入缓存文件" + fileN.getName() + "进度: " + progress);
                    }
                    fos.flush();
                    LogUtil.i("zzz1", "文件下载成功,准备展示文件。");
                    //2.ACache记录文件的有效期
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            superFileView.displayFile(fileN);
                        }
                    });

                } catch (Exception e) {
                    LogUtil.i("zzz1", "文件下载异常 = " + e.toString());
                } finally {
                    try {
                        if (is != null)
                            is.close();
                    } catch (IOException e) {
                    }
                    try {
                        if (fos != null)
                            fos.close();
                    } catch (IOException e) {
                    }
                }
            }
        });

    }

    /***
     * 获取缓存目录
     *
     * @param url
     * @return
     */
    private File getCacheDir(String url) {

        return new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/007/");

    }
    /***
     * 绝对路径获取缓存文件
     *
     * @param url
     * @return
     */
    private File getCacheFile(String url) {
        File cacheFile = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/007/"
                + getFileName(url));
        LogUtil.i("zzz1", "缓存文件 = " + cacheFile.toString());
        return cacheFile;
    }

    /***
     * 根据链接获取文件名（带类型的），具有唯一性
     *
     * @param url
     * @return
     */
    private String getFileName(String url) {
        String fileName = Md5Tool.hashKey(url) + "." + getFileType(url);
        return fileName;
    }

    /***
     * 获取文件类型
     *
     * @param paramString
     * @return
     */
    private String getFileType(String paramString) {
        String str = "";

        if (TextUtils.isEmpty(paramString)) {
            LogUtil.i("zzz1",  "paramString---->null");
            return str;
        }
        LogUtil.i("zzz1", "paramString:"+paramString);
        int i = paramString.lastIndexOf('.');
        if (i <= -1) {
            LogUtil.i("zzz1", "i <= -1");
            return str;
        }


        str = paramString.substring(i + 1);
        LogUtil.i("zzz1", "paramString.substring(i + 1)------>"+str);
        return str;
    }

    public void showCustomToast(String pMsg) {
        ToastUtils toastUtils = ToastUtils.createToastConfig();
        toastUtils.showToast(getApplicationContext(), pMsg);
    }
}
