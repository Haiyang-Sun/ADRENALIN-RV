package ch.usi.dag.rv;

import ch.usi.dag.rv.MonitorContext;
import ch.usi.dag.rv.MonitorEventProcessor;

public class MonitorState {
	protected MonitorContext ctx;
	protected int state;
	protected MonitorEventProcessor processor;
	public MonitorState(MonitorEventProcessor processor, MonitorContext ctx){
		this.ctx = ctx;
		this.state = 0;
		this.processor = processor;
	}
	public boolean violationBasic(){
		MonitorContext cur = this.ctx;
		boolean violated = true;
		while(cur.getParent()!=null){
			MonitorState parentState = cur.getState(processor);
			if(parentState != null && parentState.state >= 0){
				violated = false;
				break;
			}
			cur = cur.getParent();
		}
		return violated;
	}
}
