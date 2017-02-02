package ch.usi.dag.rv.infoleak.events;

import ch.usi.dag.rv.infoleak.DataLeakEvent;
import ch.usi.dag.rv.utils.Runtime;

public class MethodExitEvent extends DataLeakEvent{
    public MethodExitEvent(String dexName, final String method){
    	super (dexName, "MethodTrace exiting "+method, (int) Runtime.getThreadId());
    }

    @Override
    public boolean needProcessing(){
        return false;
    }
}
