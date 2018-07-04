package ai.agent;

import ai.*;
import ai.explorationStrategy.Search;
import ai.explorationStrategy.standard.StandardSearch;
import ai.problem.Problem;

public class NotDeterministicProblemAgent extends SimpleSolveProblemAgent{

    public NotDeterministicProblemAgent() {
    }

    public NotDeterministicProblemAgent(State state, Problem problem, Search searchStrategy, Goal goal) {
        super(state, problem, searchStrategy, goal);
    }

    public NotDeterministicProblemAgent(State state, Problem problem, Search searchStrategy) {
        super(state, problem, searchStrategy);
    }

    @Override
    protected Action getNextAction() {
        Plan solution = (Plan) this.solution;
        return solution.getNextAction(this.state);
    }

    @Override
    protected void updateSolution() {
        Plan solution = (Plan) this.solution;
        this.solution = solution.getNextPlan(this.state);
    }

    @Override
    protected boolean solutionIsEmpty(){
        return this.solution == null;
    }

}
