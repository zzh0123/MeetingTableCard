package com.zkml.meetingtablecard.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.zkml.meetingtablecard.R;
import com.zkml.meetingtablecard.bean.AgendaItemBean;
import com.zkml.meetingtablecard.utils.MyTextUtil;

import java.util.List;


/**
 * @author: zzh
 * data : 2020/12/7
 * description：会议信息列表适配器
 */
public class AgendaItemAdapter extends RecyclerView.Adapter<AgendaItemAdapter.ViewHolder> {

    private Context context;
    private int pos;
    /**
     * 数据
     */
    private List<AgendaItemBean> agendaList;
    private OnItemClickListener onItemClickListener;

    public AgendaItemAdapter(Context context, List<AgendaItemBean> agendaList) {
        this.context = context;
        this.agendaList = agendaList;
    }

    @Override
    public AgendaItemAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.meeting_agenda_item, parent, false);
        AgendaItemAdapter.ViewHolder vh = new AgendaItemAdapter.ViewHolder(view);
        return vh;
    }

    @Override
    public void onBindViewHolder(final AgendaItemAdapter.ViewHolder holder, final int position) {
        pos = position;
        AgendaItemBean bean = agendaList.get(position);
        if (bean != null){

            // 会议时间
            String meetingdate = context.getString(R.string.meeting_date1) + bean.getBeginTime() + "～" + bean.getEndTime();
            holder.tvMeetingTime.setText(MyTextUtil.transEmptyToPlaceholder(meetingdate));
            // 会议地点
            String meetingLocation = context.getString(R.string.meeting_location) + bean.getAddress();
            holder.tvMeetingLocation.setText(MyTextUtil.transEmptyToPlaceholder(meetingLocation));
            // 会议名称
            holder.tvMeetingName.setText(MyTextUtil.transEmptyToPlaceholder(bean.getMatter()));
        }
    }

    @Override
    public int getItemCount() {
        if (agendaList != null) {
            return agendaList.size();
        }
        return 0;
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        private TextView tvMeetingTime; // 会议时间
        private TextView tvMeetingLocation; // 会议地点
        private TextView tvMeetingName; // 会议名称

        public ViewHolder(View itemView) {
            super(itemView);
            tvMeetingTime = itemView.findViewById(R.id.tv_meeting_time);
            tvMeetingLocation = itemView.findViewById(R.id.tv_meeting_location);
            tvMeetingName = itemView.findViewById(R.id.tv_meeting_name);
        }
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.onItemClickListener = listener;

    }

    public interface OnItemClickListener {
        void onItemClick(View view, int pos);
    }
}
