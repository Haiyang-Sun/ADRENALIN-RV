package ch.usi.dag.rv.processing;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

import ch.usi.dag.rv.MonitorContext;
import ch.usi.dag.rv.MonitorEvent;
import ch.usi.dag.rv.PropertyProcessor;
import ch.usi.dag.rv.binder.BinderEvent;
import ch.usi.dag.rv.jni.RVNativeWrapper;
import ch.usi.dag.rv.nfa.AutomataInstance;
import ch.usi.dag.rv.nfa.AutomataInstance.AutomataState;
import ch.usi.dag.rv.nfa.DirectedGraph;
import ch.usi.dag.rv.nfa.NFA;
import ch.usi.dag.rv.nfa.RegEx;
import ch.usi.dag.rv.utils.AndroidRuntime;

public class ProcessingTemplate extends PropertyProcessor {
	ArrayList<DirectedGraph> _dfas = new ArrayList<DirectedGraph>();
	public ProcessingTemplate(String name, int pid) {
		super(name, pid);
		updateDfaByPid(pid);
		int idx = 0;
		for(DirectedGraph _dfa: _dfas)
			_dfa.dumpToFile(new File("gv/" + name + "." + pid + "."
					+ AndroidRuntime.getPName(pid) + "."+(idx++)+".gv"));
	}
	
	public void updateDfaByPid(int pid){
		_dfas.clear();
		String pname = AndroidRuntime.getPName(pid);
		RegEx re = RegEx.getRegEx(getRE());
		if(pname == null){
			//only in simulator
			return;
		}
		for(NFA nfa: re.getNFA(pname)) {
			DirectedGraph dg = nfa.toDirectedGraph();
			dg.addEpsilonEdgeToStart();
			dg.removeEpsilonEdges();
			_dfas.add(dg.nfa2dfa());
		}
	}

	@Override
	public void process(MonitorContext ctx, MonitorEvent event) {
		if(event.pid != this.pid){
			updateDfaByPid(event.pid);
			ctx.reset(propertyId, _dfas);
			this.pid = event.pid;
		}
		ArrayList<AutomataInstance> dfaInstances = ctx.getDFA(propertyId, this._dfas);// iter.next();
		for(AutomataInstance dfaInstance : dfaInstances){
			if(!dfaInstance.isEventRelevant(event)){
				continue;
			}
			AutomataState state = dfaInstance.transit(event);
			if(state == reportAt()){
				if(dfaInstance.isOutMost()){
					callback(ctx, dfaInstance.getMatched());
				}else {
					storeInBinder(ctx, event, dfaInstance);	
				}
			}
			if(state == AutomataState.END || state == AutomataState.DEAD){
				dfaInstance.restart();
			}
		}
		return;
	}

	public void storeInBinder(MonitorContext ctx, MonitorEvent last, AutomataInstance dfaInstance) {
		List<MonitorEvent> events = dfaInstance.getMatched();
		byte[] mel;
		// event has to be a binder event
		BinderEvent be = (BinderEvent) last;
		List<MonitorEvent> matchedEventList = events;
		
		ByteArrayOutputStream baos = new ByteArrayOutputStream(256);

		try {
			ObjectOutputStream oos = new ObjectOutputStream(baos);
			oos.writeObject(matchedEventList);
		} catch (IOException e) {
			e.printStackTrace();
		}

		mel = baos.toByteArray();

		int theflag = RVNativeWrapper.setData(mel, last.pid);
		RVNativeWrapper.setBinderFlag(last.pid, (int) last.tid, theflag);
		if (RVNativeWrapper.isJVM)
			RVNativeWrapper.setByBinderEvent(be, theflag);
	}

	public static void callback(MonitorContext ctx, List<MonitorEvent> event) {
	}

	@Override
	public boolean filterEvent(MonitorEvent e) {
		if (processBinderEvent() && (e instanceof BinderEvent)) {
			return true;
		}
		return e.processingName.equals(this.propertyId); // && !(e instanceof
													// BinderEvent);
	}

	public static String getRE() {
		// to be updated with weaver
		String regExpr = "DEFAULTREGULAR";
		return regExpr;
	}

	public static boolean processBinderEvent() {
		String binderOrNot = "DEFAULTBINDER";
		return binderOrNot.equals("true");
	}

	public static boolean complement() {
		String binderOrNot = "DEFAULTCOMPLEMENT";
		return binderOrNot.equals("true");
	}

	public static AutomataState reportAt() {
		String reportAt = "DEFAULTSTATE";
		if(reportAt.equals("matched")){
			return AutomataState.ACCEPT;
		}
		if(reportAt.equals("fail")){
			return AutomataState.DEAD;
		}
		System.err.println("unknown reportAt");
		return AutomataState.ACCEPT;
	}
	
	public static String getScope() {
		return "DEFAULTSCOPE";
	}

	public static void main(String[] args) {
		try {
			Class clz = Class.forName("ch.usi.dag.rv.processing." + args[0]);
			Object obj = clz.getConstructor(String.class, int.class).newInstance("?",123);
			System.out.println(clz.getMethod("getNFA").invoke(obj));
			System.out.println(clz.getMethod("processBinderEvent").invoke(obj));
			System.out.println(clz.getMethod("complement").invoke(obj));
			System.out.println(clz.getMethod("getScope").invoke(obj));
		} catch (InstantiationException | IllegalAccessException
				| IllegalArgumentException | InvocationTargetException
				| NoSuchMethodException | SecurityException
				| ClassNotFoundException e) {
			e.printStackTrace();
		}
	}
}
