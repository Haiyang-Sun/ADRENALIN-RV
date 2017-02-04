package ch.usi.dag.rv;

import java.util.concurrent.atomic.AtomicLong;

import ch.usi.dag.rv.MonitorContext;
import ch.usi.dag.rv.MonitorEventProcessor;
import ch.usi.dag.rv.utils.DefaultLog;

public class MonitorViolation {
	static AtomicLong idGen = new AtomicLong(0);
	protected long id;
	MonitorContext ctx;
	MonitorEventProcessor processor;
	String violation;
	public MonitorViolation(MonitorContext ctx, MonitorEventProcessor processor, String violation) {
		this.id = idGen.incrementAndGet();
		this.ctx = ctx;
		this.processor = processor;
		this.violation = violation;
	}
	public void print(){
		DefaultLog.v("Violation-"+id+"-"+processor.name, violation);
		for(MonitorEvent event: ctx.getEvents()){
			if(processor.filterEvent(event)) {
				DefaultLog.v("VEvent-"+id, event.toString());
			}
		}
		try {
			throw new Exception();
		}catch (Exception e){
			for(StackTraceElement trace : e.getStackTrace()){
				DefaultLog.v("VTrace-"+id, trace.toString());
			}
		}
	}
}
