package ch.usi.dag.rv.jni;

import java.util.HashMap;

import ch.usi.dag.rv.binder.BinderEvent;

public class RVNativeWrapper {
	static class KV<V> extends HashMap<Integer, V> {
		int id = 0;
		public int setData(V value){
			id++;
			this.put(id, value);
			return id;
		}
	}
	static HashMap<Integer, KV<byte[]>> fakeStore = new HashMap<Integer, RVNativeWrapper.KV<byte[]>>();
	
	
	private static HashMap<String, Integer> fakeFlags = new HashMap<String, Integer>();
	
	public static void setByBinderEvent(BinderEvent be, int flag){
		if(!isJVM)
			return;
		if(be.getType() == 3){
//			System.out.println(""+be.fromPid+":"+be.fromTid+":"+be.getTransactionId()+" has flag "+flag);
			fakeFlags.put(""+be.fromPid+":"+be.fromTid+":"+be.getTransactionId(), flag);
		}else {
			return;
		}
	}
	public static int getByBinderEvent(BinderEvent be){
		if(!isJVM)
			return be.flag;
		if(be.getType() == 5){
			String key = ""+be.fromPid+":"+be.fromTid+":"+be.getTransactionId();
			if(fakeFlags.containsKey(key))
				return fakeFlags.get(key);
			else
				return 0;
		}else {
			return be.flag;
		}
	}
	
	public static boolean isJVM = Boolean.getBoolean("disl.isjvm");
	
	public static byte[] getByFlag0(int flag){
		return RVNative.getByFlag0(flag);
	}
	
	public static byte[] getByFlag(int pid, int flag){
		if(isJVM) {
			//TODO: check why the following can happen 
			if(fakeStore.containsKey(pid) && fakeStore.get(pid) != null)
				return fakeStore.get(pid).get(flag);
			else
				return null;
		}else {
			return RVNative.getByFlag(pid, flag);
		}
	}
	
	public static int setData(byte[] value,int pid) {
		if(isJVM){
			if(!fakeStore.containsKey(pid)){
				fakeStore.put(pid, new KV<byte[]>());
			}
			int res = fakeStore.get(pid).setData(value);
//			System.out.println("flag "+res+" is set in "+pid);
			return res;
		}else{
			return RVNative.setData(value);
		}
	}
	
	//get the current flag for binder
	
	static HashMap<Integer, KV<Integer>> fakeBinderFlag = new HashMap<Integer, RVNativeWrapper.KV<Integer>>();
	
	public static int getBinderFlag(int pid, int tid) {
		if(isJVM){
			if(!fakeBinderFlag.containsKey(pid)){
				fakeBinderFlag.put(pid, new KV<Integer>());
				fakeBinderFlag.get(pid).put(tid, 0);
			}
			return fakeBinderFlag.get(pid).get(tid);
		}else{
			return RVNative.getBinderFlag();
		}
	}
	//set the flag for the next binder call in this thread
	public static void setBinderFlag(int pid, int tid, int flag) {
		if(isJVM){
			if(!fakeBinderFlag.containsKey(pid)){
				fakeBinderFlag.put(pid, new KV<Integer>());
			}
			fakeBinderFlag.get(pid).put(tid, flag);
		}else {
			RVNative.setBinderFlag(flag);
		}
		
	}
	//clear the flag so next binder won't have any flag
	public static void clearBinderFlag(int pid, int tid){
		if(isJVM){
			if(!fakeBinderFlag.containsKey(pid)){
				fakeBinderFlag.put(pid, new KV<Integer>());
			}
			fakeBinderFlag.get(pid).put(tid, 0);
		}else {
			RVNative.clearBinderFlag();
		}
	}
	public static HashMap<Integer, String> procs = new HashMap<Integer, String>();
	public static String getThisProcName(int pid){
		if(isJVM){
			return getProcName(pid);
		}else{
			return RVNative.getThisProcName();
		}
	}
	public static String getProcName(int pid){
		if(isJVM){
			return procs.get(pid);
		}else{
			return RVNative.getProcName(pid);
		}
	}
}
