package ch.usi.dag.rv.infoleak;

import java.util.ArrayList;
import java.util.HashMap;

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
		return e.getClass().getName().contains("infoleak"); 
	}
	static class DataLeakState extends MonitorState{
		final ArrayList<DataSourceEvent> sources = new ArrayList <DataSourceEvent> ();

		public ArrayList<DataSourceEvent> getSources() {
			return sources;
		}

		public void newSource(DataSourceEvent source) {
			sources.add(source);
		}
		
		public boolean hasSource(){
			return !sources.isEmpty();
		}
	}
	public synchronized DataLeakState getState(MonitorContext ctx){
		DataLeakState res = ctx.getState();
		if(res == null) {
			res = new DataLeakState();
			ctx.setState(res);
		}
		return res;
	}
	
    @Override
    public boolean process(MonitorContext context, Event event) {
    	DataLeakState state = getState(context);
    	
        if(event instanceof DataSourceEvent){
            state.newSource ((DataSourceEvent)event);
        }else if(event instanceof DataSinkEvent){ 
        	if(context.getParent() != null){
	        	DataLeakState parentState = getState(context.getParent());
	        	if(parentState.hasSource()){
	        		ArrayList<DataSourceEvent> validSources = new ArrayList<DataSourceEvent>();
	    	        
	                final DataSinkEvent sink = (DataSinkEvent)event;
	                for(final DataSourceEvent source : parentState.getSources()){
	                	//compare the value sent at the sink with the value retrieved from the source
	                    if(sink.matches(source)){
	                    	validSources.add(source);
	                    }
	                }
	                if(!validSources.isEmpty()) {
	                	Violation violation = new Violation(context, this, getViolation(validSources, context.getEvents(), sink));
	                	violation.print();
	                }
	        	}
        	}
        }
        return false;
    }
    
    String getViolation(ArrayList<DataSourceEvent> validSources, Iterable<Event> iterable, DataSinkEvent sink){
    	String strictSource = "";
		String potentialSource = "";
		for(DataSourceEvent source:validSources){
			if(source.getThreadId() == sink.getThreadId())
				strictSource += source.toString()+" ";
			else 
				potentialSource += source.toString()+" ";
		}
    	return strictSource+"leaked at "+sink.toString();
    }
}
