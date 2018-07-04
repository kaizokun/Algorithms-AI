package ai.explorationStrategy.local;

import ai.State;
import ai.problem.Problem;

import java.util.List;

public class ParticleSwarmConfig extends LocalSearchConfig {

    private int nb_individus;

    /**
     * iterationLimit = 200
     * nb_individus = 30
     * **/
    public ParticleSwarmConfig(){
        this.iterationLimit = 200;
        this.nb_individus = 30;
    }

    public ParticleSwarmConfig(int nb_individus, int iterationLimit) {
        this.nb_individus = nb_individus;
        this.iterationLimit = iterationLimit;
    }

    public int getNb_individus() {
        return nb_individus;
    }

    public void setNb_individus(int nb_individus) {
        this.nb_individus = nb_individus;
    }

    public boolean stopLoop(int iterationCount) {
        return (iterationCount > iterationLimit);
    }

    public void updateSolutions(Problem problem, State bestState, State bestStateCurrent, List<State> solutions){
        throw new UnsupportedOperationException();
    }

}
