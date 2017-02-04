package ch.usi.dag.rv.javamop.hasnext;

import java.util.Iterator;

import android.view.animation.DecelerateInterpolator;
import ch.usi.dag.disl.annotation.Before;
import ch.usi.dag.disl.annotation.GuardMethod;
import ch.usi.dag.disl.dynamiccontext.DynamicContext;
import ch.usi.dag.disl.marker.BytecodeMarker;
import ch.usi.dag.disl.processorcontext.ArgumentProcessorContext;
import ch.usi.dag.disl.processorcontext.ArgumentProcessorMode;
import ch.usi.dag.disl.staticcontext.DexStaticContext;
import ch.usi.dag.rv.MonitorProcessorManager;
import ch.usi.dag.rv.javamop.hasnext.HasNextEvent;
import ch.usi.dag.rv.javamop.hasnext.NextEvent;
import ch.usi.dag.rv.utils.DefaultLog;

public class HasNextDiSLClass {
	@Before(marker = BytecodeMarker.class, guard = Guard.HasNextGuard.class, args = "invokevirtual, invokespecial, invokeinterface")
	public static void hasNext(final DexStaticContext dsc, ArgumentProcessorContext apc) {
		Iterator<?> iter = (Iterator<?>) apc.getReceiver(ArgumentProcessorMode.CALLSITE_ARGS);
		MonitorProcessorManager.newEvent(new HasNextEvent(dsc.getDexShortName(), dsc.thisMethodFullName(),iter));
	}
	
	@Before(marker = BytecodeMarker.class, guard = Guard.NextGuard.class, args = "invokevirtual, invokespecial, invokeinterface")
	public static void next(final DexStaticContext dsc, ArgumentProcessorContext apc) {
		Iterator<?> iter = (Iterator<?>) apc.getReceiver(ArgumentProcessorMode.CALLSITE_ARGS);
		MonitorProcessorManager.newEvent(new NextEvent(dsc.getDexShortName(), dsc.thisMethodFullName(),iter));
	}

	static class Guard {
		public static class HasNextGuard {
			@GuardMethod
			public static boolean guard(final DexStaticContext msc) {
				final String name = msc.getInvocationClass();
				if(!msc.isInterfaceOrChildOf(name, Iterator.class.getName()))
					return false;
				return msc.getInvocationSignature().contains("hasNext");
			}
		}
		public static class NextGuard {
			@GuardMethod
			public static boolean guard(final DexStaticContext msc) {
				if(msc.isMethodBridge()){
					return false;
				}
				final String name = msc.getInvocationClass();
				if(!msc.isInterfaceOrChildOf(name, Iterator.class.getName()))
					return false;
				return msc.getInvocationSignature().contains("next");
			}
		}
	}
}
