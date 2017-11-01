package me.militch.quick.h5panel.util;

import android.util.Log;

public class LogUtil {
    public static void d(Class c,String msg){
        Log.d(c.getSimpleName(),msg);
    }
    public static void d(Class c,String msg,Object... args){
        d(c,String.format(msg,args));
    }
    public static void i(Class c,String msg){
        Log.i(c.getSimpleName(),msg);
    }
    public static void i(Class c,String msg,Object... args){
        i(c,String.format(msg,args));
    }
    public static void w(Class c,String msg){
        Log.w(c.getSimpleName(),msg);
    }
    public static void w(Class c,String msg,Object... args){
        w(c,String.format(msg,args));
    }
    public static void e(Class c,String msg){
        Log.e(c.getSimpleName(),msg);
    }
    public static void e(Class c,String msg,Object... args){
        e(c,String.format(msg,args));
    }
}
