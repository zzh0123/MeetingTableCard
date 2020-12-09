package com.zkml.meetingtablecard.view.dialog;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.zkml.meetingtablecard.R;
import com.zkml.meetingtablecard.utils.ActivityUtils;
import com.zkml.meetingtablecard.utils.DataTools;
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

public class DateSelectView extends BaseDialog implements
        View.OnClickListener {

    public static final int DIALOG_MODE_CENTER = 0;
    public static final int DIALOG_MODE_BOTTOM = 1;
    public int delayMinutes = 30;//延迟多少分钟提醒
    private int MIN_YEAR;
    @SuppressWarnings("unused")
    private int MAX_YEAR;
    private Context context;
    private WheelView wvDate;
    private WheelView wvHour;
    private WheelView wvMinute;
    private View vDialog;
    private View vDialogChild;
    private ViewGroup VDialogPicker;
    private TextView tvTitle;
    private TextView btnSure;
    private TextView btnCancel;
    private ArrayList<String> arry_dates = new ArrayList<String>();
    private ArrayList<String> arry_hours = new ArrayList<String>();
    private ArrayList<String> arry_minutes = new ArrayList<String>();
    private CalendarTextAdapter mDateAdapter;
    private CalendarTextAdapter mHourAdapter;
    private CalendarTextAdapter mMinuteAdapter;
    private int hour;
    private int minute;
    private int currentDate = 0;
    private int currentHour = 0;
    private int currentMinute = 0;
    private int mCurrentHour = 0;
    private int mCurrentMinute = 0;
    private int maxTextSize = 20;
    private int minTextSize = 15;
    private boolean issetdata = false;

    private int selectDate = 0;
    private String selectHour;
    private String selectMinute;
    private SimpleDateFormat mFormatter = new SimpleDateFormat("yyyy-MM-dd HH:mm");

    private String strTitle = "用车时间";

    private OnTimePickListener onTimePickListener;
    private boolean mFlag;//最近三个月false,
    private boolean mIsSevenDayFlag = false;//最近三天flag,
    public DateSelectView(Context context, boolean flag) {
        super(context, R.layout.car_easy_rent_dialog_picker_center);
        this.context = context;
        mFlag = flag;
    }

    public DateSelectView(Context context, boolean flag, boolean mIsSevenDayFlag) {
        super(context, R.layout.car_easy_rent_dialog_picker_center);
        this.context = context;
        mFlag = flag;
        this.mIsSevenDayFlag = mIsSevenDayFlag;
    }

    public DateSelectView(Context context, boolean flag, String strTitle) {
        super(context, R.layout.car_easy_rent_dialog_picker_center);
        this.context = context;
        mFlag = flag;
        this.strTitle = strTitle;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init();
    }

    private void init() {
        maxTextSize = DataTools.px2sp(context, 48);
        minTextSize = DataTools.px2sp(context, 33);
        VDialogPicker = (ViewGroup) findViewById(R.id.ly_dialog_picker);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT, 1.0f);
        // 此处相当于布局文件中的Android:layout_gravity属性
        lp.gravity = Gravity.CENTER_VERTICAL;

        wvDate = new WheelView(context);
        wvDate.setLayoutParams(lp);
        VDialogPicker.addView(wvDate);

        wvHour = new WheelView(context);
        wvHour.setLayoutParams(lp);
//		wvHour.setCyclic(true);
        VDialogPicker.addView(wvHour);

        wvMinute = new WheelView(context);
        wvMinute.setLayoutParams(lp);
//		wvMinute.setCyclic(true);
        VDialogPicker.addView(wvMinute);

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

        if (!issetdata) {
            initTime();
        }
        mCurrentHour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
        mCurrentMinute = Calendar.getInstance().get(Calendar.MINUTE);
        initDates();
        mDateAdapter = new CalendarTextAdapter(context, arry_dates,
                currentDate, maxTextSize, minTextSize);
        wvDate.setVisibleItems(3);
        wvDate.setViewAdapter(mDateAdapter);
        wvDate.setCurrentItem(currentDate);
        if (selectDate == 0) {
            initHourAdapter();
            if (mCurrentHour == Integer.parseInt(selectHour)) {
                initMinuteAdapter();
            } else {
                initMinuteAdapterByMinute();
            }

        } else {
            initHourAdapterByHour();
            initMinuteAdapterByMinute();
        }
        mDateAdapter = new CalendarTextAdapter(context, arry_dates, selectDate, maxTextSize, minTextSize);
        wvDate.setVisibleItems(3);
        wvDate.setViewAdapter(mDateAdapter);
        wvDate.setCurrentItem(selectDate);

        wvDate.addChangingListener(new OnWheelChangedListener() {

            @Override
            public void onChanged(WheelView wheel, int oldValue, int newValue) {
                String currentText = (String) mDateAdapter.getItemText(wheel.getCurrentItem());
                selectDate = wheel.getCurrentItem();
                setTextviewSize(currentText, mDateAdapter);
                Log.d("lyyo", "selectDate: " + selectDate);
                mDateAdapter = new CalendarTextAdapter(context, arry_dates, selectDate, maxTextSize, minTextSize);
                wvDate.setVisibleItems(3);
                wvDate.setViewAdapter(mDateAdapter);
                wvDate.setCurrentItem(selectDate);
                if (selectDate == 0) {
                    initHourAdapter();
                    if (mCurrentHour == Integer.parseInt(selectHour)) {
                        initMinuteAdapter();
                    } else {
                        initMinuteAdapterByMinute();
                    }
                } else {
                    initHourAdapterByHour();
                    initMinuteAdapterByMinute();
                }
            }
        });

        wvDate.addScrollingListener(new OnWheelScrollListener() {

            @Override
            public void onScrollingStarted(WheelView wheel) {

            }

            @Override
            public void onScrollingFinished(WheelView wheel) {
                String currentText = (String) mDateAdapter.getItemText(wheel
                        .getCurrentItem());
                setTextviewSize(currentText, mDateAdapter);
            }
        });

        wvHour.addChangingListener(new OnWheelChangedListener() {

            @Override
            public void onChanged(WheelView wheel, int oldValue, int newValue) {
                String currentText = (String) mHourAdapter.getItemText(wheel
                        .getCurrentItem());
                setTextviewSize(currentText, mHourAdapter);
                if (currentText != null) {
                    currentText = currentText.replace(context.getString(R.string.dianstr), "");
                }
                selectHour = currentText;
                if (selectDate == 0 && mCurrentHour == Integer.parseInt(selectHour)) {
                    initMinuteAdapter();
                } else {
                    initMinuteAdapterByMinute();
                }

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
            }
        });

        wvMinute.addChangingListener(new OnWheelChangedListener() {

            @Override
            public void onChanged(WheelView wheel, int oldValue, int newValue) {
                String currentText = (String) mMinuteAdapter.getItemText(wheel
                        .getCurrentItem());
                setTextviewSize(currentText, mMinuteAdapter);
                if (currentText != null) {
                    currentText = currentText.replace(context.getString(R.string.fenstr), "");
                }
                selectMinute = currentText;
            }
        });

        wvMinute.addScrollingListener(new OnWheelScrollListener() {

            @Override
            public void onScrollingStarted(WheelView wheel) {

            }

            @Override
            public void onScrollingFinished(WheelView wheel) {
                String currentText = (String) mMinuteAdapter.getItemText(wheel
                        .getCurrentItem());
                setTextviewSize(currentText, mMinuteAdapter);
            }
        });

    }

    //非当前时间的分钟初始化
    private void initMinuteAdapterByMinute() {
        arry_minutes.clear();
        for (int i = 0; i < minute; i++) {
            if (i % 5 == 0) {
                if (i <= 5) {
                    arry_minutes.add("0" + i + context.getString(R.string.fenstr));
                } else {
                    arry_minutes.add(i + "" + context.getString(R.string.fenstr));
                }
            }
        }
        mMinuteAdapter = new CalendarTextAdapter(context, arry_minutes,
                Integer.parseInt(selectMinute) / 5, maxTextSize, minTextSize);
        wvMinute.setVisibleItems(3);
        wvMinute.setViewAdapter(mMinuteAdapter);
        wvMinute.setCurrentItem(Integer.parseInt(selectMinute) / 5);
    }

    //非当前时间的小时初始化
    private void initHourAdapterByHour() {
        arry_hours.clear();
        for (int i = 0; i < hour; i++) {
            if (i < 10) {
                arry_hours.add("0" + i + context.getString(R.string.dianstr));
            } else {
                arry_hours.add(i + "" + context.getString(R.string.dianstr));
            }
        }
        mHourAdapter = new CalendarTextAdapter(context, arry_hours,
                Integer.parseInt(selectHour), maxTextSize, minTextSize);
        wvHour.setVisibleItems(3);
        wvHour.setViewAdapter(mHourAdapter);
        wvHour.setCurrentItem(Integer.parseInt(selectHour));
    }

    //当前时间的分钟初始化
    private void initMinuteAdapter() {
        initMinutes(minute);
        if (mCurrentMinute > Integer.parseInt(selectMinute)) {
            mMinuteAdapter = new CalendarTextAdapter(context, arry_minutes,
                    0, maxTextSize, minTextSize);
            wvMinute.setVisibleItems(3);
            wvMinute.setViewAdapter(mMinuteAdapter);
            wvMinute.setCurrentItem(0);
        } else {
            mMinuteAdapter = new CalendarTextAdapter(context, arry_minutes,
                    (Integer.parseInt(selectMinute) ) / 5, maxTextSize, minTextSize);
            wvMinute.setVisibleItems(3);
            wvMinute.setViewAdapter(mMinuteAdapter);
            wvMinute.setCurrentItem((Integer.parseInt(selectMinute) ) / 5);
        }

    }

    //当前时间的小时初始化
    private void initHourAdapter() {
        initHours(hour);
        if (mCurrentHour >= Integer.parseInt(selectHour)) {
            mHourAdapter = new CalendarTextAdapter(context, arry_hours,
                    0, maxTextSize, minTextSize);
            wvHour.setVisibleItems(3);
            wvHour.setViewAdapter(mHourAdapter);
            wvHour.setCurrentItem(0);
        } else {
            mHourAdapter = new CalendarTextAdapter(context, arry_hours,
                    Integer.parseInt(selectHour) - mCurrentHour, maxTextSize, minTextSize);
            wvHour.setVisibleItems(3);
            wvHour.setViewAdapter(mHourAdapter);
            wvHour.setCurrentItem(Integer.parseInt(selectHour) - mCurrentHour);
        }

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

    //初始化最近三个月的数据
    public void initDates() {
        Calendar calendar = Calendar.getInstance();
        Calendar mcalendar = Calendar.getInstance();
        if (mFlag) {
            if (mIsSevenDayFlag) {
                //12月需求 个人出行用车申请时间延长至15天
                mcalendar.add(Calendar.DAY_OF_MONTH, 15);
            } else {
                mcalendar.add(Calendar.MONTH, 3);
            }
        } else {
            mcalendar.add(Calendar.YEAR, -10);
        }
        Date nowday = calendar.getTime(); //得到当前月的时间
        Date threeDay = mcalendar.getTime();
        calendar.set(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
        mcalendar.set(mcalendar.get(Calendar.YEAR), mcalendar.get(Calendar.MONTH), mcalendar.get(Calendar.DAY_OF_MONTH));
        boolean isSameYear = calendar.get(Calendar.YEAR) == mcalendar.get(Calendar.YEAR);
        if (isSameYear) {
            MIN_YEAR = calendar.get(Calendar.YEAR);
            MAX_YEAR = calendar.get(Calendar.YEAR);
        } else {
            MIN_YEAR = calendar.get(Calendar.YEAR);
            MAX_YEAR = calendar.get(Calendar.YEAR) + 1;
        }
        if (mFlag) {
            while (nowday.before(threeDay)) {
                final Date date = calendar.getTime();
                arry_dates.add(getTimeFormat("MM月dd日 EEE", date));
                calendar.add(Calendar.DATE, 1);
                nowday = calendar.getTime(); //得到当前月的时间
            }
        } else {
            while (threeDay.before(nowday)) {
                final Date date = mcalendar.getTime();
                arry_dates.add(getTimeFormat("yyyy年MM月dd日", date));
                mcalendar.add(Calendar.DATE, 1);
                threeDay = mcalendar.getTime(); //得到当前月的时间
            }
        }


    }

    public void initHours(int hours) {
        arry_hours.clear();
        for (int i = 0; i < hours; i++) {
            if (i < 10) {
                if (mCurrentHour < 10) {
                    if (i >= mCurrentHour) {
                        arry_hours.add("0" + i + context.getString(R.string.dianstr));
                    }
                }
            } else {
                if (i >= mCurrentHour) {
                    arry_hours.add(i + "" + context.getString(R.string.dianstr));
                }
            }
        }
    }

    public void initMinutes(int minutes) {
        arry_minutes.clear();
        for (int i = 0; i < minutes; i++) {
            if (i % 5 == 0) {
                if (i <= 5) {
               /*     if (mCurrentMinute < 10) {
                        if (i >= mCurrentMinute) {
                            arry_minutes.add("0" + i + context.getString(R.string.fenstr));
                        }
                    }*/
                    arry_minutes.add("0" + i + context.getString(R.string.fenstr));
                } else {
              /*      if (i >= mCurrentMinute) {
                        arry_minutes.add(i + "" + context.getString(R.string.fenstr));
                    }*/
                    arry_minutes.add(i + "" + context.getString(R.string.fenstr));
                }
            }
        }
    }

    private boolean afterToday(Date date2) {
        Calendar calendar = Calendar.getInstance();
        Date date1 = calendar.getTime();
        String tr = mFormatter.format(date1.getTime() + 1000 * 60 * delayMinutes);
        try {
            Date d = mFormatter.parse(tr);
            //72小时之内单独做判断
            if (delayMinutes == 72 * 60) {
                String tr1 = mFormatter.format(d.getTime() - 1000 * 60 * delayMinutes);
                if (mFormatter.parse(tr1).after(date2)) {
                    ActivityUtils.toast(mContext, mContext.getString(R.string.alertmsg1));
                    return false;
                } else if (!(d.after(date2) || d.equals(date2))) {
                    ActivityUtils.toast(mContext, mContext.getString(R.string.alertmsg3));
                    return false;
                }
                return true;
            } else if (delayMinutes == 100) {//不能大于当前时间
                if (!date1.after(date2)) {
                    ActivityUtils.toast(mContext, mContext.getString(R.string.alertmsg4));
                    return false;
                }
                return true;
            } else if (delayMinutes == 1000) {
                return true;
            } else {//不是72小时之内的判断
                if (d.after(date2)) {
                    if (delayMinutes == 30) {
                        ActivityUtils.toast(mContext, mContext.getString(R.string.alertmsg));
                    } else {
                        ActivityUtils.toast(mContext, mContext.getString(R.string.alertmsg1));
                    }

                    return false;
                }
            }

        } catch (ParseException e) {
            e.printStackTrace();
            return true;
        }
        return true;
    }

    @Override
    public void onClick(View v) {

        if (v == btnSure) {
            if (onTimePickListener != null) {
                Calendar calendar = Calendar.getInstance();
                //calendar.set(MIN_YEAR, 0, 1);
                if (mFlag) {
                    calendar.add(Calendar.DATE, selectDate);
                } else {
                    calendar.add(Calendar.YEAR, -10);
                    calendar.add(Calendar.DATE, selectDate);
                }


                String date = calendar.get(Calendar.YEAR) + "-" + (calendar.get(Calendar.MONTH) + 1) + "-" + calendar.get(Calendar.DAY_OF_MONTH) + " " + selectHour + ":" + selectMinute;

//				Calendar calendar2 = Calendar.getInstance();
//				Date date1=calendar2.getTime();
                try {
                    Date dates = mFormatter.parse(date);
//                    afterToday(dates);
//					if (date1.after(dates)) {
//						ActivityUtils.toast((Activity) mContext, mContext.getString(R.string.alertmsg1));
//					}else{
//						onTimePickListener.onClick(selectDate, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH) + 1, calendar.get(Calendar.DAY_OF_MONTH),selectHour, selectMinute);
//					}
                    if (afterToday(dates)) {
                        onTimePickListener.onClick(selectDate, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH) + 1, calendar.get(Calendar.DAY_OF_MONTH), selectHour, selectMinute);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
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
        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month - 1, day);
        this.currentDate = calendar.get(Calendar.DAY_OF_YEAR) - 1;
        if (month < 6) {
            MIN_YEAR = year - 1;
            MAX_YEAR = year;
            this.currentDate += calDaysOfYear(MIN_YEAR);
        } else {
            MIN_YEAR = year;
            MAX_YEAR = year + 1;
        }

        selectDate = mselectData;
        selectHour = hour + "";
        if (minute % 5 == 0) {
            selectMinute = minute +"";
        } else {
            selectMinute = (minute / 5 + 1 ) * 5 + "";
            if (Integer.parseInt(selectMinute) > 55) {
                selectMinute = "55";
            }
        }
        issetdata = true;
        this.currentHour = hour;
        this.currentMinute = minute;
        this.hour = 24;
        this.minute = 60;

    }
    /**
     * 设置日期
     *
     * @param date
     */
    public int setDate(Date date) {

        Calendar cal = Calendar.getInstance();
        cal.set(MIN_YEAR, 1, 1);
        long time1 = cal.getTimeInMillis();
        cal.setTime(date);
        long time2 = cal.getTimeInMillis();
        long between_days = (time2 - time1) / (1000 * 3600 * 24);
        return Integer.parseInt(String.valueOf(between_days)) + 1;
    }

    /**
     * 计算每年多少天
     *
     * @return
     */
    public int calDaysOfYear(int year) {
        if (year % 4 == 0 && year % 100 != 0) {
            return 366;
        } else {
            return 365;
        }
    }

    /**
     * 计算每月多少天
     */
    public int calDaysOfMonth(int year, int month) {
        int day = 0;
        boolean leayyear = false;
        if (year % 4 == 0 && year % 100 != 0) {
            leayyear = true;
        } else {
            leayyear = false;
        }
        for (int i = 1; i <= 12; i++) {
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
        }
        return day;
    }

    public interface OnTimePickListener {
        void onClick(int selectDate, int year, int month, int day, String hour, String minute);
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