package ch.usi.dag.rv.utils;


import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.Map;

import ch.usi.dag.rv.jni.RVNative;
import ch.usi.dag.rv.jni.RVNativeWrapper;

public class AndroidRuntime {
	public static boolean isJVM = Boolean.getBoolean("disl.isjvm");
	
	public static long getThreadId(){
		if(isJVM)
    		return 0;
    	else
    		return RVNative.getTid();
	}
	
    public static int getPid(){
    	if(isJVM)
    		return 0;
    	else
    		return RVNative.getPid();
    }

    public static android.app.Activity getActivity() {
        try {
            Class activityThreadClass = Class.forName("android.app.ActivityThread");
            Object activityThread = activityThreadClass.getMethod("currentActivityThread").invoke(null);
            Field activitiesField = activityThreadClass.getDeclaredField("mActivities");
            activitiesField.setAccessible(true);

            if (activityThread == null)
                return null;

            Map<Object, Object> activities = (Map<Object, Object>) activitiesField.get(activityThread);
            if(activities == null)
                return null;

            for (Object activityRecord : activities.values()) {
                Class activityRecordClass = activityRecord.getClass();
                Field pausedField = activityRecordClass.getDeclaredField("paused");
                pausedField.setAccessible(true);
                if (!pausedField.getBoolean(activityRecord)) {
                    Field activityField = activityRecordClass.getDeclaredField("activity");
                    activityField.setAccessible(true);
                    return (android.app.Activity) activityField.get(activityRecord);
                }
            }
        } catch (NoSuchFieldException | ClassNotFoundException | InvocationTargetException | NoSuchMethodException | IllegalAccessException e){
            e.printStackTrace();
        }

        return null;
    }

    public static String getPName(int pid){
    	return RVNativeWrapper.getProcName(pid);
    }
}
