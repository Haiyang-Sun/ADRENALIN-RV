package ch.usi.dag.rv.infoleak.instr;

import ch.usi.dag.disl.annotation.After;
import ch.usi.dag.disl.annotation.Before;
import ch.usi.dag.disl.marker.BodyMarker;
import ch.usi.dag.disl.staticcontext.DexStaticContext;
import ch.usi.dag.rv.MonitorEvent;
import ch.usi.dag.rv.MonitorProcessorManager;
import ch.usi.dag.rv.infoleak.events.MethodEntryEvent;
import ch.usi.dag.rv.infoleak.events.MethodExitEvent;

public class MethodTraceDiSLClass {
    @Before (
        marker = BodyMarker.class
        )
    public static void before_enter (final DexStaticContext dsc) {
    	MonitorEvent event = new MethodEntryEvent (dsc.getDexShortName(), dsc.thisMethodFullName ());
        MonitorProcessorManager.newEvent (event);
    }
    
    @After (
        marker = BodyMarker.class
        )
    public static void after_enter (final DexStaticContext dsc) {
    	MonitorEvent event = new MethodExitEvent (dsc.getDexShortName(), dsc.thisMethodFullName ());
    	MonitorProcessorManager.newEvent (event);
    }
}
