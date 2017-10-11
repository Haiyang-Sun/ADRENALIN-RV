package ch.usi.dag.rv;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.lang.reflect.Constructor;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import ch.usi.dag.rv.binder.BinderEvent;
import ch.usi.dag.rv.jni.RVNativeWrapper;
import ch.usi.dag.rv.processing.PropertyRegistry;
import ch.usi.dag.rv.utils.AndroidRuntime;

public class PropertyProcessorManager {
	int pid;
	public PropertyProcessorManager(int pid){
		this.pid = pid;
		String propertyNames = PropertyRegistry.properties;
		for(String name: propertyNames.split(",")){
			if(!name.equals("")){
				newProcessor(name);
			}
		}
	}
	
	public static PropertyProcessorManager instance = new PropertyProcessorManager(AndroidRuntime.getPid());
	
	public ContextManager mcm = new ContextManager(this);
	
	List<PropertyProcessor> processors = new LinkedList<PropertyProcessor>();

	public synchronized void addProcessor(
			final PropertyProcessor processing) {
		processors.add(processing);
	}
	static boolean loggingForSimulation = !RVNativeWrapper.isJVM && false;//true;
	public synchronized void newEvent(final MonitorEvent e){
		if(loggingForSimulation){
			try {
				ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream(256);
				ObjectOutputStream oos = new ObjectOutputStream(byteArrayOutputStream);
				if(e instanceof BinderEvent){
					BinderEvent be = (BinderEvent)e;
					oos.writeObject(be);
					byte[] ba = byteArrayOutputStream.toByteArray();
					if(!RVNativeWrapper.isJVM){
						System.out.println("binder event_trace:"+Arrays.toString(ba));
					}
				}else {
					oos.writeObject(e);
					byte[] ba = byteArrayOutputStream.toByteArray();
					if(!RVNativeWrapper.isJVM){
						System.out.println("event_trace:"+Arrays.toString(ba));
					}
				}
				oos.close();
				byteArrayOutputStream.close();
				
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}
			
		if(e instanceof BinderEvent){
			//do common binder operation here
			BinderEvent be = (BinderEvent)e;
			if(be.getType() == 2) {
				RVNativeWrapper.clearBinderFlag(e.pid, (int) e.tid);
			}
			if(be.getType() == 5 && RVNativeWrapper.isJVM){
				
			}
		}


		Object ctxObj = e.getContextObject();
		MonitorContext ctx = mcm.getContext(ctxObj);
		if(e.needProcessing()){
			for(MonitorContext related: ctx.getChildrenContext()){
				for(PropertyProcessor processor: processors){
					if(processor.filterEvent(e)) {
						processor.process(related, e);
					}
				}
			}
		}
	}
	public static boolean debug = Boolean.getBoolean("rv.debug");
	public void newProcessor(String name){
		for(PropertyProcessor processor:processors){
			if(processor.propertyId.equals(name))
				return;
		}
		//use reflection to load the processor class generated automatically
		Constructor<?> constructor;
		try {
			constructor = Class.forName("ch.usi.dag.rv.processing."+name).getConstructor(String.class, int.class);
			addProcessor((PropertyProcessor) constructor.newInstance(name, this.pid));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
