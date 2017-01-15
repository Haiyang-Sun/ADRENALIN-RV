package ch.usi.dag.rv.infoleak.events;

import ch.usi.dag.rv.MonitorEvent;

public class MethodTraceEvent extends MonitorEvent{

    boolean isEnter = true;
    public MethodTraceEvent(String dexName, final String method, final boolean isEnter){
        super (dexName, "MethodTraceEvent"+(isEnter?": entering ":": leaving ")+method, true);
        this.isEnter = isEnter;
    }

    @Override
    public boolean needProcess(){
        return false;
    }
}
