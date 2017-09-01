package ch.usi.dag.rv.simulator;

import java.io.ByteArrayOutputStream;
import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import ch.usi.dag.rv.MonitorEvent;

/**
 * Created by alexandernorth on 17.05.17.
 */
public class EventQueue implements Externalizable {

    HashMap<Integer, String> pidPname = new HashMap<>();

    List<MonitorEvent> events = new ArrayList<>();


    public void updatePname(MonitorEvent e){

        if (!pidPname.containsKey(e.pid)) {
        	String pname = (e.pname == null || e.pname.equals("")) ? String.valueOf(e.pid) : e.pname;
            pidPname.put(e.pid, pname);
            System.out.println("pid "+e.pid+" -> "+pidPname.get(e.pid));
        }
        e.pname = pidPname.get(e.pid);
    }

    public void addEvent(MonitorEvent e){
    	System.out.println("adding event "+e);
        events.add(e);
    }



    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeObject(pidPname);
        out.writeObject(events);
    }

    @Override
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        pidPname = (HashMap<Integer, String>) in.readObject();
        events = (List<MonitorEvent>) in.readObject();
    }
    
    public byte[] toByteArray() throws IOException {
		ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream(256);
		ObjectOutputStream oos = new ObjectOutputStream(byteArrayOutputStream);

		oos.writeObject(this);

		return byteArrayOutputStream.toByteArray();
	}
}
