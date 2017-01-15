package ch.usi.dag.rv.utils;

import android.util.Log;

public class DefaultLog {
    static String TagPrefix = "DISL-";
    public static void v(final String tag, final String args){
        Log.v(TagPrefix+tag, Runtime.getPid() +"-"+ Runtime.getThreadId()+": " + args);
    }
    public static void d(final String tag, final String args){
        Log.d(TagPrefix+tag, Runtime.getPid() +"-"+ Runtime.getThreadId()+": " + args);
    }
}
