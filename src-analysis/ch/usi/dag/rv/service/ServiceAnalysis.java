package ch.usi.dag.rv.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import android.R.integer;
import ch.usi.dag.rv.MonitorContext;
import ch.usi.dag.rv.MonitorEvent;
import ch.usi.dag.rv.binder.BinderEvent;
import ch.usi.dag.rv.binder.BinderEvent.BinderType;
import ch.usi.dag.rv.nfa.DirectedGraph;
import ch.usi.dag.rv.utils.AndroidRuntime;

public class ServiceAnalysis {
	public static void process(MonitorContext context,
			List<MonitorEvent> events) {
		String involvedProcess = "";
		for(MonitorEvent event:events){
			if(event instanceof BinderEvent){
				BinderEvent be = (BinderEvent) event;
				if(be.getType()== BinderType.REPLY_RECEIVED.val && be.pid != context.getPid()){
					involvedProcess = AndroidRuntime.getPName(be.pid);
				}
				continue;
			}
			String className = (String) event.dynamicInfo[0];
			String methodName = (String) event.dynamicInfo[1];
			String pname = AndroidRuntime.getPName(context.getPid());
			onNewServiceUse(pname, className, methodName, involvedProcess);
		}
	}
	
	public static void onNewServiceUse(String processName, String serviceName, String serviceMethod, String proxyProcess){
		if(!proxyProcess.equals(""))
			System.out.println("Indirect Service Use "+processName+ " "+serviceName +" "+serviceMethod+" via "+proxyProcess);
		PerServiceReport.create(processName).newRemoteCall(serviceName, serviceName+"."+serviceMethod);
	}
	
	static class Report{
		String id;
		Report(String id)
		{
			this.id = id;
		}
	}
	static class PerProcessReport extends Report{
		static HashMap<String, PerProcessReport> all = new HashMap<String, ServiceAnalysis.PerProcessReport>();
		PerProcessReport(String process){
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
		static HashMap<String, PerServiceReport> all = new HashMap<String, ServiceAnalysis.PerServiceReport>();
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
		int numRemote = 0;
		public void newRemoteCall(String proc, String method){
			incre(proc, procCount);
			incre(method, methodCount);
			numRemote++;
		}
		public void report(){
			System.out.println("report for service "+id);
			System.out.println("\tprocess calling count:");
			print(procCount, "\t\t");
			System.out.println("\tmethod called count:");
			print(methodCount, "\t\t");
		}
		public String toCsv(){
			StringBuilder sb = new StringBuilder();
			sb.append(id);
			sb.append(",").append(procCount.size());
			sb.append(",").append(methodCount.size());
			sb.append(",").append(numRemote);
			return sb.toString();
		}
		static {
			Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
			    @Override
			    public void run() {
			    	StringBuilder sb = new StringBuilder();
			    	sb.append("service,procs,APIs,calls\n");
			    	for(PerServiceReport report: all.values()){
			    		report.report();
			    		sb.append(report.toCsv());
			    		sb.append("\n");
			    	}
			    }
			}){});
		}
	}
	
}
