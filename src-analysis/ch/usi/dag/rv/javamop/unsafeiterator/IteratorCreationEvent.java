package ch.usi.dag.rv.javamop.unsafeiterator;

import java.util.Collection;
import java.util.Iterator;

import ch.usi.dag.rv.MonitorContextManager;

public class IteratorCreationEvent extends UnsafeIteratorEvent {

	public IteratorCreationEvent(String dexName, String location, Collection<?> collection, Iterator<?> iter) {
		super(dexName, "IteratorCreation "+location, System.identityHashCode(iter));
		MonitorContextManager.bindContext(System.identityHashCode(iter), System.identityHashCode(collection));
		System.out.println("DISL "+System.identityHashCode(iter)+ " is iterator created by "+ System.identityHashCode(collection));
	}

}
