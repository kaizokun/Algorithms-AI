package ai.explorationStrategy.standard;

import ai.State;
import ai.problem.Problem;

/**
 * Created by monsio on 7/16/17.
 */
public class Astar extends UCS{

    @Override
    protected void setPriority(Problem problem, State rsState) {
        rsState.setPriority(rsState.getWayCost()+problem.getGoalCostEstimation(rsState));
    }

    @Override
    protected double getPriority(Problem problem, State rsState) {
        return rsState.getWayCost()+problem.getGoalCostEstimation(rsState);
    }
}
