package ai.agent.vacuum;

import ai.State;
import ai.agent.SimpleSolveProblemAgent;
import ai.explorationStrategy.Search;
import ai.explorationStrategy.standard.LDFS;
import ai.explorationStrategy.standard.StandardSearch;
import ai.problem.Problem;
import ai.problem.VacuumProblem;

public class VacuumAgentFullyObservable extends SimpleSolveProblemAgent {
/*
    public VacuumAgentNotDeterministicFullyObservable() {
    }

    public VacuumAgentNotDeterministicFullyObservable(State state, Problem problem, Search searchStrategy, Goal goal) {
        super(state, problem, searchStrategy, goal);
    }
*/
    public VacuumAgentFullyObservable(State state, Problem problem, Search searchStrategy) {
        super(state, problem, searchStrategy);
    }

    public static void main(String[] args) {

        Problem problem = new VacuumProblem(3,3);
        State initialState = problem.rdmState();
        Search search = new LDFS();

        VacuumAgentFullyObservable vacuumAgent = new VacuumAgentFullyObservable(initialState, problem, search);

        System.out.println(initialState);

        try {

            vacuumAgent.start(true);

        } catch (StandardSearch.ExplorationFailedException e) {
            e.printStackTrace();
        }

    }

}
