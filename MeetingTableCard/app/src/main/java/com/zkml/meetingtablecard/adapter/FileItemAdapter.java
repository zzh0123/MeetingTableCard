package com.zkml.meetingtablecard.adapter;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.zkml.meetingtablecard.R;
import com.zkml.meetingtablecard.activity.FileDisplayActivity;
import com.zkml.meetingtablecard.bean.FileItemBean;
import com.zkml.meetingtablecard.utils.LogUtil;
import com.zkml.meetingtablecard.utils.MyTextUtil;
import com.zkml.meetingtablecard.utils.cache.StringUtils;
import com.zkml.meetingtablecard.view.dialog.ImageDialog;

import java.util.List;


/**
 * @author: zzh
 * data : 2020/12/07
 * description：会议资料列表适配器
 */
public class FileItemAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

    private final int IMAGE_VIEW = 1;
    private final int FILE_VIEW = 2;

    private Context context;
    private int pos;
    private ImageDialog dialog;
    /**
     * 数据
     */
    List<FileItemBean> fileList;
    private OnItemClickListener onItemClickListener;
    private OnItemDownloadClickListener onItemDownloadClickListener;

    public FileItemAdapter(Context context, List<FileItemBean> fileList) {
        this.context = context;
        this.fileList = fileList;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
//        View view = LayoutInflater.from(context).inflate(R.layout.meeting_item, parent, false);
//        FileItemAdapter.ViewHolder vh = new FileItemAdapter.ViewHolder(view);
//        view.setOnClickListener(this);

        View view;
        if (viewType == IMAGE_VIEW) {
            view = LayoutInflater.from(context).inflate(R.layout.file_item_img, parent, false);
            return new FileItemAdapter.ImageViewHolder(view);
        } else {
            view = LayoutInflater.from(context).inflate(R.layout.file_item, parent, false);
            return new FileItemAdapter.FileViewHolder(view);
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (fileList.get(position).getType().equals("" + IMAGE_VIEW)) {
            return IMAGE_VIEW;
        } else {
            return FILE_VIEW;
        }
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {
        pos = position;
        FileItemBean bean = fileList.get(position);
        if (bean != null) {
            if (holder instanceof ImageViewHolder) {
                ImageViewHolder imageViewHolder = (ImageViewHolder) holder;
                imageViewHolder.tvDate.setText(MyTextUtil.transEmptyToPlaceholder(bean.getCreateTime()));
                List<String> urls = bean.getFileUrls();
                if (urls != null && urls.size() > 0) {
                    ImageAdapter adapter = new ImageAdapter(context, urls);
                    imageViewHolder.gridView.setAdapter(adapter);
                    imageViewHolder.gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            String url = urls.get(position);
                            if (!StringUtils.isStrEmpty(url)){
                                showDialog(url);
                            }
                        }
                    });
                }
            } else if (holder instanceof FileViewHolder) {
                FileViewHolder fileViewHolder = (FileViewHolder) holder;
                fileViewHolder.tvFileName.setText(MyTextUtil.transEmptyToPlaceholder(bean.getFileName()));
                fileViewHolder.tvDownload.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String fileUrl = bean.getFileUrls().get(0);
                        if (!StringUtils.isStrEmpty(fileUrl)){
                            onItemDownloadClickListener.onItemDownloadClick(v, fileUrl, position);
                        }
                    }
                });
            }
        }
    }

    private void showDialog(String url) {
        if (dialog == null) {
            dialog = new ImageDialog(context);
            dialog.setImageUrl(url);
            dialog.setNoOnclickListener(new ImageDialog.OnCancelOnclickListener() {
                @Override
                public void onCancelClick() {
                    dialog.dismiss();
                    dialog = null;
                }
            });
            WindowManager.LayoutParams lp = dialog.getWindow().getAttributes();
            lp.dimAmount = 0.5f;
            dialog.getWindow().setAttributes(lp);
            dialog.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
            dialog.show();
        }
    }

    @Override
    public int getItemCount() {
        if (fileList != null) {
            return fileList.size();
        }
        return 0;
    }


    class ImageViewHolder extends RecyclerView.ViewHolder {
        private TextView tvDate; // 时间
        private GridView gridView; // 图片

        public ImageViewHolder(View itemView) {
            super(itemView);
            tvDate = itemView.findViewById(R.id.tv_date);
            gridView = itemView.findViewById(R.id.gridView);
        }
    }

    class FileViewHolder extends RecyclerView.ViewHolder {
        private TextView tvFileName; // 文件名
        private TextView tvDownload; // 下载文件

        public FileViewHolder(View itemView) {
            super(itemView);
            tvFileName = itemView.findViewById(R.id.tv_file_name);
            tvDownload = itemView.findViewById(R.id.tv_download);
        }
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.onItemClickListener = listener;
    }

    public interface OnItemClickListener {
        void onItemClick(View view, int pos);
    }

    public void setOnItemDownloadClickListener(OnItemDownloadClickListener listener) {
        this.onItemDownloadClickListener = listener;
    }

    public interface OnItemDownloadClickListener {
        void onItemDownloadClick(View view,  String fileUrl, int pos);
    }
}
