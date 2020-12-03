package com.zkml.meetingtablecard.utils.cache;

import android.text.TextUtils;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * 字符串操作工具包
 *
 * @author liux (http://my.oschina.net/liux)
 * @version 1.0
 * @created 2012-3-21
 */
public class StringUtils {
    private final static Pattern emailer = Pattern
            .compile("\\w+([-+.]\\w+)*@\\w+([-.]\\w+)*\\.\\w+([-.]\\w+)*");

    private final static ThreadLocal<SimpleDateFormat> dateFormater = new ThreadLocal<SimpleDateFormat>() {
        @Override
        protected SimpleDateFormat initialValue() {
            return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        }
    };

    private final static ThreadLocal<SimpleDateFormat> dateFormater2 = new ThreadLocal<SimpleDateFormat>() {
        @Override
        protected SimpleDateFormat initialValue() {
            return new SimpleDateFormat("yyyy-MM-dd");
        }
    };

    //饿汉式单例 节省内存
    public static Gson getInstance() {
        Gson gson = null;
        if (gson == null) {
            synchronized (Gson.class) {
                if (gson == null) {
                    gson = new Gson();
                }
            }
        }
        return gson;
    }

    /**
     * 将字符串转位日期类型
     *
     * @param sdate
     * @return
     */
    public static Date toDate(String sdate) {
        try {
            return dateFormater.get().parse(sdate);
        } catch (ParseException e) {
            return null;
        }
    }

    /**
     * 以友好的方式显示时间
     *
     * @param sdate
     * @return
     */
    public static String friendly_time(String sdate) {

        Date time = toDate(sdate);

        if (time == null) {
            return "Unknown";
        }
        String ftime = "";
        Calendar cal   = Calendar.getInstance();

        // 判断是否是同一天
        String curDate   = dateFormater2.get().format(cal.getTime());
        String paramDate = dateFormater2.get().format(time);
        if (curDate.equals(paramDate)) {
            int hour = (int) ((cal.getTimeInMillis() - time.getTime()) / 3600000);
            if (hour == 0)
                ftime = Math.max(
                        (cal.getTimeInMillis() - time.getTime()) / 60000, 1)
                        + "分钟前";
            else
                ftime = hour + "小时前";
            return ftime;
        }

        long lt = time.getTime() / 86400000;
        long ct = cal.getTimeInMillis() / 86400000;
        int days = (int) (ct - lt);
        if (days == 0) {
            int hour = (int) ((cal.getTimeInMillis() - time.getTime()) / 3600000);
            if (hour == 0)
                ftime = Math.max(
                        (cal.getTimeInMillis() - time.getTime()) / 60000, 1)
                        + "分钟前";
            else
                ftime = hour + "小时前";
        } else if (days == 1) {
            ftime = "昨天";
        } else if (days == 2) {
            ftime = "前天";
        } else if (days > 2 && days < 31) {
            ftime = days + "天前";
        } else if (days >= 31 && days <= 2 * 31) {
            ftime = "一个月前";
        } else if (days > 2 * 31 && days <= 3 * 31) {
            ftime = "2个月前";
        } else if (days > 3 * 31 && days <= 4 * 31) {
            ftime = "3个月前";
        } else {
            ftime = dateFormater2.get().format(time);
        }
        return ftime;
    }

    /**
     * 以友好的方式显示时间
     *
     * @param date
     * @return
     */
    public static String friendly_time(Date date) {
        SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return friendly_time(f.format(date));
    }

    /**
     * 判断给定字符串时间是否为今日
     *
     * @param sdate
     * @return boolean
     */
    public static boolean isToday(String sdate) {
        boolean b     = false;
        Date time  = toDate(sdate);
        Date today = new Date();
        if (time != null) {
            String nowDate  = dateFormater2.get().format(today);
            String timeDate = dateFormater2.get().format(time);
            if (nowDate.equals(timeDate)) {
                b = true;
            }
        }
        return b;
    }

    /**
     * 返回long类型的今天的日期
     *
     * @return
     */
    public static long getToday() {
        Calendar cal     = Calendar.getInstance();
        String curDate = dateFormater2.get().format(cal.getTime());
        curDate = curDate.replace("-", "");
        return Long.parseLong(curDate);
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
     * 判断是不是一个合法的电子邮件地址
     *
     * @param email
     * @return
     */
    public static boolean isEmail(String email) {
        if (email == null || email.trim().length() == 0)
            return false;
        return emailer.matcher(email).matches();
    }

    /**
     * 字符串转整数
     *
     * @param str
     * @param defValue
     * @return
     */
    public static int toInt(String str, int defValue) {
        try {
            return Integer.parseInt(str);
        } catch (Exception e) {
        }
        return defValue;
    }

    /**
     * 对象转整数
     *
     * @param obj
     * @return 转换异常返回 0
     */
    public static int toInt(Object obj) {
        if (obj == null)
            return 0;
        return toInt(obj.toString(), 0);
    }

    /**
     * 对象转整数
     *
     * @param obj
     * @return 转换异常返回 0
     */
    public static long toLong(String obj) {
        try {
            return Long.parseLong(obj);
        } catch (Exception e) {
        }
        return 0;
    }

    /**
     * 字符串转布尔值
     *
     * @param b
     * @return 转换异常返回 false
     */
    public static boolean toBool(String b) {
        try {
            return Boolean.parseBoolean(b);
        } catch (Exception e) {
        }
        return false;
    }

    /**
     * 将一个InputStream流转换成字符串
     *
     * @param is
     * @return
     */
    public static String toConvertString(InputStream is) {
        StringBuffer res  = new StringBuffer();
        InputStreamReader isr  = new InputStreamReader(is);
        BufferedReader read = new BufferedReader(isr);
        try {
            String line;
            line = read.readLine();
            while (line != null) {
                res.append(line);
                line = read.readLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (null != isr) {
                    isr.close();
                    isr.close();
                }
                if (null != read) {
                    read.close();
                    read = null;
                }
                if (null != is) {
                    is.close();
                    is = null;
                }
            } catch (IOException e) {
            }
        }
        return res.toString();
    }
    public static Map<String, Object> transJsonToLinkedHashMap(String jsonStr) {
        try {
            if (null != jsonStr && !"".equals(jsonStr) && !"{}".equals(jsonStr) && !"null".equals(jsonStr)) {
                JSONObject jsonObject = new JSONObject(jsonStr);
                Iterator<String> keyItems   = jsonObject.keys();
                Map<String, Object> map        = new LinkedHashMap<>();
                String key, value;
                while (keyItems.hasNext()) {
                    key = keyItems.next();
                    value = jsonObject.getString(key);
                    map.put(key, value);
                }
                return map;
            }
        } catch (JSONException e) {
            try {
                Map<String, Object> map = new LinkedHashMap<>();
                map.put("list", jsonStr);
                return map;
            } catch (Exception e2) {
                e2.printStackTrace();
            }
            return null;
        }
        return null;
    }

    public static Map<String, Object> transJsonToMap(String jsonStr) {
        try {
            if (null != jsonStr && !"".equals(jsonStr) && !"{}".equals(jsonStr) && !"null".equals(jsonStr)) {
                JSONObject jsonObject = new JSONObject(jsonStr);
                Iterator<String> keyItems   = jsonObject.keys();
                Map<String, Object> map        = new HashMap<>();
                String key, value;
                while (keyItems.hasNext()) {
                    key = keyItems.next();
                    value = jsonObject.getString(key);
                    map.put(key, value);
                }
                return map;
            }
        } catch (JSONException e) {
            try {
                Map<String, Object> map = new HashMap<>();
                map.put("list", jsonStr);
                return map;
            } catch (Exception e2) {
                e2.printStackTrace();
            }
            return null;
        }
        return null;
    }
    public static Map<String, Object> transResultJsonToMap(String jsonStr) {
        try {
            if (null != jsonStr && !"".equals(jsonStr) && !"{}".equals(jsonStr) && !"null".equals(jsonStr)) {
                JSONObject jsonObject = new JSONObject(jsonStr);
                Iterator<String> keyItems   = jsonObject.keys();
                Map<String, Object> map        = new HashMap<>();
                String key, value;
                while (keyItems.hasNext()) {
                    key = keyItems.next();
                    value = jsonObject.getString(key);
                    map.put(key, value);
                }
                return map;
            }
        } catch (JSONException e) {
            try {
                Map<String, Object> map = new HashMap<>();
                map.put("randomCode", jsonStr);//返回json是乱码
                return map;
            } catch (Exception e2) {
                e2.printStackTrace();
            }
            return null;
        }
        return null;
    }
    //转换成有序map
    public static Map<String, Object> transJsonToTreeMap(String jsonStr) {
        try {
            if (null != jsonStr && !"".equals(jsonStr) && !"{}".equals(jsonStr) && !"null".equals(jsonStr)) {
                JSONObject jsonObject = new JSONObject(jsonStr);
                Iterator<String> keyItems   = jsonObject.keys();
                Map<String, Object> map        = new TreeMap<>();
                String key, value;
                while (keyItems.hasNext()) {
                    key = keyItems.next();
                    value = jsonObject.getString(key);
                    map.put(key, value);
                }
                return map;
            }
        } catch (JSONException e) {
            try {
                Map<String, Object> map = new TreeMap<>();
                map.put("list", jsonStr);
                return map;
            } catch (Exception e2) {
                e2.printStackTrace();
            }
            return null;
        }
        return null;
    }

    public static Object convertMapToList(String reqResultStr, TypeToken typeToken) {
        Gson gson = getInstance();
        Type type = typeToken.getType();
        if (null == reqResultStr || "".equals(reqResultStr) || "{}".equals(reqResultStr)) {
            return null;
        } else {
            return gson.fromJson(reqResultStr, type);
        }
    }

    //保存用户信息到sp
    private static void saveUserMessageToSp(Map<String, String> map, Map<String, Object> modelMap) {
        if (modelMap.get("loginBusinessUserDTO") != null) {
             String loginBusinessUserDTO    = (String) modelMap.get("loginBusinessUserDTO");
             Map<String, Object> loginBusinessUserDTOMap = StringUtils.transJsonToMap(loginBusinessUserDTO);

            if (loginBusinessUserDTOMap.get("roleType") != null) {
                map.put("role_type", loginBusinessUserDTOMap.get("roleType").toString());
            } else {
                map.put("role_type", "");
            }
            if (loginBusinessUserDTOMap.get("isDriver") != null) {
                map.put("isdriver", loginBusinessUserDTOMap.get("isDriver").toString());
            } else {
                map.put("isdriver", "");
            }
            if (loginBusinessUserDTOMap.get("belongSource") != null) {
                map.put("belongSource", loginBusinessUserDTOMap.get("belongSource").toString());
            } else {
                map.put("belongSource", "");
            }
            if (loginBusinessUserDTOMap.get("isCanFuPin") != null) {
                map.put("isCanFuPin", loginBusinessUserDTOMap.get("isCanFuPin").toString());
            } else {
                map.put("isCanFuPin", "NO");
            }
            if (loginBusinessUserDTOMap.get("isHaveCarCompile") != null) {
                map.put("isHaveCarCompile", loginBusinessUserDTOMap.get("isHaveCarCompile").toString());
            } else {
                map.put("isHaveCarCompile", "NO");
            }
            if (loginBusinessUserDTOMap.get("isShowTrainingColumn") != null) {
                if (!StringUtils.isStrEmpty(loginBusinessUserDTOMap.get("isShowTrainingColumn").toString())) {
                    map.put("isShowTrainingColumn", loginBusinessUserDTOMap.get("isShowTrainingColumn").toString());
                } else {
                    map.put("isShowTrainingColumn", "NO");
                }
            } else {
                map.put("isShowTrainingColumn", "NO");
            }
            if (loginBusinessUserDTOMap.get("isHasPatrolCheckCar") != null) {
                map.put("isHasPatrolCheckCar", loginBusinessUserDTOMap.get("isHasPatrolCheckCar").toString());
            } else {
                map.put("isHasPatrolCheckCar", "NO");
            }
            if (loginBusinessUserDTOMap.get("isLawDriver") != null) {
                map.put("isLawDriver", loginBusinessUserDTOMap.get("isLawDriver").toString());
            } else {
                map.put("isLawDriver", "NO");
            }
            if (loginBusinessUserDTOMap.get("isDiaodu") != null) {
                map.put("isdiaodu", loginBusinessUserDTOMap.get("isDiaodu").toString());
            } else {
                map.put("isdiaodu", "");
            }
            if (loginBusinessUserDTOMap.get("isMajor") != null) {
                map.put("ismajor", loginBusinessUserDTOMap.get("isMajor").toString());
            } else {
                map.put("ismajor", "");
            }
            if (loginBusinessUserDTOMap.get("isCheck") != null) {
                map.put("ischeck", loginBusinessUserDTOMap.get("isCheck").toString());
            } else {
                map.put("ischeck", "");
            }
            if (loginBusinessUserDTOMap.get("isInputWatch") != null) {
                map.put("isinputwatch", loginBusinessUserDTOMap.get("isInputWatch").toString());
            } else {
                map.put("isinputwatch", "");
            }
            if (loginBusinessUserDTOMap.get("organNo") != null) {
                map.put("orgnano", loginBusinessUserDTOMap.get("organNo").toString());
            } else {
                map.put("orgnano", "");
            }
            if (loginBusinessUserDTOMap.get("organId") != null) {
                map.put("organid", loginBusinessUserDTOMap.get("organId").toString());
            } else {
                map.put("organid", "");
            }
            if (loginBusinessUserDTOMap.get("organName") != null) {
                map.put("orgnaname", loginBusinessUserDTOMap.get("organName").toString());
            } else {
                map.put("orgnaname", "");
            }
            if (loginBusinessUserDTOMap.get("isDriverTour") != null) {
                map.put("isDriverTour", loginBusinessUserDTOMap.get("isDriverTour").toString());
            } else {
                map.put("isDriverTour", "");
            }
            if (loginBusinessUserDTOMap.get("phoneMap") != null) {
                map.put("phoneMap", loginBusinessUserDTOMap.get("phoneMap").toString());
            } else {
                map.put("phoneMap", "");
            }
            if (loginBusinessUserDTOMap.get("deptId") != null) {
                if (!TextUtils.isEmpty(loginBusinessUserDTOMap.get("deptId").toString()) && !TextUtils.equals("null", loginBusinessUserDTOMap.get("deptId").toString())) {
                    map.put("deptId", loginBusinessUserDTOMap.get("deptId").toString());
                } else {
                    map.put("deptId", "");
                }
            } else {
                map.put("deptId", "");
            }
            if (loginBusinessUserDTOMap.get("deptName") != null) {
                if (!TextUtils.isEmpty(loginBusinessUserDTOMap.get("deptName").toString()) && !TextUtils.equals("null", loginBusinessUserDTOMap.get("deptName").toString())) {
                    map.put("deptName", loginBusinessUserDTOMap.get("deptName").toString());
                } else {
                    map.put("deptName", "");
                }
            } else {
                map.put("deptName", "");
            }
            if (loginBusinessUserDTOMap.get("userId") != null) {
                if (!TextUtils.isEmpty(loginBusinessUserDTOMap.get("userId").toString()) && !TextUtils.equals("null", loginBusinessUserDTOMap.get("userId").toString())) {
                    map.put("applyUserId", loginBusinessUserDTOMap.get("userId").toString());
                } else {
                    map.put("applyUserId", "");
                }
            } else {
                map.put("applyUserId", "");
            }
            if (!TextUtils.isEmpty(loginBusinessUserDTOMap.get("userRealName").toString()) && !TextUtils.equals("null", loginBusinessUserDTOMap.get("userRealName").toString())) {
                map.put("applyUserRealName", loginBusinessUserDTOMap.get("userRealName").toString());
            } else {
                map.put("applyUserRealName", "");
            }
            if (!TextUtils.isEmpty(loginBusinessUserDTOMap.get("userPhone").toString()) && !TextUtils.equals("null", loginBusinessUserDTOMap.get("userPhone").toString())) {

                map.put("applyUserPhone", loginBusinessUserDTOMap.get("userPhone").toString());
            } else {
                map.put("applyUserPhone", "");
            }
            if (loginBusinessUserDTOMap.get("jobNo") != null) {
                if (!TextUtils.isEmpty(loginBusinessUserDTOMap.get("jobNo").toString()) && !TextUtils.equals("null", loginBusinessUserDTOMap.get("jobNo").toString())) {
                    map.put("jobNo", loginBusinessUserDTOMap.get("jobNo").toString());
                } else {
                    map.put("jobNo", "");
                }
            } else {
                map.put("jobNo", "");
            }
            if (loginBusinessUserDTOMap.get("duty") != null) {
                if (!TextUtils.isEmpty(loginBusinessUserDTOMap.get("duty").toString()) && !TextUtils.equals("null", loginBusinessUserDTOMap.get("duty").toString())) {
                    map.put("duty", loginBusinessUserDTOMap.get("duty").toString());
                } else {
                    map.put("duty", "");
                }
            } else {
                map.put("duty", "");
            }
            if (loginBusinessUserDTOMap.get("isOilCheck") != null) {
                if (!StringUtils.isStrEmpty(loginBusinessUserDTOMap.get("isOilCheck").toString())) {
                    map.put("isOilCheck", loginBusinessUserDTOMap.get("isOilCheck").toString());
                } else {
                    map.put("isOilCheck", "NO");
                }
            } else {
                map.put("isOilCheck", "NO");
            }
            if (loginBusinessUserDTOMap.get("isShowAttendance") != null) {
                if (!StringUtils.isStrEmpty(loginBusinessUserDTOMap.get("isShowAttendance").toString())) {
                    map.put("isShowAttendance", loginBusinessUserDTOMap.get("isShowAttendance").toString());
                } else {
                    map.put("isShowAttendance", "NO");
                }
            } else {
                map.put("isShowAttendance", "NO");
            }
            if (loginBusinessUserDTOMap.get("oneKeySendCarPermission") != null) {
                if (!StringUtils.isStrEmpty(loginBusinessUserDTOMap.get("oneKeySendCarPermission").toString())) {
                    map.put("oneKeySendCarPermission", loginBusinessUserDTOMap.get("oneKeySendCarPermission").toString());
                } else {
                    map.put("oneKeySendCarPermission", "NO");
                }
            } else {
                map.put("oneKeySendCarPermission", "NO");
            }
            if (loginBusinessUserDTOMap.get("holidaysCarReportPermission") != null) {
                if (!StringUtils.isStrEmpty(loginBusinessUserDTOMap.get("holidaysCarReportPermission").toString())) {
                    map.put("holidaysCarReportPermission", loginBusinessUserDTOMap.get("holidaysCarReportPermission").toString());
                } else {
                    map.put("holidaysCarReportPermission", "NO");
                }
            } else {
                map.put("holidaysCarReportPermission", "NO");
            }
            //维保 车管员权限
            if (loginBusinessUserDTOMap.get("maintenanceCarManager") != null) {
                if (!StringUtils.isStrEmpty(loginBusinessUserDTOMap.get("maintenanceCarManager").toString())) {
                    map.put("maintenanceCarManager", loginBusinessUserDTOMap.get("maintenanceCarManager").toString());
                } else {
                    map.put("maintenanceCarManager", "NO");
                }
            } else {
                map.put("maintenanceCarManager", "NO");
            }
            //维保 审核员权限
            if (loginBusinessUserDTOMap.get("maintenanceAuditor") != null) {
                if (!StringUtils.isStrEmpty(loginBusinessUserDTOMap.get("maintenanceAuditor").toString())) {
                    map.put("maintenanceAuditor", loginBusinessUserDTOMap.get("maintenanceAuditor").toString());
                } else {
                    map.put("maintenanceAuditor", "NO");
                }
            } else {
                map.put("maintenanceAuditor", "NO");
            }
            //维保 签批员权限
            if (loginBusinessUserDTOMap.get("maintenanceApprover") != null) {
                if (!StringUtils.isStrEmpty(loginBusinessUserDTOMap.get("maintenanceApprover").toString())) {
                    map.put("maintenanceApprover", loginBusinessUserDTOMap.get("maintenanceApprover").toString());
                } else {
                    map.put("maintenanceApprover", "NO");
                }
            } else {
                map.put("maintenanceApprover", "NO");
            }
            //地区Id
            if (loginBusinessUserDTOMap.get("areaId") != null) {
                if (!StringUtils.isStrEmpty(loginBusinessUserDTOMap.get("areaId").toString())) {
                    map.put("areaId", loginBusinessUserDTOMap.get("areaId").toString());
                } else {
                    map.put("areaId", "");
                }
            } else {
                map.put("areaId", "");
            }
            //维保权限oneKeyRepairAndMaintainSwitch
            if (loginBusinessUserDTOMap.get("oneKeyRepairAndMaintainSwitch") != null) {
                if (!StringUtils.isStrEmpty(loginBusinessUserDTOMap.get("oneKeyRepairAndMaintainSwitch").toString())) {
                    map.put("oneKeyRepairAndMaintainSwitch", loginBusinessUserDTOMap.get("oneKeyRepairAndMaintainSwitch").toString());
                } else {
                    map.put("oneKeyRepairAndMaintainSwitch", "");
                }
            } else {
                map.put("oneKeyRepairAndMaintainSwitch", "");
            }
            //通勤班车权限
            if (loginBusinessUserDTOMap.get("showCommuterBusSwitch") != null) {
                if (!StringUtils.isStrEmpty(loginBusinessUserDTOMap.get("showCommuterBusSwitch").toString())) {
                    map.put("showCommuterBusSwitch", loginBusinessUserDTOMap.get("showCommuterBusSwitch").toString());
                } else {
                    map.put("showCommuterBusSwitch", "NO");
                }
            } else {
                map.put("showCommuterBusSwitch", "NO");
            }
            //一键出行开关
            if (loginBusinessUserDTOMap.get("oneKeyTripOutSwitch") != null) {
                if (!StringUtils.isStrEmpty(loginBusinessUserDTOMap.get("oneKeyTripOutSwitch").toString())) {
                    map.put("oneKeyTripOutSwitch", loginBusinessUserDTOMap.get("oneKeyTripOutSwitch").toString());
                } else {
                    map.put("oneKeyTripOutSwitch", "NO");
                }
            } else {
                map.put("oneKeyTripOutSwitch", "NO");
            }
            //会议服务开关
            if (loginBusinessUserDTOMap.get("enableAppMeetingFeature") != null) {
                if (!StringUtils.isStrEmpty(loginBusinessUserDTOMap.get("enableAppMeetingFeature").toString())) {
                    map.put("enableAppMeetingFeature", loginBusinessUserDTOMap.get("enableAppMeetingFeature").toString());
                } else {
                    map.put("enableAppMeetingFeature", "NO");
                }
            } else {
                map.put("enableAppMeetingFeature", "NO");
            }
            //个人出行开关
            if (loginBusinessUserDTOMap.get("isHidesPersonalTravelFunctions") != null) {
                if (!StringUtils.isStrEmpty(loginBusinessUserDTOMap.get("isHidesPersonalTravelFunctions").toString())) {
                    map.put("isHidesPersonalTravelFunctions", loginBusinessUserDTOMap.get("isHidesPersonalTravelFunctions").toString());
                } else {
                    map.put("isHidesPersonalTravelFunctions", "YES");
                }
            } else {
                map.put("isHidesPersonalTravelFunctions", "YES");
            }

            //培训文案
            if (loginBusinessUserDTOMap.get("appTrainingModuleName") != null) {
                if (!StringUtils.isStrEmpty(loginBusinessUserDTOMap.get("appTrainingModuleName").toString())) {
                    map.put("appTrainingModuleName", loginBusinessUserDTOMap.get("appTrainingModuleName").toString());
                } else {
                    map.put("appTrainingModuleName", "");
                }
            } else {
                map.put("appTrainingModuleName", "");
            }
            //驾驶员报销审核权限
            if (loginBusinessUserDTOMap.get("reimbursementChecker") != null) {
                if (!StringUtils.isStrEmpty(loginBusinessUserDTOMap.get("reimbursementChecker").toString())) {
                    map.put("isReimbursementCheck", loginBusinessUserDTOMap.get("reimbursementChecker").toString());
                } else {
                    map.put("isReimbursementCheck", "NO");
                }
            } else {
                map.put("isReimbursementCheck", "NO");
            }
            //调度审核权限
            if (loginBusinessUserDTOMap.get("isDispatchOrderCheck") != null) {
                if (!StringUtils.isStrEmpty(loginBusinessUserDTOMap.get("isDispatchOrderCheck").toString())) {
                    map.put("isDispatchOrderCheck", loginBusinessUserDTOMap.get("isDispatchOrderCheck").toString());
                } else {
                    map.put("isDispatchOrderCheck", "NO");
                }
            } else {
                map.put("isDispatchOrderCheck", "NO");
            }

        }
    }


    /**
     * 大陆手机号码11位数，匹配格式：前三位固定格式+后8位任意数
     * 此方法中前三位格式有：
     * 13+任意数
     * 15+除4的任意数
     * 18+除1和4的任意数
     * 17+除9的任意数
     * 147
     */
    public static boolean isChinaPhoneLegal(String str) {
        String regExp = null;
        try {
//            regExp = "^((13[0-9])|(15[^4])|(18[0,2,3,5-9])|(17[0-8])|(147))\\d{8}$";
            regExp = "^1\\d{10}$";
            Pattern p = Pattern.compile(regExp);
            Matcher m = p.matcher(str);
            return m.matches();
        } catch (Exception e) {
            //e.printStackTrace();
            return false;
        }
    }

    /**
     * 判断字符串是否为空
     */
    public static boolean isStrEmpty(String str) {
        if (!TextUtils.isEmpty(str) && !TextUtils.equals("null", str)) {
            return false;
        } else {
            return true;
        }
    }

    /**
     * JSON对象转java对象
     * @param jSonObjectStr
     * @param tClass
     * @return
     */
    public static <T> T convertJSonObjectStrToObject(String jSonObjectStr, Class<T> tClass) {
        Gson gson = getInstance();
        return gson.fromJson(jSonObjectStr, tClass);
    }

    /**
     * 是否是座机号格式
     * @param str
     * @return
     */
    public static boolean isTelLegal(String str) {
        String regExp = null;
        try {
            //            regExp = "^((13[0-9])|(15[^4])|(18[0,2,3,5-9])|(17[0-8])|(147))\\d{8}$";
            regExp = "0\\d{2,3}-\\d{7,8}";
            Pattern p = Pattern.compile(regExp);
            Matcher m = p.matcher(str);
            return m.matches();
        } catch (Exception e) {
            //e.printStackTrace();
            return false;
        }
    }

    /**
     * 判断字符串是否为某一状态
     * @param str  要比较的字符串
     * @param type  要比较的类型
     * @return
     */
    public static boolean getTypeState(String str, String type) {
        return !StringUtils.isStrEmpty(str) && TextUtils.equals(str, type);
    }
}