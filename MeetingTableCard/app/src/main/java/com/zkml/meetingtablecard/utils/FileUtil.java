/*************************************************************************************************
 * 版权所有 (C)2012,  深圳市康佳集团股份有限公司 
 *
 * 文件名称：FileUtil.java
 * 内容摘要：文件工具类
 * 当前版本：
 * 作         者： hexiaoming
 * 完成日期：2012-12-26
 * 修改记录：
 * 修改日期：
 * 版   本  号：
 * 修   改  人：
 * 修改内容：
 ************************************************************************************************/
package com.zkml.meetingtablecard.utils;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;

import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;

import java.io.File;
import java.io.IOException;

/**
 * 类描述：FileUtil
 *
 * @author hexiaoming
 */
public class FileUtil {

    /***********保存升级APK的目录***********/
    public static final String KonkaApplication = "konkaUpdateApplication";
    public static File updateDir        = null;
    public static File updateFile       = null;
    public static boolean isCreateFileSucess;
    private static String APP_DIR_NAME  = "honjane";
    private static String FILE_DIR_NAME = "files";
    private static String mRootDir;
    private static String mAppRootDir;
    private static String mFileDir;

    /**
     * 方法描述：createFile方法
     *
     * @param app_name
     * @return
     * @see FileUtil
     */
    public static void createFile(String app_name) {

        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
            isCreateFileSucess = true;

            updateDir = new File(Environment.getExternalStorageDirectory() + "/" + KonkaApplication + "/");
            updateFile = new File(updateDir + "/" + app_name + ".apk");

            if (!updateDir.exists()) {
                updateDir.mkdirs();
            }
            if (!updateFile.exists()) {
                try {
                    updateFile.createNewFile();
                } catch (IOException e) {
                    isCreateFileSucess = false;
                    e.printStackTrace();
                }
            }

        } else {
            isCreateFileSucess = false;
        }
    }

    public static void init() {
        mRootDir = getRootPath();
        if (mRootDir != null && !"".equals(mRootDir)) {
            mAppRootDir = mRootDir + "/" + APP_DIR_NAME;
            mFileDir = mAppRootDir + "/" + FILE_DIR_NAME;
            File appDir = new File(mAppRootDir);
            if (!appDir.exists()) {
                appDir.mkdirs();
            }
            File fileDir = new File(mAppRootDir + "/" + FILE_DIR_NAME);
            if (!fileDir.exists()) {
                fileDir.mkdirs();
            }

        } else {
            mRootDir = "";
            mAppRootDir = "";
            mFileDir = "";
        }
    }

    public static String getFileDir() {
        return mFileDir;
    }


    public static String getRootPath() {
        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
            return Environment.getExternalStorageDirectory().getAbsolutePath(); // filePath:  /sdcard/
        } else {
            return Environment.getDataDirectory().getAbsolutePath() + "/data"; // filePath:  /data/data/
        }
    }

    /**
     * 打开文件
     * 兼容7.0
     *
     * @param context     activity
     * @param file        File
     * @param contentType 文件类型如：文本（text/html）
     *                    当手机中没有一个app可以打开file时会抛ActivityNotFoundException
     */
    public static void startActionFile(Context context, File file, String contentType) throws ActivityNotFoundException {
        if (context == null) {
            return;
        }
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.addCategory(Intent.CATEGORY_DEFAULT);
        intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        intent.setFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);//增加读写权限
        intent.setDataAndType(getUriForFile(context, file), contentType);
        if (!(context instanceof Activity)) {
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        }
        context.startActivity(intent);
    }

    /**
     * 打开相机
     * 兼容7.0
     *
     * @param activity    Activity
     * @param file        File
     * @param requestCode result requestCode
     */
    public static void startActionCapture(Activity activity, File file, int requestCode) {
        if (activity == null) {
            return;
        }
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, getUriForFile(activity, file));
        activity.startActivityForResult(intent, requestCode);
    }

    /**
     * 打开相机
     * 兼容7.0
     *
     * @param fragment    Fragment
     * @param file        File
     * @param requestCode result requestCode
     */
    public static void startActionCapture(Fragment fragment, File file, int requestCode) {
        if (fragment == null) {
            return;
        }
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, getUriForFile(fragment.getActivity(), file));
        fragment.startActivityForResult(intent, requestCode);
    }

    /**
     * 裁剪
     * 兼容7.0
     *
     * @param activity    Activity
     * @param file        File
     * @param requestCode result requestCode
     */
    public static void startActionCut(Activity activity, File file, int requestCode) {
        if (activity == null) {
            return;
        }
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        intent.setDataAndType(getUriForFile(activity, file), "image/*");
        intent.putExtra("crop", "true");
        if (Build.MANUFACTURER.contains("HUAWEI")) {// 华为特殊处理 不然会显示圆
            intent.putExtra("aspectX", 9998);
            intent.putExtra("aspectY", 9999);
        } else {
            intent.putExtra("aspectX", 1);
            intent.putExtra("aspectY", 1.5);
        }
        intent.putExtra("outputX", 250);
        intent.putExtra("outputY", 250);
        intent.putExtra("outputFormat", "JPEG");
        intent.putExtra("noFaceDetection", true);// 取消人脸识别
        intent.putExtra("return-data", true);// true:不返回uri，false：返回uri
        activity.startActivityForResult(intent, requestCode);
    }

    /**
     * 裁剪
     * 兼容7.0
     *
     * @param activity    Activity
     * @param uri         Uri
     * @param outputFile  File
     */
    public static void startActionCut(Activity activity, Uri uri, File outputFile, int requestCode) {
        if (activity == null) {
            return;
        }
        Intent intent = new Intent("com.android.camera.action.CROP");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            intent.setDataAndType(uri, "image/*");
            //临时赋予读写Uri的权限
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
        } else {
            intent.setDataAndType(uri, "image/*");
        }

        intent.putExtra("crop", "true");
        if (Build.MANUFACTURER.contains("HUAWEI")) {// 华为特殊处理 不然会显示圆
            intent.putExtra("aspectX", 9998);
            intent.putExtra("aspectY", 9999);
        } else {
            intent.putExtra("aspectX", 1);
            intent.putExtra("aspectY", 1.5);
        }
        intent.putExtra("outputX", 800);
        intent.putExtra("outputY", 800);

        //return-data为true时，直接返回bitmap，可能会很占内存，不建议，小米等个别机型会出异常！！！
        //所以适配小米等个别机型，裁切后的图片，不能直接使用data返回，应使用uri指向
        //裁切后保存的URI，不属于我们向外共享的，所以可以使用fill://类型的URI
        Uri outputUri = Uri.fromFile(outputFile);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, outputUri);
        intent.putExtra("return-data", false);

        intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
        intent.putExtra("noFaceDetection", true);
        activity.startActivityForResult(intent, requestCode);
    }

    /**
     * 裁剪
     * 兼容7.0
     *
     * @param fragment    Fragment
     * @param file        File
     * @param requestCode result requestCode
     */
    public static void startActionCut(Fragment fragment, File file, int requestCode) {
        if (fragment == null) {
            return;
        }
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        intent.setDataAndType(getUriForFile(fragment.getActivity(), file), "image/*");
        intent.putExtra("crop", "true");
        if (Build.MANUFACTURER.contains("HUAWEI")) {// 华为特殊处理 不然会显示圆
            intent.putExtra("aspectX", 9998);
            intent.putExtra("aspectY", 9999);
        } else {
            intent.putExtra("aspectX", 1);
            intent.putExtra("aspectY", 1.5);
        }
        intent.putExtra("outputX", 250);
        intent.putExtra("outputY", 250);
        intent.putExtra("outputFormat", "JPEG");
        intent.putExtra("noFaceDetection", true);// 取消人脸识别
        intent.putExtra("return-data", true);// true:不返回uri，false：返回uri
        fragment.startActivityForResult(intent, requestCode);
    }
    /**
     * 裁剪
     * 兼容7.0
     *
     * @param fragment    Fragment
     * @param uri         Uri
     * @param outputFile  File
     */
    public static void startActionCut(Fragment fragment, Uri uri, File outputFile, int requestCode) {
        if (fragment == null) {
            return;
        }
        Intent intent = new Intent("com.android.camera.action.CROP");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            intent.setDataAndType(uri, "image/*");
            //临时赋予读写Uri的权限
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
        } else {
            intent.setDataAndType(uri, "image/*");
        }

        intent.putExtra("crop", "true");
        if (Build.MANUFACTURER.contains("HUAWEI")) {// 华为特殊处理 不然会显示圆
            intent.putExtra("aspectX", 9998);
            intent.putExtra("aspectY", 9999);
        } else {
            intent.putExtra("aspectX", 1);
            intent.putExtra("aspectY", 1.5);
        }
        intent.putExtra("outputX", 250);
        intent.putExtra("outputY", 250);

        //return-data为true时，直接返回bitmap，可能会很占内存，不建议，小米等个别机型会出异常！！！
        //所以适配小米等个别机型，裁切后的图片，不能直接使用data返回，应使用uri指向
        //裁切后保存的URI，不属于我们向外共享的，所以可以使用fill://类型的URI
        Uri outputUri = Uri.fromFile(outputFile);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, outputUri);
        intent.putExtra("return-data", false);

        intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
        intent.putExtra("noFaceDetection", true);
        fragment.startActivityForResult(intent, requestCode);
    }
    /**
     * 设置Intent的data和类型，并赋予目标程序临时的URI读写权限
     * @param context 上下文
     * @param intent 意图
     * @param type 类型
     * @param fileUri 文件uri
     * @param writeAble 是否赋予可写URI的权限
     */
    public static void setIntentDataAndType(Context context,
                                            Intent intent,
                                            String type,
                                            Uri fileUri,
                                            boolean writeAble) {
        //7.0以上进行适配
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            intent.setDataAndType(fileUri, type);
            //临时赋予读写Uri的权限
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            if (writeAble) {
                intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
            }
        } else {
            intent.setDataAndType(fileUri, type);
        }
    }

    public static Uri getImageContentUri(Activity context, File imageFile) {
        String filePath = imageFile.getAbsolutePath();
        Cursor cursor = context.getContentResolver().query(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                new String[]{MediaStore.Images.Media._ID},
                MediaStore.Images.Media.DATA + "=? ",
                new String[]{filePath}, null);

        if (cursor != null && cursor.moveToFirst()) {
            int id = cursor.getInt(cursor
                    .getColumnIndex(MediaStore.MediaColumns._ID));
            Uri baseUri = Uri.parse("content://media/external/images/media");
            return Uri.withAppendedPath(baseUri, "" + id);
        } else {
            if (imageFile.exists()) {
                ContentValues values = new ContentValues();
                values.put(MediaStore.Images.Media.DATA, filePath);
                return context.getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
            } else {
                return null;
            }
        }
    }


    public static Uri getUriForFile(Context context, File file) {
        if (context == null || file == null) {
            throw new NullPointerException();
        }
        Uri uri;
        if (Build.VERSION.SDK_INT >= 24) {
            uri = FileProvider.getUriForFile(context.getApplicationContext(), getFileProviderName(context), file);
        } else {
            uri = Uri.fromFile(file);
        }
        return uri;
    }

    public final static String getFileProviderName(Context context) {
        return context.getPackageName() + ".fileprovider";
    }
}