package ch.usi.dag.rv;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

import ch.usi.dag.rv.infoleak.events.MethodEntryEvent;

public abstract class MonitorEventProcessor {
	public MonitorEventProcessor(){
		MonitorState.addProcessing(this);
	}
	public static class MonitorEventQueue implements Iterable<MonitorEvent>{
		List<MonitorEvent> globalEvents;
		List<MonitorEvent> localEvents;
		public MonitorEventQueue(List<MonitorEvent> globalEvents, List<MonitorEvent> localEvents){
			this.globalEvents = globalEvents;
			this.localEvents = localEvents;
		}
		@Override
		public Iterator<MonitorEvent> iterator() {
			return new MonitorEventIterator();
		}
		class MonitorEventIterator implements Iterator<MonitorEvent>{
			Iterator<MonitorEvent> iter1;
			Iterator<MonitorEvent> iter2;
			int flag;
			MonitorEvent buf1;
			MonitorEvent buf2;
			public MonitorEventIterator(){
				iter1 = globalEvents.iterator();
				iter2 = localEvents.iterator();
				flag = 0;
				buf1 = null;
				buf2 = null;
			}
			@Override
			public boolean hasNext() {
				return buf1 != null || buf2 != null || iter1.hasNext() || iter2.hasNext();
			}
			@Override
			public MonitorEvent next() {
				MonitorEvent res;
				if(buf1 == null && !iter1.hasNext()){
					res = buf2 != null? buf2: iter2.next();
					buf2 = null;
					flag = 2;
				} else if(buf2 == null && !iter2.hasNext()){
					res = buf1 != null? buf1: iter1.next();
					buf1 = null;
					flag = 1;
				} else {
					if(buf1 == null)
						buf1 = iter1.next();
					if(buf2 == null)
						buf2 = iter2.next();
					if(buf1.timestamp < buf2.timestamp) {
						res = buf1;
						buf1 = null;
						flag = 1;
					}else {
						res = buf2;
						buf2 = null;
						flag = 2;
					}
				}
				return res;
			}
			@Override
			public void remove() {
				if(flag == 1)
					iter1.remove();
				else if(flag == 2)
					iter2.remove();
				else
					throw new NoSuchElementException();
				flag = 0;
			}
		}
	}
    public abstract void process(MonitorEventQueue eventQueue);
}
