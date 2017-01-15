package ch.usi.dag.rv;

import java.util.List;

public interface MonitorEventProcessor {
    public void process(List<MonitorEvent> events);
}
