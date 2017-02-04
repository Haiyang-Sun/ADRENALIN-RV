package ch.usi.dag.rv.javamop.unsafeiterator;

import ch.usi.dag.rv.MonitorEvent;
import ch.usi.dag.rv.MonitorProcessorManager;
import ch.usi.dag.rv.utils.DefaultLog;

public class UnsafeIteratorEvent extends MonitorEvent{
	public UnsafeIteratorEvent(String dexName, String desc, int ctxId) {
		super(dexName, desc, ctxId);
	}
	static {
		MonitorProcessorManager.addProcessor(new UnsafeIteratorProcessor());
	}
}
