package ai.explorationStrategy.standard;

import ai.Action;
import ai.State;
import ai.problem.Problem;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by monsio on 7/12/17.
 */
public class BFS extends Explore {

    @Override
    public List<Action> search(Problem problem) throws StandardSearch.ExplorationFailedException {

        problem.init();

        LinkedList<State> frontier = new LinkedList<State>();
        frontier.add(problem.getInitialState());
        problem.setIsLeaf(problem.getInitialState());

        //frontier.add(problem.getInitialState());

        while( !frontier.isEmpty() ){
            //BFS récuperer le premier element, DFS recuperer le dernier, coût uniforme .
            State state = frontier.removeFirst();
            problem.setIsNotLeaf(state);
            nodesDeployed++;
            //System.out.println(Util.getIdent(state.getDepth())+" STATE :  "+state+" -  COUT CHEMIN : "+state.getWayCost()+" - ESTIMATION : "+state.getPriority());

            //ARBRE : ajouter la valeur du noeud dans un table de hachage
            //GRAPHE : marquer le noeud comme visité
            problem.setStateVisited(state);

            for(Action action : problem.getActions(state)){
                State rsState = problem.getResult(state, action);
                if(rsState == null) continue;
                //System.out.println(Util.getIdent(state.getDepth())+"ACTION "+action+" - LEAF :  "+action.getResult().isLeaf()+" - VISITED : "+action.getResult().isVisited());
                if(!problem.isVisited(rsState) && !problem.isLeaf(rsState) ) {
                    //BFS OU DFS les noeuds s'ajoutent à la fin de la liste
                   this.nodesAddToFrontier++;
                    action.setResult(rsState);
                    rsState.setAction(action);
                    rsState.setSource(state);
                    problem.setIsLeaf(rsState);
                    rsState.setDepth(state.getDepth()+1);
                    rsState.setWayCost(state.getWayCost() + action.getCost());

                   // System.out.println(Util.getIdent(rsState.getDepth())+"AJOUT  CITY :  "+rsState+" -  COUT CHEMIN : "+rsState.getWayCost()+" - ESTIMATION : "+rsState.getPriority());
                    //System.out.println(problem.isGoal(rsState));
                    if(problem.isGoal(rsState)) {
                        //System.out.println("IS GOAL "+this.getSolution(rsState));
                       // System.exit(0);
                        return this.getSolution(rsState);
                    }

                    frontier.add(rsState);

                }
            }

        }

        throw new StandardSearch.ExplorationFailedException();
    }


}
