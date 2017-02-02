package ch.usi.dag.rv;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import ch.usi.dag.rv.ProcessorManager.MonitorEventProcessor;
import ch.usi.dag.rv.utils.DefaultLog;
import ch.usi.dag.rv.utils.MixedEventQueue;

//organize events like a tree
public class ContextManager {
	static HashMap<Integer, MonitorContext> monitorContexts = new HashMap<Integer, MonitorContext>();
	public static MonitorContext getContext(MonitorContext parent, int ctxId){
		if (!monitorContexts.containsKey(ctxId)) {
			monitorContexts.put(ctxId, new MonitorContext(null, ctxId));
		}
		MonitorContext res = monitorContexts.get(ctxId);
		if(res.getParent() == null && parent != null){
			res.addParent(parent);
		}
		if(res.getParent()!=null && parent != null && !res.getParent().equals(parent) ){
			DefaultLog.e("A context cannot have multiple parents", ctxId + " =>" + parent.getContextId() + " & " + res.getParent().getContextId());
		}
		return res;
	}
	public static abstract class MonitorState{
		protected MonitorContext ctx;
		public MonitorState(MonitorContext ctx){
			this.ctx = ctx;
		}
		public abstract boolean isViolated();
	}
	public static class MonitorContext {
		private int contextId;
		private MonitorContext parent;
		HashSet<MonitorContext> children = new HashSet<MonitorContext>();
		List<Event> events = new ArrayList<Event>();
		HashMap<Long, MonitorState> state = new HashMap<Long, ContextManager.MonitorState>();
		public synchronized <T extends MonitorState> void setState (MonitorEventProcessor processor,T state) {
			this.state.put(processor.id, state);
		}
		public <T extends MonitorState> T getState (MonitorEventProcessor processor) {
			return (T)(state.get(processor.id));
		}
		public MonitorContext(int contextId){
			this.contextId = contextId;
			this.parent = null;
		}
		public MonitorContext(MonitorContext parentCtx, int contextId){
			this.contextId = contextId;
			addParent(parentCtx);
		}
		public MonitorContext getParent() {
			return parent;
		}
		public int getContextId() {
			return contextId;
		}
		public Set<MonitorContext> getChildrenContext(){
			HashSet<MonitorContext> res = new HashSet<MonitorContext>();
			getChildren(this, res);
			return res;
		}
		private static void getChildren(MonitorContext ctx, HashSet<MonitorContext> res){
			if(res.contains(ctx))
				return;
			res.add(ctx);
			for(MonitorContext child: ctx.children){
				getChildren(child, res);
			}
		}
		public void addParent(MonitorContext ctxctx){
			this.parent = ctxctx;
			if(ctxctx != null) {
				ctxctx.children.add(this);
			}
		}
		public Iterable<Event> getEvents() {
			if(parent != null)
				return new MixedEventQueue(parent.getEvents(), this.events);
			else
				return this.events;
		}
		public void addEvent(Event e) {
			this.events.add(e);
		}
		@Override
		public int hashCode() {
			return contextId;
		}
		@Override
		 public boolean equals(Object obj) {
	        if(obj == null) {
	        	return false;
	        }else if(!(obj instanceof MonitorContext)){
	        	return false;
	        }else{
	        	MonitorContext ctx = (MonitorContext)obj;
	        	return (ctx.contextId == this.contextId);
	        }
	    }
		
	}
}
