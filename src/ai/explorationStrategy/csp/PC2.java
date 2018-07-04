package ai.explorationStrategy.csp;

import ai.problem.csp.BinaryCSP;
import ai.problem.csp.constraint.BinaryConstraint;

import java.util.ArrayList;
import java.util.List;

/**
 * Etend AC3 en ajoutant la coherence de chemin
 *
 * **/

public class PC2 extends AC3 {

    @Override
    protected boolean checkWayConstraints() {
        return true;
    }

    @Override
    protected boolean wayConstraintsSatisfied(BinaryCSP<?> CSP, BinaryConstraint binaryConstraint) {

        //System.out.println("WAY CONSTRAINT");

        //List les pair d'arc qui relient les variables de la contraintes en passant par un autre noeud intermediaire
        for(List<BinaryConstraint> pairBinaryConstraints : CSP.findWayConstraints(binaryConstraint)){
            //recupere la premiere contrainte
            BinaryConstraint XmXi = pairBinaryConstraints.get(0);
            //recupere la deuxieme contrainte
            BinaryConstraint XmXj = pairBinaryConstraints.get(1);
            //System.out.println("CONTRAINTE CHEMIN : "+XmXi);
            //System.out.println("CONTRAINTE CHEMIN : "+XmXj);
            //System.out.println();
            //recupere le domaine de la variable intermediaire

            ArrayList<?> XmDomain = CSP.getDomain(XmXi.getXi());
            //considere par defaut qu'aucune assignation à cette variable
            //ne valide la coherence de chemin
            boolean ok = false;

            //sauvegarde le fait que la variable etait assignée ou non
            boolean xmAssigned = XmXi.getXi().getValue() != null;

            for(int m = 0 ; m < XmDomain.size() ; m ++){
                //attribue une valeur du domaine à la variable intermediaire
                //correspondant à Xi dans les contraintes binaires retrouvées
                //la premiere variable de la contrainte est identique aux deux arcs
                XmXi.getXi().setValue(XmDomain.get(m));
                //XmXj.getXi().setObjectRef(XmDomain.get(m));
                //verifie si la contrainte est satisfaite pour les deux arcs
                //par exemple si deux variable doivent avoir une couleur differente,
                //rouge bleu passerait pour le premier
                //rouge rouge ne passerait pas pour le deuxieme
                if(XmXi.satisfied() && XmXj.satisfied()){
                    ok = true;
                    //à partir du moment on l'on trouve une valeur pour Xm
                    //qui permet avec l'assignation de Xi d'aller vers Xj en passant par Xm
                    //la coherence de chemin est verifie
                    break;
                }

            }

            //si la variable etait non assigné on la remet à null après avoir testé les assignations potentielles
            if(!xmAssigned) {
                XmXi.getXi().setValue(null);
            }

            //si aucune des valeur xm ne valide la coherence d'arc avec l'assignation courante
            //on retourne vrai pour indiquer que la valeur de xi pourrait etre supprime du domaine
            //cependant une autre valeur assigné à xj pourrait rendre le chemin coherent
            //donc on ne peut pas supprimmer la valeur du domaine à moins qu'aucune valeur attribué à Xj ne fonctionne
            //ce qui se fera dans la fonction appelante
            //il n'est pas utile de tester d'autre chemin si celui si echoue
            if(!ok){
               // System.out.println("PROBLEME DETECTE");
                /*
                if(!xiDomainRm.contains(i)) {
                    xiDomainRm.add(i);
                }
*/
                return false;
                //System.out.println(XmXi+" "+XmXi.satisfied());
                //System.out.println(XmXj+" "+XmXj.satisfied());
                //System.out.println("NOT OK "+xiDomainRm);
            }



        }

        return true;

    }
}
