package ch.usi.dag.rv.javamop.hasnext;

import java.util.Iterator;

public class NextEvent extends HasNextCaseEvent{
	public NextEvent(String dexName, String location, Iterator<?> iter) {
		super(dexName, "next "+location, System.identityHashCode(iter));
	}
}
