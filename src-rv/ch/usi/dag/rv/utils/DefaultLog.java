package ch.usi.dag.rv.utils;

public class DefaultLog {
    static String TagPrefix = "DISL-";
    public static void v(final String tag, final String args){
    	if(AndroidRuntime.isJVM)
    		System.out.println(TagPrefix+tag+" "+AndroidRuntime.getPid() +"-"+ AndroidRuntime.getThreadId()+": " + args);
    	else
    		System.out.println(TagPrefix+tag+" "+AndroidRuntime.getPid() +"-"+ AndroidRuntime.getThreadId()+": " + args);
    }
    public static void d(final String tag, final String args){
    	if(AndroidRuntime.isJVM)
    		System.out.println(TagPrefix+tag+" "+AndroidRuntime.getPid() +"-"+ AndroidRuntime.getThreadId()+": " + args);
    	else
    		System.out.println(TagPrefix+tag+" "+AndroidRuntime.getPid() +"-"+ AndroidRuntime.getThreadId()+": " + args);
    }
    public static void e(final String tag, final String args){
    	if(AndroidRuntime.isJVM)
    		System.out.println(TagPrefix+tag+" "+AndroidRuntime.getPid() +"-"+ AndroidRuntime.getThreadId()+": " + args);
    	else
    		System.out.println(TagPrefix+tag+" "+AndroidRuntime.getPid() +"-"+ AndroidRuntime.getThreadId()+": " + args);
    }
}
