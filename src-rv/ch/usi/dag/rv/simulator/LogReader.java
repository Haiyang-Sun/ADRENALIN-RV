package ch.usi.dag.rv.simulator;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileReader;
import java.io.ObjectInputStream;

import ch.usi.dag.rv.MonitorEvent;
import ch.usi.dag.rv.binder.BinderEvent;
import ch.usi.dag.rv.jni.RVNativeWrapper;

public class LogReader {
	public static void main(String args[]) {
		try {
			simulateLog(args[0]);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void simulateLog(String file) throws Exception {
		EventQueue eq = new EventQueue();
		RVNativeWrapper.procs = eq.pidPname;
		try {

			FileReader fr;

			fr = new FileReader(new File(file));

			BufferedReader buffer = new BufferedReader(fr);
			String line;
			int idx = 0;
			boolean rightEvent = false;
			int maxEvents = 0;
			while ((line = buffer.readLine()) != null) {
				maxEvents--;
				if (maxEvents == 0)
					break;
				String[] byteVals = line.split(" ");
				byte[] bytes = new byte[byteVals.length];
				for (int i = 0; i < byteVals.length; i++) {
					bytes[i] = (byte) Integer.parseInt(byteVals[i]);
				}
				ObjectInputStream ois = new ObjectInputStream(
						new ByteArrayInputStream(bytes));
				MonitorEvent event = (MonitorEvent) ois.readObject();

				eq.updatePname(event);

				if (event instanceof BinderEvent) {

					BinderEvent be = (BinderEvent) event;
					if (true) {
						if (be.getType() == 2) {
							rightEvent = true;
						}
						if (be.getType() == 5) {
							rightEvent = false;
						}
						if (!be.canIgnore()) {
							eq.addEvent((MonitorEvent) event);
							System.out.println(++idx + " " + event);
						} else {
						}
					}
				} else {
					eq.addEvent((MonitorEvent) event);
					System.out.println(++idx + " " + event);
				}

			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(
				eq.toByteArray());
		SimulatorManager sm = new SimulatorManager(byteArrayInputStream);

		System.out.println("---------------------------------");

		sm.prepare();
		sm.simulate();
	}

}
