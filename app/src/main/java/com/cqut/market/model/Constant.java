package com.cqut.market.model;

import com.cqut.market.beans.Message;
import com.cqut.market.beans.Order;
import com.cqut.market.beans.User;

import java.util.ArrayList;
import java.util.List;

public class Constant {
    public static final String HOST_LOCAL = "http://192.168.43.245:8088/LiteMarket/";
    public static final String HOST_INTERNET = "https://3j4m729509.goho.co/LiteMarket/";
    public static final String HOST = HOST_INTERNET;
    public static final String QQ_KEY = "QcaiDn5ut1HzdJKL-A9UJR0EhZ__5_EN";
    public static final String QQ_URL = "mqqwpa://im/chat?chat_type=wpa&uin=763819849&version=1";
    public static final String IS_USER_LOADED = "isLoaded";//是否加载了用户信息
    public static final int REQUESTCODE_ORDER = 1000;
    public final static int MINE_ORDER = 0;//我的订单
    public final static int MINE_RECIVIE_GOOD_INFO = 1;//收货信息 提交
    public final static int MINE_RECIVIE_GOOD_EDIT_INFO = 5;//收货信息 修改
    public final static int MINE_PERSONAL_INFO = 2;//个人信息
    public final static int MINE_PROBLEM_CALLBACK = 3;//问题反馈
    public final static int MINE_CLEAR_MENMORY = 4;//清除缓存
    public final static int MINE_MESSAGE = 5;//清除缓存
    public final static int GET_MESSAGE_NEW = 1;
    public static final int GET_MESSAGE_ALL = 0;
    public final static int JOIN_US = 7;//加入我们

    public final static int MINE_ABOUT = 6;//关于我们
    public static final String HEADIMAGE_NAME = "headImage.jpg";
    public static final String HEADIMAGE_PATH = "headImagePath";
    public static final String PROBLEMIMAGE_NAME = "problem";
    public static final String PROBLEMIMAGE_PATH = "problemPath";
    public static final int GET_MESSAGE_NEW_ACTIVITY = 1000000;
    public static final String DELETE_ACCOUNT_SUCESS = "-1300";
    public static final String CONTENT_ERROR = "-1400";//用户名或者密码错误
    public static final String UPLOAD_FILE_SUCCESS = "-1500";
    public static final String UPLOAD_FILE_FAILED = "-1600";
    public static final String SEND_MESSAGE_FAILED = "-1700";
    public static final String SEND_MESSAGE_SUCCESS = "-1800";
    public static final String MESSAGE_NONE = "-1900";//没有消息
    public static final String VIBRATORABLE = "vibrateable";//允许震动
    public static final long LUNCH_TIME = 0;//启动时间
    public static final String BING_PIC = "bing_pic";
    public static final String DAY = "day";//日期 、判断是不是新的一天，
    public static List<Order> orders = new ArrayList<>();//缓存
    public static User user;//缓存
    public static String MY_MARKET_NAME = "MY_MARKET";//本地数据表名称
    public static String USER_ID = "oid";//用户id
    public static boolean NETWORK_INFO;
    public static String USER_NAME = "userName";
    public static String PASSWORD = "password";
    public static String PHONE_NUMBER = "phoneNumber";
    public static String QQ_NUMBER = "qqNumber";
    public static String ADDR = "addr";
    public static String EMAIL = "email";
    public static String NICK_NAME = "nickName";
    public static String LAST_LOGIN_TIME = "lastTime";//上次登录时间，超过几天失效
    public static long INVALID_TIME = 1000 * 60 * 60 * 24 * 1;//三天登录失效
    public static String LAST_GOOD_IMAGE_UPDATE_TIME = "imageUpdateTime";//一天
    public static final String NEW_MESSAGE_COUNT = "countOfNewMessage";
    public static long GOOD_IMAGE_UPDATE_TIME = 1000 * 60 * 60*24;//一分钟
    public static String CONNECT_FAILED = "100";//
    public static String USER_EXISTED = "-100";//
    public static String LOGIN_SUCCESS = "-200";
    public static String LOGIN_FAILED = "-300";
    public static String SIGNUP_SUCCESS = "-400";
    public static String COMMENT_SUCCESS = "-500";
    public static String COMMENT_FAILED = "-600";
    public static String USER_NOT_EXISTED = "-700";
    public static String ORDER_NOT_EXISTED = "-800";
    public static String ORDER_NONE = "-900";
    public static String ORDER_APPLY_SUCCESS = "-1000";
    public static String ORDER_APPLY_FAILED = "-1100";
    public static String UPDATE_SUCCESS = "-1200";
    public static final String ORDER_CANCEL_FAILED = "-2000";
    public static final String ORDER_CANCEL_SUCCESS ="-2100";
    public static final String CLEAR_MESSAGE_SUCCESS = "-2200";
    public static String LIKES_SUCCESS="-2300";

    public static String EXTERNAL_STORAGE;
    public static int ANIMATOR_IN = 1;
    public static int ANIMATOR_OUT = 0;

    public static List<Message> getLocalMessages() {
        List<Message> messages = new ArrayList<>();
        String jsonstr = FileUtil.getData("message");
        if (jsonstr != null && !jsonstr.equals("")) {
            String[] strings = jsonstr.split("\n");
            for (String s : strings)
                messages.add(JsonUtil.parseMessage(s));
        }
        return messages;
    }


}
