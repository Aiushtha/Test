package org.lxz.utils.log;

import com.orhanobut.logger.Logger;
import com.orhanobut.logger.Printer;

/**
 * Created by Lin on 2017/3/27.
 */

public class L {

    private static boolean isShow=true;
//    private static Printer printer;
    private static String tag="ashttp";

    public static void initSetShow(boolean hide){
        isShow=hide;
//        printer=Logger.t(1);
    }

//    public static Printer t(String tag) {
//        return isShow?Logger.t(tag):printer;
//    }
//
//    public static Printer t(int methodCount) {
//        return isShow?Logger.t(methodCount):printer;
//    }
//
//    public static Printer t(String tag, int methodCount) {
//        return isShow?Logger.t(tag, methodCount):printer;
//    }

    public static void log(int priority, String tag, String message, Throwable throwable) {
        if(isNotNull(tag)) if(isShow)Logger.log(priority, tag, message, throwable);
    }

    public static void d(String message, Object... args) {
        if(isNotNull(message))if(isShow)Logger.d(message, args);
    }

    public static void d(Object object) {
        if(isShow)Logger.d(object.toString());
    }

    public static void e(String message, Object... args) {
        if(isNotNull(message))if(isShow)Logger.e(null, message, args);
    }

    public static void e(Throwable throwable, String message, Object... args) {
        if(isNotNull(message)) if(isShow)Logger.e(throwable, message, args);
    }

    public static void i(String message, Object... args) {
        if(isNotNull(message))if(isShow)Logger.i(message, args);
    }

    public static void v(String message, Object... args) {
        if(isNotNull(message))if(isShow)Logger.v(message, args);
    }

    public static void w(String message, Object... args) {
        if(isNotNull(message))if(isShow)Logger.w(message, args);
    }

    public static void wtf(String message, Object... args) {
        if(isNotNull(message))if(isShow)Logger.wtf(message, args);
    }

    /**
     * Formats the json content and print it
     *
     * @param json the json content
     */
    public static void json(String json) {
        if(isNotNull(json))if(isShow)Logger.json(json);
    }

    /**
     * Formats the json content and print it
     *
     * @param xml the xml content
     */
    public static void xml(String xml) {
        if(isNotNull(xml))if(isShow)Logger.xml(xml);
    }

    public static boolean isNotNull(Object o){
        if(o==null){L.d("null");return false;}
        return true;
    }
}
