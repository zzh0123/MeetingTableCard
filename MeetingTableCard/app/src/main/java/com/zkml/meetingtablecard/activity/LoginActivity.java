package com.zkml.meetingtablecard.activity;

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
import android.view.inputmethod.InputMethodManager;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.fragment.app.FragmentActivity;

import com.zkml.meetingtablecard.R;
import com.zkml.meetingtablecard.application.MyApplication;
import com.zkml.meetingtablecard.constant.Constant;
import com.zkml.meetingtablecard.utils.ActivityManager;
import com.zkml.meetingtablecard.utils.ActivityUtils;
import com.zkml.meetingtablecard.utils.JsonUtil;
import com.zkml.meetingtablecard.utils.LogUtil;
import com.zkml.meetingtablecard.utils.NetworkDetector;
import com.zkml.meetingtablecard.utils.PackageUtil;
import com.zkml.meetingtablecard.utils.ToastUtils;
import com.zkml.meetingtablecard.utils.asynctask.HttpPostAsyncTask;
import com.zkml.meetingtablecard.utils.cache.StringUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executors;

/**
 * @author: zzh
 * data : 2020/8/10
 * description：登录（大小屏共用）
 */
public class LoginActivity extends FragmentActivity implements View.OnClickListener, View.OnFocusChangeListener {

    /**
     * 退出
     */
    private ImageView mIvLoginOut;
    /**
     * 用户名
     */
    private EditText mEtUserName;
    /**
     * 密码
     */
    private EditText mEtPassword;
    private String mUserName;
    private String mPassword;
    /**
     * 登录
     */
    private TextView mTvLogin;
    private ImageView mNameCleanView, mPwdSee;
    /**
     * 默认隐藏密码
     */
    private boolean mIsShowPwd = false;

    /**
     * 记住密码
     */
    private CheckBox mCbPassword;
    private boolean isChecked;
    /**
     * 渠道名称，区分大小屏，进行跳转
     */
    private String channel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ActivityManager.getInstance().addActivity(this);
        initView();
        inputInit();
    }

    /**
     * 初始化view
     */
    private void initView() {
        channel = PackageUtil.getAppMetaData(this, "channel");
        mIvLoginOut = (ImageView) findViewById(R.id.iv_login_out);
        mIvLoginOut.setOnClickListener(this);

        mEtUserName = (EditText) findViewById(R.id.et_user_name);
        mEtPassword = (EditText) findViewById(R.id.et_password);
        mTvLogin = (TextView) findViewById(R.id.tv_login);
        mTvLogin.setOnClickListener(this);

        mEtUserName.setOnFocusChangeListener(this);
        mEtPassword.setOnFocusChangeListener(this);
        //输入框右侧操作
        mNameCleanView = (ImageView) findViewById(R.id.name_clean);
        mPwdSee = (ImageView) findViewById(R.id.pwd_see);
        mNameCleanView.setOnClickListener(this);
        mPwdSee.setOnClickListener(this);

        mCbPassword = (CheckBox) findViewById(R.id.cb_password);
        SharedPreferences sps = ActivityUtils.selSharedPreferencesData(LoginActivity.this, MyApplication.USER_INFO_NAME);
        isChecked = sps.getBoolean("isChecked", false);
        mCbPassword.setChecked(isChecked);
        if (isChecked) {
            String userName = sps.getString("userName", "");
            String password = sps.getString("password", "");
            if (!StringUtils.isStrEmpty(userName) && !StringUtils.isStrEmpty(password)) {
                mEtUserName.setText(userName);
                mEtPassword.setText(password);
            } else {
                mEtUserName.setText("");
                mEtPassword.setText("");
            }
        } else {
            mEtUserName.setText("");
            mEtPassword.setText("");
        }
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.tv_login) { // 登录
            login();
        } else if (i == R.id.name_clean) {
            mEtPassword.setText("");
            mEtUserName.setText("");
            mEtUserName.setFocusable(true);
            mEtUserName.setFocusableInTouchMode(true);
            mEtUserName.requestFocus();
        } else if (i == R.id.pwd_see) {
            if (mIsShowPwd) {
                int position = mEtPassword.getSelectionStart();
                mEtPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
                mPwdSee.setImageResource(R.mipmap.et_password_close);
                mEtPassword.setSelection(position);
            } else {
                int position = mEtPassword.getSelectionStart();
                mEtPassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                mPwdSee.setImageResource(R.mipmap.et_password_open);
                mEtPassword.setSelection(position);
            }
            mIsShowPwd = !mIsShowPwd;
        } else if (i == R.id.iv_login_out) { // 退出
            ActivityManager.getInstance().exit();
        }
    }

    /**
     * 输入操作
     */
    private void inputInit() {
        mEtUserName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                mUserName = s.toString();
                if (TextUtils.isEmpty(mUserName)) {
                    mNameCleanView.setVisibility(View.GONE);
                } else {
                    mNameCleanView.setVisibility(View.VISIBLE);
                }
            }
        });

        mEtPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                mPassword = s.toString();
            }
        });
    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        int i = v.getId();
    }

    /**
     * 登录
     */
    public void login() {
        mUserName = mEtUserName.getText().toString().trim();
        mPassword = mEtPassword.getText().toString().trim();
        boolean valiLoginForm = valiLoginForm(mUserName, mPassword);
        if (valiLoginForm) {
            if (!StringUtils.isStrEmpty(channel)) {
                if ("projectSmall".equals(channel) || "projectBig".equals(channel)) {
                    projectLogin();
                } else {
                    reqLogin();
                }
            }

        }
    }

    public boolean valiLoginForm(String userName, String password) {
        if (TextUtils.isEmpty(userName)) {
            showCustomToast(getString(R.string.user_name_error));
            return false;
        }
        if (TextUtils.isEmpty(password)) {
            showCustomToast(getString(R.string.password_error));
            return false;
        }
        return true;
    }

    /**
     * 登录,不走网关
     */
    private void projectLogin() {
        boolean net_flag = NetworkDetector.isNetworkConnected(this);
        if (net_flag) {
            SharedPreferences sharedPreferences = ActivityUtils.selSharedPreferencesData(LoginActivity.this, MyApplication.USER_INFO_NAME);
            final SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.remove("token");
            editor.commit();
            HttpPostAsyncTask commAsyncTask = new HttpPostAsyncTask(this, null);
            Map<String, Object> reqParamMap = new HashMap<>();

            reqParamMap.put("username", mUserName); // 用户名
            reqParamMap.put("password", mPassword); // 密码

            commAsyncTask.setShowDialog(0);
            commAsyncTask.setPostCompleteListener(new HttpPostAsyncTask.PostFormCompleteListener() {
                @Override
                public void postFormComplete(Map<String, Object> resultMap, Map<String, String> reqMap) {
                    try {
                        if (null != resultMap) {
                            String result = (String) resultMap.get("success");
                            String message = (String) resultMap.get("message");
                            if (!StringUtils.isStrEmpty(result) && "true".equals(result)) {
                                String data = (String) resultMap.get("data");
                                if (!StringUtils.isStrEmpty(data)) {
                                    Map<String, Object> dataMap = StringUtils.transJsonToMap(data);
                                    String token = (String) dataMap.get("token");
                                    LogUtil.i("zzz1", "token new  " + token);
                                    if (!StringUtils.isStrEmpty(token)) {
                                        editor.putString("token", token);
                                        boolean isChecked = mCbPassword.isChecked();
                                        editor.putBoolean("isChecked", isChecked);
                                        if (isChecked) {
                                            editor.putString("userName", mUserName);
                                            editor.putString("password", mPassword);
                                        } else {
                                            editor.putString("userName", "");
                                            editor.putString("password", "");
                                        }
                                        editor.commit();
                                        if (!StringUtils.isStrEmpty(channel)) {
                                            Intent intent = null;
//                                            if ("projectSmall".equals(channel)) {
//                                                intent = new Intent(LoginActivity.this, SmallScreenActivity.class);
//                                            } else if ("projectBig".equals(channel)) {
//                                                intent = new Intent(LoginActivity.this, BigScreenActivity.class);
//                                            }
//                                            startActivity(intent);
                                        }
                                    }
                                } else {
                                    showCustomToast(getString(R.string.system_error_tip));
                                }
                            } else {
                                if (!StringUtils.isStrEmpty(message)) {
                                    showCustomToast(message);
                                } else {
                                    showCustomToast(getString(R.string.system_error_tip));
                                }
                            }

                        } else {
                            showCustomToast(getString(R.string.system_error_tip));
                        }
                    } catch (Exception e) {
                        showCustomToast(getString(R.string.system_error_tip));
                    }
                }
            });
            commAsyncTask.execute(Constant.LOGINURL_PROJECT, null, JsonUtil.toJson(reqParamMap));
        } else {
            showCustomToast(getString(R.string.net_exception_tip));
        }

    }

    /**
     * 登录
     */
    private void reqLogin() {
        boolean connected = NetworkDetector.isNetworkConnected(this);
        if (connected) {
            SharedPreferences sharedPreferences = ActivityUtils.selSharedPreferencesData(LoginActivity.this, MyApplication.USER_INFO_NAME);
            final SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.remove("token");
            editor.commit();
            Map<String, String> reqParamMap = new HashMap<>();
            reqParamMap.put("username", mUserName);
            reqParamMap.put("password", mPassword);

            HttpPostAsyncTask commAsyncTask = new HttpPostAsyncTask(LoginActivity.this, null);
            commAsyncTask.setShowDialog(0);
            commAsyncTask.setPostCompleteListener(new HttpPostAsyncTask.PostFormCompleteListener() {
                @Override
                public void postFormComplete(Map<String, Object> resultMap, Map<String, String> reqMap) {
                    if (null != resultMap) {
                        String result = (String) resultMap.get("result");
                        if ("success".equals(result)) {
                            String model = (String) resultMap.get("model");
                            Map<String, Object> jsonToMap = StringUtils.transJsonToMap(model);
                            String token = (String) jsonToMap.get("token");
                            String gatewayRelayServiceConfAuth = (String) jsonToMap.get("gatewayRelayServiceConfAuth");
                            Map<String, Object> gatewayMap = StringUtils.transJsonToMap(gatewayRelayServiceConfAuth);
                            String meetingV2 = (String) gatewayMap.get("meeting_v2_saas_online");
                            Map<String, Object> meetingV2Map = StringUtils.transJsonToMap(meetingV2);
                            String deploySign = (String) meetingV2Map.get("deploySign");
                            LogUtil.i("zzz1", "deploySign  " + deploySign);
                            if (!StringUtils.isStrEmpty(token)) {
                                editor.putString("token", token);
                                if (!StringUtils.isStrEmpty(deploySign)) {
                                    editor.putString("deploySign", deploySign);
                                }
                                boolean isChecked = mCbPassword.isChecked();
                                editor.putBoolean("isChecked", isChecked);
                                if (isChecked) {
                                    editor.putString("userName", mUserName);
                                    editor.putString("password", mPassword);
                                } else {
                                    editor.putString("userName", "");
                                    editor.putString("password", "");
                                }
                                editor.commit();
                                if (!StringUtils.isStrEmpty(channel)) {
//                                    Intent intent = null;
//                                    if ("small".equals(channel)) {
//                                        intent = new Intent(LoginActivity.this, SmallScreenActivity.class);
//                                    } else {
//                                        intent = new Intent(LoginActivity.this, BigScreenActivity.class);
//                                    }
//                                    startActivity(intent);
                                }
                            } else {
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
            commAsyncTask.executeOnExecutor(Executors.newCachedThreadPool(), Constant.LOGINURL, reqParamMap);
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
