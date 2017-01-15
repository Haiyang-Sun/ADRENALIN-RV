package ch.usi.dag.rv;

import ch.usi.dag.rv.utils.DefaultLog;

public abstract class MonitorViolation {
	static int idGen = 0;
	protected int id;
	public MonitorViolation() {
		this.id = ++idGen;
	}
	@Override
	public String toString(){
		return "";
	}
	public void print(){
		DefaultLog.v("Violation("+id+")", this.toString());
	}
}
