package ai.explorationStrategy.standard;

import ai.Action;
import ai.State;
import ai.problem.Problem;
import util.Util;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by monsio on 7/13/17.
 */
public class DFS extends Explore {


    public List<Action> search(Problem problem) throws ExplorationFailedException {
        return this.search(problem,Integer.MAX_VALUE);
    }

    protected List<Action> search(Problem problem, int limit) throws ExplorationFailedException {

        boolean showLog = false;

        if(showLog)
            System.out.println("SEARCH");

        //LinkedList<State> frontier = new LinkedList<State>();
        problem.init();

        // System.out.println("LIMIT : "+limit);
        LinkedList<State> frontier = new LinkedList<State>();
        frontier.add(problem.getInitialState());
        problem.setIsLeaf(problem.getInitialState());


        //frontier.add(problem.getInitialState());
        int lastDepth = -1;
        while( !frontier.isEmpty()/* && nodesDeployed < 100 */){


            //BFS récuperer le premier element, DFS recuperer le dernier, coût uniforme .
            State state = frontier.getLast();
            String ident = Util.getIdent(state.getDepth());
            this.nodesDeployed++;
            //Le DFS avec une boucle necessite de pouvoir demarquer un noeud come visité lorsque
            //l'on a terminé d'explorer ses enfants.
            //pour cela au lien de le retirer de la frontiere on le laisse
            //si on retombe sur un noeud visité precedemment
            //il possede une profondeur inferieur à celui qui vient d'être visité
            //c'est que l'on a visité tout ses enfants on peut le retirer et le demarquer
            if(state.getDepth() < lastDepth){
                problem.setStateUnVisited(frontier.removeLast());
                lastDepth = state.getDepth();
                continue;
            }

            lastDepth = state.getDepth();
            problem.setIsNotLeaf(state);
            if(showLog) {
                System.out.println();
                System.out.println(ident + " ================= STATE :  " + state + " -  COUT CHEMIN : " + state.getWayCost() + " - ESTIMATION : " + state.getPriority() + "\n ");
            }
            //ARBRE : ajouter la valeur du noeud dans un table de hachage
            //GRAPHE : marquer le noeud comme visité
            problem.setStateVisited(state);

            if(problem.isGoal(state))
                return this.getSolution(state);

            boolean noAction = true;
            List<Action> actions = problem.getActions(state);

            if(showLog) {
                System.out.println(ident + " --------- ACTION DISPONIBLES ------------ ");
                for (Action action : actions)
                    System.out.println(ident + " ACTION ----------- " + action.getActionName());
            }

            if(showLog)
                System.out.println();
            for(Action action : actions){
                State rsState = problem.getResult(state, action);
                if(showLog) {
                    System.out.println(ident + state);
                    System.out.println(ident + " >>>>>>>>>>>>>>>>>>>>>>>> ACTION " + action.getActionName());
                    System.out.println(ident + " :::::::::::::::::::::::: RS : " + rsState);
                }
                if(rsState != null && !problem.isVisited(rsState) && !problem.isLeaf(rsState) && ( state.getDepth() + 1 ) < limit) {
                    //BFS OU DFS les noeuds s'ajoutent à la fin de la liste
                    this.nodesAddToFrontier++;
                    action.setResult(rsState);
                    rsState.setAction(action);
                    rsState.setSource(state);
                    problem.setIsLeaf(rsState);
                    rsState.setDepth(state.getDepth()+1);
                    rsState.setWayCost(state.getWayCost() + action.getCost());

                    //System.out.println(ident+"AJOUT   :  "+rsState+" -  COUT CHEMIN : "+rsState.getWayCost()+" - ESTIMATION : "+rsState.getPriority());

                    frontier.add(rsState);
                    noAction = false;
                }else{
                    if(showLog)
                        System.out.println(ident+" !!!!!!!!!!!!!!!!!!!!!!!!! NULL VISITE OU FRONTIERE "+rsState);
                }
            }
            //Il faut egalement demarquer un noeud si il ne reste aucune action
            if(noAction){
                // System.out.println(Util.getIdent(frontier.getLast().getDepth())+"NO ACTION "+frontier.getLast());
                problem.setStateUnVisited(frontier.removeLast());
            }

        }

        throw new ExplorationFailedException();
    }


}
