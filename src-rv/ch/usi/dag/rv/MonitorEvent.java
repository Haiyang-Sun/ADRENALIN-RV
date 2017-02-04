package ch.usi.dag.rv;

import java.util.concurrent.atomic.AtomicLong;

import android.R.integer;
import android.nfc.INfcTag;
import ch.usi.dag.rv.utils.Runtime;

public abstract class MonitorEvent {
	
	protected long tid;
    protected int pid;
    protected String desc;
    protected long timestamp;
    //the bytecode filename where the event is generated
    protected String dexName;
    //the context belonging to
    protected int contextId;
    static AtomicLong eventGen = new AtomicLong(0);
    
    public MonitorEvent(String dexName, final String desc, int ctxId){
        this.timestamp = eventGen.incrementAndGet();//System.nanoTime();
        this.tid = Runtime.getThreadId();
        this.pid = Runtime.getPid();
        this.desc = desc;
        this.dexName = dexName;
        this.contextId = ctxId;
    }
    
    public long getTimestamp() {
		return timestamp;
	}
    public int getContextId() {
		return contextId;
	}
    public long getThreadId() {
		return tid;
	}
    //event can be divived into two catagories
    //1. need to be processed
    //2. no need to be processed, but only used for extra info
    public boolean needProcessing(){
        return true;
    }
    @Override
    public String toString(){
    	return desc+"(dex:"+dexName+" tid:"+tid+")";
    }
}
