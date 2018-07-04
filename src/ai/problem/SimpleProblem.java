package ai.problem;

import ai.Action;
import ai.Goal;
import ai.State;

import java.util.HashSet;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by monsio on 7/18/17.
 */
public abstract class SimpleProblem implements Problem {


    protected Goal goal;
    protected State initialState;

    protected Hashtable<Object, State> leafs = new Hashtable<>();
    protected HashSet<Object> visited = new HashSet<>();

    protected State visitedState;


    public SimpleProblem() {
    }

    public SimpleProblem(Goal goal, State initialState) {
        this.goal = goal;
        this.initialState = initialState;
    }

    @Override
    public State getVisitedState() {
        return visitedState;
    }

    public Goal getGoal() {
        return goal;
    }

    @Override
    public void setGoal(Goal goal) {
        this.goal = goal;
    }

    @Override
    public State getInitialState() {
        return initialState;
    }

    @Override
    public void setInitialState(State initialState) {
        this.initialState = initialState;
    }


    @Override
    public boolean isGoal(State state) {
        return this.goal.isGoal(state);
    }


    @Override
    public void setStateVisited(State state) {
        this.visited.add(state.hashCode());
    }

    @Override
    public boolean isVisited(State rsState) {
        return this.visited.contains(rsState.hashCode());
    }

    @Override
    public boolean isLeaf(State rsState) {
        return this.leafs.containsKey(rsState.hashCode());
    }

    @Override
    public State getLeaf( State state) {
        return this.leafs.get(state.hashCode());
    }

    @Override
    public void setIsLeaf(State state) {
        this.leafs.put(state.hashCode(),state);
    }

    @Override
    public void setStateUnVisited(State lastState) {
        this.visited.remove(lastState.hashCode());
    }

    @Override
    public void setIsNotLeaf(State state) {
        this.leafs.remove(state.hashCode());
    }

    @Override
    public void init() {
        this.leafs.clear();
        this.visited.clear();
    }

    @Override
    public double getStateValue(State state) {
        return 0;
    }

    @Override
    public State bestResult(State state, List<Action> actions) {

        List<State> states = new LinkedList<>();

        for(Action action : actions ){

            State rsState = getResult(state, action);

            if(rsState == null) continue;

            rsState.setAction(action);
            rsState.setSource(state);
            states.add(rsState);

        }

        return bestState(states);
    }

    @Override
    public State bestResultUnvisited(State state, List<Action> actions) {

        List<State> states = new LinkedList<>();

        for(Action action : actions ){

            State rsState = getResult(state, action);

            if(rsState == null || isVisited(rsState))
                continue;

            rsState.setAction(action);
            rsState.setSource(state);
            states.add(rsState);

        }

        return bestState(states);
    }

    @Override
    public State bestState(List<State> states){

        double bestValue = Double.NEGATIVE_INFINITY, rsValue;
        State bestState = null;

        for(State rsState : states){

            rsValue = getStateValue(rsState);

            if( rsValue > bestValue ){
                bestValue = rsValue;
                bestState = rsState;
                bestState.setScore(bestValue);
            }

        }

        return bestState;

    }

    @Override
    public State rdmState() {
        return this.initialState;
    }

    @Override
    public List<State> getResults(State state, Action action) {
        throw  new UnsupportedOperationException();
    }

    @Override
    public State getResult(State state, Action action) {
        throw new UnsupportedOperationException();
    }

    @Override
    public double getGoalCostEstimation(State state) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Long getStateKey(State state) {
        return null;
    }

    @Override
    public boolean isVisitedSame(State state) {
        return false;
    }
}
