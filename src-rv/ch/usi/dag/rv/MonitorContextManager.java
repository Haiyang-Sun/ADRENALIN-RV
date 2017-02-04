package ch.usi.dag.rv;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import ch.usi.dag.rv.MonitorEventProcessor;
import ch.usi.dag.rv.utils.DefaultLog;
import ch.usi.dag.rv.utils.MixedEventQueue;

//organize events like a tree
public class MonitorContextManager {
	static HashMap<Integer, MonitorContext> monitorContexts = new HashMap<Integer, MonitorContext>();
	public static void bindContext(int ctxChild, int ctxParent){
		MonitorContext parent = getContext(ctxParent);
		setContextWithParent(parent, ctxChild);
	}
	public static MonitorContext getContext(int ctxId){
		MonitorContext res;
		if (!monitorContexts.containsKey(ctxId)) {
			res = new MonitorContext(ctxId);
			monitorContexts.put(ctxId, res);
		}else {
			res =monitorContexts.get(ctxId);
		}
		return res;
	}
	private static void setContextWithParent(MonitorContext parent, int ctxId){
		MonitorContext res = getContext(ctxId);
		if(parent != null && res.getParent() == null){
			res.updateParent(parent);
		}
		if(parent != null && res.getParent()!=null && !res.getParent().equals(parent) ){
			DefaultLog.e("A context cannot have multiple parents", ctxId + " => " + parent.getContextId() + " & " + res.getParent().getContextId());
			res.updateParent(parent);
		}
	}
}
