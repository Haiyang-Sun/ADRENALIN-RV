package ch.usi.dag.rv.binder;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

import ch.usi.dag.rv.MonitorContext;
import ch.usi.dag.rv.MonitorEvent;
import ch.usi.dag.rv.PropertyManager;
import ch.usi.dag.rv.jni.RVNative;
import ch.usi.dag.rv.utils.AndroidRuntime;

public class BinderEvent extends MonitorEvent{
	private static final long serialVersionUID = 7932441892173467340L;
	private int type;
	public int flag;
	public int fromPid;
	public int fromTid;
	private int transactionId;
	private boolean oneWay;
	public int toPid;
	public int toTid;
	
	public enum BinderType {
		REQUEST_SENT(0), 
		REQUEST_RECEIVING(1), 
		REQUEST_RECEIVED(2), 
		REPLY_SENDING(3), 
		REPLY_SENT(4), 
		REPLY_RECEIVED(5);
		public int val;
		BinderType(int val){
			this.val = val;
		}
	};

	public BinderEvent(int type, int flag, int fromPid, int fromTid, int transactionId, boolean oneWay, int toPid, int toTid) {
		super("", ""+type, type%5==0?fromPid:toPid,type%5==0?fromTid:toTid ,MonitorContext.GLOBALCTX, "", type, flag, fromPid, fromTid, transactionId, oneWay, toPid, toTid);
		this.type = type;
		this.flag = flag;
		this.fromPid = fromPid;
		this.fromTid = fromTid;
		this.transactionId = transactionId;
		this.oneWay = oneWay;
		this.toPid = toPid;
		this.toTid = toTid;
	}
	public static void onBinder(int type, int flag, int fromPid, int fromTid, int transactionId, boolean oneWay, int toPid, int toTid){
		if(type == 0 || type == 1 || type == 4)
			return;
		PropertyManager.instance.newEvent(new BinderEvent(type, flag, fromPid, fromTid, transactionId, oneWay, toPid, toTid));
	}
	
	public boolean canIgnore(){
		if(oneWay)
			return true;
		if(type == 0){
			return true;
		}
		if(type % 5 != 0){
			return AndroidRuntime.getPName(fromPid) == null;
		}
		if(type == 5){
			return AndroidRuntime.getPName(toPid) == null;
		}
		return false;
	}

	public BinderEvent(){}

	@Override
	public String getTransitionName() {
		return AndroidRuntime.getPName(this.toPid)+":"+this.type;
	}
	public String getAmbiguousName() {
		return "_"+":"+this.type;
	}
	
	public boolean isCaller(){
		return type % 5 ==0;
	}

	public int getType(){
		return this.type;
	}

	public int getFlag(){
		return this.flag;
	}

	public int getFromPid() {
		return this.fromPid;
	}

	public int getFromTid() {
		return this.fromTid;
	}

	public int getTransactionId() {
		return this.transactionId;
	}

	public boolean isOneWay() {
		return this.oneWay;
	}

	public int getToPid() {
		return this.toPid;
	}

	public int getToTid() {
		return this.toTid;
	}

	@Override
    public String toString(){
    	return "binder event ("+pname+") of type "+this.type+"("+this.fromPid+","+this.fromTid+") -> ("+this.toPid+","+this.toTid+") -> "+this.transactionId+" "+this.flag;
    }
	
	@Override
    public void writeExternal(ObjectOutput out) throws IOException {
        super.writeExternal(out);
		out.writeInt(type);
		out.writeInt(flag);
		out.writeInt(fromPid);
		out.writeInt(fromTid);
		out.writeInt(transactionId);
		out.writeBoolean(oneWay);
		out.writeInt(toPid);
		out.writeInt(toTid);
    }

    @Override
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        super.readExternal(in);
        type = in.readInt();
        flag = in.readInt();
		fromPid = in.readInt();
		fromTid = in.readInt();
		transactionId = in.readInt();
		oneWay = in.readBoolean();
		toPid = in.readInt();
		toTid = in.readInt();
	}
    //0 -> 12 - 34 -> 5

    //debug only
	public static synchronized int appendString(String datString){
		int oldFlag = RVNative.getBinderFlag();
		if(oldFlag > 0) {
			byte[] oldData = RVNative.getByFlag0(oldFlag);
			String old = new String(oldData);
			datString = old +"-"+datString;
		}
		int flag = RVNative.setData(datString.getBytes());
		String str1 = new String(RVNative.getByFlag(AndroidRuntime.getPid(), flag));
		RVNative.setBinderFlag(flag);
		return flag;
	}

}
