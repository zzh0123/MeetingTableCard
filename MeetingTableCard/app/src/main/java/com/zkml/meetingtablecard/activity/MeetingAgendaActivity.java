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
import com.zkml.meetingtablecard.adapter.AgendaDateAdapter;
import com.zkml.meetingtablecard.adapter.AgendaItemAdapter;
import com.zkml.meetingtablecard.bean.AgendaDateBean;
import com.zkml.meetingtablecard.bean.AgendaItemBean;
import com.zkml.meetingtablecard.constant.Constant;
import com.zkml.meetingtablecard.utils.ActivityManager;
import com.zkml.meetingtablecard.utils.DateUtils;
import com.zkml.meetingtablecard.utils.LogUtil;
import com.zkml.meetingtablecard.utils.NetworkDetector;
import com.zkml.meetingtablecard.utils.ToastUtils;
import com.zkml.meetingtablecard.utils.Utils;
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
public class MeetingAgendaActivity extends AppCompatActivity {

    /**
     * 会议id, 日期
     */
    private String meetingApplyId, meetingDate = "";
    /**
     * 会议信息列表
     */
    private RecyclerView recyclerView;
    private List<AgendaItemBean> agendaList = new ArrayList<>();
    private List<AgendaItemBean> agendaListDay = new ArrayList<>();
    private AgendaItemAdapter adapter;
    private LinearLayout llBack;
    /**
     * 日期列表
     */
    private RecyclerView recyclerViewDate;
    private List<AgendaDateBean> dateList = new ArrayList<>();
    private AgendaDateAdapter dateAdapter;
    private String monthDay = "";

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
        initEvent();
        getMeetingAgenda();
    }

    /**
     * 初始化view
     */
    private void initView() {
        meetingApplyId = getIntent().getStringExtra("meetingApplyId");
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new AgendaItemAdapter(this, agendaListDay);
        recyclerView.setAdapter(adapter);

        // 日期横向列表
        recyclerViewDate = findViewById(R.id.recyclerView_date);
        recyclerViewDate.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(RecyclerView.HORIZONTAL);
        recyclerViewDate.setLayoutManager(layoutManager);
        dateAdapter = new AgendaDateAdapter(this, dateList);
        recyclerViewDate.setAdapter(dateAdapter);

        llBack = findViewById(R.id.ll_back);
    }

    private void initEvent() {
        llBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        dateAdapter.setOnItemClickListener(new AgendaDateAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int pos) {
                setSelectDay(pos);
            }
        });
    }

    private void setSelectDay(int pos){
        for (int i = 0; i < dateList.size(); i++){
            dateList.get(i).setSelected(false);
        }
        dateList.get(pos).setSelected(true);
        String monthDay = dateList.get(pos).getMonthDay();
        LogUtil.i("zzz1", "sel monthDay " + monthDay);
        agendaListDay.clear();
        for (int i = 0; i < agendaList.size(); i++){
            if (agendaList.get(i).getBeginTime().contains(monthDay)){
                agendaListDay.add(agendaList.get(i));
            }
        }
        dateAdapter.notifyDataSetChanged();
        adapter.notifyDataSetChanged();
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
//                        String resultStr = Utils.readAssert(MeetingAgendaActivity.this, "test1.txt");
//                        resultMap = StringUtils.transResultJsonToMap(resultStr);
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
                                    setDateList(agendaList);
                                }
                                adapter.notifyDataSetChanged();
                                dateAdapter.notifyDataSetChanged();
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

    private void setDateList(List<AgendaItemBean> agendaList){
        AgendaItemBean agendaBean = null;
        for (int i = 0; i < agendaList.size(); i++){
            agendaBean = agendaList.get(i);
            if (DateUtils.getMonthDay(agendaBean.getBeginTime()).equals(monthDay)){
            } else {
                AgendaDateBean dateBean = new AgendaDateBean();
                dateBean.setWeek(DateUtils.getWeek(agendaBean.getBeginTime()));
                monthDay = DateUtils.getMonthDay(agendaBean.getBeginTime());
                dateBean.setMonthDay(DateUtils.getMonthDay(agendaBean.getBeginTime()));
                dateList.add(dateBean);
            }
        }
        dateList.get(0).setSelected(true);
        setSelectDay(0);
    }

    public void showCustomToast(String pMsg) {
        ToastUtils toastUtils = ToastUtils.createToastConfig();
        toastUtils.showToast(getApplicationContext(), pMsg);
    }

}
