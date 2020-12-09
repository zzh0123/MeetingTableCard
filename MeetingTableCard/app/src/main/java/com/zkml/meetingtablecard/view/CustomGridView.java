package com.zkml.meetingtablecard.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.GridView;

/**
 * @author: zzh
 * data : 2020/12/7
 * description：
 */

public class CustomGridView extends GridView {
    //前三个为构造函数
    public CustomGridView(Context context) {
        super(context);
    }

    public CustomGridView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CustomGridView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    /**
     * 改写gridview高度
     * @param widthMeasureSpec
     * @param heightMeasureSpec
     */
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int expandSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2,
                MeasureSpec.AT_MOST);
        super.onMeasure(widthMeasureSpec, expandSpec);
    }
}