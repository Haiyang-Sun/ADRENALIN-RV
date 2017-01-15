package ch.usi.dag.rv.infoleak.instr;

import ch.usi.dag.disl.annotation.AfterReturning;
import ch.usi.dag.disl.dynamiccontext.DynamicContext;
import ch.usi.dag.disl.marker.BytecodeMarker;
import ch.usi.dag.disl.staticcontext.DexStaticContext;
import ch.usi.dag.rv.infoleak.DataLeakMonitorState;
import ch.usi.dag.rv.infoleak.events.datasource.GetDeviceIdEvent;

public class TelephonyDiSLClass {
    @AfterReturning(marker=BytecodeMarker.class,
    guard=Guard.DeviceIdGuard.class,
    args = "invokevirtual")
    public static void getDeviceId (final DynamicContext dc, final DexStaticContext dsc) {
        final String value = dc.getStackValue (0, String.class);
        DataLeakMonitorState.getInstance ().newEvent (new GetDeviceIdEvent (dsc.getDexShortName(), value));
    }
}
