package ai.explorationStrategy.standard;

import ai.Action;
import ai.State;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by monsio on 7/13/17.
 */
public abstract class Explore implements StandardSearch {


    protected int nodesDeployed, nodesAddToFrontier;

    protected List<Action> getSolution(State leaf){

        LinkedList<Action> solution = new LinkedList<Action>();

        //System.out.println(solution);

        while (leaf.getSource() != null){
            //System.out.println("SOLUTION ");
           // if(leaf.getAction() != null)
              //  System.out.print(leaf.getAction().getActionName()+" "+leaf.getStatement()+"\n");

                //System.out.println(leaf.getAction().getActionName()+" "+leaf.getStatement());

            solution.addFirst(leaf.getAction());
            leaf = leaf.getSource();
        }

        return solution;

    }


    @Override
    public int getNodesDeployed() {
        return nodesDeployed;
    }

    @Override
    public int getNodesAddToFrontier() {
        return nodesAddToFrontier;
    }
}
