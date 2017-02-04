package ch.usi.dag.rv;

import java.util.LinkedList;
import java.util.List;

import ch.usi.dag.rv.MonitorContext;

public class MonitorProcessorManager {
	static List<MonitorEventProcessor> processors = new LinkedList<MonitorEventProcessor>();

	public synchronized static void addProcessor(
			final MonitorEventProcessor processing) {
		processors.add(processing);
	}
	
//	public synchronized static void newEvent(final Event e){
//		newEvent(null, e);
//	}
//	public synchronized static void newEvent(int parentCtxId, final Event e){
//		newEvent(ContextManager.getContext(null, parentCtxId), e);
//	}
//	private static void newEvent(MonitorContext parentCtx, final Event e) {
	public synchronized static void newEvent(final MonitorEvent e){
		int cid = e.getContextId();
		MonitorContext ctx = MonitorContextManager.getContext(cid);
		ctx.addEvent(e);
		//only process the event which need processing
		if(e.needProcessing()){
			//the event will influence all children contexts which rely on its context
			for(MonitorContext related: ctx.getChildrenContext()){
				//run all the processors
				for(MonitorEventProcessor processor: processors){
					//filter no use events
					if(processor.filterEvent(e)) {
						processor.process(related, e);
					}
				}
			}
		}
	}
}
