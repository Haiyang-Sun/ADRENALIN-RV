package ch.usi.dag.rv.javamop.hasnext;

import ch.usi.dag.rv.MonitorContext;
import ch.usi.dag.rv.MonitorState;
import ch.usi.dag.rv.MonitorEvent;
import ch.usi.dag.rv.MonitorEventProcessor;
import ch.usi.dag.rv.MonitorViolation;

public class HasNextProcessor extends MonitorEventProcessor{
	public HasNextProcessor() {
		super("HasNext");
	}
	public boolean filterEvent(MonitorEvent e){
		//return e.getClass().getName().contains("ch.usi.dag.rv.javamop.hasnext");
		return e instanceof HasNextCaseEvent;
	}
    class HasNextState extends MonitorState{
		public HasNextState(MonitorContext ctx) {
			super(HasNextProcessor.this, ctx);
		}
		public void onHasNext(HasNextEvent event){
			state = 1;
		}
		public void onNext(NextEvent event){
			if(state > 0)
				state = 0;
			else {
				new MonitorViolation(ctx, processor, "hasnext violated for "+event).print();
				state = 0;
			}
		}
	}
	public synchronized HasNextState getState(MonitorContext ctx){
		HasNextState res = (HasNextState) ctx.getState(this);
		if(res == null) {
			res = new HasNextState(ctx);
			ctx.setState(this, res);
		}
		return res;
	}
	
	@Override
	public boolean process(MonitorContext ctx, MonitorEvent event) {
		HasNextState state = getState(ctx);
		if(event instanceof HasNextEvent){
			state.onHasNext((HasNextEvent) event);
		}else if(event instanceof NextEvent){
			state.onNext((NextEvent)event);
		}
		return true;
	}
}
