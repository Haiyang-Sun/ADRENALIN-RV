package ch.usi.dag.rv.infoleak.instr;

import ch.usi.dag.disl.annotation.GuardMethod;
import ch.usi.dag.disl.staticcontext.DexStaticContext;

public class Guard {
    public static class DeviceIdGuard{
        @GuardMethod
        public static boolean guard (final DexStaticContext msc) {
            final String name = msc.getCallee ();
            final boolean res = name.contains("android/telephony/TelephonyManager.getDeviceId");
            if(res){
                System.out.println("weaving: found invocation to TelephonyManager.getDeviceId at "+msc.thisMethodFullName());
            }
            return res;
        }
    }
    public static class SubscriberIdGuard{
        @GuardMethod
        public static boolean guard (final DexStaticContext msc) {
            final String name = msc.getCallee ();
            final boolean res = name.contains("android/telephony/TelephonyManager.getSubscriberId");
            if(res){
            	System.out.println("weaving: found invocation to TelephonyManager.getSubscriberId at "+msc.thisMethodFullName());
            }
            return res;
        }
    }
}
