package ai.explorationStrategy.csp;

import ai.explorationStrategy.ForwardCheckingWayConstraints;
import ai.explorationStrategy.Search;
import ai.explorationStrategy.csp.value.sort.LessStressedValue;
import ai.explorationStrategy.csp.value.sort.SortDomainMode;
import ai.explorationStrategy.csp.variable.selection.DegreeHeuristic;
import ai.explorationStrategy.csp.variable.selection.DegreeHeuristicOrMRV;
import ai.explorationStrategy.csp.variable.selection.MinimumRemainValue;
import ai.explorationStrategy.csp.variable.selection.VariableSelectionMode;
import ai.problem.csp.BinaryCSP;
import ai.problem.csp.CSPvariable;
import ai.problem.csp.constraint.BinaryConstraint;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashSet;

public class BackTrackingExploration {

    private int assignCount = 0;
    private int cptNodesDeploied = 0;
    private VariableSelectionMode variableSelectionMode;
    private SortDomainMode sortDomainMode;
    private Inference inference;
    private boolean showLog = false;

    public BackTrackingExploration() {
        this.variableSelectionMode = new DegreeHeuristicOrMRV();
        this.sortDomainMode = new LessStressedValue();
        this.inference = new AC3();
    }

    public LinkedHashSet<CSPvariable> backTrack(BinaryCSP<?> CSP, int depth) throws ExplorationCSPFailedException{

        if(this.assignCount == CSP.varCount())
            return null;

        String ident = null;

        if(showLog)
            ident = util.Util.getIdent(depth);

        //Selection de la variable à traiter en fonction du mode de selection choisi
        CSPvariable variable = variableSelectionMode.selectVariable(CSP);
        if(showLog)
            System.out.println(ident+"TEST VARIABLE "+variable);

        //liste des voisins de variable déja assignés
        HashSet<CSPvariable> conflicts = new LinkedHashSet<>();
        for(BinaryConstraint constraint : CSP.getConstraints(variable)){
            if(constraint.getXj().getValue() != null){
                conflicts.add(constraint.getXj());
            }
        }

        //trie des valeurs du domaine en fonction du mode de trie choisi
        this.sortDomainMode.sortDomain(CSP, variable);
        //pour chaque valeur du domaine
        for( Object value : CSP.getDomain(variable)){
            if(showLog)
                System.out.println(ident+"TEST VALEUR "+value);

            cptNodesDeploied ++;
            //assigner une valeur à la variable
            variable.setValue(value);
            //vérifie si l'assignation est valide en fonction des contraintes du CSP est des autres assignations
            if(CSP.assignationValid() && CSP.assignationUsefull()){
                if(showLog)
                    System.out.println(ident+"ASSIGNATION VALIDE "+CSP.getVariables());
                //on clone le CSP, seul les domaines sont clonés pour effectuer les inferences et les reduires
                //on rapelle la fonction avec le CSP avec les domaines clonés
                //en cas d'erreur on en revient aux domaines precedent
                BinaryCSP cloneCSP = CSP.clone();

                //reduire le domaine de la variable à la valeur assignée
                ArrayList<Object> varDomain = cloneCSP.getDomain(variable);
                varDomain.clear();
                varDomain.add(value);

                //vérifie si l'assignation permet de continuer les assignations
                //pour les autres variables (domaine non vide)
                //lors du objectHash d'inference il est important que les variables assignées pendant le objectHash
                //soient remis à nul si elle n'était pas assigné auparavant
                boolean coherent = this.inference.isCoherent(cloneCSP, variable);

                if(coherent){
                    if(showLog)
                        System.out.println(ident+"ASSIGNATION COHERENTE");

                    try {

                        this.assignCount ++;
                        this.backTrack(cloneCSP, depth + 1);
                        return null;

                    }catch (ExplorationCSPFailedException e){

                        this.assignCount --;

                        //si les conflicts retourné ne contienne pas la variable courante
                        //modifier sa valeur ne permettra pas de regler le probleme
                        //il faut remonter jusqu'à une variable qui possedait une contrainte avec celle qui a échouée
                        //precedemment. On retourne les conflicts renvoyé par l'exception

                        if(showLog)
                            System.out.println(ident+"CONFLICTS : "+e.getConflicts());
                        if(!e.getConflicts().contains(variable)){
                            if(showLog)
                                System.out.println(ident+"VARIABLE "+variable+" IGNOREE");
                            throw new ExplorationCSPFailedException(e.getConflicts());
                        }else{
                            if(showLog)
                                System.out.println(ident+"ABSORBTION DES CONFLICTS");
                            //on absorbe l'ensemble de conflicts
                            conflicts.addAll(e.getConflicts());
                            //on retire de l'ensemble des conflits la variable courante
                            //elle etait en conflit plus en profondeur mais plu sici etant donne qu'on lui assigne d autres valeurs
                            conflicts.remove(variable);
                        }

                    }

                }else{
                    if(showLog) {
                        System.out.println(ident + "ASSIGNATION NON COHERENTE ");
                        for (CSPvariable var : CSP.getVariables())
                            System.out.println(ident + "DOMAINE " + var + " : " + cloneCSP.getDomain(var));
                    }
                }
            }

            variable.setValue(null);

        }

        //ajoute la liste des conflicts (variables = assignations) qui ne menent
        //à rien afin d'eviter une nouvelle attribution
        CSP.addUselessAssignation(conflicts);

        throw new ExplorationCSPFailedException(conflicts);
    }

    public int getCptNodesDeploied() {
        return cptNodesDeploied;
    }


    public static class ExplorationCSPFailedException extends Exception{

        private HashSet<CSPvariable> conflicts;

        public ExplorationCSPFailedException() { }

        public ExplorationCSPFailedException(HashSet<CSPvariable> conflicts) {
            this.conflicts = conflicts;
        }

        public HashSet<CSPvariable> getConflicts() {
            return conflicts;
        }

        public void setConflicts(HashSet<CSPvariable> conflicts) {
            this.conflicts = conflicts;
        }
    }

}
