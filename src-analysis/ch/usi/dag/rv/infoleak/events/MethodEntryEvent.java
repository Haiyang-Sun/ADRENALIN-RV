package ch.usi.dag.rv.infoleak.events;

import ch.usi.dag.rv.MonitorEvent;

public class MethodEntryEvent extends MonitorEvent{
    public MethodEntryEvent(String dexName, final String method){
        super (dexName, "MethodTrace entering "+method, true);
    }

    @Override
    public boolean needProcess(){
        return false;
    }
}
