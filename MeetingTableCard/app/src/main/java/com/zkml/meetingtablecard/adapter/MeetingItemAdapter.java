package com.zkml.meetingtablecard.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.zkml.meetingtablecard.R;
import com.zkml.meetingtablecard.bean.MeetingItemBean;
import com.zkml.meetingtablecard.utils.MyTextUtil;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;


/**
 * @author: zzh
 * data : 2020/12/03
 * description：会议信息列表适配器
 */
public class MeetingItemAdapter extends RecyclerView.Adapter<MeetingItemAdapter.ViewHolder> {

    private Context context;
    private int pos;
    /**
     * 数据
     */
    List<MeetingItemBean> meetingList;
    private OnItemClickListener onItemClickListener;

    public MeetingItemAdapter(Context context, List<MeetingItemBean> meetingList) {
        this.context = context;
        this.meetingList = meetingList;
    }

    @Override
    public MeetingItemAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.meeting_item, parent, false);
        MeetingItemAdapter.ViewHolder vh = new MeetingItemAdapter.ViewHolder(view);
        return vh;
    }

    @Override
    public void onBindViewHolder(final MeetingItemAdapter.ViewHolder holder, final int position) {
        pos = position;
        MeetingItemBean bean = meetingList.get(position);
        if (bean != null){
            // 会议名称
            holder.tvMeetingName.setText(bean.getConferenceName());
            // 会议时间
            String meetingdate = context.getString(R.string.meeting_date1) + bean.getBeginTime() + "～" + bean.getEndTime();
            holder.tvMeetingTime.setText(MyTextUtil.transEmptyToPlaceholder(meetingdate));
            // 承办单位
            String meetingUnit = context.getString(R.string.meeting_unit) + bean.getOrganName();
            holder.tvMeetingUnit.setText(MyTextUtil.transEmptyToPlaceholder(meetingUnit));
            // 会议地点
            String meetingLocation = context.getString(R.string.meeting_location) + bean.getAddress();
            holder.tvMeetingLocation.setText(MyTextUtil.transEmptyToPlaceholder(meetingLocation));
            // 承办人
            String meetingUnderTaker = context.getString(R.string.meeting_under_taker) + bean.getApplyRealName();
            holder.tvMeetingUnderTaker.setText(MyTextUtil.transEmptyToPlaceholder(meetingUnderTaker));
            holder.tvGetInto.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (onItemClickListener != null) {
                        onItemClickListener.onItemClick(v, position);
                    }
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        if (meetingList != null) {
            return meetingList.size();
        }
        return 0;
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        private TextView tvMeetingName; // 会议名称
        private TextView tvMeetingTime; // 会议时间
        private TextView tvMeetingUnit; // 承办单位
        private TextView tvMeetingLocation; // 会议地点
        private TextView tvMeetingUnderTaker; // 承办人
        private TextView tvGetInto; // 进入

        public ViewHolder(View itemView) {
            super(itemView);
            tvMeetingName = itemView.findViewById(R.id.tv_meeting_name);
            tvMeetingTime = itemView.findViewById(R.id.tv_meeting_time);
            tvMeetingUnit = itemView.findViewById(R.id.tv_meeting_unit);
            tvMeetingLocation = itemView.findViewById(R.id.tv_meeting_location);
            tvMeetingUnderTaker = itemView.findViewById(R.id.tv_meeting_under_taker);
            tvGetInto = itemView.findViewById(R.id.tv_get_into);
        }
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.onItemClickListener = listener;

    }

    public interface OnItemClickListener {
        void onItemClick(View view, int pos);
    }

    /**
     * 时间差
     *
     * @param serverTime
     * @return
     */
    public String getTimeFormatText(String serverTime) {
        String format = "yyyy-MM-dd HH:mm:ss";
        SimpleDateFormat sdf = new SimpleDateFormat(format, Locale.CHINESE);
        sdf.setTimeZone(TimeZone.getTimeZone("GMT+8:00"));
        Date date = null;
        try {
            date = sdf.parse(serverTime);
        } catch (Exception e) {
            e.printStackTrace();
        }

        long minute = 60 * 1000;// 1分钟
        long hour = 60 * minute;// 1小时
        long day = 24 * hour;// 1天
        long month = 31 * day;// 月
        long year = 12 * month;// 年

        if (date == null) {
            return null;
        }
        long diff = new Date().getTime() - date.getTime();
        long r = 0;
        if (diff > year) {
            r = (diff / year);
            return r + "年前";
        }
        if (diff > month) {
            r = (diff / month);
            return r + "个月前";
        }
        if (diff > day) {
            r = (diff / day);
            return r + "天前";
        }
        if (diff > hour) {
            r = (diff / hour);
            return r + "小时前";
        }
        if (diff > minute) {
            r = (diff / minute);
            return r + "分钟前";
        }
        return "刚刚";
    }
}
