package com.zkml.meetingtablecard.utils;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.Display;
import android.view.Gravity;
import android.view.TouchDelegate;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;

import androidx.core.content.FileProvider;

import com.zkml.meetingtablecard.R;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by lenovo on 2016/3/25.
 */
public class ActivityUtils {

    /**
     * 客服
     */
    public static final  int    CUSTOM   = 0;
    /**
     * 调度员
     */
    public static final  int    DISPATCH = 1;
    /**
     * 驾驶员
     */
    public static final  int    DRIVER   = 2;
    private static final String TAG      = "ActivityUtils";
    private static Dialog mProgDialog;
    private static Dialog sDialog;
    private static Dialog mSuccessOrFailDialog;
    private static Dialog mProgDialog1;
    private static Dialog sDialog1;


    public static void cancelDiaolg1() {
        if (mProgDialog1 != null) {
            mProgDialog1.dismiss();
        }
    }


    //短暂提示信息toast
    public static void toast(Context activity, String str) {
        //Crouton.makeText(activity, str, Style.CONFIRM).show();
        ToastUtils toastUtils = ToastUtils.createToastConfig();
        toastUtils.showToast(activity, str);
    }

    //短暂提示信息toast(在底部提示)
    public static void toastBottom(Context activity, String str) {
        //Crouton.makeText(activity, str, Style.CONFIRM).show();
        ToastUtils toastUtils = ToastUtils.createToastConfig();
        toastUtils.showToastBottom(activity, str);
    }

    //短暂提示信息toast
    public static void toast(Context activity, int resid) {
        //Crouton.makeText(activity, activity.getString(resid), Style.CONFIRM).show();
        ToastUtils toastUtils = ToastUtils.createToastConfig();
        toastUtils.showToast(activity, activity.getString(resid));
    }

    public static void cancelDiaolg() {
        if (mProgDialog != null) {
            mProgDialog.dismiss();
        }
    }

    //向SharedPreferences插入数据
    public static void addSharedPreferencesData(Context context, Map<String, String> addMap, String xmlName) {
        if (context != null) {
            SharedPreferences ss     = context.getSharedPreferences(xmlName, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = ss.edit();
            if (null != addMap && addMap.size() > 0) {
                for (String key : addMap.keySet()) {
                    editor.putString(key, addMap.get(key));
                }
            }
            editor.commit();
        }

    }

    //SharedPreferences取出数据
    public static SharedPreferences selSharedPreferencesData(Context context, String xmlName) {
        SharedPreferences ss = context.getSharedPreferences(xmlName, Context.MODE_PRIVATE);
        return ss;
    }

    public static String getStringXmlValue(Context context, int resId) {
        return context.getResources().getString(resId);
    }


    //获取activity跳转参数
    public static String getBundleParam(Activity activity, String param) {
        Intent intent = activity.getIntent();
        Bundle bundle = intent.getExtras();
        String str    = "";
        if (null != bundle) {
            if (null != bundle.getString(param)) {
                str = bundle.getString(param);
            }
        }
        return str;
    }

    //获取activity跳转参数
    public static Map<String, String> getBundleParam(Activity activity) {
        Intent intent = activity.getIntent();
        Bundle bundle = intent.getExtras();
        Map<String, String> map    = new HashMap<String, String>();
        if (null != bundle) {
            for (String key : bundle.keySet()) {
                map.put(key, bundle.getString(key));
            }
        }
        return map;
    }

    //判断EditText is null
    public static boolean isEditTextEmpty(EditText et) {
        if (et != null && !et.getText().toString().trim().equals("")) {
            return false;
        }
        return true;
    }

    //获取字符串信息
    public static String getString(Context context, int id) {
        return context.getResources().getString(id);
    }

    //请求网络小车在动的dialog
    public static Dialog showDialogByViewForGif(Activity activity, View view) {
        sDialog = new Dialog(activity);
        sDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        sDialog.setContentView(view);
        // 设置是否响应返回键可以取消
        sDialog.setCancelable(false);
        // 触摸窗口外边可以取消
        sDialog.setCanceledOnTouchOutside(false);
        // 获得当前手机屏幕的尺寸
        WindowManager wm           = (WindowManager) activity.getSystemService(Context.WINDOW_SERVICE);
        Display display      = wm.getDefaultDisplay();
        Window dialogWindow = sDialog.getWindow();
        dialogWindow.setWindowAnimations(R.style.AnimationFade); //设置窗口弹出动画
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        dialogWindow.setBackgroundDrawableResource(R.drawable.dialog_background);
        //        dialogWindow.setBackgroundDrawable(new BitmapDrawable(activity.getResources(),FastBlurUtility.getBlurBackgroundDrawer(activity)));
        dialogWindow.setGravity(Gravity.CENTER);
        //        lp.x = 100; // 新位置X坐标
        //        lp.y = 100; // 新位置Y坐标
        lp.width = activity.getResources().getDimensionPixelOffset(R.dimen.px460); // 宽度
        lp.height = activity.getResources().getDimensionPixelOffset(R.dimen.px270); // 高度
        lp.alpha = 1.0f; // 透明度
        lp.dimAmount = 0.3f;//背景变暗程度
        dialogWindow.setAttributes(lp);
        sDialog.show();
        return sDialog;
    }

    /**
     * 网络请求的dialog（外部传入res）
     * @param activity
     * @param view
     * @return
     */
    public static Dialog showDialogByViewForRes(Activity activity, View view) {
        sDialog1 = new Dialog(activity);
        sDialog1.requestWindowFeature(Window.FEATURE_NO_TITLE);
        sDialog1.setContentView(view);
        // 设置是否响应返回键可以取消
        sDialog1.setCancelable(false);
        // 触摸窗口外边可以取消
        sDialog1.setCanceledOnTouchOutside(false);
        // 获得当前手机屏幕的尺寸
        WindowManager wm = (WindowManager) activity.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        Window dialogWindow = sDialog1.getWindow();
        dialogWindow.setWindowAnimations(R.style.AnimationFade); //设置窗口弹出动画
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        dialogWindow.setBackgroundDrawableResource(R.drawable.dialog_background_res);
        //        dialogWindow.setBackgroundDrawable(new BitmapDrawable(activity.getResources(),FastBlurUtility.getBlurBackgroundDrawer(activity)));
        dialogWindow.setGravity(Gravity.CENTER);
        //        lp.x = 100; // 新位置X坐标
        //        lp.y = 100; // 新位置Y坐标
        lp.width = activity.getResources().getDimensionPixelOffset(R.dimen.dp121); // 宽度
        lp.height = activity.getResources().getDimensionPixelOffset(R.dimen.px300); // 高度
        lp.alpha = 1.0f; // 透明度
        lp.dimAmount = 0.3f;//背景变暗程度
        dialogWindow.setAttributes(lp);
        sDialog1.show();
        return sDialog1;
    }

    public static Dialog showDialogByUpate(Context context, View view, float xScale, float yScale) {
        Dialog sDialog = new Dialog(context);
        //sDialog.setTitle(title);
        sDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        sDialog.setContentView(view);
        sDialog.setCancelable(false);
        sDialog.setCanceledOnTouchOutside(false);
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        Window dialogWindow = sDialog.getWindow();
        dialogWindow.setWindowAnimations(R.style.AnimationFade);
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        dialogWindow.setGravity(Gravity.CENTER);
        dialogWindow.setBackgroundDrawableResource(R.drawable.update_background_main_tab);
        lp.width = (int) (display.getWidth() * xScale) - DataTools.dip2px(context, 64); // 宽度
//        lp.height = display.getHeight() * 2 / 3; // 高度
        //lp.alpha = 1.0f; // 透明度
        dialogWindow.setAttributes(lp);
        sDialog.show();
        return sDialog;
    }

    public static boolean isServiceRunning(Context context, String serviceName) {
        android.app.ActivityManager manager = (android.app.ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        for (android.app.ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            //Log.e("gac", "serviceName:" + service.service.getClassName());
            if (serviceName.equals(service.service.getClassName())) {

                return true;
            }
        }
        return false;
    }



    // 扩大点击事件触摸范围，提高用户体验
    public static void expandViewTouchDelegate(final View view, final int top,
                                               final int bottom, final int left, final int right) {

        ((View) view.getParent()).post(new Runnable() {
            @Override
            public void run() {
                Rect bounds = new Rect();
                view.setEnabled(true);
                view.getHitRect(bounds);
                bounds.top -= top;
                bounds.bottom += bottom;
                bounds.left -= left;
                bounds.right += right;

                TouchDelegate touchDelegate = new TouchDelegate(bounds, view);

                if (View.class.isInstance(view.getParent())) {
                    ((View) view.getParent()).setTouchDelegate(touchDelegate);
                }
            }
        });
    }

    public static String convertListToString(List<String> items) {
        String temp = "";
        for (int i = 0; i < items.size(); i++) {
            if (i == items.size() - 1) {
                temp += items.get(0);
            } else {
                temp += items.get(items.size() - 1 - i) + "|";
            }
        }
        return temp;
    }





    /**
     * 6到15位数字和字母组合
     * 密码是数字和字母组合--必须是6-15位
     */
    public static boolean isRightPasswordFormat(String password) {
        Pattern p = Pattern.compile("^(?![0-9]+$)(?![a-zA-Z]+$)[0-9A-Za-z]{6,15}$");//
        Matcher m = p.matcher(password);
        return m.matches();
    }



    //获取已展示listview高度
    public static int getListViewHeight(ListView listView) {
        ListAdapter mAdapter = listView.getAdapter();
        if (mAdapter == null) {
            return 0;
        }
        int totalHeight = 0;
        for (int i = 0; i < mAdapter.getCount(); i++) {
            View mView = mAdapter.getView(i, null, listView);
            mView.measure(
                    View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
                    View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));

            //mView.measure(0, 0);

            totalHeight += mView.getMeasuredHeight();
        }
        return totalHeight + (listView.getDividerHeight() * (mAdapter.getCount() - 1));
    }



    /**
     * 获取当前手机系统版本号
     *
     * @return 系统版本号
     */
    public static String getSystemVersion() {
        return android.os.Build.VERSION.RELEASE;
    }

    /**
     * list集合转换成json
     *
     * @param list
     * @return json字符串
     */
    public static String listTojson(List<String> list) {
        StringBuilder json = new StringBuilder();
        json.append("[");
        if (list != null && list.size() > 0) {
            for (int i = 0; i < list.size(); i++) {
                json.append(list.get(i));
                json.append(",");
            }
            json.setCharAt(json.length() - 1, ']');
        } else {
            json.append("]");
        }
        return json.toString();
    }

    /**
     * 显示dialog
     *
     * @param context
     * @param view
     * @param xScale
     * @param yScale
     * @return
     */
    public static Dialog showComputerRule(Context context, View view, float xScale, float yScale) {
        Dialog sDialog = new Dialog(context);
        //sDialog.setTitle(title);
        sDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        sDialog.setContentView(view);
        sDialog.setCancelable(false);
        sDialog.setCanceledOnTouchOutside(false);
        WindowManager wm           = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display      = wm.getDefaultDisplay();
        Window dialogWindow = sDialog.getWindow();
        dialogWindow.setWindowAnimations(R.style.AnimationFade);
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        dialogWindow.setGravity(Gravity.CENTER);
        dialogWindow.setBackgroundDrawableResource(R.drawable.update_background_main_tab);
        //lp.width = (int) (display.getWidth() * xScale) - DataTools.dip2px(context, 64); // 宽度
        //lp.alpha = 1.0f; // 透明度
        dialogWindow.setAttributes(lp);
        sDialog.show();
        return sDialog;
    }

    /**
     * 键盘自动弹出
     */
    public static void showKeyboard(Activity activity) {
        InputMethodManager inputMethodManager = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.toggleSoftInput(0, InputMethodManager.SHOW_FORCED);
    }






    public static void installApk(File file, Context context) {
            ActivityUtils.publicApk(file,context);
    }
    public static void publicApk(File file, Context context) {
        //适配安卓7.0
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);//安卓7.0默认取消，安卓9.0又重新需要
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            Uri contentUri = FileProvider.getUriForFile(context, FileUtil.getFileProviderName(context), file);
            intent.setDataAndType(contentUri, "application/vnd.android.package-archive");
        } else {
            intent.setDataAndType(Uri.fromFile(file), "application/vnd.android.package-archive");
        }
        context.startActivity(intent);
    }

    // 两次点击间隔不能少于1000ms
    private static final int FAST_CLICK_DELAY_TIME = 1000;
    private static long lastClickTime;
    //防止多次点击
    public static boolean isFastClick() {
        boolean flag = true;
        long currentClickTime = System.currentTimeMillis();
        if ((currentClickTime - lastClickTime) >= FAST_CLICK_DELAY_TIME ) {
            flag = false;
        }
        lastClickTime = currentClickTime;
        return flag;
    }
}

