package ch.usi.dag.rv;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

import ch.usi.dag.rv.ContextManager.MonitorContext;

public class ProcessorManager {
	public static abstract class MonitorEventProcessor {
		String name;
		static AtomicLong idGen = new AtomicLong(0);
		long id;
		public MonitorEventProcessor(String name){
			id = idGen.incrementAndGet();
			this.name = name;
		}
	    public abstract boolean process(MonitorContext ctx, Event event);
		public abstract boolean filterEvent(Event e);
	}

	static List<MonitorEventProcessor> processors = new LinkedList<MonitorEventProcessor>();

	public synchronized static void addProcessor(
			final MonitorEventProcessor processing) {
		processors.add(processing);
	}
	
	public synchronized static void newEvent(final Event e){
		newEvent(null, e);
	}
	public synchronized static void newEvent(int parentCtxId, final Event e){
		newEvent(ContextManager.getContext(null, parentCtxId), e);
	}
	private static void newEvent(MonitorContext parentCtx, final Event e) {
		int cid = e.getContextId();
		MonitorContext ctx = ContextManager.getContext(parentCtx, cid);
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
