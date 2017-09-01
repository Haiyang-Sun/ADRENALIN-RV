package ch.usi.dag.rv;

import java.util.concurrent.atomic.AtomicLong;

public abstract class PropertyProcessor {
	//the unique property name defined in the @Property annotation
	public String propertyId;
	
	static AtomicLong idGen = new AtomicLong(0);
	long id;
	
	//the process id running this property processor
	protected int pid;
	public long getId() {
		return id;
	}
	public PropertyProcessor(String name,int pid){
		id = idGen.incrementAndGet();
		this.propertyId = name;
		this.pid = pid;
	}
    public abstract void process(MonitorContext ctx, MonitorEvent event);
	public abstract boolean filterEvent(MonitorEvent e);
}
