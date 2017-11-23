package ch.usi.dag.rv;

import ch.usi.dag.disl.annotation.Before;
import ch.usi.dag.disl.marker.BodyMarker;
import ch.usi.dag.disl.staticcontext.DexStaticContext;

public class EmptyDiSLClass {
	@Before (
	        marker = BodyMarker.class,
	        scope = "NOTHING"
	        )
	    public static void before_enter (final DexStaticContext dsc) {
	    	MonitorEvent event = MonitorEvent.newTLEvent("DataLeak", "MethodEntry", dsc.getDexShortName());
	    }
}
