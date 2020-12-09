package com.zkml.meetingtablecard.constant;

/**
 * @author: zzh
 * data : 2020/7/27
 * description：IP配置
 */
public class IPConfig {

    //IP路径：内网和公网
    public static String IPCONFIG;
    //IP路径：API网关的路径地址
    public static String API_IPCONFIG;
    public static String LOGIN_IPCONFIG;
    public static String IP_PROJECT = "http://10.5.4.97:2004"; // 智慧后勤
    public static String IP_TEST = "http://10.5.4.35:2403";
    public static String IP_FORMAL = "http://app-gateway.luoex.cn:8088";
//    public static String IP_FORMAL_LOGIN = "http://app-gateway.luoex.cn:8088";
    //存放升级app xml存放路径
    public static String XMLURL;
    private static String PLUGIN_XMLURL;
    //IP路径：内网和公网
    public static String SERVER_URI;

    ///false:测试网。true：公网
    public static boolean setFlag(boolean flag) {
        if (flag) {//公网
            IPCONFIG = "http://app-gateway.luoex.cn:8088";//API网关地址
            LOGIN_IPCONFIG = "http://person.http.luoex.xin:6969";
            SERVER_URI = "tcp://imessage.broker.luoex.xin:20555"; // 生产环境, 消息订阅tcp接口地址
            return true;
        } else {//测试网
//            IPCONFIG = "http://10.5.4.49:4404";//个人平台-重构调试
//            IPCONFIG = "http://117.71.53.199:59038";
//            IPCONFIG = "http://172.16.8.22:9069"; // 本地 meeting
            IPCONFIG = "http://10.5.4.242:2004"; // 测试环境网关地址* http://10.5.4.35:2403  http://10.5.4.97:2004 http://10.5.4.242:2004
            SERVER_URI = "tcp://10.5.4.27:20555"; // 生产环境, 消息订阅tcp接口地址
            return false;
        }
    }
}
