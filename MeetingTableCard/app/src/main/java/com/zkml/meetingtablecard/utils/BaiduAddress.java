package com.zkml.meetingtablecard.utils;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;


public class BaiduAddress {

    //1.百度地图包名
    public static final String BAIDUMAP_PACKAGENAME = "com.baidu.BaiduMap";
    //2.高德地图包名
    public static final String AUTONAVI_PACKAGENAME = "com.autonavi.minimap";
    //3.腾讯地图包名
    public static final String QQMAP_PACKAGENAME    = "com.tencent.map";
    //三大导航包名
    public static final String[] MAP_PACKAGES         = {BAIDUMAP_PACKAGENAME, AUTONAVI_PACKAGENAME, QQMAP_PACKAGENAME};

    /**
     * @param args
     */
    public static void main(String[] args) {
    }

    public static String getJsonAddr(String lat, String lng) {// lat 纬度坐标 lng
        // 经度坐标
        String jsonaddr = "";
        // 服务器地址
        String urlPath = "http://api.map.baidu.com/geocoder/v2/?ak=5b86c0258dc8b040cc171dd288183fbb&location="
                + lat + "," + lng + "&output=json&pois=0";
        StringBuffer sbf    = new StringBuffer();
        BufferedWriter writer = null;
        BufferedReader reader = null;
        HttpURLConnection uc     = null;
        try {
            URL url = new URL(urlPath);
            uc = (HttpURLConnection) url.openConnection();
            uc.setDoOutput(true);
            reader = new BufferedReader(new InputStreamReader(
                    uc.getInputStream(), "utf-8"));// 设置编码,否则中文乱码;//读取服务器响应信息

            String line;
            while ((line = reader.readLine()) != null) {
                sbf.append(line);
                sbf.append("\r\n");
            }
            reader.close();
            uc.disconnect();
        } catch (Exception e) {
            sbf.append("服务器连接失败！请稍后重新操作");
            e.printStackTrace();
        } finally {
            closeIO(writer, reader); // 关闭流
        }
        jsonaddr = sbf.toString().trim();
        return jsonaddr;
    }

    /**
     * 关闭流
     */
    private static void closeIO(BufferedWriter writer, BufferedReader reader) {
        if (writer != null) {
            try {
                writer.close();
                writer = null;
            } catch (Exception e) {

            }
        }
        if (reader != null) {
            try {
                reader.close();
                reader = null;
            } catch (Exception e) {

            }
        }
    }

    public static double getDistance(double lat_a, double lng_a, double lat_b, double lng_b) {
        final double EARTH_RADIUS = 6378137.0;// 定义地球半径常数
        double radLat1 = (lat_a * Math.PI / 180.0);
        double radLat2 = (lat_b * Math.PI / 180.0);
        double a = radLat1 - radLat2;
        double b = (lng_a - lng_b) * Math.PI / 180.0;
        double s = 2 * Math.asin(Math.sqrt(Math.pow(Math.sin(a / 2), 2)
                + Math.cos(radLat1) * Math.cos(radLat2)
                * Math.pow(Math.sin(b / 2), 2)));
        s = s * EARTH_RADIUS;
        s = Math.round(s * 10000) / 10000;
        return s;
    }

    public static String getAddrFromJsonAddr(String jsonAddr) {
        String addr = null;
        JSONObject jsonAddrObject;
        try {
            jsonAddrObject = new JSONObject(jsonAddr);
            jsonAddrObject = new JSONObject(jsonAddrObject.getString("result"));
            addr = jsonAddrObject.getString("formatted_address");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return addr;
    }

    //百度经纬度和真实经纬度互换
    public static String getCityAddr(String jsonAddr) {
        String addr = null;
        JSONObject jsonAddrObject;
        try {
            jsonAddrObject = new JSONObject(jsonAddr);
            jsonAddrObject = new JSONObject(jsonAddrObject.getString("result"));
            addr = jsonAddrObject.getString("addressComponent");
            jsonAddrObject = new JSONObject(addr);
            addr = jsonAddrObject.getString("city");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return addr;
    }

    public static String getDecimels(double input) {
        DecimalFormat df = new DecimalFormat("0.00");
        return df.format(input);
    }

    //List<String> packages = checkInstalledPackage(MAP_PACKAGES);
    /**
     * 检查手机上是否安装了指定的软件
     *
     * @param packageNames 可变参数 String[]
     * @return 目标软件中已安装的列表
     */
    public static List<String> checkInstalledPackage(Context mContext, String... packageNames) {
        //获取packageManager
        final PackageManager packageManager = mContext.getPackageManager();
        //获取所有已安装程序的包信息
        List<PackageInfo> packageInfos = packageManager.getInstalledPackages(0);
        //用于存储
        List<String> newPackageNames = new ArrayList<>();
        int          count           = packageNames.length;
        if (packageInfos != null && packageInfos.size() > 0) {
            outermost:for (String packageName : packageNames) {
                for (int i = 0; i < packageInfos.size(); i++) {
                    String packageInfo = packageInfos.get(i).packageName;
                    if (packageInfo.contains(packageName)) {
                        newPackageNames.add(packageName);
                        if (newPackageNames.size() == count) {
                            break outermost;//这里使用了循环标记，跳出外层循环
                        }
                    }
                }
            }
        }
        //判断packageNames中是否有目标程序的包名，有TRUE，没有FALSE
        return newPackageNames;
    }

}
