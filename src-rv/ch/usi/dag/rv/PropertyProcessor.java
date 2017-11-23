package ch.usi.dag.rv;

public abstract class PropertyProcessor {
	//the unique property name defined in the @Property annotation
	public String propertyId;
	
	//the process id running this property processor
	protected int pid;
	public PropertyProcessor(String name,int pid){
		this.propertyId = name;
		this.pid = pid;
	}
    public abstract void process(MonitorContext ctx, MonitorEvent event);
	public abstract boolean filterEvent(MonitorEvent e);
}
