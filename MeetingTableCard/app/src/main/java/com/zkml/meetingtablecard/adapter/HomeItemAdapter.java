package com.zkml.meetingtablecard.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.zkml.meetingtablecard.R;
import com.zkml.meetingtablecard.bean.HomeItemBean;
import com.zkml.meetingtablecard.utils.MyTextUtil;

import java.util.List;


/**
 * @author: zzh
 * data : 2020/12/03
 * description：目录列表适配器
 */
public class HomeItemAdapter extends RecyclerView.Adapter<HomeItemAdapter.ViewHolder> {

    private Context context;
    private int pos;
    /**
     * 数据
     */
    List<HomeItemBean> homeItemList;
    private OnItemClickListener onItemClickListener;

    public HomeItemAdapter(Context context, List<HomeItemBean> homeItemList) {
        this.context = context;
        this.homeItemList = homeItemList;
    }

    @Override
    public HomeItemAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.home_item, parent, false);
        HomeItemAdapter.ViewHolder vh = new HomeItemAdapter.ViewHolder(view);
        return vh;
    }

    @Override
    public void onBindViewHolder(final HomeItemAdapter.ViewHolder holder, final int position) {
        pos = position;
        HomeItemBean bean = homeItemList.get(position);
        if (bean != null){
            // icon
            holder.ivIcon.setImageResource(bean.getIconRes());
            // 名称
            holder.tvName.setText(MyTextUtil.transEmptyToPlaceholder(bean.getName()));
            holder.llHomeItem.setOnClickListener(new View.OnClickListener() {
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
        if (homeItemList != null) {
            return homeItemList.size();
        }
        return 0;
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        private ImageView ivIcon; // icon
        private TextView tvName; // 名称
        private LinearLayout llHomeItem; // 外层

        public ViewHolder(View itemView) {
            super(itemView);
            ivIcon = itemView.findViewById(R.id.iv_icon);
            tvName = itemView.findViewById(R.id.tv_name);
            llHomeItem = itemView.findViewById(R.id.ll_home_item);
        }
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.onItemClickListener = listener;

    }

    public interface OnItemClickListener {
        void onItemClick(View view, int pos);
    }
}
