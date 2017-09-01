package ch.usi.dag.rv.simulator;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;

public class Simulator {
	public static void main(String[] args) throws Exception{
		LogReader.simulateLog(args[0]);
	}

	private static byte[] toByteArray(EventQueue eventQueue) throws IOException {
		ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream(256);
		ObjectOutputStream oos = new ObjectOutputStream(byteArrayOutputStream);

		oos.writeObject(eventQueue);

		return byteArrayOutputStream.toByteArray();
	}

}
