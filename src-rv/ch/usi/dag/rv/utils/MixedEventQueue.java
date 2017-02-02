package ch.usi.dag.rv.utils;

import java.util.Iterator;
import java.util.NoSuchElementException;

import ch.usi.dag.rv.Event;

//treat two iterable of events as one and sort by timestamp
public class MixedEventQueue implements Iterable<Event> {
	Iterable<Event> parantEvents;
	Iterable<Event> childEvents;

	public MixedEventQueue(Iterable<Event> globalEvents,
			Iterable<Event> localEvents) {
		this.parantEvents = globalEvents;
		this.childEvents = localEvents;
	}

	@Override
	public Iterator<Event> iterator() {
		return new MixedIterator();
	}

	class MixedIterator implements Iterator<Event> {
		Iterator<Event> iter1;
		Iterator<Event> iter2;
		int flag;
		Event buf1;
		Event buf2;

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
		public Event next() {
			Event res;
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
