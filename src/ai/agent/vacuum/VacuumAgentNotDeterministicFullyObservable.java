package ai.agent.vacuum;

import ai.State;
import ai.agent.NotDeterministicProblemAgent;
import ai.explorationStrategy.AndOrSearch;
import ai.explorationStrategy.Search;
import ai.explorationStrategy.standard.StandardSearch;
import ai.problem.NotDeterministicVacummProblem;
import ai.problem.Problem;

public class VacuumAgentNotDeterministicFullyObservable extends NotDeterministicProblemAgent {
/*
    public VacuumAgentNotDeterministicFullyObservable() {
    }

    public VacuumAgentNotDeterministicFullyObservable(State state, Problem problem, Search searchStrategy, Goal goal) {
        super(state, problem, searchStrategy, goal);
    }
*/
    public VacuumAgentNotDeterministicFullyObservable(State state, Problem problem, Search searchStrategy) {
        super(state, problem, searchStrategy);
    }

    public static void main(String[] args) {

        Problem problem = new NotDeterministicVacummProblem(3,1);
        State initialState = problem.rdmState();
        Search search = new AndOrSearch();

        VacuumAgentNotDeterministicFullyObservable vacuumAgent = new VacuumAgentNotDeterministicFullyObservable(initialState, problem, search);

        System.out.println(initialState);

        try {

            vacuumAgent.start(true);

        } catch (StandardSearch.ExplorationFailedException e) {
            e.printStackTrace();
        }

    }

}
