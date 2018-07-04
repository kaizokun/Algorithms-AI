package ai.explorationStrategy.csp;

import ai.problem.csp.BinaryCSP;
import ai.problem.csp.CSPvariable;
import ai.problem.csp.constraint.BinaryConstraint;

import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;

/**
 * Le forward checking test la coherence d'arc ou de chemin
 * sans propager aux contraintes concerannt un variable dont on a reduit le domaine
 *
 * En plus il se limite aux contraintes concernant une seule variable
 *
 * */

public class ForwardChecking implements Inference{

    public boolean isCoherent(BinaryCSP<?> CSP, CSPvariable variable){

        HashSet<BinaryConstraint> setConstraints = new LinkedHashSet<>();
        LinkedList<BinaryConstraint> constraints = new LinkedList<>();

        HashSet<CSPvariable> conflicts = new LinkedHashSet<>();
        //recuperer toutes les variables voisines de Xi
        for(BinaryConstraint binaryConstraint : CSP.getConstraints(variable)) {
            //si la variable est non assignée
            if(binaryConstraint.getXj().getValue() == null) {

                //on recupere la contrainte inverse de Xj à Xi
                BinaryConstraint constraint = CSP.getConstraint(binaryConstraint.getXj(), binaryConstraint.getXi());

                if(constraint != null) {
                    setConstraints.add(constraint);
                    constraints.add(constraint);
                }

            }
        }

        return this.isCoherent(CSP, setConstraints, constraints);

    }

    /**
     *
     * Utile pour les classes AC3 et PC2 qui testent toutes les contraintes des le depart
     *
     * */
    public boolean isCoherent(BinaryCSP<?> CSP){

        HashSet<BinaryConstraint> setConstraints = new LinkedHashSet<>();
        LinkedList<BinaryConstraint> constraints = new LinkedList<>(CSP.getConstraints());

        for(BinaryConstraint binaryConstraint : constraints)
            setConstraints.add(binaryConstraint);

        return isCoherent(CSP, setConstraints, constraints);

    }


    protected boolean isCoherent(BinaryCSP<?> CSP,
                                 HashSet<BinaryConstraint> setConstraints, LinkedList<BinaryConstraint> constraints){

        while(!constraints.isEmpty()){
            //recupere la première contrainte
            BinaryConstraint constraint = constraints.removeFirst();
            setConstraints.remove(constraint);
            //System.out.println(constraint);

            //test si le pour chaque valeur du domaine de la variable statement de la contrainte Xi
            //il existe une valeur dans le domaine de la variable destination de la contrainte Xj
            //qui valide la contrainte et donc rendrait l'assignation de la valeur à Xi valide
            //les valeurs du domaine de Xi ne trouvant pas d'association correcte avec une valeur du domaine de Xj
            //en focntion de la contrainte sont supprimées du domaine de Xi

            //System.out.println(this.review(CSP, constraint));
            //System.out.println(constraint);
            // System.out.println(CSP.getDomain(constraint.getXi())+" "+CSP.getDomain(constraint.getXj()));
            if(this.review(CSP, constraint)){
                //System.out.println("REVIEW "+CSP.getDomain(constraint.getXi())+" "+CSP.getDomain(constraint.getXj()));
                //si le domaine de Xi est vide le CSP n'a pas de solution avec les domaines actuels
                if(CSP.isEmptyDomain(constraint.getXi())){
                    //System.out.println("EMPTY");
                    return false;
                }

                this.addConstraints(CSP,constraints,setConstraints,constraint);

            }

        }

        return true;
    }

    public boolean review(BinaryCSP CSP, BinaryConstraint binaryConstraint){

        boolean modified = false;

        List<Object> XiDomain = CSP.getDomain(binaryConstraint.getXi()),
                XjDomain = CSP.getDomain(binaryConstraint.getXj());

        //Si une des variables est deja assigné avant le review
        //en cas d'assignation partielle avant le test de coherence
        //Son domain est deja limité à la valeur assignée
        //Si elle n'est pas assignée il faut pouvoir la remettre à nulle
        //après les tests.
        boolean xiAssigned = binaryConstraint.getXi().getValue() != null;
        boolean xjAssigned = binaryConstraint.getXj().getValue() != null;

        //System.out.println(binaryConstraint);
        // System.out.println(XiDomain);
        // System.out.println(XjDomain);
        LinkedList<Integer> xiDomainRm = new LinkedList<>();

        for( int i = 0 ; i < XiDomain.size() ; i ++){

            boolean ok = false;

            binaryConstraint.getXi().setValue(XiDomain.get(i));

            for(int j = 0 ; j < XjDomain.size() ; j ++){

                binaryConstraint.getXj().setValue(XjDomain.get(j));
                //si on ne fait que de la coherence d'arc on considere que la coherence de chemin est toujours vrai
                //si on fait de la coherence de chemin en plus les deux doivent être vrai au moins
                //une fois pour que l'assignation à xi soit validé
                if(binaryConstraint.satisfied() && this.wayConstraintsSatisfied(CSP, binaryConstraint)){

                    ok = true;

                    //au cas ou on fait un test sur les chemins, on ne peut pas s'arreter des que l'on
                    //a une coherence d'arc, car si on a une coherence d'arc mais pas de chemin pour l'assignation courante
                    //de xj il se peut que l'on en est une pour une autre assignation à xj, si on fait un break
                    //on ne pourra pas le savoir. Par exemple à la figure 6.1 p.217 de AIMA.
                    //avec xi = NGS {ROUGE}, xj = V {VERT, BLEU}, et xm = AM {BLEU}
                    //la coherence d'arc fonctionne pour (xi,xj) € { (ROUGE, VERT), (ROUGE, BLEU) }
                    //mais la coherence d'arc ne fonctionne pas pour (xi,xm,xj) = (ROUGE,BLEU,BLEU)
                    //ce qui ne veut pas dire qu'il faut supprimer la valeur attribuée à xi
                    //car avec la combinaison (xi,xm,xj) = (ROUGE,BLEU,VERT) la cohérence de chemin fonctionne
                    //pour suprimmer la valeur attribué à XI de son domaine il faudrait qu'aucune valeur attribué
                    //à xj ne satisfasse à la coherence de chemin.

                    if(!this.checkWayConstraints()) {
                        break;
                    }
                }

            }

            if(!ok) {

                xiDomainRm.add(i);
                modified = true;
            }

        }

        //System.out.println(xiDomainRm);
        while (!xiDomainRm.isEmpty()){
            //System.out.println(xiDomainRm.getLast().intValue()+" "+XiDomain);
            XiDomain.remove(xiDomainRm.removeLast().intValue());
        }

        if(!xiAssigned)
            binaryConstraint.getXi().setValue(null);

        if(!xjAssigned)
            binaryConstraint.getXj().setValue(null);


        // System.out.println(XiDomain);
        // System.out.println(XjDomain);



        return modified;

    }

    protected boolean checkWayConstraints(){
        return false;
    }

    protected boolean wayConstraintsSatisfied(BinaryCSP<?> CSP, BinaryConstraint binaryConstraint) {
        return true;
    }

    protected void addConstraints(BinaryCSP<?> CSP, LinkedList<BinaryConstraint> constraints, HashSet<BinaryConstraint> setConstraints, BinaryConstraint constraint){ }


    @Override
    public HashSet<CSPvariable> getConflicts() {
        return null;
    }

}

