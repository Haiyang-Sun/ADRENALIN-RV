package ch.usi.dag.rv.javamop.unsafeiterator;

import java.util.Collection;
import java.util.Iterator;

import ch.usi.dag.disl.annotation.AfterReturning;
import ch.usi.dag.disl.annotation.Before;
import ch.usi.dag.disl.annotation.GuardMethod;
import ch.usi.dag.disl.annotation.SyntheticLocal;
import ch.usi.dag.disl.dynamiccontext.DynamicContext;
import ch.usi.dag.disl.marker.BytecodeMarker;
import ch.usi.dag.disl.processorcontext.ArgumentProcessorContext;
import ch.usi.dag.disl.processorcontext.ArgumentProcessorMode;
import ch.usi.dag.disl.staticcontext.DexStaticContext;
import ch.usi.dag.rv.MonitorProcessorManager;
import ch.usi.dag.rv.javamop.unsafeiterator.CollectionModifyEvent;
import ch.usi.dag.rv.javamop.unsafeiterator.IteratorCreationEvent;
import ch.usi.dag.rv.javamop.unsafeiterator.IteratorNextEvent;

public class UnsafeIteratorDiSLClass {
	@Before(marker = BytecodeMarker.class, guard = Guard.NextGuard.class, args = "invokevirtual, invokespecial, invokeinterface")
	public static void next(final DexStaticContext dsc, ArgumentProcessorContext apc) {
		Iterator<?> iter = (Iterator<?>) apc.getReceiver(ArgumentProcessorMode.CALLSITE_ARGS);
		MonitorProcessorManager.newEvent(new IteratorNextEvent(dsc.getDexShortName(), dsc.thisMethodFullName(),iter));
	}
	
	@Before(marker = BytecodeMarker.class, guard = Guard.CollectionModifyGuard.class, args = "invokevirtual, invokespecial, invokeinterface")
	public static void modifyCollection(final DexStaticContext dsc, ArgumentProcessorContext apc) {
		Collection<?> collection = (Collection<?>) apc.getReceiver(ArgumentProcessorMode.CALLSITE_ARGS);
		MonitorProcessorManager.newEvent(new CollectionModifyEvent(dsc.getDexShortName(), dsc.thisMethodFullName(), collection));
	}
	@SyntheticLocal
	static Collection<?> collection;
	
	@Before(marker = BytecodeMarker.class, order = 1, guard = Guard.CreateIteratorGuard.class, args = "invokevirtual, invokespecial, invokeinterface")
	public static void beforeCreateIterator(final DexStaticContext dsc, ArgumentProcessorContext apc) {
		collection = (Collection<?>) apc.getReceiver(ArgumentProcessorMode.CALLSITE_ARGS);
	}
	
	@AfterReturning(marker = BytecodeMarker.class, order = 1, guard = Guard.CreateIteratorGuard.class, args = "invokevirtual, invokespecial, invokeinterface")
	public static void afterCreateIterator(final DynamicContext dc,
			final DexStaticContext dsc) {
		Iterator<?> iter = dc.getStackValue(0, Iterator.class);
		MonitorProcessorManager.newEvent(new IteratorCreationEvent(dsc.getDexShortName(), dsc.thisMethodFullName(), collection, iter));
	}
	static class Guard {
		public static class CreateIteratorGuard {
			@GuardMethod
			public static boolean guard(final DexStaticContext msc) {
				final String name = msc.getInvocationClass();
				if(!msc.isInterfaceOrChildOf(name, Collection.class.getName()))
					return false;
				return msc.getInvocationSignature().contains("iterator");
			}
		}
		public static class CollectionModifyGuard {
			@GuardMethod
			public static boolean guard(final DexStaticContext msc) {
				final String name = msc.getInvocationClass();
				if(!msc.isInterfaceOrChildOf(name, Collection.class.getName()))
					return false;
				return msc.getInvocationSignature().contains("add") || msc.getInvocationSignature().contains("remove") || msc.getInvocationSignature().contains("clear");
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
