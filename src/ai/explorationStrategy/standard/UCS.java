package ai.explorationStrategy.standard;

import ai.Action;
import ai.State;
import ai.problem.Problem;
import dataStructure.tas.TasMin;

import java.util.List;

/**
 * Created by monsio on 7/16/17.
 */
public class UCS extends Explore{

    public List<Action> search(Problem problem) throws StandardSearch.ExplorationFailedException {

        problem.init();

        //System.out.println("UCS INITIAL STATE "+problem.getInitialState());

        TasMin frontier = new TasMin();
        frontier.insert(problem.getInitialState());
        problem.setIsLeaf(problem.getInitialState());

        this.nodesDeployed = 0 ;
        this.nodesAddToFrontier = 0;

        while( !frontier.isEmpty()){

            this.nodesDeployed++;
            //BFS récuperer le premier element, DFS recuperer le dernier, coût uniforme .
            State state = (State) frontier.extractMin();

            problem.setIsNotLeaf(state);

            //System.out.println(Util.getIdent(state.getDepth())+"  STATE : "+state+" -  COUT CHEMIN : "+state.getWayCost()+" - ESTIMATION : "+state.getPriority()+" "+problem.isVisited(state));

            //ARBRE : ajouter la valeur du noeud dans un table de hachage
            //GRAPHE : marquer le noeud comme visité
            problem.setStateVisited(state);

            //System.out.println(Util.getIdent(state.getDepth())+"  STATE : "+state+" -  COUT CHEMIN : "+state.getWayCost()+" - ESTIMATION : "+state.getPriority()+" "+problem.isVisited(state));

            if(problem.isGoal(state)) {
               // System.out.println("GOAL");
                //System.out.println(state);
                return this.getSolution(state);
            }

            for(Action action : problem.getActions(state)){

                //System.out.println("ACTION COST "+action);
                State rsState = problem.getResult(state, action);
                if(rsState == null) continue;
                //System.out.println(Util.getIdent(state.getDepth())+"ACTION "+action.getActionName()+" - LEAF :  "+problem.isLeaf(action.getResult())+" - VISITED : "+problem.isVisited(action.getResult()));
                double wayCost = state.getWayCost() + action.getCost();
                if( !problem.isLeaf(rsState)) {

                    if(!problem.isVisited(rsState)) {
                        this.nodesAddToFrontier ++;
                        //BFS OU DFS les noeuds s'ajoutent à la fin de la liste
                        //System.out.println(action.getCost());
                        action.setResult(rsState);
                        rsState.setAction(action);
                        rsState.setSource(state);
                        problem.setIsLeaf(rsState);
                        rsState.setDepth(state.getDepth() + 1);
                        rsState.setWayCost(wayCost);
                        this.setPriority(problem, rsState);

                        //System.out.println(Util.getIdent(rsState.getDepth())+"AJOUT  :  "+rsState+" -  COUT CHEMIN : "+rsState.getWayCost()+" - ESTIMATION : "+rsState.getPriority());

                        frontier.insert(rsState);
                    }

                }else {

                    rsState = problem.getLeaf(rsState);

                    if (/*problem.isLeaf(rsState) &&*/ rsState.getWayCost() > wayCost) {

                        this.nodesAddToFrontier ++;
                        action.setResult(rsState);
                        rsState.setAction(action);
                        rsState.setSource(state);
                        rsState.setDepth(state.getDepth() + 1);
                        rsState.setWayCost(wayCost);

                        //this.setPriority(problem, rsState);

                        /*!!!! important ici ne pas modifier la priorité directement
                        * mais passer par la file de priorité pour mettre à jour la valeur de priorité est réorganiser la file
                        * Sans quoi l'etat mis à jour ne sera jamais selectionné au bon moment meme si ca
                         * priorité est modifiée il se trouvera au mauvais endroit dans la file*/
                        try {
                            frontier.upKey(rsState, this.getPriority(problem,rsState));
                        } catch (Exception e) {
                            //e.printStackTrace();
                        }

                    }
                }
            }


        }

        throw new StandardSearch.ExplorationFailedException();
    }


    protected void setPriority(Problem problem, State rsState){
        rsState.setPriority(rsState.getWayCost());

    }

    protected double getPriority(Problem problem, State rsState){
        return rsState.getWayCost();
    }

}

