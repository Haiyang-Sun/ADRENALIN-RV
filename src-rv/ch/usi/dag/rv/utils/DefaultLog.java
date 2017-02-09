package ch.usi.dag.rv.utils;

public class DefaultLog {
    static String TagPrefix = "DISL-";
    public static void v(final String tag, final String args){
    	System.out.println(TagPrefix+tag+" "+Runtime.getPid() +"-"+ Runtime.getThreadId()+": " + args);
//        Log.v(TagPrefix+tag, Runtime.getPid() +"-"+ Runtime.getThreadId()+": " + args);
    }
    public static void d(final String tag, final String args){
    	System.out.println(TagPrefix+tag+" "+Runtime.getPid() +"-"+ Runtime.getThreadId()+": " + args);
//        Log.d(TagPrefix+tag, Runtime.getPid() +"-"+ Runtime.getThreadId()+": " + args);
    }
    public static void e(final String tag, final String args){
    	System.out.println(TagPrefix+tag+" "+Runtime.getPid() +"-"+ Runtime.getThreadId()+": " + args);
//        Log.e(TagPrefix+"Error-"+tag, Runtime.getPid() +"-"+ Runtime.getThreadId()+": " + args);
    }
}
