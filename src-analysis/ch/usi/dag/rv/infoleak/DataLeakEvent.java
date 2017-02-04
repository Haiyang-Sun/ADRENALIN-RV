package ch.usi.dag.rv.infoleak;

import ch.usi.dag.rv.MonitorEvent;
import ch.usi.dag.rv.MonitorProcessorManager;

//for each case, need an abstract event class to add the processor
public abstract class DataLeakEvent extends MonitorEvent{
	static {
		//ProcessorManager.addProcessor(DataLeakEventProcessor.getInstance());
		MonitorProcessorManager.addProcessor(new DataLeakEventProcessor());
	}
	public DataLeakEvent(String dexName, String desc, int ctxId) {
		super(dexName, "DataLeak-"+desc, ctxId);
	}
}
