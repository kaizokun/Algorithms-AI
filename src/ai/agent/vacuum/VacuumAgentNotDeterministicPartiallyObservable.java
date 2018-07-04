package ai.agent.vacuum;

import ai.State;
import ai.agent.NotDeterministicProblemAgent;
import ai.explorationStrategy.AndOrSearch;
import ai.explorationStrategy.Search;
import ai.explorationStrategy.standard.StandardSearch;
import ai.problem.NotDeterministicPartiallyObservableVacuumProblem;
import ai.problem.Problem;

public class VacuumAgentNotDeterministicPartiallyObservable extends NotDeterministicProblemAgent {
/*
    public VacuumAgentNotDeterministicFullyObservable() {
    }

    public VacuumAgentNotDeterministicFullyObservable(State state, Problem problem, Search searchStrategy, Goal goal) {
        super(state, problem, searchStrategy, goal);
    }
*/
    public VacuumAgentNotDeterministicPartiallyObservable(State state, Problem problem, Search searchStrategy) {
        super(state, problem, searchStrategy);
    }

    public static void main(String[] args) {

        Problem problem = new NotDeterministicPartiallyObservableVacuumProblem(3,4);
        State initialState = problem.rdmState();
        Search search = new AndOrSearch();

        VacuumAgentNotDeterministicPartiallyObservable vacuumAgent = new VacuumAgentNotDeterministicPartiallyObservable(initialState, problem, search);

       // System.out.println(initialState);

        try {

            vacuumAgent.start(true);

            System.out.println("FINAL STATE : \n"+((NotDeterministicPartiallyObservableVacuumProblem)problem).getVacuumEnvironmentState());

        } catch (StandardSearch.ExplorationFailedException e) {
            e.printStackTrace();
        }

    }

}
