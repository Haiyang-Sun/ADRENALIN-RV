package ch.usi.dag.rv;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map.Entry;
import java.util.Set;

import ch.usi.dag.rv.nfa.AutomataInstance;
import ch.usi.dag.rv.nfa.DirectedGraph;

public class MonitorContext {
	public Object contextObj;
	 
	public static Object GLOBALCTX =  null;
	 
	private MonitorContext parent;
	HashSet<MonitorContext> children = new HashSet<MonitorContext>();
	public synchronized static int getHash(Object obj){
		return System.identityHashCode(obj);
	}
	ContextManager mnr;
	MonitorContext(ContextManager mnr){
		this.contextObj = GLOBALCTX;
		this.parent = null;
		this.mnr = mnr;
	}
	public int getPid(){
		return mnr.monitorProcessorManager.pid;
	}
	public MonitorContext(ContextManager mnr, Object contextObj){
		this.contextObj = contextObj;
		this.parent = mnr.globalContext;
		this.parent.children.add(this);
		this.dfaMap = stateCopy(this.parent.dfaMap);
		this.mnr = mnr;
	}

	public MonitorContext getParent() {
		return parent;
	}
	
	public Set<MonitorContext> getChildrenContext(){
		HashSet<MonitorContext> res = new HashSet<MonitorContext>();
		getChildren(this, res);
		return res;
	}
	
	public Set<MonitorContext> getParentContext(){
		HashSet<MonitorContext> res = new HashSet<MonitorContext>();
		MonitorContext ctx = this;
		do{
			res.add(ctx);
		}
		while(ctx.parent != null);
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
				//shuoldn't happen
				this.parent = ctxctx;
				if(ctxctx != null) {
					ctxctx.children.add(this);
				}
			}else if(this.parent.contextObj == ctxctx.contextObj) {
				return;
			}else {
				this.parent.children.remove(this);
				this.parent = ctxctx;
				ctxctx.children.add(this);
			}
		}
	}
	@Override
	public int hashCode() {
		return System.identityHashCode(contextObj);
	}
	@Override
	 public boolean equals(Object obj) {
        if(obj == null) {
        	return false;
        }else if(!(obj instanceof MonitorContext)){
        	return false;
        }else{
        	MonitorContext ctx = (MonitorContext)obj;
        	return (ctx.contextObj == this.contextObj);
        }
    }
	
	private HashMap<String, ArrayList<AutomataInstance>> stateCopy(
			HashMap<String, ArrayList<AutomataInstance>> old) {
		HashMap<String,ArrayList<AutomataInstance>> res = new HashMap<String, ArrayList<AutomataInstance>>();
		for(Entry<String, ArrayList<AutomataInstance> > entry:old.entrySet()){
			ArrayList<AutomataInstance> newArr = new ArrayList<AutomataInstance>();
			for(AutomataInstance oldinstance : entry.getValue()){
				AutomataInstance newinstance = oldinstance.exactCopy();
				newArr.add(newinstance);
			}
			res.put(entry.getKey(), newArr);
		}
		return res;
	}

	public HashMap<String,ArrayList<AutomataInstance>> dfaMap = new HashMap<>();
	
	public void reset(String propertyId, ArrayList<DirectedGraph> _dfas) {
		ArrayList<AutomataInstance> instances = new ArrayList<AutomataInstance>();
		for(DirectedGraph dg: _dfas){
			instances.add(new AutomataInstance(dg));
		}
		dfaMap.put(propertyId, instances);
	}
	
	public synchronized ArrayList<AutomataInstance> getDFA(String propertyId, ArrayList<DirectedGraph> _dfas) {
		if(!dfaMap.containsKey(propertyId))
			reset(propertyId, _dfas);
		return dfaMap.get(propertyId);
	}
}
