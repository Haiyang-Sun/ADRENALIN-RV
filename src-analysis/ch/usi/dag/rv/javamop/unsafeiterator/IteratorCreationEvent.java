package ch.usi.dag.rv.javamop.unsafeiterator;

import java.util.Collection;
import java.util.Iterator;

import ch.usi.dag.rv.MonitorManager;

public class IteratorCreationEvent extends UnsafeIteratorEvent {

	public IteratorCreationEvent(String dexName, String location, Collection<?> collection, Iterator<?> iter) {
		super(dexName, "IteratorCreation "+location, System.identityHashCode(iter));
		MonitorManager.bindContext(System.identityHashCode(iter), System.identityHashCode(collection));
	}

}
