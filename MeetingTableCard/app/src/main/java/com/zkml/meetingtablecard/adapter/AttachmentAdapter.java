package com.zkml.meetingtablecard.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.zkml.meetingtablecard.R;
import com.zkml.meetingtablecard.bean.AgendaItemBean;
import com.zkml.meetingtablecard.bean.AttachmentBean;
import com.zkml.meetingtablecard.utils.MyTextUtil;

import java.util.List;


/**
 * @author: zzh
 * data : 2020/12/9
 * description：附件列表适配器
 */
public class AttachmentAdapter extends RecyclerView.Adapter<AttachmentAdapter.ViewHolder> {

    private Context context;
    private int pos;
    /**
     * 数据
     */
    private List<AttachmentBean> attachmentList;
    private OnItemClickListener onItemClickListener;

    public AttachmentAdapter(Context context, List<AttachmentBean> attachmentList) {
        this.context = context;
        this.attachmentList = attachmentList;
    }

    @Override
    public AttachmentAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.attachment_item, parent, false);
        AttachmentAdapter.ViewHolder vh = new AttachmentAdapter.ViewHolder(view);
        return vh;
    }

    @Override
    public void onBindViewHolder(final AttachmentAdapter.ViewHolder holder, final int position) {
        pos = position;
        AttachmentBean bean = attachmentList.get(position);
        if (bean != null){
            holder.tvFileName.setText(MyTextUtil.transEmptyToPlaceholder(bean.getName()));
        }
        holder.tvFileName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onItemClickListener.onItemClick(v, position);
            }
        });
    }

    @Override
    public int getItemCount() {
        if (attachmentList != null) {
            return attachmentList.size();
        }
        return 0;
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        private TextView tvFileName; // 文件名称
        public ViewHolder(View itemView) {
            super(itemView);
            tvFileName = itemView.findViewById(R.id.tv_file_name);
        }
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.onItemClickListener = listener;

    }

    public interface OnItemClickListener {
        void onItemClick(View view, int pos);
    }
}
