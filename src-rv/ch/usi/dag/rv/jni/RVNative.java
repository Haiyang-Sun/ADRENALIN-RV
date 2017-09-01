package ch.usi.dag.rv.jni;

public class RVNative {
	public static native byte[] getByFlag0(int flag);
	public static native byte[] getByFlag(int pid, int flag);
	public static native int setData(byte[] value);
	
	//get the current flag for binder
	public static native int getBinderFlag();
	//set the flag for the next binder call in this thread
	public static native void setBinderFlag(int flag);
	//clear the flag so next binder won't have any flag
	public static native void clearBinderFlag();
	
	public static native String getThisProcName();
	public static native String getProcName(int pid);
	public static native int getTid();
	public static native int getPid();
}
