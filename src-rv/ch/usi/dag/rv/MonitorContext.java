package ch.usi.dag.rv;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import ch.usi.dag.rv.MonitorEventProcessor;
import ch.usi.dag.rv.utils.MixedEventQueue;

public class MonitorContext {
	private int contextId;
	private MonitorContext parent;
	HashSet<MonitorContext> children = new HashSet<MonitorContext>();
	List<MonitorEvent> events = new ArrayList<MonitorEvent>();
	HashMap<Long, MonitorState> state = new HashMap<Long, MonitorState>();
	public MonitorContext(int contextId){
		this.contextId = contextId;
		this.parent = null;
	}
	public MonitorContext getParent() {
		return parent;
	}
	public int getContextId() {
		return contextId;
	}
	public void reset(){
		this.parent.children.remove(this);
		children.clear();
		events.clear();
		state.clear();
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
	public void updateParent(MonitorContext ctxctx){
		if(ctxctx != null) {
			
			if(this.parent == null) {
				this.parent = ctxctx;
				if(ctxctx != null) {
					ctxctx.children.add(this);
				}
			}else if(this.parent.getContextId() == ctxctx.contextId) {
				return;
			}else {
				reset();
				this.parent = ctxctx;
				ctxctx.children.add(this);
			}
		}
	}
	public Iterable<MonitorEvent> getEvents(long timestamp) {
		if(parent != null)
			return new MixedEventQueue(parent.getEvents(), this.events, timestamp);
		else
			return this.events;
	}
	
	public Iterable<MonitorEvent> getEvents() {
		if(parent != null)
			return new MixedEventQueue(parent.getEvents(), this.events);
		else
			return this.events;
	}
	public void addEvent(MonitorEvent e) {
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
	public synchronized void setState (MonitorEventProcessor processor,MonitorState state) {
		this.state.put(processor.id, state);
	}
	public MonitorState getState (MonitorEventProcessor processor) {
		return state.get(processor.id);
	}
}
