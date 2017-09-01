package ch.usi.dag.rv.permission;

import java.awt.Color;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.Random;

import ch.usi.dag.rv.MonitorContext;
import ch.usi.dag.rv.MonitorEvent;
import ch.usi.dag.rv.binder.BinderEvent;
import ch.usi.dag.rv.utils.AndroidRuntime;

public class PermissionAnalysis {

	public static void process(MonitorContext context, List<MonitorEvent> events) {
		int index = 0;
		for(MonitorEvent event:events){
			System.out.println("event["+(index++)+"]:"+event.toString());
			if(event instanceof BinderEvent){
				continue;
			}
			String className = (String) event.dynamicInfo[0];
			String methodName = (String) event.dynamicInfo[1];
			String permission = simplifyKey((String) event.dynamicInfo[2]);
			String pname = AndroidRuntime.getPName(context.getPid());
			pname = simplifyPname(pname);
			
			permission = simplifyPermission(permission);
			System.out.println("violation process "+pname +" uses "+permission);
			PerServiceReport.create(pname).newRemoteCall("", permission);
			PerServiceReport.create("global").newRemoteCall("", permission);
		}
	}
	private static String simplifyPermission(String permission) {
		permap.put("INTERACT.ACROSS.USERS.FULL", "INTERACT.ACROSS.USERS");
		permap.put("com.google.android.providers.gsf.permission.READ.GSERVICES", "READ.GSERVICES");
		if(permap.containsKey(permission)) {
			return permap.get(permission);
		}
		return permission;
	}
	static HashMap<String, String> permap = new HashMap<String, String>();
	
	public static String generateRandomColor(Color mix) {
	    Random random = new Random();
	    int red = random.nextInt(256);
	    int green = random.nextInt(256);
	    int blue = random.nextInt(256);

	    if (mix != null) {
	        red = (red + mix.getRed()) / 2;
	        green = (green + mix.getGreen()) / 2;
	        blue = (blue + mix.getBlue()) / 2;
	    }
	    return String.format("%d, %d, %d", red, green, blue);
	}
	
	public static double round(double value, int places) {
	    if (places < 0) throw new IllegalArgumentException();

	    BigDecimal bd = new BigDecimal(value);
	    bd = bd.setScale(places, RoundingMode.HALF_UP);
	    return bd.doubleValue();
	}
	
	static String simplifyKey(String key){
		String res = key;
		if(key.startsWith("android.permission.")){
			res = key.replace("android.permission.", "");
		}
		res = res.replace("_", ".");
		return res;
	}
	private static String simplifyPname(String pname) {
		pname = pname.substring(pname.lastIndexOf('.')+1);
		if(pname.contains(":")){
			pname = pname.substring(0, pname.indexOf(':'));
		}
		if(pname.equals("googlequicksearchbox")){
			pname = "quicksearchbox";
		}
		return pname;
	}

	static class Report{
		String id;
		Report(String id)
		{
			this.id = id;
		}
	}
	static class PerPermissionReport extends Report{
		static HashMap<String, PerPermissionReport> all = new HashMap<String, PerPermissionReport>();
		PerPermissionReport(String process){
			super(process);
		}
		void newRemoteCall(String service, String method){
			
		}
	}
	static void incre(String key, HashMap<String, Integer> map){
		if(map.containsKey(key)){
			map.put(key, map.get(key)+1);
		}else{
			map.put(key, 1);
		}
	}
	static void print(HashMap<String, Integer> map, String prefix){
		for(Entry<String, Integer> entry:map.entrySet()){
			System.out.println(prefix+entry.getKey()+" => "+entry.getValue());
		}
	}
	static class PerServiceReport extends Report{
		static HashMap<String, PerServiceReport> all = new HashMap<String, PerServiceReport>();
		public static PerServiceReport create(String id){
			PerServiceReport res = all.get(id);
			if(res == null) {
				res = new PerServiceReport(id);
				all.put(id, res);
			}
			return res;
		}
		private PerServiceReport(String service){
			super(service);
		}
		HashMap<String, Integer> procCount = new HashMap<String, Integer>();
		HashMap<String, Integer> methodCount = new HashMap<String, Integer>();
		public void newRemoteCall(String proc, String method){
			incre(proc, procCount);
			incre(method, methodCount);
		}
		static ArrayList<String> permissions = new ArrayList<String>();
		public static String toGnuplot(){
			StringBuilder sb = new StringBuilder();
			
			sb.append("app");
			for(String permission: permissions){
				sb.append(","+permission);
			}
			sb.append('\n');
			for(PerServiceReport report: all.values()){
				if(report.id.equals("global")){
					continue;
				}
				if(report.procCount.get("") < 20)
					continue;
				sb.append(report.id);
				for(String permission: permissions){
					sb.append(',');
					if(!permission.equals("Others")){
						
						if(report.methodCount.containsKey(permission)){
							int value = report.methodCount.get(permission);
							sb.append(value);
						}
					}else {
						int Others = 0;
						for(Entry<String, Integer> entry: report.methodCount.entrySet()){
							if(!permissions.contains(entry.getKey())){
								Others += entry.getValue();
							}
						}
						sb.append(Others);
					}
				}
				
				sb.append('\n');
	    	}
			return sb.toString();
		}
		
		public void report(){
			System.out.println("report for "+id);
			print(procCount, "\t\t");
			print(methodCount, "\t\t");
		}
		
		static {
			Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
			    @Override
			    public void run() {
			    	for(PerServiceReport report: all.values()){
			    		report.report();
			    	}
			    }
			}){});
		}
	}
}
