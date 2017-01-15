package ch.usi.dag.rv;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import ch.usi.dag.rv.utils.DefaultLog;
import ch.usi.dag.rv.utils.Runtime;

public abstract class MonitorState {
	HashMap<Long, ThreadEventQueue> threadEventQueues = new HashMap<Long, MonitorState.ThreadEventQueue>();
	List<MonitorEventProcessor> processings = new ArrayList<MonitorEventProcessor>();
	List<MonitorEvent> globalEvents = new ArrayList<MonitorEvent>();
	class ThreadEventQueue {
		public ThreadEventQueue(){
			this.eventList.addAll(globalEvents);
		}
		public void addEvent(MonitorEvent e) {
			this.eventList.add(e);
		}

		public void process(List<MonitorEventProcessor> processings) {
			for (final MonitorEventProcessor p : processings) {
				p.process(this.eventList);
			}
		}
		List<MonitorEvent> eventList = new ArrayList<MonitorEvent>();
	}

	public synchronized void addProcessing(
			final MonitorEventProcessor processing) {
		processings.add(processing);
	}

	public synchronized void newEvent(final MonitorEvent e) {
		if (e.isThreadLocal()) {
			final long tid = Runtime.getThreadId();
			if (!threadEventQueues.containsKey(tid))
				threadEventQueues.put(tid, new ThreadEventQueue());
			ThreadEventQueue eventQ = threadEventQueues.get(tid);
			eventQ.addEvent(e);
			if (e.needProcess()) {
				eventQ.process(processings);
			}
		} else {
			globalEvents.add(e);
			for (ThreadEventQueue eventQ : threadEventQueues.values()) {
				eventQ.addEvent(e);
				if (e.needProcess()) {
					eventQ.process(processings);
				}
			}
		}
	}
}
