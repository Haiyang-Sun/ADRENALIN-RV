package ch.usi.dag.rv.javamop.unsafeiterator;

import java.util.Collection;
import java.util.Iterator;

public class IteratorNextEvent extends UnsafeIteratorEvent {
	public IteratorNextEvent(String dexName, String location, Iterator<?> iter) {
		super(dexName, "IteratorNext "+location, System.identityHashCode(iter));
	}
}
