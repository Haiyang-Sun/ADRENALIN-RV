package ch.usi.dag.rv.infoleak.events;

import ch.usi.dag.rv.Event;
import ch.usi.dag.rv.infoleak.DataLeakEvent;
import ch.usi.dag.rv.utils.Runtime;

public class MethodEntryEvent extends DataLeakEvent{
    public MethodEntryEvent(String dexName, final String method){
        super (dexName, "MethodTrace entering "+method, (int) Runtime.getThreadId());
    }

    @Override
    public boolean needProcessing(){
        return false;
    }
}
