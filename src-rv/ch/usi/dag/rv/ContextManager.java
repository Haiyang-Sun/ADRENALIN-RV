package ch.usi.dag.rv;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/*
 * A context manager is used to manage events in different MonitorContext
 */
public class ContextManager {
	// HashMap for MonitorContexts
	// In some rare cases the hashcode is not accessible, we cannot simply use HashMap<ContextObject, MonitorContext>
	private HashMap<Integer, List<MonitorContext>> monitorContexts = new HashMap<Integer, List<MonitorContext>>();
	
	// Property Manager for multiple properties
	private PropertyManager propertyManager;
	
	// The default root context among all
	private MonitorContext globalContext = MonitorContext.createGlobal(this);
		
	public ContextManager(PropertyManager propertyManager) {
		this.propertyManager = propertyManager;
	}

	// To build the dependency for contexts
	public void bindContext(Object ctxChild, Object ctxParent) {
		MonitorContext parent = getContext(ctxParent);
		setContextWithParent(parent, ctxChild);
	}
	// helper method for bindContext
	private void setContextWithParent(MonitorContext parent,
			Object ctxObject) {
		MonitorContext res = getContext(ctxObject);
		if (parent != null && res.getParent() == null) {
			res.updateParent(parent);
		}
		if (parent != null && res.getParent() != null
				&& !res.getParent().equals(parent)) {
			res.updateParent(parent);
		}
	}
	
	// Get or create a new MonitorContext for ctxObject
	public synchronized MonitorContext getContext(Object ctxObject) {
		if(ctxObject == MonitorContext.GLOBALCTX)
			return globalContext;
		MonitorContext res = null;
		int ctxId = MonitorContext.getHash(ctxObject);
		if (!monitorContexts.containsKey(ctxId)) {
			monitorContexts.put(ctxId, new ArrayList<MonitorContext>());
		}
		List<MonitorContext> ctxs = monitorContexts.get(ctxId);
		for (MonitorContext ctx : ctxs) {
			if(ctx == null){
				System.out.println("get ctx null for "+ctxObject+" "+ctxs.size());
			}else if (ctx.contextObj == ctxObject) {
				res = ctx;
				break;
			}
		}
		if (res == null) {
			res = new MonitorContext(this, ctxObject);
			ctxs.add(res);
		}
		return res;
	}

	public int getPid() {
		return this.propertyManager.pid;
	}

	public MonitorContext getGlobalContext() {
		return this.globalContext;
	}
}
