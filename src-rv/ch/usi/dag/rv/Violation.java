package ch.usi.dag.rv;

import java.util.concurrent.atomic.AtomicLong;

import ch.usi.dag.rv.ContextManager.MonitorContext;
import ch.usi.dag.rv.ProcessorManager.MonitorEventProcessor;
import ch.usi.dag.rv.utils.DefaultLog;

public class Violation {
	static AtomicLong idGen = new AtomicLong(0);
	protected long id;
	MonitorContext ctx;
	MonitorEventProcessor processor;
	String violation;
	public Violation(MonitorContext ctx, MonitorEventProcessor processor, String violation) {
		this.id = idGen.incrementAndGet();
		this.ctx = ctx;
		this.processor = processor;
		this.violation = violation;
	}
	public void print(){
		DefaultLog.v("Violation-"+id+"-"+processor.name, violation);
		for(Event event: ctx.getEvents()){
			if(processor.filterEvent(event)) {
				DefaultLog.v("VTrace-"+id, event.toString());
			}
		}
	}
}
