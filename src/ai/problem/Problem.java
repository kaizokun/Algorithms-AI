package ai.problem;

import ai.Action;
import ai.Goal;
import ai.State;

import java.util.List;

/**
 * Created by monsio on 7/12/17.
 */
public interface Problem {

    List<Action> getActions(State state);

    State getResult(State state, Action action);

    List<State> getResults(State state,Action action);

    boolean isGoal(State state);

    void setInitialState(State state);

    void setGoal(Goal goal);

    State getInitialState();

    void setStateVisited(State state);

    State getVisitedState();

    boolean isVisited(State rsState);

    boolean isLeaf(State rsState);

    State getLeaf( State state);

    void setIsLeaf(State state);

    void init();

    double getGoalCostEstimation(State state);

    double getStateValue(State state);

    void setStateUnVisited(State state);

    void setIsNotLeaf(State state);

    State bestResult(State state, List<Action> action);

    State bestResultUnvisited(State state, List<Action> actions);

    State bestState( List<State> states);

    State rdmState();

    Long getStateKey(State state);

    boolean isVisitedSame(State state);
}
