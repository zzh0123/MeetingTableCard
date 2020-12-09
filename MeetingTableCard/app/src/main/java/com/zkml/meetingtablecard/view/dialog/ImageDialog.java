package com.zkml.meetingtablecard.view.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.zkml.meetingtablecard.R;

import java.util.HashMap;

/**
 * @author: zzh
 * data : 2020/12/07
 * description:
 */
public class ImageDialog extends Dialog {

    private ImageView ivItem;// 图片
    private ImageView ivCancel;//取消按钮
    private String imgUrl;

    private OnCancelOnclickListener onCancelOnclickListener;//取消按钮被点击了的监听器


    private Context context;
    /**
     * 设置取消按钮的显示内容和监听
     *
     * @param onCancelOnclickListener
     */
    public void setNoOnclickListener(OnCancelOnclickListener onCancelOnclickListener) {
        this.onCancelOnclickListener = onCancelOnclickListener;
    }

    public ImageDialog(Context context) {
        super(context, R.style.ImageDialog);
        this.context = context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_img);
        //按空白处不能取消动画
        setCanceledOnTouchOutside(false);

        //初始化界面控件
        initView();
        //初始化界面数据
//        initData();
        //初始化界面控件的事件
        initEvent();

    }

    /**
     * 初始化界面的确定和取消监听器
     */
    private void initEvent() {
        //设置取消按钮被点击后，向外界提供监听
        ivCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onCancelOnclickListener != null) {
                    onCancelOnclickListener.onCancelClick();
                }
            }
        });
    }

    /**
     * 初始化界面控件的显示数据
     */
//    private void initData() {
//    }

    /**
     * 初始化界面控件
     */
    private void initView() {
        ivItem = (ImageView) findViewById(R.id.iv_item);
        ivCancel = (ImageView) findViewById(R.id.iv_cancel);
        // 异步加载图片
        Glide.with(context)
                .load(imgUrl)
                .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                .skipMemoryCache(true)
                .into(ivItem);
    }

    /**
     * 从外界Activity为Dialog设置标题
     *
     * @param title
     */
    public void setTitle(String title) {
//        titleStr = title;
    }

    /**
     * 从外界Activity为Dialog设置dialog的message
     *
     * @param imgUrl
     */
    public void setImageUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }

    public interface OnCancelOnclickListener {
        public void onCancelClick();
    }

}
