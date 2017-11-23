package ch.usi.dag.rv.simulator;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.util.HashMap;

import ch.usi.dag.rv.MonitorEvent;
import ch.usi.dag.rv.PropertyManager;

public class SimulatorManager {
	HashMap<Integer, PropertyManager> realManagers = new HashMap<Integer, PropertyManager>();
	EventQueue eventQueue;
	InputStream inputStream;
	

	public void prepare() throws IOException, ClassNotFoundException {

		ObjectInputStream objectInputStream = new ObjectInputStream(inputStream);
		eventQueue = (EventQueue) objectInputStream.readObject();

		for(Integer pid:eventQueue.pidPname.keySet()){
			realManagers.put(pid, new PropertyManager(pid));
		}
	}
	
	public SimulatorManager(InputStream is) {
		this.inputStream = is;
	}

	public void newEvent(MonitorEvent event) throws Exception {
		int eventPid = event.pid;
		if(!eventQueue.pidPname.containsKey(eventPid)){
			throw new Exception("Simulation event from unknown process "+eventPid);
		}
		realManagers.get(eventPid).newEvent(event);
	}

	public void simulate() throws Exception {
		for (MonitorEvent me : eventQueue.events) {
			this.newEvent(me);
		}
	}

	
	
}
