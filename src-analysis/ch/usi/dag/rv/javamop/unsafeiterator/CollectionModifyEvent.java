package ch.usi.dag.rv.javamop.unsafeiterator;

import java.util.Collection;

public class CollectionModifyEvent extends UnsafeIteratorEvent{
	public CollectionModifyEvent(String dexName, String location, Collection<?> collection) {
		super(dexName, "CollectionModify "+location, System.identityHashCode(collection));
	}
}
