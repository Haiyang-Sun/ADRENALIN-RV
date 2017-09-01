package ch.usi.dag.rv.nfa;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import ch.usi.dag.rv.MonitorEvent;
import ch.usi.dag.rv.binder.BinderEvent;
import ch.usi.dag.rv.jni.RVNativeWrapper;

/**
 * Created by alexandernorth on 27.03.17.
 */

public class NFA {

//    Inner classes and interfaces

    public final static String EPSILON_TRANSITION_EVENT_NAME = "___EPSILON___";

    public static class TransitionType {

        String eventName;
        int binderType;

        private TransitionType(){}

        public TransitionType(String eventName, int binderType) {
            this.eventName = eventName;
            this.binderType = binderType;
        }

        public TransitionType copy(){
            return new TransitionType(this.eventName, this.binderType);
        }

        @Override
        public String toString() {
            return "TransitionType{" +
                    "eventName='" + eventName + '\'' +
                    ", binderType=" + binderType +
                    '}';
        }

        public String toDotString(){
            return "Name = '" + eventName.replace(EPSILON_TRANSITION_EVENT_NAME, "&epsilon;") + '\'' +
                    "\\nType = " + binderType;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            TransitionType that = (TransitionType) o;

            if (binderType != that.binderType) return false;
            return eventName != null ? eventName.equals(that.eventName) : that.eventName == null;
        }

        @Override
        public int hashCode() {
            int result = eventName != null ? eventName.hashCode() : 0;
            result = 31 * result + binderType;
            return result;
        }

		public String toTransition() {
			if(binderType >=0 ){
				return eventName+":"+binderType;
			} else {
				return eventName;
			}
		}
    }



    public static class State {

        /**
         * Is this an accepting state
         */
        private boolean accepting = false;

        private Map<TransitionType, Transition> transitions = new HashMap<>();


        private State(){}

        public State(boolean accepting){
            this.accepting = accepting;
        }

        /**
         * @return true if state is accepting
         */
        public boolean isAccepting(){
            return accepting;
        }


        public void addDestination(State destination, TransitionType transitionType){
            Transition theTransition = transitions.get(transitionType);
            if (theTransition == null)
                theTransition = new Transition();

            theTransition.addDestinationState(destination);

            transitions.put(transitionType, theTransition);
        }

        public Transition getTransition(TransitionType transitionType){
            return transitions.get(transitionType);
        }

        private State copy(){
            return new State(this.accepting);
        }
    }

    private static class Transition {
        private Set<State> destinationStates = new HashSet<>();

        public void addDestinationState(State destination){
            destinationStates.add(destination);
        }

        public Set<State> getDestinationStates(){
            return destinationStates;
        }
    }


    //    Fields
    private Set<State> currentStates = new HashSet<>(1);
    private State startingState;
    private boolean dead = false;
    private boolean canAccept = false;
    private boolean end = false;

    private boolean isEmpty = true;

    private List<MonitorEvent> matchedEvents = new ArrayList<>();

    public List<MonitorEvent> getMatchedEvents() {
        return new ArrayList<>(matchedEvents);
    }

    //    Constructors
    public NFA(){
        this.startingState = new State(false);
        reset();
    }

    public NFA(State startingState){
        setStartingState(startingState);
    }


    public NFA copy(){
        Set<State> visitedStates = new HashSet<>();
        Set<State> toVisit = new HashSet<>();
        HashMap<State, State> copiedStates = new HashMap<>();

        State startingCopy = this.startingState.copy();
        copiedStates.put(this.startingState, startingCopy);
        toVisit.add(this.startingState);

        while (!toVisit.isEmpty()){
            State currentState = null;

            Iterator<State> visitIter = toVisit.iterator();
            if (visitIter.hasNext()){
                currentState = visitIter.next();

                State currentStateCopy = copiedStates.get(currentState);
                if (currentStateCopy == null) {
                    currentStateCopy = currentState.copy();
                    copiedStates.put(currentState, currentState.copy());
                }

                visitIter.remove();

                if (!visitedStates.contains(currentState)){
                    visitedStates.add(currentState);
                    for (TransitionType transitionType : currentState.transitions.keySet()) {
                        Transition transition = currentState.transitions.get(transitionType);

                        for (State destState: transition.getDestinationStates()) {
                            State copiedState = copiedStates.get(destState);
                            if (copiedState == null) {
                                copiedState = destState.copy();
                                copiedStates.put(destState, copiedState);
                            }
                            currentStateCopy.addDestination(copiedState, transitionType.copy());
                            toVisit.add(destState);
                        }
                    }
                }
            }
        }

        return new NFA(startingCopy);
    }

//    Getters

    public boolean isDead() {
        return dead;
    }

    public boolean isEnd() {
        return end;
    }
    
    public boolean couldAccept(){
//    	return canAccept;
    	for(State state : currentStates){
    		if(state.accepting){
    			return true;
    		}
    	}
    	return false;
    }

    //    Methods

    public void setStartingState(State startingState){
        this.startingState = startingState;
        isEmpty = false;
        reset();
    }


    public void addTransition(State parent, String eventName, State destination){
        addTransition(parent, eventName, destination, -1);
    }

    public void addTransition(State parent, String eventName, State destination, int binderType){

        if (parent == null)
            parent = startingState;

        if (eventName == null &&  binderType >=0 )
            throw new RuntimeException("Cannot have an epsilon transition for a BinderEvent!");
        else if (eventName == null)
            eventName = EPSILON_TRANSITION_EVENT_NAME;

        isEmpty = false;

        parent.addDestination(destination, new TransitionType(eventName, binderType));
    }



    public State newStateWithTransition(State parent, String eventName, boolean accepting){
        State s = new State(accepting);
        addTransition(parent, eventName, s);
        return s;
    }



// TODO: FIX
    @SuppressWarnings("unchecked")
    public Set<State> transition(MonitorEvent event, boolean direct) throws IOException, ClassNotFoundException {

//        if (monitorEvent != null && monitorEvent instanceof BinderEvent)
//            throw new RuntimeException("Call transitionBinder() to transition on a BinderEvent!");
        TransitionType transitionType;

        if (event instanceof BinderEvent){
            BinderEvent be = (BinderEvent) event;

        	if (be.getType() == 0 || (be.getType() == 5 && RVNativeWrapper.getByBinderEvent(be)==0))
                return this.currentStates;
            
            
       
            transitionType = new TransitionType(be.getTransitionName(), be.getType());

//            if(binderEvent.getType()==0  ||  binderEvent.getType()==1 || binderEvent.getType() == 4 || binderEvent.getType() == 2){
//            	return currentStates;
//            }

            _doTransition(transitionType, event);

            if (be.getType() == 5 && !this.dead && direct) {
            	int datFlag = RVNativeWrapper.isJVM?
            			RVNativeWrapper.getByBinderEvent(be)
            		:
            			be.getFlag();
	            	if(datFlag > 0) {
	            		byte[] mel = RVNativeWrapper.getByFlag(be.getToPid(),datFlag 
	                		);
	            		if(mel == null){
	            			System.out.println("cannot be null " + be);
	            		}
	                ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(mel));
	
	                List<MonitorEvent> matchedEventList = (List<MonitorEvent>)ois.readObject();
	
	                for (MonitorEvent monitorEvent : matchedEventList) {
	                	System.out.println("event from other process "+monitorEvent +" for "+event);
	                }
	                
	                for (MonitorEvent monitorEvent : matchedEventList) {
	                    this.transition(monitorEvent, false);
	                    if(this.dead)
	                    	break;
	                }
            	}
            }

            return this.currentStates;
    		
        }else{
            transitionType = new TransitionType(event.getTransitionName(), -1);
            return _doTransition(transitionType, event);
        }



//        return _doTransition(transitionType, event);
    }

//    public Set<State> transitionBinder(BinderEvent binderEvent){
//        TransitionType transitionType = new TransitionType(binderEvent.getTransitionName(), binderEvent.getType());
//        return _doTransition(transitionType, binderEvent);
//    }

    private Set<State> _doTransition(TransitionType transitionType, MonitorEvent monitorEvent){
        Set<State> newCurrentStates = new HashSet<>();
        for (State s : this.currentStates) {
            Transition transition = s.getTransition(transitionType);
            if (transition != null){
                newCurrentStates.addAll(transition.getDestinationStates());
            }
        }

        currentStates = _epsilonClosure(newCurrentStates);
        if (currentStates.size() == 0)
            dead = true;
        else{
            end = true;
            for (State s: currentStates) {
                if (s.transitions.size() > 0){
                    end = false;
                    break;
                }
            }
        }
        if(dead){
        	//System.out.println("dead at "+monitorEvent);
        	int idx= 0 ;
//        	for(MonitorEvent e:this.matchedEvents){
//        		System.out.println("\t"+ ++idx +":" + e);
//        	}
        }
        if(end){
//        	System.out.println("end at "+monitorEvent);
        }

//        Add matched events if not BinderEvent
//        if (!(monitorEvent instanceof BinderEvent)){
            matchedEvents.add(monitorEvent);
//        }

        return currentStates;
    }

    private void _start(){
        currentStates = _epsilonClosure(currentStates);
    }

    private Set<State> _epsilonClosure(Set<State> currentStates){
        Set<State> newCurrentStates = new HashSet<>(currentStates);
        boolean newStatesAdded = false;

        Set<State> lastEpsFound = new HashSet<>(newCurrentStates);
        do {
            Set<State> epsThisLoop = new HashSet<>(lastEpsFound);
            lastEpsFound = new HashSet<>();

            for (State s : epsThisLoop) {
                Transition epsTransition = s.getTransition(new TransitionType(EPSILON_TRANSITION_EVENT_NAME, -1));
                if (epsTransition != null){
                    lastEpsFound.addAll(epsTransition.getDestinationStates());
                }
            }

            newStatesAdded = newCurrentStates.addAll(lastEpsFound);
        }while (newStatesAdded);

        return newCurrentStates;
    }

    public boolean accepts(){
        for (State s : this.currentStates) {
            if (s.isAccepting())
                return true;
        }
        return false;
    }

    public boolean startsWith(MonitorEvent monitorEvent) {

        Set<State> initialStates = new HashSet<>();

        initialStates.add(this.startingState);
        initialStates = _epsilonClosure(initialStates);

        for (State state : initialStates) {
            for (TransitionType transitionType : state.transitions.keySet()) {
                if (transitionType.eventName.equals(monitorEvent.getTransitionName())){
                    return true;
                }
            }
        }
        return false;
    }

    public void reset(){
        matchedEvents.clear();
        currentStates.clear();
        currentStates.add(startingState);
        dead = false;
        end = false;
        canAccept = false;
        _start();
    }

    public NFA concatenate(NFA nfa){
        nfa = nfa.copy();

        if (isEmpty){
            setStartingState(nfa.startingState);
        }else{
            Set<State> oldStates = getStates();
            for (State state : oldStates) {
                if (state.accepting) {
//                    addTransition(state, EPSILON_TRANSITION_EVENT_NAME, nfa.startingState);

                    for (Map.Entry<TransitionType, Transition> transitionEntry: nfa.startingState.transitions.entrySet()) {
                        for (State destState: transitionEntry.getValue().getDestinationStates()) {
                            addTransition(state, transitionEntry.getKey().eventName, destState, transitionEntry.getKey().binderType);
                        }
                    }

                    state.accepting = nfa.startingState.isAccepting();
                }
            }
            reset();
        }

        return this;

    }

    public NFA or(NFA nfa){
        nfa = nfa.copy();

        State oldStart = this.startingState;
        State newStart = new State(false);
        setStartingState(newStart);

        addTransition(newStart, EPSILON_TRANSITION_EVENT_NAME, oldStart);
        addTransition(newStart, EPSILON_TRANSITION_EVENT_NAME, nfa.startingState);

        reset();
//        transition(EPSILON_TRANSITION_EVENT_NAME, false);

        return this;
    }

    public NFA star(){
        State oldStart = this.startingState;

        Set<State> origStates = getStates();

        State n0 = new State(false);
//        State n1 = new State(false);
        State n1 = new State(false);
        State n2 = new State(true);

        setStartingState(n0);



        for (State state : origStates) {
            if (state.accepting) {
                addTransition(state, null, n1);
                state.accepting = false;
            }
        }

//        addTransition(n1, null, oldStart);


        addTransition(n0, null, oldStart);
        addTransition(n0, null, n2);

        addTransition(n1, null, oldStart);
        addTransition(n1, null, n2);

        reset();
//        transition(null, false);

        return this;

    }

    public NFA questionMark(){

        State oldStart = this.startingState;

        Set<State> origStates = getStates();

        State n0 = new State(false);
        State n1 = new State(true);

        setStartingState(n0);

        for (State state : origStates) {
            if (state.accepting) {
                addTransition(state, null, n1);
                state.accepting = false;
            }
        }
        addTransition(n0, null, oldStart);
        addTransition(n0, null, n1);

        reset();
        return this;
    }

    public NFA plus(){

//        Simple implementation
//        NFA copy = this.copy();
//        return this.concatenate(copy.star());

        Set<State> origStates = getStates();

        State a = new State(false);
        State b = new State(true);

        for (State state : origStates) {
            if (state.accepting) {
                addTransition(state, null, a);
                state.accepting = false;
            }
        }

        addTransition(a, null, b);
        addTransition(a, null, this.startingState);

        reset();
        return this;
    }


    // Doesn't apply for NFAs

//    public void complement(){
//        for (State state : getStates()) {
//            state.accepting = !state.accepting;
//        }
//    }

    private synchronized Set<State> getStates(){

        Set<State> visitedStates = new HashSet<>();
        Set<State> toVisit = new HashSet<>();

        toVisit.add(this.startingState);

        while (!toVisit.isEmpty()){
            State currentState = null;

            Iterator<State> visitIter = toVisit.iterator();
            if (visitIter.hasNext()){
                currentState = visitIter.next();
                visitIter.remove();

                if (!visitedStates.contains(currentState)){
                    visitedStates.add(currentState);
                    for (Transition transition : currentState.transitions.values()) {
                        toVisit.addAll(transition.getDestinationStates());
                    }
                }
            }
        }

        return visitedStates;

    }

    public static NFA recognizesEventName(String eventName, String pname){
        State initial = new State(false);
        NFA nfa = new NFA(initial);
        State recog = new State(true);
        nfa.addTransition(initial, eventName+"_"+pname, recog, -1);
        return nfa;
    }

    public static NFA recognizesBinderEvent(String pname, int type){
        NFA.State initial = new NFA.State(false);
        NFA nfa = new NFA(initial);
        NFA.State recog = new NFA.State(true);
        nfa.addTransition(initial, pname, recog, type);
        return nfa;
    }


    public String toDotFile(){
        StringBuilder sb = new StringBuilder();
        Set<State> visitedStates = new HashSet<>();
        Set<State> toVisit = new HashSet<>();



        int stateID = 0;

        Set<State> states = getStates();
        Map<State, Integer> stateIntegerHashMap = new HashMap<>(states.size());

        for (State s: states) {
            stateIntegerHashMap.put(s, stateID);
            ++stateID;
        }

        sb.append("digraph debug_out {\n");
//        Landscape graph
        sb.append("graph [rankdir=LR];\n");

        sb.append(stateIntegerHashMap.get(this.startingState)).append(" [shape=box] \n");


        toVisit.add(this.startingState);

        while (!toVisit.isEmpty()){
            State currentState = null;

            Iterator<State> visitIter = toVisit.iterator();
            if (visitIter.hasNext()){
                currentState = visitIter.next();
                visitIter.remove();

                if (!visitedStates.contains(currentState)){
                    visitedStates.add(currentState);

                    if (currentState.isAccepting()){
                        sb.append(stateIntegerHashMap.get(currentState)).append(" [peripheries=2];\n");
                    }

                    for (Map.Entry<TransitionType, Transition> transitionEntry : currentState.transitions.entrySet()) {
                        for (State destState: transitionEntry.getValue().getDestinationStates()) {

                            String labelName = transitionEntry.getKey().toDotString();

                            sb.append(stateIntegerHashMap.get(currentState)).append(" -> ").append(stateIntegerHashMap.get(destState))
                            .append(" [label=\"").append(labelName).append("\"];\n");

                            toVisit.add(destState);
                        }
                    }
                }
            }
        }
        sb.append("}\n");
        return sb.toString();
    }

    public DirectedGraph toDirectedGraphNoEpsilon(){
    	DirectedGraph dg = toDirectedGraph();
    	dg.removeEpsilonEdges();
    	return dg;
    }
    public DirectedGraph toDirectedGraph(){

        DirectedGraph directedGraph = new DirectedGraph();
        directedGraph.setOutMost(this.outMost);
        Set<State> visitedStates = new HashSet<>();
        Set<State> toVisit = new HashSet<>();

        
        Set<State> states = getStates();
        Map<State, Integer> stateIntegerHashMap = new HashMap<>(states.size());

        stateIntegerHashMap.put(this.startingState, 0);
        states.remove(this.startingState);

        int stateID = 1;

        for (State s: states) {
            stateIntegerHashMap.put(s, stateID);
            ++stateID;
        }

        toVisit.add(this.startingState);

        while (!toVisit.isEmpty()){
            State currentState = null;

            Iterator<State> visitIter = toVisit.iterator();
            if (visitIter.hasNext()){
                currentState = visitIter.next();
                visitIter.remove();

                if (!visitedStates.contains(currentState)){
                    visitedStates.add(currentState);

                    if (currentState.isAccepting()){
                        directedGraph.getOrCreateNode(stateIntegerHashMap.get(currentState)).setAccept();
                    }

                    for (Map.Entry<TransitionType, Transition> transitionEntry : currentState.transitions.entrySet()) {
                        for (State destState: transitionEntry.getValue().getDestinationStates()) {

//                            if (EPSILON_TRANSITION_EVENT_NAME.equals(transitionEntry.getKey().eventName)) {
//
//                            }

                            directedGraph.newEdge(stateIntegerHashMap.get(currentState), transitionEntry.getKey().toTransition(), stateIntegerHashMap.get(destState));

                            toVisit.add(destState);
                        }
                    }
                }
            }
        }
        return directedGraph;
    }

    boolean outMost = false;
    
	public void setOutMost(boolean b) {
		this.outMost = b;
	}
}
