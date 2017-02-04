package ch.usi.dag.rv.javamop.hasnext;

import java.util.Iterator;

import android.R.integer;
import ch.usi.dag.rv.MonitorEvent;
import ch.usi.dag.rv.MonitorProcessorManager;
import ch.usi.dag.rv.utils.DefaultLog;

public class HasNextCaseEvent extends MonitorEvent {
	static {
		MonitorProcessorManager.addProcessor(new HasNextProcessor());
	}
	public HasNextCaseEvent(String dexName, String desc, int ctxId) {
		super(dexName, "HasNext-"+desc, ctxId);
	}
}
