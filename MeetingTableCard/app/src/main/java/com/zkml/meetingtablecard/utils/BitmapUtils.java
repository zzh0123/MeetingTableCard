package com.zkml.meetingtablecard.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;

import com.google.zxing.BinaryBitmap;
import com.google.zxing.DecodeHintType;
import com.google.zxing.RGBLuminanceSource;
import com.google.zxing.Result;
import com.google.zxing.common.HybridBinarizer;
import com.google.zxing.qrcode.QRCodeReader;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Hashtable;

/**
 * Created by lenovo on 2016/4/1.
 */
public class BitmapUtils {


    public static boolean hasSdcard() {
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * 旋转图片
     *
     * @param angle
     * @param bitmap
     * @return Bitmap
     */
    public static Bitmap rotateImage(int angle, Bitmap bitmap) {
        // 图片旋转矩阵
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        // 得到旋转后的图片
        Bitmap resizedBitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
        return resizedBitmap;
    }

    public static File saveBitmap2file(Bitmap bmp) {
        Bitmap.CompressFormat format  = Bitmap.CompressFormat.JPEG;
        int                   quality = 100;
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

    public static File getMediaFile() {
        File sdCardDir = Environment.getExternalStorageDirectory();
        File mediaStorageDir = new File(sdCardDir + File.separator + "careasy"
                + File.separator + "careasyapp");
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                return null;
            }
        }
        File mediaFile;

        mediaFile = new File(mediaStorageDir.getPath() + File.separator
                + "IMG_" + "kaoqin" + ".jpg");

        return mediaFile;
    }

    @SuppressLint("SimpleDateFormat")
    public static File getOutputMediaFile(int type) {
        File sdCardDir = Environment.getExternalStorageDirectory();
        File mediaStorageDir = new File(sdCardDir + File.separator + "CarEasy"
                + File.separator + "CarEasyApp");
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

    //

    /**
     * bitmap 生成文件
     * @param bmp
     * @param fileName
     * @return
     */
    public static File saveBitmap2fileWrite(Bitmap bmp, String fileName) {
        File file = null;
        try {
            file = getOutputMediaFileWrite(1,fileName);
            FileOutputStream out = new FileOutputStream(file);
            bmp.compress(Bitmap.CompressFormat.PNG, 90, out);
            out.flush();
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return file;
    }

    //
    public static File getOutputMediaFileWrite(int type, String fileName) {
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
            mediaFile = new File(mediaStorageDir.getPath() + File.separator + "IMG_" + fileName + ".jpg");
        } else {
            return null;
        }
        return mediaFile;
    }

    /**
     * 获取保存到本地图片的路径
     * @return
     */
    public static String getPicImagePath(String fileName){
        String path = File.separator + "Vbox" + File.separator + "VboxApp" + File.separator + "IMG_" + fileName + ".jpg";
        return path;
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

    public static void recyleBitmap(Bitmap bitmap) {
        if (bitmap != null && !bitmap.isRecycled()) {
            Log.e("gac", "bitmap recyle");
            bitmap.recycle();
            System.gc();
        } else {
            Log.e("gac", "bitmap not recyle");
        }
    }



    public static Bitmap toRoundBitmap(Bitmap bitmap) {
        //圆形图片宽高
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        //正方形的边长
        int r = 0;
        //取最短边做边长
        if (width > height) {
            r = height;
        } else {
            r = width;
        }
        //构建一个bitmap
        Bitmap backgroundBmp = Bitmap.createBitmap(width,
                height, Bitmap.Config.ARGB_8888);
        //new一个Canvas，在backgroundBmp上画图
        Canvas canvas = new Canvas(backgroundBmp);
        Paint paint  = new Paint();
        //设置边缘光滑，去掉锯齿
        paint.setAntiAlias(true);
        //宽高相等，即正方形
        RectF rect = new RectF(0, 0, r, r);
        //通过制定的rect画一个圆角矩形，当圆角X轴方向的半径等于Y轴方向的半径时，
        //且都等于r/2时，画出来的圆角矩形就是圆形
        canvas.drawRoundRect(rect, r / 2, r / 2, paint);
        //设置当两个图形相交时的模式，SRC_IN为取SRC图形相交的部分，多余的将被去掉
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        //canvas将bitmap画在backgroundBmp上
        canvas.drawBitmap(bitmap, null, rect, paint);
        //返回已经绘画好的backgroundBmp
        return backgroundBmp;
    }

    //
    public static Bitmap getRoundRectBitmap(Bitmap bitmap, int radius) {
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setFilterBitmap(true);
        int         bmWidth  = bitmap.getWidth();
        int         bmHeight = bitmap.getHeight();
        final RectF rectF    = new RectF(0, 0, bmWidth, bmHeight);
        final Rect rect     = new Rect(0, 0, bmWidth, bmHeight);
        Canvas canvas   = new Canvas();
        paint.setXfermode(null);
        canvas.drawRoundRect(rectF, radius, radius, paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rectF, paint);
        return bitmap;
    }





    /**
     * 根据分辨率压缩图片比例
     * @param imgPath - 图片路径
     * @param w--图片宽度
     * @param h--图片高度
     * @return
     */
    private static Bitmap compressByResolution(String imgPath, int w, int h) {
        BitmapFactory.Options opts = new BitmapFactory.Options();
        opts.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(imgPath, opts);
        int width = opts.outWidth;
        int height = opts.outHeight;
        int widthScale = width / w;
        int heightScale = height / h;
        int scale;
        if (widthScale < heightScale) { //保留压缩比例小的
            scale = widthScale;
        } else {
            scale = heightScale;
        }
        if (scale < 1) {
            scale = 1;
        }
        opts.inSampleSize = scale;
        opts.inJustDecodeBounds = false;
        Bitmap bitmap = BitmapFactory.decodeFile(imgPath, opts);
        return bitmap;
    }


    /**
     *
     * @param srcPath--图片路径
     * @param ImageSize--图片大小 单位kb
     * @param savePath--图片保存路径
     * @return
     */
    public static boolean compressBitmap(String srcPath, int ImageSize, String savePath) {
        int    subtract;
        Bitmap bitmap = compressByResolution(srcPath, 1024, 720); //分辨率压缩
        if (bitmap == null) {
            return false;
        }
        ByteArrayOutputStream baos    = new ByteArrayOutputStream();
        int                   options = 100;
        bitmap.compress(Bitmap.CompressFormat.JPEG, options, baos);//质量压缩方法，这里100表示不压缩，把压缩后的数据存放到baos中
        while (baos.toByteArray().length > ImageSize * 1024) { //循环判断如果压缩后图片是否大于ImageSize kb,大于继续压缩
            subtract = setSubstractSize(baos.toByteArray().length / 1024);
            baos.reset();//重置baos即清空baos
            options -= subtract;//每次都减少10
            bitmap.compress(Bitmap.CompressFormat.JPEG, options, baos);//这里压缩options%，把压缩后的数据存放到baos中
        }
        try {
            FileOutputStream fos = new FileOutputStream(new File(savePath));//将压缩后的图片保存的本地上指定路径中
            fos.write(baos.toByteArray());
            fos.flush();
            fos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (bitmap != null) {
            bitmap.recycle();
        }
        return true; //压缩成功返回ture
    }

    /**
     * 根据图片的大小设置压缩的比例，提高速度
     * @param imageMB
     * @return
     */
    private static int setSubstractSize(int imageMB) {

        if (imageMB > 1000) {
            return 60;
        } else if (imageMB > 750) {
            return 40;
        } else if (imageMB > 500) {
            return 20;
        } else {
            return 10;
        }
    }
    //实现了将uri转为bitmap的功能
    public static Bitmap getBitmap(Context context, Uri uri){
        Bitmap bitmap = null;
        try {
            bitmap = MediaStore.Images.Media.getBitmap(context.getContentResolver(), uri);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bitmap;
    }

    //实现了将bitmap识别出文字的功能
    public static Result parsePic(Bitmap bitmap) {
        // 解析转换类型UTF-8
        Hashtable<DecodeHintType, String> hints = new Hashtable<DecodeHintType, String>();
        hints.put(DecodeHintType.CHARACTER_SET, "utf-8");
        // 新建一个RGBLuminanceSource对象，将bitmap图片传给此对象
        int lWidth = bitmap.getWidth();
        int lHeight = bitmap.getHeight();
        int[] lPixels = new int[lWidth * lHeight];
        bitmap.getPixels(lPixels, 0, lWidth, 0, 0, lWidth, lHeight);
        RGBLuminanceSource rgbLuminanceSource = new RGBLuminanceSource(lWidth,
                lHeight, lPixels);
        // 将图片转换成二进制图片
        BinaryBitmap binaryBitmap = new BinaryBitmap(new HybridBinarizer(
                rgbLuminanceSource));
        // 初始化解析对象
        QRCodeReader reader = new QRCodeReader();
        // 开始解析
        Result result = null;
        try {
            result = reader.decode(binaryBitmap, hints);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }
}
