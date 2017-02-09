package ch.usi.dag.rv.javamop.unsafeiterator;

import java.util.Collection;

import ch.usi.dag.rv.MonitorContext;

public class CollectionModifyEvent extends UnsafeIteratorEvent{
	public CollectionModifyEvent(String dexName, String location, Collection<?> collection) {
		super(dexName, "CollectionModify "+location, MonitorContext.getContextId(collection));
	}
}
