package ch.usi.dag.rv.infoleak;

import ch.usi.dag.rv.MonitorState;

public class DataLeakMonitorState extends MonitorState{
    static DataLeakMonitorState instance;
    public static DataLeakMonitorState getInstance () {
        if(instance == null){
            instance = new DataLeakMonitorState ();
            instance.addProcessing (new DataLeakEventProcessing());
        }
        return instance;
    }
}
