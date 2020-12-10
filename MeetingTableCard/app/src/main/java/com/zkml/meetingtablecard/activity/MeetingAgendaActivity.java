package com.zkml.meetingtablecard.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.reflect.TypeToken;
import com.haibin.calendarview.Calendar;
import com.haibin.calendarview.CalendarView;
import com.zkml.meetingtablecard.R;
import com.zkml.meetingtablecard.adapter.AgendaItemAdapter;
import com.zkml.meetingtablecard.bean.AgendaItemBean;
import com.zkml.meetingtablecard.constant.Constant;
import com.zkml.meetingtablecard.utils.ActivityManager;
import com.zkml.meetingtablecard.utils.NetworkDetector;
import com.zkml.meetingtablecard.utils.ToastUtils;
import com.zkml.meetingtablecard.utils.asynctask.HttpGetAsyncTask;
import com.zkml.meetingtablecard.utils.cache.StringUtils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author: zzh
 * data : 2020/12/04
 * description：会议议程
 */
public class MeetingAgendaActivity extends AppCompatActivity implements CalendarView.OnCalendarSelectListener, CalendarView.OnMonthChangeListener {

    private CalendarView mCalendarView;
    /**
     * 当前日历所显示的年月日
     */
    private int mYear;
    private int mMonth;
    private int mDay;
    /**
     * 会议id, 日期
     */
    private TextView etMeetingName, tvStartDate, tvEndDate;
    private String meetingApplyId, meetingDate;
    /**
     * 会议信息列表
     */
    private RecyclerView recyclerView;
    private List<AgendaItemBean> agendaList = new ArrayList<>();
    private AgendaItemAdapter adapter;
    private LinearLayout llBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //设置没有标题
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        //设置全屏
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_meeting_agenda);
        ActivityManager.getInstance().addActivity(this);
        initView();
    }

    /**
     * 初始化view
     */
    private void initView() {
        meetingApplyId = getIntent().getStringExtra("meetingApplyId");
        // 日历
        mCalendarView = findViewById(R.id.calendar_view);
        mCalendarView.setOnCalendarSelectListener(this);
        mCalendarView.setOnMonthChangeListener(this);
        mCalendarView.scrollToCurrent();

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new AgendaItemAdapter(this, agendaList);
        recyclerView.setAdapter(adapter);
        llBack = findViewById(R.id.ll_back);
        llBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    @Override
    public void onCalendarOutOfRange(Calendar calendar) {

    }

    private Calendar getSchemeCalendar(int year, int month, int day, int color, String text) {
        Calendar calendar = new Calendar();
        calendar.setYear(year);
        calendar.setMonth(month);
        calendar.setDay(day);
        //如果单独标记颜色、则会使用这个颜色
        calendar.setSchemeColor(color);
        calendar.setScheme(text);
        return calendar;
    }

    @Override
    public void onCalendarSelect(Calendar calendar, boolean isClick) {
        int year = calendar.getYear();
        int month = calendar.getMonth();
        int day = calendar.getDay();
        meetingDate = "" + year + "-" + month + "-" + day; // yyyy-MM-dd
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        try {
            Date date = sdf.parse(meetingDate); //将字符型转换成日期型
            meetingDate = sdf.format(date);
        } catch (Exception e) {
        }

        getMeetingAgenda();
    }

    /**
     * 滑动日历切换月份
     */
    @Override
    public void onMonthChange(int year, int month) {
        mYear = year;
        mMonth = month;
    }

    /**
     * 获取会议议程信息列表
     */
    private void getMeetingAgenda() {
        boolean connected = NetworkDetector.isNetworkConnected(this);
        if (connected) {
            Map<String, String> reqParamMap = new HashMap<>();
            reqParamMap.put("meetingApplyId", meetingApplyId);
            reqParamMap.put("meetingDate", meetingDate);
            HttpGetAsyncTask commAsyncTask = new HttpGetAsyncTask(this, null);
            commAsyncTask.setShowDialog(0);
            commAsyncTask.setGetCompleteListener(new HttpGetAsyncTask.ReqGetCompleteListener() {
                @Override
                public void reqGetComplete(Map<String, Object> resultMap) {
                    if (null != resultMap) {
                        String success = (String) resultMap.get("success");
                        if ("true".equals(success)) {
                            String data = (String) resultMap.get("data");
                            if (!StringUtils.isStrEmpty(data)) {
                                agendaList.clear();
                                TypeToken<List<AgendaItemBean>> typeToken = new TypeToken<List<AgendaItemBean>>() {
                                };
                                List<AgendaItemBean> modelBeanList = (List<AgendaItemBean>) StringUtils.convertMapToList(data, typeToken);
                                if (modelBeanList != null && modelBeanList.size() > 0) {
                                    agendaList.addAll(modelBeanList);
                                }
                                adapter.notifyDataSetChanged();
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
            commAsyncTask.execute(Constant.GET_MEETING_AGENDA, reqParamMap);
        } else {
            showCustomToast(getString(R.string.net_exception_tip));
        }
    }

    public void showCustomToast(String pMsg) {
        ToastUtils toastUtils = ToastUtils.createToastConfig();
        toastUtils.showToast(getApplicationContext(), pMsg);
    }

}
