package ch.usi.dag.rv;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map.Entry;
import java.util.Set;

import ch.usi.dag.rv.nfa.AutomataInstance;
import ch.usi.dag.rv.nfa.DirectedGraph;

public class MonitorContext {
	// each context has a unique context object
	public Object contextObj;
	
	// the specific object for global context
	public static Object GLOBALCTX =  null;
	 
	// the parent context of this context, null for global 
	private MonitorContext parent;
	
	// the children context(s) of this context
	private HashSet<MonitorContext> children = new HashSet<MonitorContext>();

	// the context manager of this context 
	private ContextManager mnr;
	
	// For each property, we maintain a list of DFA instances (for different roles of an MRE)
	private HashMap<String,ArrayList<AutomataInstance>> dfaMap = new HashMap<>();
	
	
	// In some rare cases, some classes forbids calling to HashCode
	public synchronized static int getHash(Object obj){
		return System.identityHashCode(obj);
	}
	
	// create global context for mnr
	public static MonitorContext createGlobal(ContextManager mnr){
		return new MonitorContext(mnr);
	}
	
	// only used for global context creation
	private MonitorContext(ContextManager mnr){
		this.contextObj = GLOBALCTX;
		this.parent = null;
		this.mnr = mnr;
	}
	
	public int getPid(){
		return mnr.getPid();
	}
	
	// create a context for contextObj and managed by mnr
	public MonitorContext(ContextManager mnr, Object contextObj){
		this.contextObj = contextObj;
		this.parent = mnr.getGlobalContext();
		this.parent.children.add(this);
		this.dfaMap = stateCopy(this.parent.dfaMap);
		this.mnr = mnr;
	}

	public MonitorContext getParent() {
		return parent;
	}
	
	public Set<MonitorContext> getChildrenContext(){
		HashSet<MonitorContext> res = new HashSet<MonitorContext>();
		_getChildrenContext(this, res);
		return res;
	}
	
	// helper method for getChildrenContext
	private static void _getChildrenContext(MonitorContext ctx, HashSet<MonitorContext> res){
		if(res.contains(ctx))
			return;
		res.add(ctx);
		for(MonitorContext child: ctx.children){
			_getChildrenContext(child, res);
		}
	}
	
	//
	public void updateParent(MonitorContext _parent){
		if(_parent != null) {
			if(this.parent == null) {
				//shuoldn't happen
				this.parent = _parent;
				if(_parent != null) {
					_parent.children.add(this);
				}
			}else if(this.parent.contextObj == _parent.contextObj) {
				return;
			}else {
				this.parent.children.remove(this);
				this.parent = _parent;
				_parent.children.add(this);
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

	// reset the DFAs to the start state
	public void reset(String propertyId, ArrayList<DirectedGraph> _dfas) {
		ArrayList<AutomataInstance> instances = new ArrayList<AutomataInstance>();
		for(DirectedGraph dg: _dfas){
			instances.add(new AutomataInstance(dg));
		}
		dfaMap.put(propertyId, instances);
	}
	
	// get the DFA instances for a property from the DFA definitions
	public synchronized ArrayList<AutomataInstance> getDFA(String propertyId, ArrayList<DirectedGraph> _dfas) {
		if(!dfaMap.containsKey(propertyId))
			reset(propertyId, _dfas);
		return dfaMap.get(propertyId);
	}
}
