package ch.usi.dag.rv.infoleak;

import java.util.ArrayList;

import ch.usi.dag.rv.MonitorContext;
import ch.usi.dag.rv.MonitorState;
import ch.usi.dag.rv.MonitorEvent;
import ch.usi.dag.rv.MonitorEventProcessor;
import ch.usi.dag.rv.MonitorViolation;
import ch.usi.dag.rv.infoleak.events.datasink.DataSinkEvent;
import ch.usi.dag.rv.infoleak.events.datasource.DataSourceEvent;

public class DataLeakEventProcessor extends MonitorEventProcessor{
	public DataLeakEventProcessor() {
		super("DataLeak");
	}
	@Override
	public boolean filterEvent(MonitorEvent e){
		return e instanceof DataLeakEvent;
	}
    class DataLeakState extends MonitorState{
		ArrayList<DataSourceEvent> sources = new ArrayList <DataSourceEvent> ();
		public DataLeakState(MonitorContext ctx) {
			super(DataLeakEventProcessor.this, ctx);
		}
		public void onDataSource(DataSourceEvent source) {
			state = -1;
			sources.add(source);
		}
		public boolean extraCheck(DataSinkEvent event){
			DataLeakState parentState = null;
			if(this.ctx.getParent() == null){
				return false;
			}
			parentState = getState(this.ctx.getParent());
			if(parentState != null) {
				for(DataSourceEvent source: parentState.sources){
					if(event.matches(source) && source.getThreadId() == event.getThreadId())
						return true;
				}
			}
			return false;
		}
		
		public void onDataSink(DataSinkEvent event) {
        	if(extraCheck(event)) {
        		MonitorViolation violation = new MonitorViolation(this.ctx, this.processor, "DataLeak Detected");
            	violation.print();
        	}
		}
	}
    
    @Override
	public synchronized DataLeakState getState(MonitorContext ctx){
		DataLeakState res = (DataLeakState) ctx.getState(this);
		if(res == null) {
			res = new DataLeakState(ctx);
			ctx.setState(this, res);
		}
		return res;
	}
	
    @Override
    public boolean process(MonitorContext context, MonitorEvent event) {
    	DataLeakState state = getState(context);
    	
        if(event instanceof DataSourceEvent){
            state.onDataSource ((DataSourceEvent)event);
        }else if(event instanceof DataSinkEvent){ 
        	state.onDataSink((DataSinkEvent)event);
        }
        return false;
    }
}
