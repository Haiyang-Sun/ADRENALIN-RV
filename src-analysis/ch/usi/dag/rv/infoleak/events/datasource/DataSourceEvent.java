package ch.usi.dag.rv.infoleak.events.datasource;

import ch.usi.dag.rv.Event;
import ch.usi.dag.rv.infoleak.DataLeakEvent;
import ch.usi.dag.rv.infoleak.events.datasink.DataSinkEvent;

public abstract class DataSourceEvent extends DataLeakEvent {
    Object value;
    public Object getValue(){
        return value;
    }
    public DataSourceEvent(String dexName, final String desc, final Object value){
        super(dexName, desc, 0);
        this.value = value;
    }
}
