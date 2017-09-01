package ch.usi.dag.rv.permission;

import ch.usi.dag.disl.annotation.Before;
import ch.usi.dag.disl.annotation.Property;
import ch.usi.dag.disl.dynamiccontext.MonitorDynamicContext;
import ch.usi.dag.disl.marker.BodyMarker;
import ch.usi.dag.disl.processorcontext.ArgumentProcessorContext;
import ch.usi.dag.disl.processorcontext.ArgumentProcessorMode;
import ch.usi.dag.disl.staticcontext.DexStaticContext;
import ch.usi.dag.rv.MonitorEvent;
import ch.usi.dag.rv.PropertyProcessorManager;

public class PermissionDiSLClass {
	@Before(marker = BodyMarker.class, scope = "ActivityManager.checkComponentPermission")
	public static void methodUse(final DexStaticContext dsc,
			final ArgumentProcessorContext pc) {
		final Object[] args = pc.getArgs(ArgumentProcessorMode.METHOD_ARGS);
		if (args[0] != null) {
			final String permisssionUsed = args[0].toString();
			if (permisssionUsed != null) {
				PropertyProcessorManager.instance.newEvent(MonitorEvent
						.newThreadLocalEvent("permission", "check", 
								"",
								dsc.thisClassSimpleName(),
								dsc.thisMethodName(),
								permisssionUsed));
			}
		}
	}

	@Property(name = "permission", ere = "#_(#system_server(check+))", binder = "true", reportAt = "matched" )
	public static void permission(MonitorDynamicContext mdc) {
		System.out.println("violation found");
		PermissionAnalysis.process(mdc.getContext(), mdc.getEvents());
	}
}
