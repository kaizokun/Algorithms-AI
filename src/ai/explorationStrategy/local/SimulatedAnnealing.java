package ai.explorationStrategy.local;

import ai.Action;
import ai.State;
import ai.problem.Problem;
import java.util.List;
import java.util.Random;

public class SimulatedAnnealing extends LocalSearch {

    private SimulatedAnnealingConfig config;

    public SimulatedAnnealing() {
        this.config = new SimulatedAnnealingConfig();
    }

    public SimulatedAnnealing(SimulatedAnnealingConfig config) {
        this.config = config;
    }

    @Override
    public State search(Problem problem) throws ExplorationFailedException {

        problem.init();

        Random random = new Random();

        State currentState = problem.getInitialState();
        currentState.setScore(problem.getStateValue(currentState));

        State bestState, bestStateEver = currentState;

        double T = this.config.getInitialTemperature();

        int iterationCount = 0;
        int neighbourTestCount = 0;

        while ( !config.stopLoop(iterationCount, neighbourTestCount, T) ){

            T = config.decreaseTemperature(T);
            iterationCount ++;

            List<Action> actions = problem.getActions(currentState);

            /*interdire les etats déja visité ameliore le resultat*/
            /*
            problem.setStateVisited(currentState);
            bestState = problem.bestResultUnvisited(currentState, actions);
*/
            bestState = problem.bestResult(currentState, actions);

            if(bestState == null){
                //Si le meilleur état est null c'est que l'on a pu generer aucun successeur
                //si les successeurs sont generes aléatoirement ou pourrait en generer à nouveau
                //jusqu'à une certaine limite
                //Si tout les successeurs sont generes à chaque fois on s'arette des que cette variable vaut 1.
                neighbourTestCount ++;
                continue;
            }

            double deltaE = problem.getStateValue(currentState) - problem.getStateValue(bestState);

            double prob = -1;

            if( deltaE < 0 || random.nextDouble() < (prob = Math.exp( -deltaE / T ))){

                if(bestState.getScore() > bestStateEver.getScore()){
                    bestStateEver = bestState;
                    //System.out.println("++++ EVER : "+bestStateEver+" "+bestStateEver.getScore());
                }

                //System.out.println("CURRENT : "+bestState+" "+bestState.getScore()+" "+prob);

                currentState = bestState;
                neighbourTestCount = 0;

            }else {
                //si on ne selectionne pas le meilleur état successeur on augmente cette variable
                //si les successeurs sont generes aleatoirement on peut tester d'en generer d'autres
                //en esperant avoir un meilleur resultat qu imodifira l'état courant.
                //Si il sont tous générés en une fois et que l'on a pas modifié l'état courant inutile de continuer
                neighbourTestCount++;
            }

        }

        this.nodeDeployed = iterationCount;

        return bestStateEver;
    }

}
