package ai.explorationStrategy.local;

import ai.Action;
import ai.State;
import ai.problem.Problem;

import java.util.LinkedList;
import java.util.List;

public class TabooSearch extends LocalSearch {

    private TabooSearchConfig config;

    public TabooSearch(TabooSearchConfig config) {
        this.config = config;
    }

    public TabooSearch() {
        this.config = new TabooSearchConfig();
    }

    @Override
    public State search(Problem problem) throws ExplorationFailedException {

        problem.init();

        State currentState = problem.getInitialState();
        State bestState, bestStateEver = currentState;
        bestStateEver.setScore(Double.NEGATIVE_INFINITY);

        LinkedList<State> visited = new LinkedList<>();
        visited.add(currentState);
        problem.setStateVisited(currentState);

        int iterationCount = 0;
        int neighbourTestCount = 0;

        this.nodeDeployed = 0;

        while( !config.stopLoop(neighbourTestCount, iterationCount) ) {

            iterationCount ++;

            List<Action> actions = problem.getActions(currentState);

            bestState = problem.bestResultUnvisited(currentState, actions);

            //pas de voisinage ou tous visité
            if(bestState == null){
                neighbourTestCount ++;
                continue;
            }

            //on a obtenu le meilleur etat parmis les successeurs
            problem.setStateVisited(bestState);
            visited.add(bestState);

            if(visited.size() > config.getVisitedMaxSize()){
                State firstVisited = visited.removeFirst();
                problem.setStateUnVisited(firstVisited);
            }

            currentState = bestState;

           // System.out.println("CURRENT : "+bestState+" "+bestState.getScore());

            if(bestState.getScore() > bestStateEver.getScore()){
                bestStateEver = bestState;
                //System.out.println("++++ EVER : "+bestStateEver+" "+bestStateEver.getScore());
            }

            neighbourTestCount = 0;

        }

        this.nodeDeployed = iterationCount;

        return bestStateEver;

    }


}
