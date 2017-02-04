package ch.usi.dag.rv;

import java.util.concurrent.atomic.AtomicLong;

public abstract class MonitorEventProcessor {
	String name;
	static AtomicLong idGen = new AtomicLong(0);
	long id;
	public long getId() {
		return id;
	}
	public MonitorEventProcessor(String name){
		id = idGen.incrementAndGet();
		this.name = name;
	}
    public abstract boolean process(MonitorContext ctx, MonitorEvent event);
	public abstract boolean filterEvent(MonitorEvent e);
	public abstract <T extends MonitorState> T getState(MonitorContext ctx);
}
