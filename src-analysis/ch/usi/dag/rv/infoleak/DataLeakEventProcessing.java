package ch.usi.dag.rv.infoleak;

import java.util.ArrayList;
import java.util.List;

import ch.usi.dag.rv.MonitorEvent;
import ch.usi.dag.rv.MonitorEventProcessor;
import ch.usi.dag.rv.infoleak.events.datasink.DataSinkEvent;
import ch.usi.dag.rv.infoleak.events.datasource.DataSourceEvent;
import ch.usi.dag.rv.utils.DefaultLog;

public class DataLeakEventProcessing extends MonitorEventProcessor{
	public DataLeakEventProcessing(){
		super();
	}
    @Override
    public void process(MonitorEventQueue eventQueue) {
        final ArrayList<DataSourceEvent> sources = new ArrayList <DataSourceEvent> ();
        MonitorEvent lastEvent = null;
        for(final MonitorEvent event: eventQueue){
            if(!event.needProcess ()) {
                continue;
            }
            lastEvent = event;
            if(event instanceof DataSourceEvent){
                sources.add ((DataSourceEvent)event);
            }
        }
        if(sources.isEmpty()){
        	return;
        }
        ArrayList<DataSourceEvent> validSources = new ArrayList<DataSourceEvent>();
        if(lastEvent instanceof DataSinkEvent){
            final DataSinkEvent sink = (DataSinkEvent)lastEvent;
            for(final DataSourceEvent source : sources){
            	//compare the value sent at the sink with the value retrieved from the source
                if(sink.matches(source)){
                	validSources.add(source);
                }
            }
            if(!validSources.isEmpty()) {
            	DataLeakViolation violation = new DataLeakViolation(validSources, eventQueue, sink);
            	violation.print();
            }
        }
    }
}
