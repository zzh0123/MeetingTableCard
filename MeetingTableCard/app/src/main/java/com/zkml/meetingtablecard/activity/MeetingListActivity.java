package com.zkml.meetingtablecard.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.reflect.TypeToken;
import com.zkml.meetingtablecard.R;
import com.zkml.meetingtablecard.adapter.MeetingItemAdapter;
import com.zkml.meetingtablecard.bean.MeetingItemBean;
import com.zkml.meetingtablecard.constant.Constant;
import com.zkml.meetingtablecard.utils.ActivityManager;
import com.zkml.meetingtablecard.utils.DateUtils;
import com.zkml.meetingtablecard.utils.JsonUtil;
import com.zkml.meetingtablecard.utils.LogUtil;
import com.zkml.meetingtablecard.utils.NetworkDetector;
import com.zkml.meetingtablecard.utils.ToastUtils;
import com.zkml.meetingtablecard.utils.asynctask.HttpPostAsyncTask;
import com.zkml.meetingtablecard.utils.cache.StringUtils;
import com.zkml.meetingtablecard.view.datepick.DateHHMMSelectView;
import com.zkml.meetingtablecard.view.dialog.DateSelectView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author: zzh
 * data : 2020/12/02
 * description：会议信息列表界面
 */
public class MeetingListActivity extends AppCompatActivity implements View.OnClickListener,
        DateHHMMSelectView.OnTimePickListener{

    /**
     * 会议开始日期，结束日期，名称
     */
    private TextView etMeetingName, tvStartDate, tvEndDate;
    private String conferenceName, beginDate, endDate;
    private RelativeLayout rlStartDate, rlEndDate;
    /**
     * 查询
     */
    private TextView tvSearch;

    /**
     * 时间选择器选择的年月日
     */
    private int mYear = 0, mMonth = 0, mDay = 0, mSelectData = 0;
    private String mHour = "", mMin = "";
    private Calendar calendar;
    /**
     * 时间
     */
    private String maintenanceDate = "";

    /**
     * 会议信息列表
     */
    private RecyclerView recyclerView;
    private List<MeetingItemBean> meetingList = new ArrayList<>();
    private MeetingItemAdapter adapter;
    private ImageView ivLoginOut;

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
        calendar = Calendar.getInstance();
        etMeetingName = (TextView) findViewById(R.id.et_meeting_name);
        tvStartDate = (TextView) findViewById(R.id.tv_start_date);
        tvEndDate = (TextView) findViewById(R.id.tv_end_date);
        rlStartDate = (RelativeLayout) findViewById(R.id.rl_start_date);
        rlEndDate = (RelativeLayout) findViewById(R.id.rl_end_date);
        tvSearch = (TextView) findViewById(R.id.tv_search);

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        ivLoginOut = (ImageView) findViewById(R.id.iv_login_out);
        ivLoginOut.setOnClickListener(this);
    }

    private void initEvent() {
        rlStartDate.setOnClickListener(this);
        rlEndDate.setOnClickListener(this);
        tvSearch.setOnClickListener(this);
        adapter = new MeetingItemAdapter(this, meetingList);
        adapter.setOnItemClickListener(new MeetingItemAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int pos) {
                MeetingItemBean itemBean = meetingList.get(pos);
                if (itemBean != null){
                    Intent intent = new Intent(MeetingListActivity.this, HomeActivity.class);
                    intent.putExtra("itemBean",itemBean);
                    startActivity(intent);
                }
            }
        });
        recyclerView.setAdapter(adapter);

    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.tv_search) { // 查询
            getMeetingList();
        } else if (i == R.id.rl_start_date) { ;
            //时间选择器
            chooseBuyCarTime(true);
        } else if (i == R.id.rl_end_date) { ;
            //时间选择器
            chooseBuyCarTime(false);
        } else if (i == R.id.iv_login_out) { // 退出
            ActivityManager.getInstance().exit();
        }
    }

    /**
     * 选择保养时间
     */
    private void chooseBuyCarTime(boolean isStart) {
        DateHHMMSelectView  mTimePickerDialog = null;
        mTimePickerDialog = new DateHHMMSelectView(this, calendar.get(Calendar.YEAR) - 30);
        mTimePickerDialog.setDialogMode(DateSelectView.DIALOG_MODE_BOTTOM);
        mTimePickerDialog.setTimePickListener(this);
        mTimePickerDialog.isStart(isStart);
        if (mYear == 0 && mMonth == 0 && mDay == 0) {
            mTimePickerDialog.setTime(30, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH) + 1, calendar.get(Calendar.DAY_OF_MONTH), calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE));
            mTimePickerDialog.setEndYearTime(calendar.get(Calendar.YEAR) + 30);
        } else {
            mTimePickerDialog.setTime(mSelectData, mYear, mMonth, mDay, Integer.parseInt(mHour), Integer.parseInt(mMin));
        }
        mTimePickerDialog.show();
    }

    @Override
    public void onClick(int selectDate, int year, int month, int day, String hour, String minute, boolean isSart) {
        mSelectData = selectDate;
        mYear = year;
        mMonth = month;
        mDay = day;
        mHour = hour;
        mMin = minute;
        String date = mYear + "-" + mMonth + "-" + mDay + " " + mHour + ":" + mMin;
        String mTimeStr = DateUtils.getTimeFormat("yyyy-MM-dd HH:mm:ss", DateUtils.getDateFromString2(date));
        String mTimeNowStr = calendar.get(Calendar.YEAR) + "-" + (calendar.get(Calendar.MONTH) + 1)
                + "-" + calendar.get(Calendar.DAY_OF_MONTH) + " " + calendar.get(Calendar.HOUR_OF_DAY)
                + ":"+ calendar.get(Calendar.MINUTE);
        Date dateSelect = DateUtils.getDateFromString(mTimeStr, "yyyy-MM-dd HH:mm");
        Date dateNow = DateUtils.getDateFromString(mTimeNowStr, "yyyy-MM-dd HH:mm");

        if (dateSelect != null && dateNow != null) {
            if (dateSelect.getTime() > dateNow.getTime()) {
                showCustomToast(getResources().getString(R.string.assets_select_time_over_now));
                return;
            }
        } else {
            showCustomToast(getResources().getString(R.string.assets_select_time_failure));
            return;
        }
        if (isSart){
            LogUtil.i("zzz1", "beginDate--->  " + mTimeStr);
            beginDate = mTimeStr;
            tvStartDate.setText(beginDate);
        } else {
            LogUtil.i("zzz1", "endDate--->  " + mTimeStr);
            endDate = mTimeStr;
            tvEndDate.setText(endDate);
        }

//        tvMaintenanceDate.setText(maintenanceDate);
    }

    /**
     * 获取当前登录人会议信息列表
     */
    private void getMeetingList() {
        boolean connected = NetworkDetector.isNetworkConnected(this);
        if (connected) {
            if (!StringUtils.isStrEmpty(beginDate) && StringUtils.isStrEmpty(endDate)){
                showCustomToast(getString(R.string.date_tip1));
                return;
            }
            if (StringUtils.isStrEmpty(beginDate) && !StringUtils.isStrEmpty(endDate)){
                showCustomToast(getString(R.string.date_tip));
                return;
            }
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
                                TypeToken<List<MeetingItemBean>> typeToken = new TypeToken<List<MeetingItemBean>>() {
                                };
                                List<MeetingItemBean> modelBeanList = (List<MeetingItemBean>) StringUtils.convertMapToList(data, typeToken);
                                if (modelBeanList != null && modelBeanList.size() > 0) {
                                    meetingList.clear();
                                    meetingList.addAll(modelBeanList);
                                    adapter.notifyDataSetChanged();
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
            commAsyncTask.execute(Constant.GET_MEETING_LIST, null, JsonUtil.toJson(reqParamMap));
        } else {
            showCustomToast(getString(R.string.net_exception_tip));
        }
    }

    public void showCustomToast(String pMsg) {
        ToastUtils toastUtils = ToastUtils.createToastConfig();
        toastUtils.showToast(getApplicationContext(), pMsg);
    }
}
