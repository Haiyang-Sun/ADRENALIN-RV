package ch.usi.dag.rv.processing;

import ch.usi.dag.rv.jni.RVNativeWrapper;

public class PropertyRegistry {
	public static String properties = RVNativeWrapper.isJVM?(System.getProperty("rv.property")):"permission";
	//public static String properties = RVNativeWrapper.isJVM?(System.getProperty("rv.property")):"remote";//"permission";
}
