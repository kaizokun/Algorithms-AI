package ai.explorationStrategy.csp.variable.selection;

import ai.problem.csp.BinaryCSP;
import ai.problem.csp.CSPvariable;
import ai.problem.csp.constraint.BinaryConstraint;

/**
 *
 * Selectione la variable qui à le plus de contraintes avec les autres
 * **/

public class DegreeHeuristic extends VariableSelectionMode {

    @Override
    public CSPvariable selectVariable(BinaryCSP<?> csp) {


        int maxConstraint = -1;
        CSPvariable bestVar = null;
        //pour chaque variable du CSP
        for( CSPvariable var : csp.getVariables() ){
            //si la variable est déja assigné on passe à la suivante
            if(var.getValue() != null)
                continue;

            int totalContraint = 0;
            //pour chaque contrainte binaire lié à la variable courante
            for(BinaryConstraint constraint : csp.getConstraints(var)){
                //si la seconde variable lié par la contrainte est non assignée
                if(constraint.getXj().getValue() == null){
                    //on compte une contrainte de plus
                    totalContraint ++;
                }

            }
            //si le nombre de contrainte pour la variable courant est superieur au maximum courant
            // on le remplace ainsi que la variable
            if(totalContraint > maxConstraint){
                maxConstraint = totalContraint;
                bestVar = var;
            }

        }

        return bestVar;
    }
}
