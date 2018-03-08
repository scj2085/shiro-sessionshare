package com.gome.meidian.account.shiroimagecode1;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

/**
 * Created by dinggang on 2016/7/13.
 */
public class MeiproEnvironment {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final static String CLIENT = "client";
    private final static String TRACE_ID = "trace_id";
    private final static String PHONE_TYPE = "phoneType";
    private final static String DEV_ID = "devId";
    private final static String IP_ADDRESS = "remoteAddr";
    private final static String APP_VERSION = "appVersion";
    private final static String USER_ID = "user_id";

    public static void put(String key, String val) {
        MDC.put(key, val);
    }

    public static void putTraceId(String value) {
        MDC.put(TRACE_ID, value);
    }

    public static void putClient(String value) {
        MDC.put(CLIENT, value);
    }

    public static void putPhoneType(String value) {
        MDC.put(PHONE_TYPE, value);
    }

    public static void putDevId(String value) {
        MDC.put(DEV_ID, value);
    }

    public static void putRemoteAddr(String value) {
        MDC.put(IP_ADDRESS, value);
    }

    public static void putAppVersion(String value) {
        MDC.put(APP_VERSION, value);
    }

    public static void putUserId(String value) {
        MDC.put(USER_ID, value);
    }

    /**
     *
     * @return
     */
    public static String getRemoteAddr() {
        return MDC.get(IP_ADDRESS);
    }

    /**
     *
     * @return
     */
    public static String getTraceId() {
        return MDC.get(CLIENT);
    }

    /**
     *
     * @return
     */
    public static String getClient() {
        return MDC.get(CLIENT);
    }

    /**
     *
     * @return
     */
    public static String getPhoneType() {
        return MDC.get(CLIENT);
    }

    /**
     *
     * @return
     */
    public static String getDevId() {
        return MDC.get(DEV_ID);
    }

    /**
     *
     * @return
     */
    public static String getAppVersion() {
        return MDC.get(APP_VERSION);
    }

    public static String getUserId() {
        return MDC.get(USER_ID);
    }
}
