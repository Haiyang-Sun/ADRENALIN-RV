package ch.usi.dag.rv.utils;

public class Runtime {
	public static long getThreadId(){
        return Thread.currentThread ().getId ();
    }
    public static int getPid(){
    	return android.os.Process.myPid();
    }
}
