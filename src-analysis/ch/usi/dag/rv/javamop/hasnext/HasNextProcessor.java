package ch.usi.dag.rv.javamop.hasnext;

import ch.usi.dag.rv.ContextManager.MonitorContext;
import ch.usi.dag.rv.ContextManager.MonitorState;
import ch.usi.dag.rv.Event;
import ch.usi.dag.rv.ProcessorManager.MonitorEventProcessor;
import ch.usi.dag.rv.Violation;

public class HasNextProcessor extends MonitorEventProcessor{
	public HasNextProcessor() {
		super("HasNext");
	}
	public boolean filterEvent(Event e){
		//return e.getClass().getName().contains("ch.usi.dag.rv.javamop.hasnext");
		return e instanceof HasNextCaseEvent;
	}
    class HasNextState extends MonitorState{
		public HasNextState(MonitorContext ctx) {
			super(ctx);
		}
		int state = 0;
		public void onHasNext(){
			state = 1;
		}
		public void onNext(){
			if(state > 0)
				state = 0;
			else 
				state = -1;
		}
		@Override
		public boolean isViolated() {
			return state < 0;
		}
	}
	public synchronized HasNextState getState(MonitorContext ctx){
		HasNextState res = ctx.getState(this);
		if(res == null) {
			res = new HasNextState(ctx);
			ctx.setState(this, res);
		}
		return res;
	}
	
	@Override
	public boolean process(MonitorContext ctx, Event event) {
		HasNextState state = getState(ctx);
		if(event instanceof HasNextEvent){
			state.onHasNext();
		}else if(event instanceof NextEvent){
			state.onNext();
		}
		if(state.isViolated()){
			new Violation(ctx, this, "hasnext violated for "+event).print();;
		}
		return true;
	}
}
