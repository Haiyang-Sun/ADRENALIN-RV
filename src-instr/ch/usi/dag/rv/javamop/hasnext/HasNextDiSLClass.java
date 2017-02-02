package ch.usi.dag.rv.javamop.hasnext;

import java.util.Iterator;

import ch.usi.dag.disl.annotation.Before;
import ch.usi.dag.disl.annotation.GuardMethod;
import ch.usi.dag.disl.dynamiccontext.DynamicContext;
import ch.usi.dag.disl.marker.BytecodeMarker;
import ch.usi.dag.disl.staticcontext.DexStaticContext;
import ch.usi.dag.rv.ProcessorManager;

public class HasNextDiSLClass {
	@Before(marker = BytecodeMarker.class, guard = Guard.HasNextGuard.class, args = "invokevirtual, invokespecial, invokeinterface")
	public static void hasNext(final DynamicContext dc,
			final DexStaticContext dsc) {
		Iterator<?> iter = dc.getStackValue(0, Iterator.class);
		ProcessorManager.newEvent(new HasNextEvent(dsc.getDexShortName(), dsc.thisMethodFullName(),iter));
	}
	
	@Before(marker = BytecodeMarker.class, guard = Guard.NextGuard.class, args = "invokevirtual, invokespecial, invokeinterface")
	public static void next(final DynamicContext dc,
			final DexStaticContext dsc) {
		Iterator<?> iter = dc.getStackValue(0, Iterator.class);
		ProcessorManager.newEvent(new NextEvent(dsc.getDexShortName(), dsc.thisMethodFullName(),iter));
	}

	static class Guard {
		public static class HasNextGuard {
			@GuardMethod
			public static boolean guard(final DexStaticContext msc) {
				final String name = msc.getInvocationClass();
				if(!msc.isInterfaceOf(name, Iterator.class.getName()))
					return false;
				return msc.getInvocationSignature().contains("hasNext");
			}
		}
		public static class NextGuard {
			@GuardMethod
			public static boolean guard(final DexStaticContext msc) {
				final String name = msc.getInvocationClass();
				if(!msc.isInterfaceOf(name, Iterator.class.getName()))
					return false;
				return msc.getInvocationSignature().contains("next");
			}
		}
	}
}
