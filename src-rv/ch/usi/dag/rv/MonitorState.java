package ch.usi.dag.rv;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import ch.usi.dag.rv.MonitorEventProcessor.MonitorEventQueue;
import ch.usi.dag.rv.infoleak.DataLeakEventProcessing;
import ch.usi.dag.rv.utils.DefaultLog;
import ch.usi.dag.rv.utils.Runtime;

public class MonitorState {
	static HashMap<Long, ThreadEventQueue> threadEventQueues = new HashMap<Long, MonitorState.ThreadEventQueue>();
	static List<MonitorEventProcessor> processings = new LinkedList<MonitorEventProcessor>();
	static List<MonitorEvent> globalEvents = new LinkedList<MonitorEvent>();

	public synchronized static void addProcessing(
			final MonitorEventProcessor processing) {
		processings.add(processing);
	}

	public synchronized static void newEvent(final MonitorEvent e) {
		if (e.isThreadLocal()) {
			final long tid = Runtime.getThreadId();
			if (!threadEventQueues.containsKey(tid))
				threadEventQueues.put(tid, new ThreadEventQueue());
			ThreadEventQueue eventQ = threadEventQueues.get(tid);
			eventQ.addEvent(e);
			if (e.needProcess()) {
				eventQ.process();
			}
		} else {
			globalEvents.add(e);
			if (e.needProcess()) {
				for (ThreadEventQueue eventQ : threadEventQueues.values()) {
					//eventQ.addEvent(e);
					eventQ.process();
				}
			}
		}
	}
	
	static class ThreadEventQueue {
		int noneProcessableCount = 0;
		List<MonitorEvent> localEvents = new ArrayList<MonitorEvent>();
		public ThreadEventQueue(){
			//this.eventList.addAll(globalEvents);
		}
		public void addEvent(MonitorEvent e) {
			if(!e.needProcess()){
				noneProcessableCount++;
			}
			if(noneProcessableCount > 1000) {
				cleanEventList();
			}
			this.localEvents.add(e);
		}
		public void process() {
			for (final MonitorEventProcessor p : processings) {
				p.process(new MonitorEventQueue(globalEvents, localEvents));
			}
		}
		private void cleanEventList(){
			Iterator<MonitorEvent> iter = this.localEvents.iterator();
			while(iter.hasNext()){
				MonitorEvent event = iter.next();
				if(!event.needProcess()){
					iter.remove();
				}
			}
			noneProcessableCount = 0;
		}
	}
}
