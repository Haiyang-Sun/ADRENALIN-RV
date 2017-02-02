package ch.usi.dag.rv.javamop.hasnext;

import java.util.Iterator;

public class HasNextEvent extends HasNextCaseEvent{
	public HasNextEvent(String dexName, String location, Iterator<?> iter) {
		super(dexName, "has next "+location, iter);
	}
}
