package ch.usi.dag.rv.javamop.hasnext;

import java.util.Iterator;

import ch.usi.dag.rv.MonitorContext;

public class NextEvent extends HasNextCaseEvent{
	public NextEvent(String dexName, String location, Iterator<?> iter) {
		super(dexName, "next "+location, MonitorContext.getContextId(iter));
	}
}
