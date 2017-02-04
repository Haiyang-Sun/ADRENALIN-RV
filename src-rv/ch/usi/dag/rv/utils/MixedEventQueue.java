package ch.usi.dag.rv.utils;

import java.util.Iterator;
import java.util.NoSuchElementException;

import ch.usi.dag.rv.MonitorEvent;

//treat two iterable of events as one and sort by timestamp
//TODO
//make sure two are iterated in timestamp order
//add implement for the field timestamp
public class MixedEventQueue implements Iterable<MonitorEvent> {
	Iterable<MonitorEvent> parantEvents;
	Iterable<MonitorEvent> childEvents;
	long timestamp;
	public MixedEventQueue(Iterable<MonitorEvent> globalEvents,
			Iterable<MonitorEvent> localEvents, long timestamp) {
		this.parantEvents = globalEvents;
		this.childEvents = localEvents;
		this.timestamp = timestamp;
	}
	
	public MixedEventQueue(Iterable<MonitorEvent> globalEvents,
			Iterable<MonitorEvent> localEvents) {
		this.parantEvents = globalEvents;
		this.childEvents = localEvents;
		this.timestamp = -1;
	}

	@Override
	public Iterator<MonitorEvent> iterator() {
		return new MixedIterator();
	}

	class MixedIterator implements Iterator<MonitorEvent> {
		Iterator<MonitorEvent> iter1;
		Iterator<MonitorEvent> iter2;
		int flag;
		MonitorEvent buf1;
		MonitorEvent buf2;

		public MixedIterator() {
			iter1 = parantEvents.iterator();
			iter2 = childEvents.iterator();
			flag = 0;
			buf1 = null;
			buf2 = null;
		}

		@Override
		public boolean hasNext() {
			return buf1 != null || buf2 != null || iter1.hasNext()
					|| iter2.hasNext();
		}

		@Override
		public MonitorEvent next() {
			MonitorEvent res;
			if (buf1 == null && !iter1.hasNext()) {
				res = buf2 != null ? buf2 : iter2.next();
				buf2 = null;
				flag = 2;
			} else if (buf2 == null && !iter2.hasNext()) {
				res = buf1 != null ? buf1 : iter1.next();
				buf1 = null;
				flag = 1;
			} else {
				if (buf1 == null)
					buf1 = iter1.next();
				if (buf2 == null)
					buf2 = iter2.next();
				if (buf1.getTimestamp() < buf2.getTimestamp()) {
					res = buf1;
					buf1 = null;
					flag = 1;
				} else {
					res = buf2;
					buf2 = null;
					flag = 2;
				}
			}
			return res;
		}

		@Override
		public void remove() {
			if (flag == 1)
				iter1.remove();
			else if (flag == 2)
				iter2.remove();
			else
				throw new NoSuchElementException();
			flag = 0;
		}
	}
}
