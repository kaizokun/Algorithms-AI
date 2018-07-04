package ai.explorationStrategy.standard;

import ai.State;
import ai.problem.Problem;

/**
 * Created by monsio on 7/16/17.
 */
public class GFS extends UCS {

    @Override
    protected void setPriority(Problem problem, State rsState) {
        rsState.setPriority(problem.getGoalCostEstimation(rsState));
    }


}
