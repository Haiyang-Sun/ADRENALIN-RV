package ch.usi.dag.rv.nfa;

import java.io.ByteArrayInputStream;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.List;

import ch.usi.dag.rv.MonitorEvent;
import ch.usi.dag.rv.binder.BinderEvent;
import ch.usi.dag.rv.jni.RVNativeWrapper;
import ch.usi.dag.rv.nfa.DirectedGraph.Edge;
import ch.usi.dag.rv.nfa.DirectedGraph.Node;

public class AutomataInstance {
	public DirectedGraph dfa;
	Node current;
	ArrayList<MonitorEvent> matchedList = new ArrayList<MonitorEvent>();
	boolean outMost;
	public AutomataInstance(DirectedGraph dfa) {
		this.dfa = dfa;
		this.current = dfa.getOrCreateNode(0);
		this.outMost = dfa.isOutMost();
	}

	public static enum AutomataState {
		ALIVE, ACCEPT, DEAD, END
	}
	

	private AutomataState transit(MonitorEvent event, boolean plain) {
		if(current == null)
			return AutomataState.DEAD;
		String edgeName = event.getTransitionName();
		Edge e = current.matchEdge(edgeName);
		if(e == null)
			e = current.matchEdge(event.getAmbiguousName());
		if (e == null) {
			matchedList.add(event);	
			return AutomataState.DEAD;
		}else {
			if(e.isRestarting()){
				matchedList.clear();
			}
			matchedList.add(event);
			current = e.to;
			if (event instanceof BinderEvent) {
				BinderEvent be = (BinderEvent) event;
				if (!plain && be.getType() == 5) {
					int datFlag = RVNativeWrapper.isJVM ? RVNativeWrapper
							.getByBinderEvent(be) : be.getFlag();
					if (datFlag > 0) {
						try {
							byte[] mel = RVNativeWrapper.getByFlag(be.getToPid(),
									datFlag);
							if(mel != null){
								ObjectInputStream ois = new ObjectInputStream(
										new ByteArrayInputStream(mel));
		
								List<MonitorEvent> matchedEventList = (List<MonitorEvent>) ois
										.readObject();
								ois.close();
								for (MonitorEvent monitorEvent : matchedEventList) {
									AutomataState state = this.transit(monitorEvent,
											true);
									if (state == AutomataState.DEAD
											|| state == AutomataState.END)
										return state;
								}
							}
						} catch (Exception e2) {
							e2.printStackTrace();
						}
					}
				}
			}
		}
		if (current.isEnd()) {
			return AutomataState.END;
		} else if (current.isAccepting()) {
			return AutomataState.ACCEPT;
		} else {
			return AutomataState.ALIVE;
		}
	}

	public AutomataState transit(MonitorEvent monitorEvent) {
		return transit(monitorEvent, false);
	}

	public ArrayList<MonitorEvent> getMatched() {
		return this.matchedList;
	}

	public void clearEventList() {
		matchedList.clear();
	}

	public AutomataInstance exactCopy() {
		AutomataInstance res = new AutomataInstance(this.dfa);
		for (MonitorEvent e : this.matchedList) {
			res.transit(e, true);
		}
		return res;
	}

	public void restart() {
		this.current = dfa.getOrCreateNode(0);
		matchedList.clear();
	}

	public boolean isEventRelevant(MonitorEvent event) {
		String edgeName = event.getTransitionName();
		if(dfa.isEdgeNameKnown(edgeName))
			return true;
		return dfa.isEdgeNameKnown(event.getAmbiguousName());
	}

	public int getCurrentState() {
		if(current == null)
			return -1;
		return current.index;
	}

	public String dump() {
		StringBuilder sb = new StringBuilder();
		sb.append("current state:");
		sb.append(getCurrentState());
		sb.append(", matched events(");
		sb.append(matchedList.size());
		sb.append("):");
		int idx = 0;
		for(MonitorEvent e: matchedList){
			sb.append(++idx).append(".").append(e).append(",");
		}
		
		return sb.toString();
	}

	public boolean isOutMost() {
		return this.outMost;
	}
}
