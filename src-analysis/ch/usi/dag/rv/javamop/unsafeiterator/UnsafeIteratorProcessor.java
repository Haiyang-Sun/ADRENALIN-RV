package ch.usi.dag.rv.javamop.unsafeiterator;

import ch.usi.dag.disldroidserver.OfflineInstrumentation;
import ch.usi.dag.rv.MonitorContext;
import ch.usi.dag.rv.MonitorState;
import ch.usi.dag.rv.MonitorEvent;
import ch.usi.dag.rv.MonitorEventProcessor;
import ch.usi.dag.rv.MonitorViolation;
import ch.usi.dag.rv.utils.DefaultLog;

public class UnsafeIteratorProcessor extends MonitorEventProcessor{
	public UnsafeIteratorProcessor() {
		super("UnsafeIterator");
	}

	@Override
	public boolean process(MonitorContext ctx, MonitorEvent event) {
		UnsafeIteratorState state = getState(ctx);
		if(event instanceof CollectionModifyEvent){
			state.onModify((CollectionModifyEvent) event);
		}else if(event instanceof IteratorCreationEvent){
			state.onCreate((IteratorCreationEvent) event);
		}else if(event instanceof IteratorNextEvent){
			state.onNext((IteratorNextEvent) event);
		}
		return true;
	}

	@Override
	public boolean filterEvent(MonitorEvent e) {
		return e instanceof UnsafeIteratorEvent;
	}
	class UnsafeIteratorState extends MonitorState{
		public UnsafeIteratorState(MonitorContext ctx) {
			super(UnsafeIteratorProcessor.this, ctx);
		}
		public void onNext(IteratorNextEvent event){
			if(violationBasic()){
				for(MonitorEvent e : this.ctx.getEvents()){
					if(e instanceof IteratorCreationEvent){
						state = 1;
					} else if(state == 1 && e instanceof CollectionModifyEvent){
						new MonitorViolation(this.ctx, this.processor, "Unsafe Iterator Violation").print();
						break;
					}
				}
			}
		}
		public void onCreate(IteratorCreationEvent event){
		}
		public void onModify(CollectionModifyEvent event){
			state = -1;
		}
	}
	public synchronized UnsafeIteratorState getState(MonitorContext ctx){
		UnsafeIteratorState res = (UnsafeIteratorState) ctx.getState(this);
		if(res == null) {
			res = new UnsafeIteratorState(ctx);
			ctx.setState(this, res);
		}
		return res;
	}
	
}
