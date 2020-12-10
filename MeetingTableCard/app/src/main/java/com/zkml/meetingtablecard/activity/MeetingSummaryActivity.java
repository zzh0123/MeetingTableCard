package com.zkml.meetingtablecard.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.reflect.TypeToken;
import com.zkml.meetingtablecard.R;
import com.zkml.meetingtablecard.adapter.AttachmentAdapter;
import com.zkml.meetingtablecard.bean.AttachmentBean;
import com.zkml.meetingtablecard.bean.ParticipantsBean;
import com.zkml.meetingtablecard.constant.Constant;
import com.zkml.meetingtablecard.utils.ActivityManager;
import com.zkml.meetingtablecard.utils.MyTextUtil;
import com.zkml.meetingtablecard.utils.NetworkDetector;
import com.zkml.meetingtablecard.utils.ToastUtils;
import com.zkml.meetingtablecard.utils.asynctask.HttpGetAsyncTask;
import com.zkml.meetingtablecard.utils.cache.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author: zzh
 * data : 2020/12/09
 * description：目录页
 */
public class MeetingSummaryActivity extends AppCompatActivity {
    /**
     * 会议id, 日期
     */
    private String meetingApplyId;
    /**
     * 会议名称, 会议时间, 会议地点, 承办人
     */
    private TextView tvMeetingName, tvDate, tvMeetingLocation, tvUndertaker;
    /**
     * 参会人员, 纪要内容
     */
    private TextView tvParticipants, tvSummaryContent;
    /**
     * 附件列表
     */
    private RecyclerView recyclerView;
    private List<AttachmentBean> attachmentList = new ArrayList<>();
    private AttachmentAdapter adapter;
    private LinearLayout llBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //设置没有标题
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        //设置全屏
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_meeting_summary);
        ActivityManager.getInstance().addActivity(this);
        initView();
        getMeetingSummary();
    }

    /**
     * 初始化view
     */
    private void initView() {
        meetingApplyId = getIntent().getStringExtra("meetingApplyId");
        tvMeetingName = findViewById(R.id.tv_meeting_name);
        tvDate = findViewById(R.id.tv_date);
        tvMeetingLocation = findViewById(R.id.tv_meeting_location);
        tvUndertaker = findViewById(R.id.tv_undertaker);
        tvParticipants = findViewById(R.id.tv_participants);
        tvSummaryContent = findViewById(R.id.tv_summary_content);

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new AttachmentAdapter(this, attachmentList);
        adapter.setOnItemClickListener(new AttachmentAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int pos) {
                String url = attachmentList.get(pos).getUrl();
                if (!StringUtils.isStrEmpty(url)) {
                    Intent intent = new Intent(MeetingSummaryActivity.this, FileDisplayActivity.class);
                    intent.putExtra("fileUrl", url);
                    startActivity(intent);
                }
            }
        });
        recyclerView.setAdapter(adapter);

        llBack = findViewById(R.id.ll_back);
        llBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    /**
     * 获取会议纪要信息
     */
    private void getMeetingSummary() {
        boolean connected = NetworkDetector.isNetworkConnected(this);
        if (connected) {
            Map<String, String> reqParamMap = new HashMap<>();
            reqParamMap.put("meetingApplyId", meetingApplyId);
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
                                Map<String, Object> dataMap = StringUtils.transJsonToLinkedHashMap(data);
                                String conferenceName = (String) dataMap.get("conferenceName");
                                tvMeetingName.setText(MyTextUtil.transEmptyToPlaceholder(conferenceName));
                                String meetingTimes = (String) dataMap.get("meetingTimes");
                                tvDate.setText(MyTextUtil.transEmptyToPlaceholder(meetingTimes));
                                String address = (String) dataMap.get("address");
                                tvMeetingLocation.setText(MyTextUtil.transEmptyToPlaceholder(address));
                                String applyRealName = (String) dataMap.get("applyRealName");
                                tvUndertaker.setText(MyTextUtil.transEmptyToPlaceholder(applyRealName));
                                String participants = (String) dataMap.get("participants");
                                // 参会人员
                                setParticipants(participants);
                                // 会议纪要
                                String summaryContent = (String) dataMap.get("summaryContent");
                                if (!StringUtils.isStrEmpty(summaryContent)) {
                                    tvSummaryContent.setText(Html.fromHtml(summaryContent));
                                }
                                // 附件
                                String attachment = (String) dataMap.get("attachment");
                                setAttachmentList(attachment);
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
            commAsyncTask.execute(Constant.GET_MEETING_SUMMARY, reqParamMap);
        } else {
            showCustomToast(getString(R.string.net_exception_tip));
        }
    }

    private void setParticipants(String data) {
        String participantsAll = "";
        if (!StringUtils.isStrEmpty(data)) {
            TypeToken<List<ParticipantsBean>> typeToken = new TypeToken<List<ParticipantsBean>>() {
            };
            List<ParticipantsBean> modelBeanList = (List<ParticipantsBean>) StringUtils.convertMapToList(data, typeToken);
            if (modelBeanList != null && modelBeanList.size() > 0) {
                for (int i = 0; i < modelBeanList.size(); i++) {
                    participantsAll += modelBeanList.get(i).getRealName();
                }
            }
        }
        tvParticipants.setText(MyTextUtil.transEmptyToPlaceholder(participantsAll));
    }

    private void setAttachmentList(String data) {
        if (!StringUtils.isStrEmpty(data)) {
            TypeToken<List<AttachmentBean>> typeToken = new TypeToken<List<AttachmentBean>>() {
            };
            List<AttachmentBean> modelBeanList = (List<AttachmentBean>) StringUtils.convertMapToList(data, typeToken);
            if (modelBeanList != null && modelBeanList.size() > 0) {
                attachmentList.clear();
                attachmentList.addAll(modelBeanList);
                adapter.notifyDataSetChanged();
            }

        }
    }

    public void showCustomToast(String pMsg) {
        ToastUtils toastUtils = ToastUtils.createToastConfig();
        toastUtils.showToast(getApplicationContext(), pMsg);
    }
}
