package ch.usi.dag.rv.infoleak.events.datasource;

import ch.usi.dag.rv.MonitorEvent;
import ch.usi.dag.rv.infoleak.events.datasink.DataSinkEvent;

public abstract class DataSourceEvent extends MonitorEvent {
    Object value;
    public Object getValue(){
        return value;
    }
    public DataSourceEvent(String dexName, final String desc, final Object value){
        super(dexName, desc, false);
        this.value = value;
    }
}
