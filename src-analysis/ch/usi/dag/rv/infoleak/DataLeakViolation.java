package ch.usi.dag.rv.infoleak;

import java.util.List;

import ch.usi.dag.rv.MonitorEvent;
import ch.usi.dag.rv.MonitorViolation;
import ch.usi.dag.rv.infoleak.events.datasink.DataSinkEvent;
import ch.usi.dag.rv.infoleak.events.datasource.DataSourceEvent;
import ch.usi.dag.rv.utils.DefaultLog;

public class DataLeakViolation extends MonitorViolation{
	List<DataSourceEvent> sources;
	DataSinkEvent sink;
	List<MonitorEvent> events;
	DataLeakViolation(List<DataSourceEvent> sources, List<MonitorEvent> events, DataSinkEvent sink){
		super();
		this.events = events;
		this.sources = sources;
		this.sink = sink;
	}
	@Override
	public void print(){
		String strictSource = "";
		String potentialSource = "";
		for(DataSourceEvent source:sources){
			if(source.sameThread(sink))
				strictSource += source.toString()+"+";
			else 
				potentialSource += source.toString()+"+";
		}
		if(strictSource.endsWith("+")) {
			strictSource = strictSource.substring(0, strictSource.length()-1);
			DefaultLog.v("Violation-"+id, strictSource+" has leaked to "+sink.toString());
		}
		if(potentialSource.endsWith("+")) {
			potentialSource = potentialSource.substring(0, potentialSource.length()-1);
			//DefaultLog.v("DataLeakViolation-"+id, potentialSource+" may have leaked to "+sink.toString());
		}
		
		for(MonitorEvent event:events){
			if(event.sameThread(sink))
				DefaultLog.v("VTrace-"+id, event.toString());
		}
	}
}
