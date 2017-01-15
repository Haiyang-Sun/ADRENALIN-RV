package ch.usi.dag.rv.infoleak.instr;

import ch.usi.dag.disl.annotation.After;
import ch.usi.dag.disl.annotation.Before;
import ch.usi.dag.disl.marker.BodyMarker;
import ch.usi.dag.disl.staticcontext.DexStaticContext;
import ch.usi.dag.disl.staticcontext.MethodStaticContext;
import ch.usi.dag.rv.infoleak.DataLeakMonitorState;
import ch.usi.dag.rv.infoleak.events.MethodTraceEvent;

/*
 * for generating runtime stack
 * Take effects on app code
 */
public class MethodEntryDiSLClass {
    @Before (
        marker = BodyMarker.class,
        order = 1000)
    public static void before_enter (final DexStaticContext dsc) {
        DataLeakMonitorState.getInstance ().newEvent (new MethodTraceEvent (dsc.getDexShortName(), dsc.thisMethodFullName (), true));
    }

    @After (
        marker = BodyMarker.class,
        order = 1000)
    public static void after_enter (final DexStaticContext dsc) {
        DataLeakMonitorState.getInstance ().newEvent (new MethodTraceEvent (dsc.getDexShortName(), dsc.thisMethodFullName (), false));
    }
}
