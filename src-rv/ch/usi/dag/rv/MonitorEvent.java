package ch.usi.dag.rv;

import java.util.concurrent.atomic.AtomicLong;

import ch.usi.dag.rv.utils.Runtime;

public abstract class MonitorEvent {
    protected String desc;
    static AtomicLong eventGen = new AtomicLong(0);
    public static String tag = "MonitorEvent";
    protected long timestamp;
    protected long tid;
    protected int pid;
    protected String dexName; 
    public MonitorEvent(String dexName, final String desc, boolean isThreadLocal){
        this.isThreadLocal = isThreadLocal;
        this.timestamp = eventGen.incrementAndGet();//System.nanoTime();
        this.tid = Runtime.getThreadId();
        this.pid = Runtime.getPid();
        this.desc = desc;
        this.dexName = dexName;
    }

    boolean isThreadLocal;
    
    public boolean isThreadLocal() {
		return isThreadLocal;
	}
    public boolean needProcess(){
        return true;
    }
    @Override
    public String toString(){
    	return desc+"("+dexName+")";
    }
	public boolean sameThread(MonitorEvent other) {
		return this.tid == other.tid;
	}
}
