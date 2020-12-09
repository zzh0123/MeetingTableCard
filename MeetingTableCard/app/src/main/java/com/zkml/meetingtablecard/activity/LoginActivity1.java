package com.zkml.meetingtablecard.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.IBinder;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.zkml.meetingtablecard.R;
import com.zkml.meetingtablecard.application.MyApplication;
import com.zkml.meetingtablecard.constant.Constant;
import com.zkml.meetingtablecard.utils.ActivityManager;
import com.zkml.meetingtablecard.utils.ActivityUtils;
import com.zkml.meetingtablecard.utils.JsonUtil;
import com.zkml.meetingtablecard.utils.LogUtil;
import com.zkml.meetingtablecard.utils.NetworkDetector;
import com.zkml.meetingtablecard.utils.ToastUtils;
import com.zkml.meetingtablecard.utils.asynctask.HttpGetAsyncTask;
import com.zkml.meetingtablecard.utils.asynctask.HttpPostAsyncTask;
import com.zkml.meetingtablecard.utils.cache.StringUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executors;
/**
 * @author: zzh
 * data : 2020/12/02
 * description：登录界面
 */
public class LoginActivity1 extends AppCompatActivity implements View.OnClickListener, View.OnFocusChangeListener{

    /**
     * 手机号
     */
    private EditText etPhone, etAuthCode;
    private ImageView ivClean, ivLoginOut;
    /**
     * 登录
     */
    private TextView tvLogin, tvGetAuthCode;
    private String phone, authCode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //设置没有标题
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        //设置全屏
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login1);
        ActivityManager.getInstance().addActivity(this);
        initView();
        inputInit();
    }

    /**
     * 初始化view
     */
    private void initView() {
        etPhone = (EditText) findViewById(R.id.et_phone);
        etAuthCode = (EditText) findViewById(R.id.et_auth_code);
        etPhone.setOnFocusChangeListener(this);
        tvLogin = (TextView) findViewById(R.id.tv_login);
        tvGetAuthCode = (TextView) findViewById(R.id.tv_get_msg_code);
        tvGetAuthCode.setOnClickListener(this);
        tvLogin.setOnClickListener(this);

        tvLogin.setClickable(false);
        tvLogin.setBackgroundResource(R.drawable.msg_code_bt_bg_grey);

        //输入框右侧操作
        ivClean = (ImageView) findViewById(R.id.iv_clean);
        ivClean.setOnClickListener(this);
        ivLoginOut = (ImageView) findViewById(R.id.iv_login_out);
        ivLoginOut.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.tv_get_msg_code) { // 获取短信验证码
            validPhone();
//            getAuthCode();
        }else if (i == R.id.tv_login) { // 登录
            login();
        } else if (i == R.id.name_clean) { ;
            etPhone.setText("");
            etPhone.setFocusable(true);
            etPhone.setFocusableInTouchMode(true);
            etPhone.requestFocus();
        } else if (i == R.id.iv_login_out) { // 退出
            ActivityManager.getInstance().exit();
        }
    }

    /**
     * 输入操作
     */
    private void inputInit() {
        etPhone.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                phone = s.toString();
                if (TextUtils.isEmpty(phone)) {
                    ivClean.setVisibility(View.GONE);
                } else {
                    ivClean.setVisibility(View.VISIBLE);
                }
                if (StringUtils.isStrEmpty(phone) || StringUtils.isStrEmpty(authCode)){
                    tvLogin.setClickable(false);
                    tvLogin.setBackgroundResource(R.drawable.msg_code_bt_bg_grey);
                } else {
                    tvLogin.setClickable(true);
                    tvLogin.setBackgroundResource(R.drawable.msg_code_bt_bg);
                }
            }
        });

        etAuthCode.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                authCode = s.toString();
                if (StringUtils.isStrEmpty(phone) || StringUtils.isStrEmpty(authCode)){
                    tvLogin.setClickable(false);
                    tvLogin.setBackgroundResource(R.drawable.msg_code_bt_bg_grey);
                } else {
                    tvLogin.setClickable(true);
                    tvLogin.setBackgroundResource(R.drawable.msg_code_bt_bg);
                }
            }
        });
    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        int i = v.getId();
    }

    /**
     * 验证手机号
     */
    private void validPhone(){
        phone = etPhone.getText().toString().trim();
        if (StringUtils.isStrEmpty(phone)){
            showCustomToast(getString(R.string.phone_empty_tip));
            return;
        }
        boolean net_flag = NetworkDetector.isNetworkConnected(this);
        if (net_flag) {
            HttpGetAsyncTask commAsyncTask = new HttpGetAsyncTask(this, null);
            Map<String, Object> reqParamMap = new HashMap<>();

            reqParamMap.put("phone", phone); // 手机号
            commAsyncTask.setShowDialog(0);
            commAsyncTask.setGetCompleteListener(new HttpGetAsyncTask.ReqGetCompleteListener() {
                @Override
                public void reqGetComplete(Map<String, Object> resultMap) {
                    if (null != resultMap) {
                        String result = (String) resultMap.get("success");
                        String message = (String) resultMap.get("message");
                        if (!StringUtils.isStrEmpty(result) && "true".equals(result)) {
                            getAuthCode();
                        }else {
                            if (!StringUtils.isStrEmpty(message)) {
                                showCustomToast(message);
                            } else {
                                showCustomToast(getString(R.string.system_error_tip));
                            }
                        }
                    }else {
                        showCustomToast(getString(R.string.system_error_tip));
                    }
                }
            });
            commAsyncTask.execute(Constant.VALID_PHONE, reqParamMap);
        } else {
            showCustomToast(getString(R.string.net_exception_tip));
        }

    }

    /**
     * 获取短信验证码
     */
    private void getAuthCode(){
        phone =  etPhone.getText().toString().trim();
        if (StringUtils.isStrEmpty(phone)){
            showCustomToast(getString(R.string.phone_empty_tip));
            return;
        }
        boolean net_flag = NetworkDetector.isNetworkConnected(this);
        if (net_flag) {
            HttpGetAsyncTask commAsyncTask = new HttpGetAsyncTask(this, null);
            Map<String, Object> reqParamMap = new HashMap<>();

            reqParamMap.put("phone", phone); // 手机号
            commAsyncTask.setShowDialog(0);
            commAsyncTask.setGetCompleteListener(new HttpGetAsyncTask.ReqGetCompleteListener() {
                @Override
                public void reqGetComplete(Map<String, Object> resultMap) {
                    if (null != resultMap) {
                        String result = (String) resultMap.get("success");
                        String message = (String) resultMap.get("message");
                        if (!StringUtils.isStrEmpty(result) && "true".equals(result)) {
                            showCustomToast(getString(R.string.msg_code_get_success));
                        }else {
                            if (!StringUtils.isStrEmpty(message)) {
                                showCustomToast(message);
                            } else {
                                showCustomToast(getString(R.string.system_error_tip));
                            }
                        }
                    }else {
                        showCustomToast(getString(R.string.system_error_tip));
                    }
                }
            });
            commAsyncTask.execute(Constant.GET_MSG_CODE, reqParamMap);
        } else {
            showCustomToast(getString(R.string.net_exception_tip));
        }

    }

    /**
     * 登录
     */
    public void login() {
        phone = etPhone.getText().toString().trim();
        authCode = etAuthCode.getText().toString().trim();
        boolean valiLoginForm = valiLoginForm(phone, authCode);
        if (valiLoginForm) {
            reqLogin();
        }
    }

    public boolean valiLoginForm(String userName, String msgCode) {
        if (TextUtils.isEmpty(userName)) {
            showCustomToast(getString(R.string.user_name_error));
            return false;
        }
        if (TextUtils.isEmpty(msgCode)) {
            showCustomToast(getString(R.string.msg_code_error));
            return false;
        }
        return true;
    }

    /**
     * 登录
     */
    private void reqLogin() {
        boolean connected = NetworkDetector.isNetworkConnected(this);
        if (connected) {
            SharedPreferences sharedPreferences = ActivityUtils.selSharedPreferencesData(LoginActivity1.this, MyApplication.USER_INFO_NAME);
            final SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.remove("token");
            editor.commit();
            Map<String, String> reqParamMap = new HashMap<>();
            reqParamMap.put("phone", phone);
            reqParamMap.put("authCode", authCode);
            HttpPostAsyncTask commAsyncTask = new HttpPostAsyncTask(LoginActivity1.this, null);
            commAsyncTask.setShowDialog(0);
            commAsyncTask.setPostCompleteListener(new HttpPostAsyncTask.PostFormCompleteListener() {
                @Override
                public void postFormComplete(Map<String, Object> resultMap, Map<String, String> reqMap) {
                    if (null != resultMap) {
                        String success = (String) resultMap.get("success");
                        if ("true".equals(success)) {
                            String data = (String) resultMap.get("data");
                            if (!StringUtils.isStrEmpty(data)) {
                                Map<String, Object> dataMap = StringUtils.transJsonToMap(data);
                                String token = (String) dataMap.get("token");
                                String userId = (String) dataMap.get("userId");
                                if (!StringUtils.isStrEmpty(token) && !StringUtils.isStrEmpty(userId)) {
                                    editor.putString("token", token);
                                    editor.putString("userId", userId);
                                    editor.commit();
                                    Intent intent = new Intent(LoginActivity1.this, MeetingListActivity.class);
                                    startActivity(intent);
                                }else {
                                    showCustomToast(getString(R.string.system_error_tip));
                                }
                            }else {
                                showCustomToast(getString(R.string.system_error_tip));
                            }
                        } else {
                            String message = (String) resultMap.get("message");
                            showCustomToast(message);
                        }
                    } else {
                        showCustomToast(getString(R.string.system_error_tip));
                    }
                }
            });
            commAsyncTask.executeOnExecutor(Executors.newCachedThreadPool(), Constant.LOGIN, reqParamMap);
        } else {
            showCustomToast(getString(R.string.net_exception_tip));
        }
    }

    public void showCustomToast(String pMsg) {
        ToastUtils toastUtils = ToastUtils.createToastConfig();
        toastUtils.showToast(getApplicationContext(), pMsg);
    }

    /**
     * 点击空白区域隐藏键盘.
     */
    @Override
    public boolean dispatchTouchEvent(MotionEvent me) {
        if (me.getAction() == MotionEvent.ACTION_DOWN) {  //把操作放在用户点击的时候
            View v = getCurrentFocus();      //得到当前页面的焦点,ps:有输入框的页面焦点一般会被输入框占据
            if (isShouldHideKeyboard(v, me)) { //判断用户点击的是否是输入框以外的区域
                hideKeyboard(v.getWindowToken());   //收起键盘
            }
        }
        return super.dispatchTouchEvent(me);
    }

    /**
     * 根据EditText所在坐标和用户点击的坐标相对比，来判断是否隐藏键盘，因为当用户点击EditText时则不能隐藏
     *
     * @param v
     * @param event
     * @return
     */
    private boolean isShouldHideKeyboard(View v, MotionEvent event) {
        if (v != null && (v instanceof EditText)) {  //判断得到的焦点控件是否包含EditText
            int[] l = {0, 0};
            v.getLocationInWindow(l);
            int left = l[0],    //得到输入框在屏幕中上下左右的位置
                    top = l[1],
                    bottom = top + v.getHeight(),
                    right = left + v.getWidth();
            if (event.getX() > left && event.getX() < right
                    && event.getY() > top && event.getY() < bottom) {
                // 点击位置如果是EditText的区域，忽略它，不收起键盘。
                return false;
            } else {
                return true;
            }
        }
        // 如果焦点不是EditText则忽略
        return false;
    }

    /**
     * 获取InputMethodManager，隐藏软键盘
     *
     * @param token
     */
    private void hideKeyboard(IBinder token) {
        if (token != null) {
            InputMethodManager im = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            im.hideSoftInputFromWindow(token, InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }
}
