package ai.problem;

import ai.Action;
import ai.Goal;
import ai.State;
import ai.datastructure.Map;
import ai.datastructure.GraphNode;

import java.util.List;

/**
 * Created by monsio on 7/12/17.
 */
public class FindWayProblemGraph extends SimpleProblem {


    protected Map map;

    public FindWayProblemGraph(Map map) {
        this.map = map;
    }

    @Override
    public List<Action> getActions(State state) {
         return ((GraphNode)state).getArcs();
    }

    @Override
    public State getResult(State state, Action action) {
        return (action).getResult();
    }

    @Override
    public boolean isGoal(State state) {
        return this.goal.isGoal(state);
    }

    @Override
    public void setGoal(Goal goal) {
        this.goal = goal;
    }

    @Override
    public void setInitialState(State state) {
        this.initialState = state;
    }

    @Override
    public State getInitialState() {
        return initialState;
    }

    @Override
    public void setStateVisited(State state) {
        state.setVisited(true);
    }


    @Override
    public void setStateUnVisited(State state) {
        state.setVisited(false);
    }

    @Override
    public void setIsNotLeaf(State state) {
        state.setIsLeaf(false);
    }

    @Override
    public void setIsLeaf(State state) {
        state.setIsLeaf(true);
    }
    @Override
    public boolean isVisited(State rsState) {
        return rsState.isVisited();
    }

    @Override
    public boolean isLeaf(State rsState) {
        return rsState.isLeaf();
    }

    @Override
    public State getLeaf(State state) {
        return state;
    }


    public Map getMap() {
        return map;
    }

    public void setMap(Map map) {
        this.map = map;
    }

    public Goal getGoal() {
        return goal;
    }

    @Override
    public void init() {

        for( GraphNode node : this.map.getNodes().values()) {
            node.init();
        }

    }

    @Override
    public double getGoalCostEstimation(State state) {
        return map.getSLDKM(state.getValue(), goal.getGoal());
    }

}
