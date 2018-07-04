package ai.explorationStrategy.local;

import ai.Action;
import ai.State;
import ai.problem.Problem;

import java.util.List;

public class HillClimbing extends LocalSearch {

    private HillClimbingConfig config;

    public HillClimbing(){
        this.config = new HillClimbingConfig();
    }

    public HillClimbing(HillClimbingConfig config) {
        this.config = config;
    }

    @Override
    public State search(Problem problem ) throws ExplorationFailedException {

        problem.init();

        State currentState = problem.getInitialState();
        State bestState;
        double currentValue, bestValue;

        int lateralMovesCount = 0;
        int neighbourTestCount = 0;

        this.nodeDeployed = 0;

        while( !config.stopLoop(lateralMovesCount, neighbourTestCount) ) {

            List<Action> actions = problem.getActions(currentState);

            bestState = problem.bestResult(currentState, actions);

            if(bestState == null){
                neighbourTestCount ++;
                continue;
            }

            bestValue = bestState.getScore();

            currentValue = problem.getStateValue(currentState);

            //pas d'amélioration parmis les états voisins
            if( currentValue > bestValue ){
               // System.out.println("WORSE");
                //on incremente la variable indiquant qu'il n y a pas eu d'amélioration
                //au cas ou le voisinage est généré aléatoirement et que tout les voisins n'ont pas été exploré
                //par exemple si le facteur de branchement est continu le voisinage peut être infiniment grand
                //dans ce cas on generera à nouveau un voisinage.
                //si le voisinage est discret et peut être généré en une fois incrementer ce compteur arretera la boucle.
                neighbourTestCount ++;
                //si l'on comptait des mouvements lateraux on reinitialise à zero
                lateralMovesCount = 0;

            }else if(currentValue == bestValue){
               // System.out.println("EQUAL");

                //si l'on trouve un état  de valeur égale ( pourrait être le même )
                //on compte le nombre de deplacement lateraux qui quand il depassera
                // une certaine limite arretera la recherche
                lateralMovesCount++;
                //si l'on comptait des regeneration de voisinage on reinitialise à zero
                neighbourTestCount = 0;
                currentState = bestState;

            }else{
                //System.out.println("BETTER");

                //si l'on comptait des mouvements lateraux on reinitialise à zero
                lateralMovesCount = 0;
                //si l'on comptait des regeneration de voisinage on reinitialise à zero
                neighbourTestCount = 0;
                currentState = bestState;

            }

            nodeDeployed ++;
        }

        return currentState;

    }


}
