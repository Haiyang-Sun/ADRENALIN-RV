package ch.usi.dag.rv;

import java.io.Externalizable;
import java.io.IOException;
import java.io.NotSerializableException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.io.Serializable;
import java.util.concurrent.atomic.AtomicLong;

import ch.usi.dag.rv.binder.BinderEvent;
import ch.usi.dag.rv.utils.AndroidRuntime;

/*
 * All the events in this system 
 * Should be serializable for passing among processes
 */
public class MonitorEvent implements Externalizable {
	private static final long serialVersionUID = -8388246696418315226L;
	public long tid;
    public int pid;
    public String pname;
    public String name;
    protected long timestamp;
    //the bytecode filename where the event is generated
    protected String staticInfo;
    //the context belonging to
    protected Object contextObj;
    public Serializable[] dynamicInfo;
    public String processingName;
    
    static AtomicLong eventGen = new AtomicLong(0);
    public static MonitorEvent newTLEvent(String processingName, final String eventName, String staticInfo, Serializable... dynamicInfo){
    	return new MonitorEvent(processingName, eventName, AndroidRuntime.getPid(), AndroidRuntime.getThreadId(), AndroidRuntime.getThreadId(), staticInfo, dynamicInfo);
    }
    public static MonitorEvent newGlobalEvent(String processingName, final String eventName, String staticInfo, Serializable... dynamicInfo){
    	return new MonitorEvent(processingName, eventName, AndroidRuntime.getPid(), AndroidRuntime.getThreadId(), MonitorContext.GLOBALCTX, staticInfo, dynamicInfo);
    }
    public static MonitorEvent newObjectEvent(String processingName, final String eventName, Object objCtx, String staticInfo, Serializable... dynamicInfo){
    	return new MonitorEvent(processingName, eventName, AndroidRuntime.getPid(), AndroidRuntime.getThreadId(), objCtx, staticInfo, dynamicInfo);
    }

    public MonitorEvent(){
    }

    protected MonitorEvent(String processingName, final String eventName, int pid, long tid, Object ctxObj, String staticInfo, Serializable... dynamicInfo){
        this.timestamp = eventGen.incrementAndGet();//System.nanoTime();
        this.pid = pid;
        this.tid = tid;
        this.name = eventName;
        this.staticInfo = staticInfo;
        this.contextObj = ctxObj;
        this.dynamicInfo = dynamicInfo;
        this.processingName = processingName;
        this.pname = AndroidRuntime.getPName(this.pid);
    }
    
    public MonitorEvent(ObjectInput in) throws ClassNotFoundException, IOException{
    	this.readExternal(in);
    }
    public long getTimestamp() {
		return timestamp;
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
    	StringBuilder sb = new StringBuilder();
    	for(Serializable v:dynamicInfo){
    		sb.append(v);
    		sb.append(" ");
    	}
    	return processingName+" monitor event ("+pname+"): "+name+"("+pid+","+tid+") with value "+sb.toString();
    }

	public Object getContextObject() {
		return contextObj;
	}
	public static boolean isBinder(MonitorEvent event) {
		return event instanceof BinderEvent;
	}

    public String getTransitionName(){
        return this.name + "_" + this.pname;
    }
    public String getAmbiguousName() {
		return this.name + "_" + "_";
	}
    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeLong(tid);
        out.writeInt(pid);
        out.writeObject(pname);
        out.writeObject(name);
        out.writeLong(timestamp);
        out.writeObject(staticInfo);
        try{
        	out.writeObject(contextObj);
        }catch (NotSerializableException e){
        	out.writeObject(this.pid+":"+System.identityHashCode(contextObj));
        }
        out.writeObject(dynamicInfo);
        out.writeObject(processingName);
    }


    @Override
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        tid = in.readLong();
        pid = in.readInt();
        pname = (String) in.readObject();
        name = (String) in.readObject();
        timestamp = in.readLong();
        staticInfo = (String) in.readObject();
        contextObj = in.readObject();
        dynamicInfo = (Serializable[]) in.readObject();
        processingName = (String) in.readObject();

    }
}
