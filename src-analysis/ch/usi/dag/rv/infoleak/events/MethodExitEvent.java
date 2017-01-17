package ch.usi.dag.rv.infoleak.events;

import ch.usi.dag.rv.MonitorEvent;

public class MethodExitEvent extends MonitorEvent{
    public MethodExitEvent(String dexName, final String method){
    	super (dexName, "MethodTrace exiting "+method, true);
    }

    @Override
    public boolean needProcess(){
        return false;
    }
}
