package ch.usi.dag.rv;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

//organize events like a tree
public class ContextManager {
	HashMap<Integer, List<MonitorContext>> monitorContexts = new HashMap<Integer, List<MonitorContext>>();
	PropertyProcessorManager monitorProcessorManager;
	public ContextManager(PropertyProcessorManager monitorProcessorManager) {
		this.monitorProcessorManager = monitorProcessorManager;
	}

	public void bindContext(Object ctxChild, Object ctxParent) {
		MonitorContext parent = getContext(ctxParent);
		setContextWithParent(parent, ctxChild);
	}
	
	MonitorContext globalContext = new MonitorContext(this);
	
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
}
