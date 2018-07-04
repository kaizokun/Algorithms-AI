package ai.explorationStrategy.csp;

import ai.problem.csp.BinaryCSP;
import ai.problem.csp.CSPvariable;

public class MinConflicts {

    public void explore(BinaryCSP<?> CSP, int maxSteps) throws BackTrackingExploration.ExplorationCSPFailedException {

        CSP.initRandomAssignation();

        CSPvariable lastVar = null;

        while (maxSteps -- > 0){

            if(CSP.assignationValid()) {
                System.out.println("VALID");
                System.out.println(maxSteps);
                return;
            }

            //CSPvariable var = CSP.getRandomConflictualVariable();

            //récuperer la variable entrant le plus en conflits avec ses voisines avec la valeur actuelle
            CSPvariable var = CSP.getMoreConflictualVariable();
            //si la variable precedente n'est pas nulle et qu'elle est égale à la precedente
            if(lastVar != null && lastVar.equals(var)) {
                //choisir une variable conflictuelle au hazard
                //System.out.println("MEME VARIABLE");
                var = CSP.getRandomConflictualVariable();
            }

            CSP.setLessConflictualValue(var);

            lastVar = var;

        }

        throw new BackTrackingExploration.ExplorationCSPFailedException();

    }

}
