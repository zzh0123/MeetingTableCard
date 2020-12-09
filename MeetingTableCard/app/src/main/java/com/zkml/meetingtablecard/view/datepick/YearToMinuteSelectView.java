package com.zkml.meetingtablecard.view.datepick;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;


import com.zkml.meetingtablecard.R;
import com.zkml.meetingtablecard.utils.LogUtil;
import com.zkml.meetingtablecard.view.BaseDialog;
import com.zkml.meetingtablecard.view.clock.AbstractWheelTextAdapter;
import com.zkml.meetingtablecard.view.clock.OnWheelChangedListener;
import com.zkml.meetingtablecard.view.clock.OnWheelScrollListener;
import com.zkml.meetingtablecard.view.clock.WheelView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

/**
 * 日期选择控件
 * 样式 yyyy-MM-dd HH:mm 滚轮滑动显示
 * 有开关控制点击确认 是否关闭对话框
 */
public class YearToMinuteSelectView extends BaseDialog implements
        View.OnClickListener {

    public static final int DIALOG_MODE_CENTER = 0;
    public static final int DIALOG_MODE_BOTTOM = 1;
    private Context context;
    private WheelView wvYear;
    private WheelView wvMonth;
    private WheelView wvDay;
    private WheelView wvHour;
    private WheelView wvMinutes;
    private View vDialog;
    private View vDialogChild;
    private ViewGroup VDialogPicker;
    private TextView tvTitle;
    private TextView btnSure;
    private TextView btnCancel;
    private ArrayList<String> arryYear = new ArrayList<String>();
    private ArrayList<String> arryMonth = new ArrayList<String>();
    private ArrayList<String> arryDay = new ArrayList<String>();
    private ArrayList<String> arryHour = new ArrayList<String>();
    private ArrayList<String> arryMinutes = new ArrayList<String>();

    private CalendarTextAdapter mYearAdapter;
    private CalendarTextAdapter mMonthAdapter;
    private CalendarTextAdapter mDayAdapter;
    private CalendarTextAdapter mHourAdapter;
    private CalendarTextAdapter mMinutesAdapter;
    private int mMonth;
    private int mDay;
    private int mHour;
    private int mMinutes;
    private int currentMonth = 0;
    private int currentDay = 0;
    private int currentHour = 0;
    private int currentMinutes = 0;
    private int maxTextSize = 20;
    private int minTextSize = 15;

    private int selectDate = 0;
    private String selectYear;
    private String selectMonth;
    private String selectDay;
    private String selectHour;
    private String selectMinutes;
    private SimpleDateFormat mFormatter = new SimpleDateFormat("yyyy-MM-dd HH:mm");

    private String strTitle = "选择时间";
    private OnTimePickListener onTimePickListener;
    //选择开始年份
    private int startTime;
    private int endYearTime;
    //默认年份间隔
    private int DEFAULTYEARGAP = 30;
    private Calendar mCurrentDate = Calendar.getInstance();
    private boolean isClickSureDismiss = true;

    public boolean isClickSureDismiss() {
        return isClickSureDismiss;
    }

    public void setClickSureDismiss(boolean clickSureDismiss) {
        isClickSureDismiss = clickSureDismiss;
    }

    public YearToMinuteSelectView(Context context, int startTime) {
        super(context, R.layout.car_easy_rent_dialog_picker_center);
        this.context = context;
        this.startTime = startTime;
    }

    public YearToMinuteSelectView(Context context, int startTime, String strTitle) {
        super(context, R.layout.car_easy_rent_dialog_picker_center);
        this.context = context;
        this.startTime = startTime;
        this.strTitle = strTitle;
    }

    public void setEndYearTime(int endYearTime) {
        this.endYearTime = endYearTime;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init();
    }

    private void init() {
        VDialogPicker = (ViewGroup) findViewById(R.id.ly_dialog_picker);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT, 1.0f);
        // 此处相当于布局文件中的Android:layout_gravity属性
        lp.gravity = Gravity.CENTER_VERTICAL;
        //年
        wvYear = new WheelView(context);
        wvYear.setLayoutParams(lp);
        VDialogPicker.addView(wvYear);
        //月
        wvMonth = new WheelView(context);
        wvMonth.setLayoutParams(lp);
        wvMonth.setCyclic(true);
        wvMonth.setVisibility(View.VISIBLE);
        VDialogPicker.addView(wvMonth);
        //日
        wvDay = new WheelView(context);
        wvDay.setLayoutParams(lp);
        wvDay.setCyclic(true);
        wvDay.setVisibility(View.VISIBLE);
        VDialogPicker.addView(wvDay);
        //时
        wvHour = new WheelView(context);
        wvHour.setLayoutParams(lp);
        wvHour.setCyclic(true);
        wvHour.setVisibility(View.VISIBLE);
        VDialogPicker.addView(wvHour);
        //分
        wvMinutes = new WheelView(context);
        wvMinutes.setLayoutParams(lp);
        wvMinutes.setCyclic(true);
        wvMinutes.setVisibility(View.VISIBLE);
        VDialogPicker.addView(wvMinutes);

        vDialog = findViewById(R.id.ly_dialog);
        vDialogChild = findViewById(R.id.ly_dialog_child);
        tvTitle = (TextView) findViewById(R.id.tv_dialog_title);
        btnSure = (TextView) findViewById(R.id.btn_dialog_sure);
        btnCancel = (TextView) findViewById(R.id.btn_dialog_cancel);

        tvTitle.setText(strTitle);
        vDialog.setOnClickListener(this);
        vDialogChild.setOnClickListener(this);
        btnSure.setOnClickListener(this);
        if (null != btnCancel) {
            btnCancel.setOnClickListener(this);
        }
        //初始化年数据
        initDates(startTime, endYearTime == 0 ? startTime + DEFAULTYEARGAP : endYearTime);
        mYearAdapter = new CalendarTextAdapter(context, arryYear, selectDate, maxTextSize, minTextSize);
        wvYear.setVisibleItems(5);
        wvYear.setViewAdapter(mYearAdapter);
        wvYear.setCurrentItem(selectDate);
//        //初始化月数据
        initMonth();
        mMonthAdapter = new CalendarTextAdapter(context, arryMonth,
                currentMonth, maxTextSize, minTextSize);
        wvMonth.setVisibleItems(5);
        wvMonth.setViewAdapter(mMonthAdapter);
        wvMonth.setCurrentItem(currentMonth);
//        //初始化天数据
        initDays(selectMonth);
        mDayAdapter = new CalendarTextAdapter(context, arryDay,
                currentDay, maxTextSize, minTextSize);
        wvDay.setVisibleItems(5);
        wvDay.setViewAdapter(mDayAdapter);
        wvDay.setCurrentItem(currentDay);
        //初始化小时数据
        initHours(mHour);
        mHourAdapter = new CalendarTextAdapter(context, arryHour,
                currentHour, maxTextSize, minTextSize);
        wvHour.setVisibleItems(5);
        wvHour.setViewAdapter(mHourAdapter);
        wvHour.setCurrentItem(currentHour);
        //初始化分钟数据
        initMinutes(mMinutes);
        mMinutesAdapter = new CalendarTextAdapter(context, arryMinutes,
                currentMinutes, maxTextSize, minTextSize);
        wvMinutes.setVisibleItems(5);
        wvMinutes.setViewAdapter(mMinutesAdapter);
        wvMinutes.setCurrentItem(currentMinutes);

        wvYear.addChangingListener(new OnWheelChangedListener() {

            @Override
            public void onChanged(WheelView wheel, int oldValue, int newValue) {
                LogUtil.i("AddRouteFragment", "*****Year*****onChanged***** oldValue = " + oldValue + "newValue = " + newValue);
                String currentText = (String) mYearAdapter.getItemText(wheel.getCurrentItem());
                setTextviewSize(currentText, mYearAdapter);
            }
        });

        wvYear.addScrollingListener(new OnWheelScrollListener() {

            @Override
            public void onScrollingStarted(WheelView wheel) {
                LogUtil.i("AddRouteFragment", "*****Year*****onScrollingStarted*****");
            }

            @Override
            public void onScrollingFinished(WheelView wheel) {
                LogUtil.i("AddRouteFragment", "*****Year*****onScrollingFinished*****");
                yearChanged(wheel);
            }
        });

        wvMonth.addChangingListener(new OnWheelChangedListener() {

            @Override
            public void onChanged(WheelView wheel, int oldValue, int newValue) {
                LogUtil.i("AddRouteFragment", "*****Month*****onChanged*****");
                String currentText = (String) mMonthAdapter.getItemText(wheel.getCurrentItem());
                setTextviewSize(currentText, mMonthAdapter);
            }
        });

        wvMonth.addScrollingListener(new OnWheelScrollListener() {

            @Override
            public void onScrollingStarted(WheelView wheel) {
                LogUtil.i("AddRouteFragment", "*****Month*****onScrollingStarted*****");
            }

            @Override
            public void onScrollingFinished(WheelView wheel) {
                LogUtil.i("AddRouteFragment", "*****Month*****onScrollingFinished*****");
                monthChanged(wheel);
            }
        });

        wvDay.addChangingListener(new OnWheelChangedListener() {

            @Override
            public void onChanged(WheelView wheel, int oldValue, int newValue) {
                String currentText = (String) mDayAdapter.getItemText(wheel
                        .getCurrentItem());
                setTextviewSize(currentText, mDayAdapter);
            }
        });

        wvDay.addScrollingListener(new OnWheelScrollListener() {

            @Override
            public void onScrollingStarted(WheelView wheel) {
            }

            @Override
            public void onScrollingFinished(WheelView wheel) {
                String currentText = (String) mDayAdapter.getItemText(wheel
                        .getCurrentItem());
                setTextviewSize(currentText, mDayAdapter);
                if (null != currentText) {
                    currentText = currentText.replace("日", "");
                }
                selectDay = currentText;
                currentDay = wheel.getCurrentItem();
                //小时
                initHours(mHour);
                if (currentHour >= arryHour.size()) {
                    currentHour = arryHour.size() - 1;
                }
                selectHour = currentHour  + "";
                mHourAdapter = new CalendarTextAdapter(context, arryHour,
                        currentHour, maxTextSize, minTextSize);
                wvHour.setVisibleItems(5);
                wvHour.setViewAdapter(mHourAdapter);
                wvHour.setCurrentItem(currentHour);
                //分钟
                initMinutes(mMinutes);
                if (currentMinutes >= arryMinutes.size()) {
                    currentMinutes = arryMinutes.size() - 1;
                }
                selectMinutes = currentMinutes  + "";
                mMinutesAdapter = new CalendarTextAdapter(context, arryMinutes,
                        currentMinutes, maxTextSize, minTextSize);
                wvMinutes.setVisibleItems(5);
                wvMinutes.setViewAdapter(mMinutesAdapter);
                wvMinutes.setCurrentItem(currentMinutes);
            }
        });

        wvHour.addChangingListener(new OnWheelChangedListener() {

            @Override
            public void onChanged(WheelView wheel, int oldValue, int newValue) {
                String currentText = (String) mHourAdapter.getItemText(wheel
                        .getCurrentItem());
                setTextviewSize(currentText, mHourAdapter);

            }
        });

        wvHour.addScrollingListener(new OnWheelScrollListener() {

            @Override
            public void onScrollingStarted(WheelView wheel) {

            }

            @Override
            public void onScrollingFinished(WheelView wheel) {
                String currentText = (String) mHourAdapter.getItemText(wheel
                        .getCurrentItem());
                setTextviewSize(currentText, mHourAdapter);
                if (currentText != null) {
                    currentText = currentText.replace(context.getString(R.string.dianstr), "");
                }
                selectHour = currentText;
                currentHour = wheel.getCurrentItem();
                //分钟
                initMinutes(mMinutes);
                if (currentMinutes >= arryMinutes.size()) {
                    currentMinutes = arryMinutes.size() - 1;
                }
                selectMinutes = currentMinutes + "";
                mMinutesAdapter = new CalendarTextAdapter(context, arryMinutes,
                        currentMinutes, maxTextSize, minTextSize);
                wvMinutes.setVisibleItems(5);
                wvMinutes.setViewAdapter(mMinutesAdapter);
                wvMinutes.setCurrentItem(currentMinutes);
            }
        });

        wvMinutes.addChangingListener(new OnWheelChangedListener() {

            @Override
            public void onChanged(WheelView wheel, int oldValue, int newValue) {
                String currentText = (String) mMinutesAdapter.getItemText(wheel
                        .getCurrentItem());
                setTextviewSize(currentText, mMinutesAdapter);
            }
        });

        wvMinutes.addScrollingListener(new OnWheelScrollListener() {

            @Override
            public void onScrollingStarted(WheelView wheel) {

            }

            @Override
            public void onScrollingFinished(WheelView wheel) {
                String currentText = (String) mMinutesAdapter.getItemText(wheel
                        .getCurrentItem());
                setTextviewSize(currentText, mMinutesAdapter);
                if (currentText != null) {
                    currentText = currentText.replace(context.getString(R.string.fenstr), "");
                }
                selectMinutes = currentText;
                currentMinutes = wheel.getCurrentItem();
            }
        });

    }

    private void monthChanged(WheelView wheel) {
        String currentText = (String) mMonthAdapter.getItemText(wheel.getCurrentItem());
        setTextviewSize(currentText, mMonthAdapter);
        if (null != currentText) {
            currentText = currentText.replace("月", "");
        }
        selectMonth = currentText;
        currentMonth = wheel.getCurrentItem();

        initDays(currentText);
        if (currentDay >= arryDay.size()) {
            currentDay = arryDay.size() - 1;
        }
        selectDay = currentDay + 1 + "";
        mDayAdapter = new CalendarTextAdapter(context, arryDay,
                currentDay, maxTextSize, minTextSize);
        wvDay.setVisibleItems(5);
        wvDay.setViewAdapter(mDayAdapter);
        wvDay.setCurrentItem(currentDay);
        //小时
        initHours(mHour);
        if (currentHour >= arryHour.size()) {
            currentHour = arryHour.size() - 1;
        }
        selectHour = currentHour  + "";
        mHourAdapter = new CalendarTextAdapter(context, arryHour,
                currentHour, maxTextSize, minTextSize);
        wvHour.setVisibleItems(5);
        wvHour.setViewAdapter(mHourAdapter);
        wvHour.setCurrentItem(currentHour);
        //分钟
        initMinutes(mMinutes);
        if (currentMinutes >= arryMinutes.size()) {
            currentMinutes = arryMinutes.size() - 1;
        }
        selectMinutes = currentMinutes  + "";
        mMinutesAdapter = new CalendarTextAdapter(context, arryMinutes,
                currentMinutes, maxTextSize, minTextSize);
        wvMinutes.setVisibleItems(5);
        wvMinutes.setViewAdapter(mMinutesAdapter);
        wvMinutes.setCurrentItem(currentMinutes);
    }

    private void yearChanged(WheelView wheel) {
        String currentText = (String) mYearAdapter.getItemText(wheel.getCurrentItem());
        setTextviewSize(currentText, mYearAdapter);
        if (null != currentText) {
            currentText = currentText.replace("年", "");
        }
        selectYear = currentText;
        selectDate = wheel.getCurrentItem();
        //月
        initMonth();
        if (selectDate == arryYear.size() - 1) {
            currentMonth = mCurrentDate.get(Calendar.MONTH);
        }
        selectMonth = currentMonth + 1 + "";
        mMonthAdapter = new CalendarTextAdapter(context, arryMonth,
                currentMonth, maxTextSize, minTextSize);
        wvMonth.setVisibleItems(5);
        wvMonth.setViewAdapter(mMonthAdapter);
        wvMonth.setCurrentItem(currentMonth);
        //日
        initDays(selectMonth);
        if (currentDay >= arryDay.size()) {
            currentDay = arryDay.size() - 1;
        }
        selectDay = currentDay + 1 + "";
        mDayAdapter = new CalendarTextAdapter(context, arryDay,
                currentDay, maxTextSize, minTextSize);
        wvDay.setVisibleItems(5);
        wvDay.setViewAdapter(mDayAdapter);
        wvDay.setCurrentItem(currentDay);
        //小时
        initHours(mHour);
        if (currentHour >= arryHour.size()) {
            currentHour = arryHour.size() - 1;
        }
        selectHour = currentHour + "";
        mHourAdapter = new CalendarTextAdapter(context, arryHour,
                currentHour, maxTextSize, minTextSize);
        wvHour.setVisibleItems(5);
        wvHour.setViewAdapter(mHourAdapter);
        wvHour.setCurrentItem(currentHour);
        //分钟
        initMinutes(mMinutes);
        if (currentMinutes >= arryMinutes.size()) {
            currentMinutes = arryMinutes.size() - 1;
        }
        selectMinutes = currentMinutes  + "";
        mMinutesAdapter = new CalendarTextAdapter(context, arryMinutes,
                currentMinutes, maxTextSize, minTextSize);
        wvMinutes.setVisibleItems(5);
        wvMinutes.setViewAdapter(mMinutesAdapter);
        wvMinutes.setCurrentItem(currentMinutes);
    }


    @SuppressLint("SimpleDateFormat")
    private String getTimeFormat(String format, Date date) {
        SimpleDateFormat f = new SimpleDateFormat(format);
        return f.format(date);
    }

    @SuppressLint("SimpleDateFormat")
    private Date getDateFromString(String timeStr, String format) {
        SimpleDateFormat dateFormat = new SimpleDateFormat(format);
        try {
            return dateFormat.parse(timeStr);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }


    //设置为当前时间
    public void initTime() {
        Calendar c = Calendar.getInstance();
        setTime(selectDate, c.get(Calendar.YEAR),
                c.get(Calendar.MONTH) + 1,
                c.get(Calendar.DAY_OF_MONTH),
                getCurrHour(), getCurrMinute());
    }

    private void initDates(int startYear, int endYear) {
        arryYear.clear();
        for (int i = startYear; i <= endYear; i++) {
            arryYear.add(i + "年");
        }

    }

    private void initMonth() {
        int month = 12;
        arryMonth.clear();
//        if (Integer.parseInt(selectYear) == mCurrentDate.get(Calendar.YEAR)) {
//            month = mCurrentDate.get(Calendar.MONTH) + 1;
//        }
        for (int i = 1; i <= month; i++) {
            if (i < 10) {
                arryMonth.add("0" + i + "月");
            } else {
                arryMonth.add(i + "月");
            }
        }
    }

    private void initDays(String selectMonth) {
        int days;
        arryDay.clear();
//        if (Integer.parseInt(selectYear) == mCurrentDate.get(Calendar.YEAR)
//                && Integer.parseInt(selectMonth) == mCurrentDate.get(Calendar.MONTH) + 1) {
//            days = mCurrentDate.get(Calendar.DAY_OF_MONTH);
//        } else {
        days = calDaysOfMonth(Integer.parseInt(selectYear), Integer.parseInt(selectMonth));
//        }
        for (int i = 1; i <= days; i++) {
            if (i < 10) {
                arryDay.add("0" + i + "日");
            } else {
                arryDay.add(i + "日");
            }
        }
    }


    public void initHours(int hours) {
        arryHour.clear();
//        if (Integer.parseInt(selectYear) == mCurrentDate.get(Calendar.YEAR)
//                && Integer.parseInt(selectMonth) == mCurrentDate.get(Calendar.MONTH) + 1
//                && Integer.parseInt(selectDay) == mCurrentDate.get(Calendar.DAY_OF_MONTH)) {
        //hours = mCurrentDate.get(Calendar.HOUR_OF_DAY);
//        }
        hours = 24;
//        if (hours < 24) {
//            hours = hours + 1;
//        }
        for (int i = 0; i < hours; i++) {
            if (i < 10) {
                arryHour.add("0" + i + context.getString(R.string.dianstr));
            } else {
                arryHour.add(i + "" + context.getString(R.string.dianstr));
            }
        }
    }

    public void initMinutes(int minutes) {
        arryMinutes.clear();
//        if (Integer.parseInt(selectYear) == mCurrentDate.get(Calendar.YEAR)
//                && Integer.parseInt(selectMonth) == mCurrentDate.get(Calendar.MONTH) + 1
//                && Integer.parseInt(selectDay) == mCurrentDate.get(Calendar.DAY_OF_MONTH)
//                && Integer.parseInt(selectHour) == mCurrentDate.get(Calendar.HOUR_OF_DAY)) {
//            minutes = mCurrentDate.get(Calendar.MINUTE);
//        }
        minutes = 60;
//        if (minutes < 60) {
//            minutes = minutes + 1;
//        }
        for (int i = 0; i < minutes; i++) {
            if (i < 10) {
                arryMinutes.add("0" + i + context.getString(R.string.fenstr));
            } else {
                arryMinutes.add(i + "" + context.getString(R.string.fenstr));
            }
        }
    }


    @Override
    public void onClick(View v) {

        if (v == btnSure) {
            if (onTimePickListener != null) {
                onTimePickListener.onClick(selectDate, Integer.parseInt(selectYear), Integer.parseInt(selectMonth), Integer.parseInt(selectDay), selectHour, selectMinutes);
                if (isClickSureDismiss) {
                    dismiss();
                    return;
                } else {
                    return;
                }
            }
        } else if (v == btnCancel) {
            dismiss();
        } else if (v == vDialogChild) {
            return;
        } else {
            dismiss();
        }
        dismiss();

    }

    public void setTimePickListener(OnTimePickListener onTimePickListener) {
        this.onTimePickListener = onTimePickListener;
    }

    /**
     * 设置dialog弹出框模式
     *
     * @param dialogMode 从屏幕中间弹出
     *                   从屏幕底部弹出
     */
    public void setDialogMode(int dialogMode) {
        if (dialogMode == DIALOG_MODE_BOTTOM) {
            resetContent(R.layout.car_easy_rent_dialog_picker_bottom);
            setAnimation(R.style.AnimationBottomDialog);
            setGravity(Gravity.BOTTOM);
        }
    }

    public void setTitle(String title) {
        this.strTitle = title;
    }

    @Override
    protected int dialogWidth() {
        DisplayMetrics metric = new DisplayMetrics();
        ((Activity) mContext).getWindowManager().getDefaultDisplay()
                .getMetrics(metric);
        return metric.widthPixels;
    }

    /**
     * 设置字体大小
     *
     * @param curriteItemText
     * @param adapter
     */
    public void setTextviewSize(String curriteItemText,
                                CalendarTextAdapter adapter) {
        ArrayList<View> arrayList = adapter.getTestViews();
        int size = arrayList.size();
        String currentText;
        for (int i = 0; i < size; i++) {
            TextView textvew = (TextView) arrayList.get(i);
            currentText = textvew.getText().toString();
            if (curriteItemText.equals(currentText)) {
                textvew.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
            } else {
                textvew.setTextSize(TypedValue.COMPLEX_UNIT_SP, 11);
            }
        }
    }

    public int getCurrHour() {
        Calendar c = Calendar.getInstance();
        return c.get(Calendar.HOUR_OF_DAY);
    }

    public int getCurrMinute() {
        Calendar c = Calendar.getInstance();
        return c.get(Calendar.MINUTE);
    }

    /**
     * 设置日期-时间
     *
     * @param year
     * @param month 1-12
     * @param day   1-31
     */
    public void setTime(int mselectData, int year, int month, int day, int hour, int minute) {
        selectYear = year + "";
        selectMonth = month + "";
        selectDay = day + "";
        selectHour = hour + "";
        selectMinutes = minute + "";
        this.currentMonth = month - 1;
        selectDate = mselectData;
        this.currentDay = day -1;
        this.currentHour = hour;
        this.currentMinutes = minute;
        this.mMonth = 12;
        this.mDay = calDaysOfMonth(year, month);
        this.mHour = 24;
        this.mMinutes = 60;

    }

    /**
     * 计算每月多少天
     */
    public int calDaysOfMonth(int year, int month) {
        int day = 0;
        boolean leayyear = false;
        if (year % 400 == 0 || year % 4 == 0 && year % 100 != 0) {
            leayyear = true;
        } else {
            leayyear = false;
        }
        switch (month) {
            case 1:
            case 3:
            case 5:
            case 7:
            case 8:
            case 10:
            case 12:
                day = 31;
                break;
            case 2:
                if (leayyear) {
                    day = 29;
                } else {
                    day = 28;
                }
                break;
            case 4:
            case 6:
            case 9:
            case 11:
                day = 30;
                break;
        }
        return day;
    }

    public interface OnTimePickListener {
        public void onClick(int selectDate, int year, int month, int day, String hour, String minute);
    }

    private class CalendarTextAdapter extends AbstractWheelTextAdapter {
        ArrayList<String> list;

        protected CalendarTextAdapter(Context context, ArrayList<String> list, int currentItem, int maxsize, int minsize) {
            super(context, R.layout.car_easy_rent_item_birth_year, NO_RESOURCE, currentItem, maxsize, minsize);
            this.list = list;
            setItemTextResource(R.id.tempValue);
        }

        @Override
        public View getItem(int index, View cachedView, ViewGroup parent) {
            View view = super.getItem(index, cachedView, parent);
            return view;
        }

        @Override
        public int getItemsCount() {
            return list.size();
        }

        @Override
        protected CharSequence getItemText(int index) {
            return list.get(index) + "";
        }

        @Override
        protected void notifyDataChangedEvent() {
            super.notifyDataChangedEvent();
        }


    }
}