package ai.games.backPack;

import ai.State;
import ai.explorationStrategy.local.ParticleSwarmConfig;
import ai.problem.BackPackProblem;
import ai.problem.Problem;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class ParticleSwarmConfigBackPack extends ParticleSwarmConfig {

    //private static int cpt = 0;

    @Override
    public void updateSolutions(Problem problem, State bestState, State bestStateCurrent, List<State> solutions) {

        //cpt++;

        BackPackProblem backPackProblem = (BackPackProblem) problem;

        ArrayList<Item> bestItems = (ArrayList<Item>) bestState.getValue();
        ArrayList<Item> bestcurrentItems = (ArrayList<Item>) bestStateCurrent.getValue();

        List<String> str = new LinkedList<>();

        int l = bestItems.size();
        int l2 = bestcurrentItems.size();


        for(State solution : solutions){
            BackPackState backPack = (BackPackState) solution;
            //si la solution est egale à une des deux meilleurs
            //la modifier provoque des conflits dans la recuperation
            //des elements aléatoires
            if(!solution.equals(bestState) && !solution.equals(bestStateCurrent)){

                //ajout d'un item aléatoire de la meilleur solution

                int index = util.Util.rdnInt(0,l-1);

                Item rdmItem = bestItems.get(index);

                if(!backPack.contain(rdmItem)){
                    backPack.insertItem(rdmItem);
                }

                //str.add(cpt+" "+i+" B "+l+" "+l2+"\n"+bestcurrentItems);


                //ajout d'un item aléatoire de la meilleur solution courante

                int index2 = util.Util.rdnInt(0,l2-1);


                rdmItem = bestcurrentItems.get(index2);


                //str.add(cpt+" "+i+" C "+l+" "+l2+"\n"+bestcurrentItems);


                if(!backPack.contain(rdmItem)){
                    backPack.insertItem(rdmItem);
                }

                //str.add(cpt+" "+i+" D "+l+" "+l2+"\n"+bestcurrentItems);


                //retrait d'objet au hazard

                int index3;
                double backPackTotalWeigh = backPack.totalWeigh();
                while(backPackTotalWeigh > backPackProblem.getMaxWeigh()){

                    index3 = util.Util.rdnInt(0,backPack.getValue().size()-1);
                    backPackTotalWeigh -= backPack.getValue().get(index3).getWeigh();
                    //backPack.getObjectRef().remove(index3);
                    backPack.removeItem(index3);
                }

                //str.add(cpt+" "+i+" E "+l+" "+l2+"\n"+bestcurrentItems);


                //ajout d'autre objets potentiels

                double weighAvalaible = backPackProblem.getMaxWeigh() - backPackTotalWeigh;

                List<Item> allItemsCopy = backPackProblem.allItemsCopy();

                allItemsCopy.removeAll(backPack.getValue());

                backPackProblem.removeTooHeavyItems(allItemsCopy, weighAvalaible);

                //str.add(cpt+" "+i+" F "+l+" "+l2+"\n"+bestcurrentItems);


                while( !allItemsCopy.isEmpty() ){

                    index = util.Util.rdnInt(0,allItemsCopy.size()-1);

                    backPack.insertItem(allItemsCopy.get(index));

                    backPackTotalWeigh += allItemsCopy.get(index).getWeigh();

                    allItemsCopy.remove(index);

                    weighAvalaible = backPackProblem.getMaxWeigh() - backPackTotalWeigh;

                    backPackProblem.removeTooHeavyItems(allItemsCopy, weighAvalaible);
                }

                //str.add(cpt+" "+i+" G "+l+" "+l2+"\n"+bestcurrentItems+"\n\n");


            }

        }
    }

}
