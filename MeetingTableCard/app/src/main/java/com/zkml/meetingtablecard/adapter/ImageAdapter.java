package com.zkml.meetingtablecard.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.zkml.meetingtablecard.R;

import java.util.List;

/**
 * @author: zzh
 * data : 2020/12/7
 * description：
 */
public class ImageAdapter extends BaseAdapter {

    private List<String> urls;
    private Context mContext;

    public ImageAdapter(Context context, List<String> urls) {
        this.mContext = context;
        this.urls = urls;
    }


    @Override
    public int getCount() {
        if (urls == null){
            return 0;
        } else {
            return urls.size();
        }
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder = null;


        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.img_item, parent, false);
            viewHolder = new ViewHolder();
            viewHolder.ivItem = (ImageView) convertView.findViewById(R.id.iv_item);

            convertView.setTag(viewHolder);

        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        String url = urls.get(position);
        // 异步加载图片
        Glide.with(mContext)
                .load(url)
                .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                .skipMemoryCache(true)
                .into(viewHolder.ivItem);

        return convertView;
    }


    class ViewHolder {
        ImageView ivItem;
    }
}

