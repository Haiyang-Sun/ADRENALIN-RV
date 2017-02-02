package ch.usi.dag.rv.infoleak;

import ch.usi.dag.rv.Event;
import ch.usi.dag.rv.ProcessorManager;

//for each case, need an abstract event class to add the processor
public abstract class DataLeakEvent extends Event{
	static {
		//ProcessorManager.addProcessor(DataLeakEventProcessor.getInstance());
		ProcessorManager.addProcessor(new DataLeakEventProcessor());
	}
	public DataLeakEvent(String dexName, String desc, int ctxId) {
		super(dexName, "DataLeak-"+desc, ctxId);
	}
}
