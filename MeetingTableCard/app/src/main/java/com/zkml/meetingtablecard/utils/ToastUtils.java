package com.zkml.meetingtablecard.utils;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.zkml.meetingtablecard.R;

public class ToastUtils {

    private static ToastUtils toastCommom;
    private Toast toast;

    public ToastUtils() {
    }

    public static ToastUtils createToastConfig() {
        if (toastCommom == null) {
            toastCommom = new ToastUtils();
        }
        return toastCommom;
    }

    public void showToast(final Context context, final String msg) {
        final View toastRoot = LayoutInflater.from(context).inflate(R.layout.car_easy_toast, null);
        TextView message   = (TextView) toastRoot.findViewById(R.id.message);
        message.setText(msg);
        if (toast == null) {
            toast = new Toast(context);
        }
        //toast = Toast.makeText(context, msg, Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
        toast.setDuration(Toast.LENGTH_SHORT);
        toast.setView(toastRoot);
        toast.show();
    }
    public void showToastBottom(final Context context, final String msg) {
        final View toastRoot = LayoutInflater.from(context).inflate(R.layout.car_easy_toast_bottom, null);
        TextView message   = (TextView) toastRoot.findViewById(R.id.message);
        message.setText(msg);
        if (toast == null) {
            toast = new Toast(context);
        }
        //toast = Toast.makeText(context, msg, Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
        toast.setDuration(Toast.LENGTH_SHORT);
        toast.setView(toastRoot);
        toast.show();
    }
}
