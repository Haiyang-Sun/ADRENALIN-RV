package ch.usi.dag.rv.infoleak.events.datasource;

public class GetDeviceIdEvent extends DataSourceEvent{
    public GetDeviceIdEvent (String dexName, final String value) {
        super (dexName, "GetDeviceId:"+value, value);
    }
}
