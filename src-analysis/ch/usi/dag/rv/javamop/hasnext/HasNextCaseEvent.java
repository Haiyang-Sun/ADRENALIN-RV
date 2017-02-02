package ch.usi.dag.rv.javamop.hasnext;

import java.util.Iterator;

import ch.usi.dag.rv.Event;
import ch.usi.dag.rv.ProcessorManager;

public class HasNextCaseEvent extends Event {
	static {
		ProcessorManager.addProcessor(new HasNextProcessor());
	}
	Iterator<?> iter;
	public HasNextCaseEvent(String dexName, String desc, Iterator<?> iter) {
		super(dexName, "HasNext-"+desc, System.identityHashCode(iter));
		this.iter = iter;
	}

}
