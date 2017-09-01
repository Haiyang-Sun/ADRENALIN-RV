package ch.usi.dag.rv.nfa;

import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Stack;
import java.util.concurrent.atomic.AtomicInteger;

import ch.usi.dag.rv.jni.RVNativeWrapper;

public class DirectedGraph {
	public DirectedGraph(){
		graphId = id.getAndIncrement();
	}
	int graphId = 0;
	static String _epsilon_ = "___EPSILON___";
	HashSet<String> knownEdgeNames = new HashSet<String>();
	class Node{
		Node(int index){
			this.index = index;
		}
		Node(int index, boolean accepts){
			this.index = index;
		}
		public int index;
		public ArrayList<Edge> edges = new ArrayList<DirectedGraph.Edge>();
	    private boolean accepts = false;
		public ArrayList<Edge> getEdges(String edgeName) {
			ArrayList<Edge> res = new ArrayList<DirectedGraph.Edge>();
			for(Edge edge:edges){
				if(edge.name.equals(edgeName))
					res.add(edge);
			}
			return res;
		}
		
		public Edge getEdge(String edgeName) {
			for(Edge edge:edges){
				if(edge.name.equals(edgeName))
					return edge;
			}
			return null;
		}
		
		public Edge matchEdge(String edgeName) {
			for(Edge edge:edges){
//				System.out.println("comparing "+edgeName + " "+edge.name);
				if(edge.name.equals(edgeName)) {
					return edge;
				}else if(edge.name.startsWith("?")) {
					if(edgeName.contains(edge.name.substring(1)))
						return edge;
				}
			}
			return null;
		}
		
		public void setAccept() {
			this.accepts = true;
		}
		public boolean isAccepting(){
			return this.accepts;
		}
		public boolean isEnd() {
			return this.edges.size()==0;
		}
		public boolean isStart() {
			return index == 0;
		}
		public void removeDuplicateEdge() {
			for(int i = 0; i < edges.size(); i++){
				int smallest = edges.get(i).to.index;
				int smallestIdx = i;
				for(int j = i+1; j < edges.size(); j++){
					if(edges.get(j).to.index < smallest){
						smallestIdx = j;
						smallest = edges.get(j).to.index;
					}
				}
				if(smallestIdx != i){
					//swap
					Edge tmp = edges.get(i);
					edges.set(i, edges.get(smallestIdx));
					edges.set(smallestIdx, tmp);
				}
			}
			ArrayList<Edge> toRemove = new ArrayList<DirectedGraph.Edge>();
			for(int i = 1; i < edges.size(); i++){
				Edge edgeI = edges.get(i);
				for(int j=i-1;j>=0;j--){
					Edge edgeJ = edges.get(j);
					if(edgeJ.to.index != edgeI.to.index)
						break;
					if(edgeJ.name.equals(edgeI.name)){
						toRemove.add(edgeI);
						break;
					}
				}
			}
			for(Edge e: toRemove){
				removeEdge(e);
			}
		}
	}
	class Edge{
		public Edge(Node from, String name, Node to) {
			this.from = from;
			this.name = name;
			this.to = to;
		}
		public String name;
		public Node from;
		public Node to;
		public boolean isEpsilon(){
			return name.equals(_epsilon_);
		}
		boolean restart = false;
		public void setRestart(){
			this.restart = true;
		}
		public boolean isRestarting(){
			return this.restart;
		}
	}
	HashMap<Integer, Node> nodes = new HashMap<Integer, DirectedGraph.Node>();
	ArrayList<Edge> edges = new ArrayList<DirectedGraph.Edge>();
	
	public Node getOrCreateNode(int index){
		if(nodes.containsKey(index))
			return nodes.get(index);
		Node res = new Node(index);
		nodes.put(index, res);
		return res;
	}
	
	public Edge newEdge(int from, String name, int to){
		Edge res = new Edge(getOrCreateNode(from), name, getOrCreateNode(to));
		edges.add(res);
		getOrCreateNode(from).edges.add(res);
		if(!name.equals(_epsilon_))
			knownEdgeNames.add(name);
		return res;
	}
	
	public Collection<Node> getNodes(){
		return nodes.values();
	}
	
	public ArrayList<Edge> getEdges(){
		return edges;
	}
	
	String toDot(){
		/*
		digraph debug_out {
			graph [rankdir=LR];
			3 [shape=box]
			3 -> 4 [label="Name = 'system_server'\nType = 2"];
			4 -> 5 [label="Name = 'check_system_server'\nType = -1"];
			5 -> 0 [label="Name = '&epsilon;'\nType = -1"];
			5 -> 6 [label="Name = '&epsilon;'\nType = -1"];
			0 -> 2 [label="Name = 'check_system_server'\nType = -1"];
			2 -> 1 [label="Name = '&epsilon;'\nType = -1"];
			1 -> 0 [label="Name = '&epsilon;'\nType = -1"];
			1 -> 6 [label="Name = '&epsilon;'\nType = -1"];
			6 -> 7 [label="Name = 'system_server'\nType = 3"];
			7 [peripheries=2];
			}
			*/
		StringBuilder sb = new StringBuilder();
		sb.append("digraph debug_out {\n");
		sb.append("graph [rankdir=LR];\n");
		for(Node node:getNodes()){
			for(Edge e: node.edges){
				String style = "";
				if(e.isRestarting()){
					style="style=dashed";
				}
				sb.append(node.index
						+" -> " + e.to.index
						+" ["+style
						+ " label=\""
						+ e.name
						+ "\"];\n");
			}
			
		}
		for(Node node:getNodes()){
			String shape = "";
			int peripheries = 1;
			if(node.accepts){
				peripheries+=1;
			}
//			if(node.isRestartPoint()){
//				peripheries+=2;
//			}
			if(node.isStart()){
				shape="shape=box";
			}
			sb.append(node.index+" ["+shape+" peripheries="+peripheries+"];\n");
		}
		sb.append("}");
		return sb.toString();
	}
	private void removeEpsilonEdge(Edge edge){
		if(edge.from == edge.to) {
			removeEdge(edge);
			return ;
		}
		if(edge.to.accepts){
			edge.from.setAccept();
		}
		
		
		
//		if(edge.to.index == 0 || edge.to.isRestartPoint()){
//			edge.from.setRestart();
//		}
		//merge edges of from and to to from
		
		Iterator<Edge> iter = edge.to.edges.iterator();
//		boolean canRemoveTo = false;//!edge.to.isStart();
		
		
//		System.out.println("merging "+edge.from.index+" -> "+edge.to.index );
//		
		
		while(iter.hasNext()){
			Edge e = iter.next();
			Edge newEdge = this.newEdge(edge.from.index, e.name, e.to.index);
			if(edge.to.index == 0){
				newEdge.setRestart();
			}
//			if(canRemoveTo) {
//				this.removeEdge(e);
//				iter = edge.to.edges.iterator();
//			}
		}
		this.removeEdge(edge);
//		if(canRemoveTo){
//			nodes.remove(edge.to.index);
//			for(Edge e: edges){
//				if(e.from == edge.to){
//					e.from = edge.from;
//				}
//				if(e.to == edge.to){
//					e.to = edge.from;
//				}
//			}
//		}
		return;
	}

//	static int debug = 0;

//	static int id = 0;
	static AtomicInteger id = new AtomicInteger();
	
	int debug = 0;
	
	public void removeDuplicateEdges(){
		for(Node node: getNodes()){
			node.removeDuplicateEdge();
		}
	}
	
	public void removeEpsilonEdges(){
//		int numRemoved = 0;
		Iterator<Edge> iter = edges.iterator();
		while(iter.hasNext()) {
			Edge e = iter.next();
			if(e.name.equals(_epsilon_)){
				removeEpsilonEdge(e);
				
//				numRemoved++;
				removeDuplicateEdges();
				if(Boolean.getBoolean("rv.debug")) {
					++debug;
					this.dumpToFile(new File("gv/debug."+graphId+"."+ (debug<10?"0":"")+debug +".gv"));
				}
				iter = edges.iterator();
			}
		}
//		
//		if(numRemoved > 0)
//			removeEpsilonEdge();
	}
	
	public void dumpToFile(File f){
		if(RVNativeWrapper.isJVM){
			try{
				FileWriter fw = new FileWriter(f);
				fw.write(this.toDot());
				fw.flush();
				fw.close();
			}catch(Exception e){
				e.printStackTrace();
			}
		}
	}
	
	//currenlty no side effect on knownEdgeNames
	private void removeEdge(Edge edge){
		edge.from.edges.remove(edge);
		edges.remove(edge);
	}

	public boolean startsWith(String transitionName) {
		return nodes.get(0).getEdge(transitionName) != null;
	}

	public Node getStartNode() {
		return nodes.get(0);
	}
	
	//add epsilon transition from each node to start node 
	public void addEpsilonEdgeToStart(){
		for(Node node: getNodes()){
			newEdge(node.index, _epsilon_, 0);
		}
	}
	
	public boolean isEdgeNameKnown(String name){
		return knownEdgeNames.contains(name);
	}
	
	public DirectedGraph nfa2dfa(){
		DirectedGraph input = this;
		HashSet<String> potentialInput = new HashSet<String>();
		for(Edge edge : input.edges){
			potentialInput.add(edge.name);
		}
		HashSet<StateSet> newStates = new HashSet<StateSet>();
		HashMap<StateSet, Integer> indexMap = new HashMap<StateSet, Integer>();
		DirectedGraph res = new DirectedGraph();
		res.setOutMost(this.outMost);
		int index = 0;
		StateSet start = new StateSet();
		start.states.add(index);
		indexMap.put(start, index);
		newStates.add(start); 
		Node newStart = res.getOrCreateNode(index);
		if(input.getStartNode().isAccepting()){
			newStart.setAccept();
		}
		Stack <StateSet> stk = new Stack<StateSet>();
		stk.add(start);
		while(!stk.isEmpty()){
			StateSet current = stk.pop();
			for(String i: potentialInput){
				boolean containAccepts = false;
				boolean allRestart = true;
				//try to add current -- i --> next
				StateSet next = new StateSet();
				for(int state: current.states){
					ArrayList<Edge> edges = input.getOrCreateNode(state).getEdges(i);
					if(edges.size() > 0){
						for(Edge edge : edges){
							if(!edge.isRestarting())
								allRestart = false;
							next.states.add(edge.to.index);
							if(edge.to.isAccepting()){
								containAccepts = true;
							}
						}
					}else{
						continue;
					}
				}
				//next is new
				if(next.states.size()!=0){
					if(!newStates.contains(next)){
						indexMap.put(next, ++index);
						newStates.add(next);
						Node newNode = res.getOrCreateNode(index);
						if(containAccepts)
							newNode.setAccept();
						stk.push(next);
//						System.out.println("adding new state "+index+":"+next+" "+containAccepts);
					}
					int fromStateId = indexMap.get(current);
					int nextStateId = indexMap.get(next);
					Edge newEdge = res.newEdge(fromStateId, i, nextStateId);
					if(allRestart)
						newEdge.setRestart();
				}
			}
		}
		return res;
	}
	static class StateSet{
		HashSet<Integer> states = new HashSet<Integer>();
		@Override
		public int hashCode() {
			int res = 0;
			for(int state: states){
				res += state;
			}
			return res;
		};
		@Override
		public boolean equals(Object obj) {
			if(obj == null || !(obj instanceof StateSet)){
				return false;
			}
			StateSet other = (StateSet)obj;
			if(this.states.size() != other.states.size()){
				return false;
			}
			for(int state: states){
				if(!other.states.contains(state)){
					return false;
				}
			}
			return true;
		}
		@Override
		public String toString() {
			String res = "stateset:";
			for(int state:states){
				res += " "+state;
			}
			return res;
		}		
	}
	boolean outMost = false;
	
	public void setOutMost(boolean v){
		this.outMost = v;
	}
	
	public boolean isOutMost() {
		return this.outMost;
	}
}
