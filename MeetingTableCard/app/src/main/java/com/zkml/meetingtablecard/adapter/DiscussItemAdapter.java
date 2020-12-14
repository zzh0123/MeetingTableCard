package com.zkml.meetingtablecard.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.zkml.meetingtablecard.R;
import com.zkml.meetingtablecard.bean.DiscussItemBean;
import com.zkml.meetingtablecard.bean.FileItemBean;
import com.zkml.meetingtablecard.utils.MyTextUtil;
import com.zkml.meetingtablecard.utils.cache.StringUtils;
import com.zkml.meetingtablecard.view.CircleImageView;
import com.zkml.meetingtablecard.view.dialog.ImageDialog;

import java.util.List;


/**
 * @author: zzh
 * data : 2020/12/08
 * description：讨论列表适配器
 */
public class DiscussItemAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

    private final int MY = 1;
    private final int OTHER = 2;

    private Context context;
    private String userId;
    /**
     * 数据
     */
    List<DiscussItemBean> discussList;
    private OnItemMyClickListener onItemMyClickListener;
    private OnItemOtherClickListener onItemOtherClickListener;

    public DiscussItemAdapter(Context context, String userId, List<DiscussItemBean> discussList) {
        this.context = context;
        this.discussList = discussList;
        this.userId = userId;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
//        View view = LayoutInflater.from(context).inflate(R.layout.meeting_item, parent, false);
//        FileItemAdapter.ViewHolder vh = new FileItemAdapter.ViewHolder(view);
//        view.setOnClickListener(this);

        View view;
        if (viewType == MY) {
            view = LayoutInflater.from(context).inflate(R.layout.discuss_item_my, parent, false);
            return new DiscussItemAdapter.MyViewHolder(view);
        } else {
            view = LayoutInflater.from(context).inflate(R.layout.discuss_item_other, parent, false);
            return new DiscussItemAdapter.OtherViewHolder(view);
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (discussList.get(position).getSelfFlag()) {
            return MY;
        } else {
            return OTHER;
        }
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {
        DiscussItemBean bean = discussList.get(position);
        if (bean != null) {
            if (holder instanceof MyViewHolder) {
                MyViewHolder myViewHolder = (MyViewHolder) holder;
                String url = bean.getHeadImgUrl();
                // 异步加载图片
                Glide.with(context)
                        .load(url)
                        .error(R.mipmap.default_head)
                        .placeholder(R.mipmap.default_head)
                        .fallback(R.mipmap.default_head)
                        .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                        .skipMemoryCache(true)
                        .into(myViewHolder.ivHead);

                myViewHolder.tvName.setText(MyTextUtil.transEmptyToPlaceholder(bean.getUsername()));
                myViewHolder.tvDate.setText(MyTextUtil.transEmptyToPlaceholder(bean.getCommentTime()));
                myViewHolder.tvContent.setText(MyTextUtil.transEmptyToPlaceholder(bean.getComment()));
                myViewHolder.tvDate.setText(MyTextUtil.transEmptyToPlaceholder(bean.getCommentTime()));
                if (bean.getTotalLikeCount() > 0){
                    myViewHolder.tvLikeCount.setText("" + bean.getTotalLikeCount());
                } else {
                    myViewHolder.tvLikeCount.setText("");
                }
                boolean currentLikeFlag = bean.getCurrentLikeFlag();
                if (currentLikeFlag){
                    myViewHolder.ivLike.setImageResource(R.mipmap.like1);
                } else {
                    myViewHolder.ivLike.setImageResource(R.mipmap.like);
                }
                String id = bean.getId();
                myViewHolder.llLike.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        onItemMyClickListener.onItemMyClick(v, id, currentLikeFlag, position);
                    }
                });

            } else if (holder instanceof OtherViewHolder) {
                OtherViewHolder otherViewHolder = (OtherViewHolder) holder;
                String url = bean.getHeadImgUrl();
                // 异步加载图片
                Glide.with(context)
                        .load(url)
                        .error(R.mipmap.default_head)
                        .placeholder(R.mipmap.default_head)
                        .fallback(R.mipmap.default_head)
                        .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                        .skipMemoryCache(true)
                        .into(otherViewHolder.ivHead);

                otherViewHolder.tvName.setText(MyTextUtil.transEmptyToPlaceholder(bean.getUsername()));
                otherViewHolder.tvDate.setText(MyTextUtil.transEmptyToPlaceholder(bean.getCommentTime()));
                otherViewHolder.tvContent.setText(MyTextUtil.transEmptyToPlaceholder(bean.getComment()));
                otherViewHolder.tvDate.setText(MyTextUtil.transEmptyToPlaceholder(bean.getCommentTime()));
                if (bean.getTotalLikeCount() > 0){
                    otherViewHolder.tvLikeCount.setText("" + bean.getTotalLikeCount());
                } else {
                    otherViewHolder.tvLikeCount.setText("");
                }
                boolean currentLikeFlag = bean.getCurrentLikeFlag();
                if (currentLikeFlag){
                    otherViewHolder.ivLike.setImageResource(R.mipmap.like1);
                } else {
                    otherViewHolder.ivLike.setImageResource(R.mipmap.like);
                }
                String id = bean.getId();
                otherViewHolder.llLike.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        onItemOtherClickListener.onItemOtherClick(v, id, currentLikeFlag, position);
                    }
                });
            }
        }
    }

    @Override
    public int getItemCount() {
        if (discussList != null) {
            return discussList.size();
        }
        return 0;
    }


    class MyViewHolder extends RecyclerView.ViewHolder {
        private CircleImageView ivHead; // 头像
        private TextView tvName; // 姓名
        private TextView tvDate; // 时间
        private TextView tvContent; // 内容
        private LinearLayout llLike; // 外层
        private ImageView ivLike; // 赞
        private TextView tvLikeCount; // 赞数量


        public MyViewHolder(View itemView) {
            super(itemView);
            ivHead = itemView.findViewById(R.id.iv_head);
            tvName = itemView.findViewById(R.id.tv_name);
            tvDate = itemView.findViewById(R.id.tv_date);
            tvContent = itemView.findViewById(R.id.tv_content);
            llLike = itemView.findViewById(R.id.ll_like);
            ivLike = itemView.findViewById(R.id.iv_like);
            tvLikeCount = itemView.findViewById(R.id.tv_like_count);
        }
    }

    class OtherViewHolder extends RecyclerView.ViewHolder {
        private CircleImageView ivHead; // 头像
        private TextView tvName; // 姓名
        private TextView tvDate; // 时间
        private TextView tvContent; // 内容
        private LinearLayout llLike; // 外层
        private ImageView ivLike; // 赞
        private TextView tvLikeCount; // 赞数量


        public OtherViewHolder(View itemView) {
            super(itemView);
            ivHead = itemView.findViewById(R.id.iv_head);
            tvName = itemView.findViewById(R.id.tv_name);
            tvDate = itemView.findViewById(R.id.tv_date);
            tvContent = itemView.findViewById(R.id.tv_content);
            llLike = itemView.findViewById(R.id.ll_like);
            ivLike = itemView.findViewById(R.id.iv_like);
            tvLikeCount = itemView.findViewById(R.id.tv_like_count);
        }
    }

    public void setOnItemMyClickListener(OnItemMyClickListener listener) {
        this.onItemMyClickListener = listener;
    }

    public interface OnItemMyClickListener {
        void onItemMyClick(View view, String id, boolean currentLikeFlag, int pos);
    }

    public void setOnItemOtherClickListener(OnItemOtherClickListener listener) {
        this.onItemOtherClickListener = listener;
    }

    public interface OnItemOtherClickListener {
        void onItemOtherClick(View view, String id, boolean currentLikeFlag, int pos);
    }
}
