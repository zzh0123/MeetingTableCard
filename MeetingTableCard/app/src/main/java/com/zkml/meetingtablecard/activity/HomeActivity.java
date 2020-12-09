package com.zkml.meetingtablecard.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.j256.ormlite.stmt.query.In;
import com.zkml.meetingtablecard.R;
import com.zkml.meetingtablecard.adapter.HomeItemAdapter;
import com.zkml.meetingtablecard.bean.HomeItemBean;
import com.zkml.meetingtablecard.bean.MeetingItemBean;
import com.zkml.meetingtablecard.utils.ActivityManager;
import com.zkml.meetingtablecard.utils.LogUtil;
import com.zkml.meetingtablecard.utils.MyTextUtil;
import com.zkml.meetingtablecard.utils.cache.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * @author: zzh
 * data : 2020/12/02
 * description：目录页
 */
public class HomeActivity extends AppCompatActivity {

    /**
     * 目录列表
     */
    private RecyclerView recyclerView;
    private List<HomeItemBean> homeItemList = new ArrayList<>();
    private HomeItemAdapter adapter;

    private TextView tvMeetingName;
    private String meetingApplyId;
    private ImageView ivLoginOut;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //设置没有标题
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        //设置全屏
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        ActivityManager.getInstance().addActivity(this);
        initView();
    }

    /**
     * 初始化view
     */
    private void initView() {
        initData();
        MeetingItemBean itemBean = (MeetingItemBean) getIntent().getSerializableExtra("itemBean");
        meetingApplyId = itemBean.getMeetingApplyId();

        tvMeetingName = findViewById(R.id.tv_meeting_name);
        tvMeetingName.setText(MyTextUtil.transEmptyToPlaceholder(itemBean.getConferenceName()));
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 3));
        adapter = new HomeItemAdapter(this, homeItemList);
        adapter.setOnItemClickListener(new HomeItemAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int pos) {
                if (!StringUtils.isStrEmpty(meetingApplyId)){
                    HomeItemBean homeItemBean = homeItemList.get(pos);
                    jumpTo(homeItemBean.getName(), meetingApplyId);
                }
            }
        });
        recyclerView.setAdapter(adapter);

        ivLoginOut = (ImageView) findViewById(R.id.iv_login_out);
        ivLoginOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ActivityManager.getInstance().exit();
            }
        });
    }

    private void initData(){
        HomeItemBean homeItemBean1 = new HomeItemBean(getString(R.string.agenda), R.mipmap.agenda);
        HomeItemBean homeItemBean2 = new HomeItemBean(getString(R.string.documentation), R.mipmap.documentation);
        HomeItemBean homeItemBean3 = new HomeItemBean(getString(R.string.discussion), R.mipmap.discussion);
//        HomeItemBean homeItemBean4 = new HomeItemBean(getString(R.string.vote), R.mipmap.vote);
        HomeItemBean homeItemBean5 = new HomeItemBean(getString(R.string.summary), R.mipmap.summary);
        homeItemList.add(homeItemBean1);
        homeItemList.add(homeItemBean2);
        homeItemList.add(homeItemBean3);
//        homeItemList.add(homeItemBean4);
        homeItemList.add(homeItemBean5);
    }

    private void jumpTo(String name, String meetingApplyId){
        Intent intent = null;
        if (getString(R.string.agenda).equals(name)){
            intent = new Intent(this, MeetingAgendaActivity.class);

        } else if (getString(R.string.documentation).equals(name)){
            intent = new Intent(this, DocumentationActivity.class);

        } else if (getString(R.string.discussion).equals(name)){
            intent = new Intent(this, DiscussActivity.class);

        } else if (getString(R.string.vote).equals(name)){
            intent = new Intent(this, MeetingAgendaActivity.class);

        } else if (getString(R.string.summary).equals(name)){
            intent = new Intent(this, MeetingSummaryActivity.class);

        }
        intent.putExtra("meetingApplyId", meetingApplyId);
        startActivity(intent);
    }
}
