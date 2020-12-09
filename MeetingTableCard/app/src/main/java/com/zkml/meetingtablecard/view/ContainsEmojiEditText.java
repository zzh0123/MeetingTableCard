package com.zkml.meetingtablecard.view;

import android.content.Context;
import android.text.Editable;
import android.text.Selection;
import android.text.Spannable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.EditText;

import com.zkml.meetingtablecard.R;
import com.zkml.meetingtablecard.utils.ActivityUtils;

/**
 * Created by lenovo on 2016/8/19.
 * 禁止输入表情符号
 */
public class ContainsEmojiEditText extends EditText {
    //输入表情前的光标位置
    private int cursorPos;
    //输入表情前EditText中的文本
    private String inputAfterText;
    //是否重置了EditText的内容
    private boolean resetText;

    private Context mContext;
    //滑动距离的最大边界
    private int mOffsetHeight;

    //是否到顶或者到底的标志
    private boolean mBottomFlag = false;
    private boolean mCanVerticalScroll;
    public ContainsEmojiEditText(Context context) {
        super(context);
        this.mContext = context;
        initEditText();
    }

    public ContainsEmojiEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mContext = context;
        initEditText();
    }

    public ContainsEmojiEditText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.mContext = context;
        initEditText();
    }

    /**
     * 检测是否有emoji表情
     *
     * @param source
     * @return
     */
    public static boolean containsEmoji(String source) {
        int len = source.length();
        for (int i = 0; i < len; i++) {
            char codePoint = source.charAt(i);
            if (!isEmojiCharacter(codePoint)) { //如果不能匹配,则该字符是Emoji表情
                return true;
            }
        }
        return false;
    }

    /**
     * 判断是否是Emoji
     *
     * @param codePoint 比较的单个字符
     * @return
     */
    private static boolean isEmojiCharacter(char codePoint) {
        return (codePoint == 0x0) || (codePoint == 0x9) || (codePoint == 0xA) ||
                (codePoint == 0xD) || ((codePoint >= 0x20) && (codePoint <= 0xD7FF)) ||
                ((codePoint >= 0xE000) && (codePoint <= 0xFFFD)) || ((codePoint >= 0x10000)
                && (codePoint <= 0x10FFFF));
    }

    // 初始化edittext 控件
    private void initEditText() {
        addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int before, int count) {
                if (!resetText) {
                    cursorPos = getSelectionEnd();
                    // 这里用s.toString()而不直接用s是因为如果用s，
                    // 那么，inputAfterText和s在内存中指向的是同一个地址，s改变了，
                    // inputAfterText也就改变了，那么表情过滤就失败了
                    inputAfterText = s.toString();
                }
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!resetText) {
                    if (count >= 2) {//表情符号的字符长度最小为2
                        Log.v("lyyo", "cursorPos: " + cursorPos);
                        //CharSequence input = s.subSequence(cursorPos, cursorPos + count);
                        //CharSequence input = s.subSequence(0, count);
                        if (containsEmoji(s.toString())) {
                            resetText = true;
                            ActivityUtils.toast(mContext, mContext.getString(R.string.inputeror));
                            //是表情符号就将文本还原为输入表情符号之前的内容
                            setText(inputAfterText);
                            CharSequence text = getText();
                            if (text instanceof Spannable) {
                                Spannable spanText = (Spannable) text;
                                Selection.setSelection(spanText, text.length());
                            }
                        }
                    }
                } else {
                    resetText = false;
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }
    //解决EditText滑动冲突问题
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        mCanVerticalScroll = canVerticalScroll();
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN)
            //如果是新的按下事件，则对mBottomFlag重新初始化
            mBottomFlag = false;
        //如果已经不要这次事件，则传出取消的信号，这里的作用不大
        if (mBottomFlag)
            event.setAction(MotionEvent.ACTION_CANCEL);

        return super.dispatchTouchEvent(event);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        boolean result = super.onTouchEvent(event);
        if (mCanVerticalScroll) {
            //如果是需要拦截，则再拦截，这个方法会在onScrollChanged方法之后再调用一次
            if (!mBottomFlag)
                getParent().requestDisallowInterceptTouchEvent(true);
        } else {
            getParent().requestDisallowInterceptTouchEvent(false);
        }
        return result;
    }

    @Override
    protected void onScrollChanged(int horiz, int vert, int oldHoriz, int oldVert) {
        super.onScrollChanged(horiz, vert, oldHoriz, oldVert);
        if (vert == mOffsetHeight || vert == 0) {
            //这里触发父布局或祖父布局的滑动事件
            getParent().requestDisallowInterceptTouchEvent(false);
            mBottomFlag = true;
        }
    }

    /**
     * EditText竖直方向是否可以滚动
     *
     * @return true：可以滚动   false：不可以滚动
     */
    private boolean canVerticalScroll() {
        //滚动的距离
        int scrollY = getScrollY();
        //控件内容的总高度
        int scrollRange = getLayout().getHeight();
        //控件实际显示的高度
        int scrollExtent = getHeight() - getCompoundPaddingTop() - getCompoundPaddingBottom();
        //控件内容总高度与实际显示高度的差值
        mOffsetHeight = scrollRange - scrollExtent;

        if (mOffsetHeight == 0) {

            return false;
        }

        return (scrollY > 0) || (scrollY < mOffsetHeight - 1);
    }
}
