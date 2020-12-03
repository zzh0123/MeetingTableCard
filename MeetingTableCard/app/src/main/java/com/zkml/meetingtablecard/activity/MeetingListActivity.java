package com.zkml.meetingtablecard.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.zkml.meetingtablecard.R;
import com.zkml.meetingtablecard.constant.Constant;
import com.zkml.meetingtablecard.utils.ActivityManager;
import com.zkml.meetingtablecard.utils.NetworkDetector;
import com.zkml.meetingtablecard.utils.ToastUtils;
import com.zkml.meetingtablecard.utils.asynctask.HttpPostAsyncTask;
import com.zkml.meetingtablecard.utils.cache.StringUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executors;

/**
 * @author: zzh
 * data : 2020/12/02
 * description：会议信息列表界面
 */
public class MeetingListActivity extends AppCompatActivity {

    /**
     * 会议开始日期，结束日期，名称
     */
    private TextView etMeetingName, tvStartDate, tvEndDate;
    private String conferenceName, beginDate, endDate;
    /**
     * 查询
     */
    private TextView tvSearch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //设置没有标题
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        //设置全屏
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_meeting_list);
        ActivityManager.getInstance().addActivity(this);
        initView();
        initEvent();
        getMeetingList();
    }

    /**
     * 初始化view
     */
    private void initView() {
        etMeetingName = (TextView) findViewById(R.id.et_meeting_name);
        tvStartDate = (TextView) findViewById(R.id.tv_start_date);
        tvEndDate = (TextView) findViewById(R.id.tv_end_date);
        tvSearch = (TextView) findViewById(R.id.tv_search);
    }

    private void initEvent() {
        tvSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getMeetingList();
            }
        });
    }

    /**
     * 获取当前登录人会议信息列表
     */
    private void getMeetingList() {
        boolean connected = NetworkDetector.isNetworkConnected(this);
        if (connected) {
            conferenceName = etMeetingName.getText().toString().trim();
            beginDate = tvStartDate.getText().toString().trim();
            endDate = tvEndDate.getText().toString().trim();
            Map<String, String> reqParamMap = new HashMap<>();
            reqParamMap.put("beginDate", beginDate);
            reqParamMap.put("conferenceName", conferenceName);
            reqParamMap.put("endDate", endDate);
            HttpPostAsyncTask commAsyncTask = new HttpPostAsyncTask(this, null);
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
                                if (!StringUtils.isStrEmpty(token)) {

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
            commAsyncTask.executeOnExecutor(Executors.newCachedThreadPool(), Constant.GET_MEETING_LIST, reqParamMap);
        } else {
            showCustomToast(getString(R.string.net_exception_tip));
        }
    }

    public void showCustomToast(String pMsg) {
        ToastUtils toastUtils = ToastUtils.createToastConfig();
        toastUtils.showToast(getApplicationContext(), pMsg);
    }
}
