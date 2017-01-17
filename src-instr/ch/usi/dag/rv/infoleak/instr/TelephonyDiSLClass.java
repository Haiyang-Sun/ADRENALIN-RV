package ch.usi.dag.rv.infoleak.instr;

import ch.usi.dag.disl.annotation.AfterReturning;
import ch.usi.dag.disl.annotation.GuardMethod;
import ch.usi.dag.disl.dynamiccontext.DynamicContext;
import ch.usi.dag.disl.marker.BytecodeMarker;
import ch.usi.dag.disl.staticcontext.DexStaticContext;
import ch.usi.dag.rv.MonitorEvent;
import ch.usi.dag.rv.infoleak.DataLeakMonitorState;
import ch.usi.dag.rv.infoleak.events.datasource.GetDeviceIdEvent;

public class TelephonyDiSLClass {
    @AfterReturning(
    		marker=BytecodeMarker.class,
    		guard=Guard.DeviceIdGuard.class,
    		args = "invokevirtual"
    )
    public static void getDeviceId (final DynamicContext dc, final DexStaticContext dsc) {
        final String retValue = dc.getStackValue (0, String.class);
        MonitorEvent event = new GetDeviceIdEvent (dsc.getDexShortName(), retValue);
        DataLeakMonitorState.getInstance ().newEvent (event);
    }
    
    static class Guard {
	    public static class DeviceIdGuard{
	        @GuardMethod
	        public static boolean guard (final DexStaticContext msc) {
	            final String name = msc.getCallee ();
	            final boolean res = name.contains("android/telephony/TelephonyManager.getDeviceId");
	            if(res){
	                System.out.println(
	                		"weaving: found invocation to TelephonyManager.getDeviceId at "
	                		+msc.thisMethodFullName());
	            }
	            return res;
	        }
	    }
	}
}
