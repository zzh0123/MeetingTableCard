package com.zkml.meetingtablecard.utils;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.widget.Toast;

import com.zkml.meetingtablecard.R;
import com.zkml.meetingtablecard.application.MyApplication;

import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.text.DecimalFormat;
import java.util.regex.Pattern;

import static com.zkml.meetingtablecard.utils.cache.StringUtils.isStrEmpty;

public class CarEasyUtil {
    private static final int TYPE_DIAL = 0;
    private static final int TYPE_MESSAGE = 1;
    private static final char[] hexDigit = {'0', '1', '2', '3', '4', '5', '6',
            '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};

    public static Bitmap makeRoundCorner(Bitmap bitmap) {
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        int left = 0, top = 0, right = width, bottom = height;
        float roundPx = height / 2;
        if (width > height) {
            left = (width - height) / 2;
            top = 0;
            right = left + height;
            bottom = height;
        } else if (height > width) {
            left = 0;
            top = (height - width) / 2;
            right = width;
            bottom = top + width;
            roundPx = width / 2;
        }

        Bitmap output = Bitmap.createBitmap(width, height,
                Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);
        int    color  = 0xff424242;
        Paint paint  = new Paint();
        Rect rect   = new Rect(left, top, right, bottom);
        RectF rectF  = new RectF(rect);

        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        canvas.drawRoundRect(rectF, roundPx, roundPx, paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);
        return output;
    }

    public static boolean hasSdcard() {
        if (Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED)) {
            return true;
        } else {
            return false;
        }
    }

    public static void recyleBitmap1(Bitmap bitmap) {
        if (bitmap != null && bitmap.isRecycled()) {
            Log.e("gac", "bitmap recyle");
            bitmap.recycle();
            System.gc();
        } else {
            Log.e("gac", "bitmap not recyle");
        }
    }

    @SuppressLint("SimpleDateFormat")
    public static File getOutputMediaFile(int type) {
        File sdCardDir = Environment.getExternalStorageDirectory();
        File mediaStorageDir = new File(sdCardDir + File.separator + "Vbox"
                + File.separator + "VboxApp");
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                return null;
            }
        }
        File mediaFile;
        if (type == 1) {
            mediaFile = new File(mediaStorageDir.getPath() + File.separator
                    + "IMG_" + "car_easy_headimg" + ".jpg");
        } else {
            return null;
        }
        return mediaFile;
    }

    public static File saveBitmap2file(Bitmap bmp) {
        CompressFormat format  = CompressFormat.JPEG;
        int            quality = 100;
        OutputStream stream  = null;
        File file    = null;
        try {
            file = getOutputMediaFile(1);
            stream = new FileOutputStream(file.getAbsolutePath());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        bmp.compress(format, quality, stream);
        return file;
    }

    public static File saveBitmap2fileWrite(Bitmap bmp) {
        File file = null;
        try {
            file = getOutputMediaFileWrite(1);
            FileOutputStream out = new FileOutputStream(file);
            bmp.compress(CompressFormat.PNG, 90, out);
            out.flush();
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return file;
    }

    @SuppressLint("SimpleDateFormat")
    public static File getOutputMediaFileWrite(int type) {
        File sdCardDir = Environment.getExternalStorageDirectory();
        File mediaStorageDir = new File(sdCardDir + File.separator + "Vbox"
                + File.separator + "VboxApp");
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                return null;
            }
        }
        File mediaFile;
        if (type == 1) {
            mediaFile = new File(mediaStorageDir.getPath() + File.separator
                    + "IMG_" + "handsign" + ".jpg");
        } else {
            return null;
        }
        return mediaFile;
    }

    public static Bitmap decodeFactory(String path) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        Bitmap bitmap     = BitmapFactory.decodeFile(path, options);
        float  realWidth  = options.outWidth;
        float  realHeight = options.outHeight;
        int    scale      = (int) ((realHeight > realWidth ? realHeight : realWidth) / 100);
        if (scale <= 0) {
            scale = 1;
        }
        options.inSampleSize = scale;
        options.inJustDecodeBounds = false;
        bitmap = BitmapFactory.decodeFile(path, options);
        return bitmap;
    }

    public static File bitmapToFile(String filePath) {
        File file = getOutputMediaFile(1);
        Bitmap bm   = getSmallBitmap(filePath);
        FileOutputStream fos;
        try {
            fos = new FileOutputStream(file);
            bm.compress(CompressFormat.JPEG, 40, fos);
            fos.flush();
            fos.close();
            return file;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static Bitmap getSmallBitmap(String filePath) {
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(filePath, options);
        options.inSampleSize = calculateInSampleSize(options, 480, 800);
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeFile(filePath, options);
    }

    public static int calculateInSampleSize(BitmapFactory.Options options,
                                            int reqWidth, int reqHeight) {
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;
        if (height > reqHeight || width > reqWidth) {
            final int heightRatio = Math.round((float) height
                    / (float) reqHeight);
            final int widthRatio = Math.round((float) width / (float) reqWidth);
            inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
        }
        return inSampleSize;
    }

    public static void recyleBitmap(Bitmap bitmap) {
        if (bitmap != null && bitmap.isRecycled()) {
            bitmap.recycle();
            System.gc();
        }
    }

    /**
     * 将字符串编码成 Unicode 形式的字符串. 如 "黄" to "\u9EC4" Converts unicodes to encoded
     * \\uxxxx and escapes special characters with a preceding slash
     *
     * @param theString   待转换成Unicode编码的字符串。
     * @param escapeSpace 是否忽略空格，为true时在空格后面是否加个反斜杠。
     * @return 返回转换后Unicode编码的字符串。
     */

    public static String toEncodedUnicode(String theString, boolean escapeSpace) {
        int len = theString.length();
        int bufLen = len * 2;
        if (bufLen < 0) {
            bufLen = Integer.MAX_VALUE;
        }
        StringBuffer outBuffer = new StringBuffer(bufLen);

        for (int x = 0; x < len; x++) {
            char aChar = theString.charAt(x);
            if ((aChar > 61) && (aChar < 127)) {
                if (aChar == '\\') {
                    outBuffer.append('\\');
                    outBuffer.append('\\');
                    continue;
                }
                outBuffer.append(aChar);
                continue;
            }

            switch (aChar) {
                case ' ':
                    if (x == 0 || escapeSpace)
                        outBuffer.append('\\');
                    outBuffer.append(' ');
                    break;
                case '\t':
                    outBuffer.append('\\');
                    outBuffer.append('t');
                    break;
                case '\n':
                    outBuffer.append('\\');
                    outBuffer.append('n');
                    break;
                case '\r':
                    outBuffer.append('\\');
                    outBuffer.append('r');
                    break;
                case '\f':
                    outBuffer.append('\\');
                    outBuffer.append('f');
                    break;
                case '=': // Fall through
                case ':': // Fall through
                case '#': // Fall through
                case '!':
                    outBuffer.append('\\');
                    outBuffer.append(aChar);
                    break;
                default:
                    if ((aChar < 0x0020) || (aChar > 0x007e)) {
                        outBuffer.append('\\');
                        outBuffer.append('u');
                        outBuffer.append(toHex((aChar >> 12) & 0xF));
                        outBuffer.append(toHex((aChar >> 8) & 0xF));
                        outBuffer.append(toHex((aChar >> 4) & 0xF));
                        outBuffer.append(toHex(aChar & 0xF));
                    } else {
                        outBuffer.append(aChar);
                    }
            }
        }
        return outBuffer.toString();
    }

    private static char toHex(int nibble) {
        return hexDigit[(nibble & 0xF)];
    }

    public static String getDecimels(double input) {
        DecimalFormat df = new DecimalFormat("#.##");
        return df.format(input);
    }

    public static String getDecimelsDot(String input) {
        Double d = Double.valueOf(input);
        return String.format("%.2f", d);
    }

    public static String getDecimelsDotOne(String input) {
        Double d = Double.valueOf(input);
        return String.format("%.1f", d);
    }

    /**
     * 提供精确的小数位四舍五入处理。
     * @param v 需要四舍五入的数字
     * @return 四舍五入后的结果
     */
    public static String roundNum(double v) {
        DecimalFormat df1 = new DecimalFormat("0");//格式化小数，不足的补0
        return df1.format((v));
    }

    // 调用系统打电话和发短信
    public static void dialOrMessage(int type, String content, Context mContext) {
        Intent intent = null;
        switch (type) {
            case TYPE_DIAL:
                try {
                    intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + content));
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    mContext.startActivity(intent);
                } catch (ActivityNotFoundException e) {
                    Toast.makeText(mContext, mContext.getResources().getString(R.string.cannot_use_phone), Toast.LENGTH_SHORT).show();
                }
                break;
            case TYPE_MESSAGE:
                try {
                    intent = new Intent();
                    // 系统默认的action，用来打开默认的短信界面
                    intent.setAction(Intent.ACTION_SENDTO);
                    // 需要发短息的号码
                    intent.setData(Uri.parse("smsto:" + content));
                    mContext.startActivity(intent);
                } catch (ActivityNotFoundException e) {
                    Toast.makeText(mContext, mContext.getResources().getString(R.string.cannot_use_msg), Toast.LENGTH_SHORT).show();
                }
                break;
            default:
                break;
        }
    }

    /**
     * 根据arearid判断是市级的还是省级的逻辑
     *
     * @param areaid
     * @return
     */
    public static boolean tabShowlogistics(String areaid) {
        if (!TextUtils.isEmpty(areaid)) {
                return false;

        }
        return false;
    }

    /**
     * 获取sd卡根目录
     *
     * @return
     */
    public static String getSDPath() {
        File sdDir = null;
        boolean sdCardExist = Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED); // 判断sd卡是否存在
        if (sdCardExist) {
            sdDir = Environment.getExternalStorageDirectory();// 获取跟目录
        }
        return sdDir.toString();
    }

    /**
     * 获取手机分辨率
     * @param mContext
     * @return
     */
    public static String getDisplayMetrics(Activity mContext){
        DisplayMetrics displayMetrics =new DisplayMetrics();
        mContext.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int    heightPixels = displayMetrics.heightPixels;
        int    widthPixels=displayMetrics.widthPixels;
        String displayMetricStr;
        if((heightPixels==1920 && widthPixels==1080) || (heightPixels==1280 && widthPixels==720)){
            displayMetricStr=widthPixels+"×"+heightPixels+".png";
        }else{
            displayMetricStr=1080+"×"+1920+".png";
        }
        return displayMetricStr;
    }

    /**
     * 获取用户手机是否是1920*1080型号的--默认是
     * @param mContext
     * @return
     */
    public static boolean getPhoneScreenSizeBy1920(Activity mContext){
        boolean        flag           = true;
        DisplayMetrics displayMetrics =new DisplayMetrics();
        mContext.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int heightPixels = displayMetrics.heightPixels;
        int widthPixels=displayMetrics.widthPixels;
        if(heightPixels==1280 && widthPixels==720){
            flag = false;
        }else{
            flag = true;
        }
        return flag;
    }

    /**
     * 判断整数（int）
     * @param str
     * @return
     */
    public static boolean isInteger(String str) {
        if (null == str || "".equals(str)) {
            return false;
        }
        Pattern pattern = Pattern.compile("^[-\\+]?[\\d]*$");
        return pattern.matcher(str).matches();
    }


    /**
     * 判断浮点数（double和float）
     */
    public static boolean isDouble(String str) {
        if (null == str || "".equals(str)) {
            return false;
        }
        Pattern pattern = Pattern.compile("^[-\\+]?[.\\d]*$");
        return pattern.matcher(str).matches();
    }

    /**
     * 得到随机四位数
     * @return
     */
    public static String getRandomNums() {
        //获取随机数Math.random()，取值范围[0, 1);
        //所有*9000后取值范围 [0, 9000)
        //最后加上1000，范围[1000, 10000)
        return String.valueOf((int)(Math.random()*9000+1000));
    }
    /**
     * 得到nonce
     * @return
     * @param timestamp
     */
    public static String getNonce(String timestamp) {
        String randomStr = getRandomNums();//随机数
        String szImei    = Build.SERIAL;//手机唯一标识
        if (!isStrEmpty(szImei) && szImei.length() >= 4) {
            szImei = (szImei.substring(szImei.length() - 4, szImei.length()));
        }
        return timestamp + randomStr + szImei;
    }
    /**
     * 判断给定字符串是否空白串。 空白串是指由空格、制表符、回车符、换行符组成的字符串 若输入字符串为null或空字符串，返回true
     *
     * @param input
     * @return boolean
     */
    public static boolean isEmpty(String input) {
        if (input == null || "".equals(input))
            return true;

        for (int i = 0; i < input.length(); i++) {
            char c = input.charAt(i);
            if (c != ' ' && c != '\t' && c != '\r' && c != '\n') {
                return false;
            }
        }
        return true;
    }
    /**
     * 得到系统参数
     * @return
     */
    public static String getSystemParame(Context mContext){
        JSONObject object =new JSONObject();
        try{

            object.put("appChannel","GW");
            object.put("terminalType", "ANDROID");
            object.put("version", MyApplication.getVersionName(mContext));
            object.put("phoneSystem", android.os.Build.VERSION.RELEASE);
        } catch (Exception e){
            e.printStackTrace();
        }
        return object.toString();
    }
}
