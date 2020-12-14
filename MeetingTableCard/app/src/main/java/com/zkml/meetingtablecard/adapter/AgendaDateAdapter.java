package com.zkml.meetingtablecard.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.zkml.meetingtablecard.R;
import com.zkml.meetingtablecard.bean.AgendaDateBean;
import com.zkml.meetingtablecard.bean.AgendaItemBean;
import com.zkml.meetingtablecard.utils.MyTextUtil;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;


/**
 * @author: zzh
 * data : 2020/12/11
 * description：会议date列表适配器
 */
public class AgendaDateAdapter extends RecyclerView.Adapter<AgendaDateAdapter.ViewHolder> {

    private Context context;
    private int pos;
    /**
     * 数据
     */
    private List<AgendaDateBean> dateList;
    private OnItemClickListener onItemClickListener;

    public AgendaDateAdapter(Context context, List<AgendaDateBean> dateList) {
        this.context = context;
        this.dateList = dateList;
    }

    @Override
    public AgendaDateAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.agenda_date_item, parent, false);
        AgendaDateAdapter.ViewHolder vh = new AgendaDateAdapter.ViewHolder(view);
        return vh;
    }

    @Override
    public void onBindViewHolder(final AgendaDateAdapter.ViewHolder holder, final int position) {
        pos = position;
        AgendaDateBean bean = dateList.get(position);
        if (bean != null){
            holder.tvWeek.setText(bean.getWeek());
            holder.tvMonthDay.setText(bean.getMonthDay());
            if (bean.isSelected()){
                holder.tvWeek.setTextColor(context.getResources().getColor(R.color.white));
                holder.tvMonthDay.setTextColor(context.getResources().getColor(R.color.white));
                holder.llDate.setBackgroundColor(context.getResources().getColor(R.color.c_5368C1));
            } else {
                holder.tvWeek.setTextColor(context.getResources().getColor(R.color.c_909399));
                holder.tvMonthDay.setTextColor(context.getResources().getColor(R.color.c_606266));
                holder.llDate.setBackgroundColor(context.getResources().getColor(R.color.c_F4F5F7));
            }

            holder.llDate.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onItemClickListener.onItemClick(v, position);
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        if (dateList != null) {
            return dateList.size();
        }
        return 0;
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        private TextView tvWeek; // 周
        private TextView tvMonthDay; // 月日
        private LinearLayout llDate; // 外层

        public ViewHolder(View itemView) {
            super(itemView);
            tvWeek = itemView.findViewById(R.id.tv_week);
            tvMonthDay = itemView.findViewById(R.id.tv_month_day);
            llDate = itemView.findViewById(R.id.ll_date);
        }
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.onItemClickListener = listener;

    }

    public interface OnItemClickListener {
        void onItemClick(View view, int pos);
    }


    
}
