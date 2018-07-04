package ai.explorationStrategy.local;

import ai.State;
import ai.explorationStrategy.Search;
import ai.problem.Problem;

import java.util.ArrayList;
import java.util.List;

public class ParticleSwarm extends LocalSearch {

    private ParticleSwarmConfig config;

    public ParticleSwarm(ParticleSwarmConfig config) {
        this.config = config;
    }

    @Override
    public State search(Problem problem) throws ExplorationFailedException {

        problem.init();

        List<State> solutions = new ArrayList<>();

        for(int i = 0 ;  i < config.getNb_individus() ; i ++){
            solutions.add(problem.rdmState());
        }

        State bestState = problem.bestState(solutions);
        State bestStateCurrent = bestState;

        int iterationCount = 0;

        while(!config.stopLoop(iterationCount)){

            config.updateSolutions(problem, bestState, bestStateCurrent, solutions);

            bestStateCurrent = problem.bestState(solutions);

            if(bestStateCurrent.getScore() > bestState.getScore())
                bestState = bestStateCurrent;

            iterationCount ++;

        }

        return bestState;

    }

}
