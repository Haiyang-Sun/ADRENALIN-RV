package ch.usi.dag.rv.infoleak.events.datasink;

import java.util.Arrays;

import ch.usi.dag.rv.infoleak.DataLeakEvent;
import ch.usi.dag.rv.infoleak.events.datasource.DataSourceEvent;
import ch.usi.dag.rv.utils.Runtime;

public abstract class DataSinkEvent extends DataLeakEvent{
    byte[] content;

    public DataSinkEvent(String dexName, final String desc, final byte[] value, final int off, final int length){
        super(dexName, desc, (int) Runtime.getThreadId());
        if(value == null){
            content = new byte[0];
            return;
        }
        content = Arrays.copyOfRange (value, off, length+off);
    }

    public boolean matches (final DataSourceEvent se) {
    	if(se == null || se.getValue() == null){
    		return false;
    	}
        final String source = se.getValue ().toString ();
        return match(this.content, source.getBytes ());
    }
    static boolean match(final byte[] a, final byte[] b){
        if(a == null || b == null || a.length < b.length) {
            return false;
        }
        for(int i = 0; i < a.length; i++){
            for(int j = 0; j < b.length; j++){
                if(b[j] != a[i+j]) {
                    break;
                }
                if(j == b.length - 1){
                    return true;
                }
            }
        }
        return false;
    }
}
