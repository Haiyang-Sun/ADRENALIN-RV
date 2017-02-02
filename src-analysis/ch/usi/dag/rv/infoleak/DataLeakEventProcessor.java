package ch.usi.dag.rv.infoleak;

import java.util.ArrayList;

import ch.usi.dag.rv.ContextManager.MonitorContext;
import ch.usi.dag.rv.ContextManager.MonitorState;
import ch.usi.dag.rv.Event;
import ch.usi.dag.rv.ProcessorManager.MonitorEventProcessor;
import ch.usi.dag.rv.Violation;
import ch.usi.dag.rv.infoleak.events.datasink.DataSinkEvent;
import ch.usi.dag.rv.infoleak.events.datasource.DataSourceEvent;

public class DataLeakEventProcessor extends MonitorEventProcessor{
	public DataLeakEventProcessor() {
		super("DataLeak");
	}
	@Override
	public boolean filterEvent(Event e){
		return e instanceof DataLeakEvent;
	}
    class DataLeakState extends MonitorState{
		int state = 0;
		//extra field
		ArrayList<DataSourceEvent> sources = new ArrayList <DataSourceEvent> ();
		public DataLeakState(MonitorContext ctx) {
			super(ctx);
		}
		public void onDataSource(DataSourceEvent source) {
			state = 1;
			sources.add(source);
		}

		@Override
		public boolean isViolated() {
			return state < 0;
		}

		public boolean extraCheck(DataSinkEvent event){
			DataLeakState parentState = null;
			if(this.ctx.getParent() == null){
				return false;
			}
			parentState = getState(this.ctx.getParent());
			if(parentState != null) {
				for(DataSourceEvent source: parentState.sources){
					if(event.matches(source))
						return true;
				}
			}
			return false;
		}
		
		public void onDataSink(DataSinkEvent event) {
        	if(state > 0 || extraCheck(event)) {
        		state = -1;
        	}
		}
	}
	public synchronized DataLeakState getState(MonitorContext ctx){
		DataLeakState res = ctx.getState(this);
		if(res == null) {
			res = new DataLeakState(ctx);
			ctx.setState(this, res);
		}
		return res;
	}
	
    @Override
    public boolean process(MonitorContext context, Event event) {
    	DataLeakState state = getState(context);
    	
        if(event instanceof DataSourceEvent){
            state.onDataSource ((DataSourceEvent)event);
        }else if(event instanceof DataSinkEvent){ 
        	state.onDataSink((DataSinkEvent)event);
        }
        if(state.isViolated()){
        	Violation violation = new Violation(context, this, "DataLeak Detected");
        	violation.print();
        }
        return false;
    }
}
