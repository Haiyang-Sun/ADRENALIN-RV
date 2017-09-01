package ch.usi.dag.rv.remote;

import ch.usi.dag.disl.annotation.Before;
import ch.usi.dag.disl.annotation.Property;
import ch.usi.dag.disl.dynamiccontext.MonitorDynamicContext;
import ch.usi.dag.disl.marker.BodyMarker;
import ch.usi.dag.disl.processorcontext.ArgumentProcessorContext;
import ch.usi.dag.disl.processorcontext.ArgumentProcessorMode;
import ch.usi.dag.disl.staticcontext.DexStaticContext;
import ch.usi.dag.rv.MonitorEvent;
import ch.usi.dag.rv.PropertyProcessorManager;
import ch.usi.dag.rv.service.ServiceAnalysis;

public class ServiceDiSLClass {
	@Before(marker = BodyMarker.class, scope = "com.android.server.*Service.*")
	public static void methodUse(final DexStaticContext dsc,
			final ArgumentProcessorContext pc) {
		final Object[] args = pc.getArgs(ArgumentProcessorMode.METHOD_ARGS);
		PropertyProcessorManager.instance.newEvent(MonitorEvent
				.newThreadLocalEvent("remote", "m", 
						"",
						dsc.thisClassSimpleName(),
						dsc.thisMethodName()));
	}

	@Property(name = "remote", ere = "#_(#system_server(m+))", binder = "true", reportAt = "matched")
	public static void permission(MonitorDynamicContext mdc) {
		System.out.println("violation found");
		ServiceAnalysis.process(mdc.getContext(), mdc.getEvents());
	}
}
