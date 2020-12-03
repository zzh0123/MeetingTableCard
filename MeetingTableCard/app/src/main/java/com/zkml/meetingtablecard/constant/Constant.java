package com.zkml.meetingtablecard.constant;


/**
 * @author: zzh
 * data : 2020/7/28
 * description：常量类
 */
public class Constant {
    public static String APP_PACKAGENAME;///APP的包名
    ///自定义的分钟显示
    public static final String[] minuts = new String[]{"00", "10", "20", "30", "40", "50"};
    /**
     * //true 正式环境  false 测试环境
     */
    public static final boolean FLAG = false;

    /**
     * TOKEN
     */
    public static String TOKEN = "";
    //版本更新
    public static final String APPNAME = "MeettingRoom.apk";
    public static final int UPDATA_CLIENT = 0;
    public static final int GET_UNDATAINFO_ERROR = 1;
    public static final int DOWN_ERROR = 2;
    public static final int NEW_VERSION = 3;

    // 版本更新
    public static final String GET_UPDATE = IPConfig.IPCONFIG + "/upgradeConfig/findLatest";
    //    public static final String GET_UPDATE = IPConfig.IPCONFIG + "/meeting_v2/ZKML_JG_MEETING_AH/meeting-test" + "/upgradeConfig/findLatest";
    // 小屏会议室列表
    public static final String GET_MEETING_INFO_SMALL = IPConfig.IPCONFIG + "/terminalConfig/hangingScreenList/";
    //    public static final String GET_MEETING_INFO_SMALL = IPConfig.IPCONFIG + "/meeting_v2/ZKML_JG_MEETING_AH/meeting-test" + "/terminalConfig/hangingScreenList/";
    // 大屏会议室列表
    public static final String GET_MEETING_INFO_BIG = IPConfig.IPCONFIG + "/terminalConfig/bigScreenList/";
    //    public static final String GET_MEETING_INFO_BIG = IPConfig.IPCONFIG + "/meeting_v2/ZKML_JG_MEETING_AH/meeting-test" + "/terminalConfig/bigScreenList/";
    //    public static final String GET_MEETING_INFO_BIG = IPConfig.IPCONFIG + "/meeting_v2_saas_online/ZKML_JG_MEETING_AH/meeting" + "/terminalConfig/bigScreenList/";
    // 个人登录的URL
//    public static final String LOGINURL = IPConfig.IPCONFIG + "/mobileTerminal/loginV2"; // http://117.71.53.199:59038"
    public static final String LOGINURL = IPConfig.LOGIN_IPCONFIG + "/mobileTerminal/loginV2";
    // 智慧后勤登录接口
    public static final String LOGINURL_PROJECT = IPConfig.IPCONFIG + "/mlyun-auth/server/auth/login";

    //网络请求中是否经过网关唯一标识（经过网关接口必须加此后缀，此值越复杂越好，仅用来app本地做逻辑判断使用）
//    public static final String GATEWAY = "APPGATEWAYIDENTIFICATION";
//    //
//    public static final String MEETING_IPCONFIG = IPConfig.IPCONFIG + "/" + GATEWAY + "/" + GatewayStingUtils.MEETINGGatewayPrefix + "/" + GATEWAY;


    // 验证手机号
    public static final String VALID_PHONE = IPConfig.IPCONFIG + "/meeting-xw" + "/elecTable/validPhone";
    // 发送短信验证码
    public static final String GET_MSG_CODE = IPConfig.IPCONFIG + "/meeting-xw" + "/elecTable/sendAuthCode";
    // 登录接口
    public static final String LOGIN = IPConfig.IPCONFIG + "/meeting-xw" + "/elecTable/login";
    // 获取当前登录人会议信息列表
    public static final String GET_MEETING_LIST = IPConfig.IPCONFIG + "/meeting-xw" + "/elecTable/getMeetingApplyList";
}