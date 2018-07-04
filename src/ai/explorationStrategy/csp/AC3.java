package ai.explorationStrategy.csp;

import ai.problem.csp.BinaryCSP;
import ai.problem.csp.constraint.BinaryConstraint;

import java.util.*;

/**
 * Etend le forwad checking en ajoutant les contraintes partant d'une variable si son domaine est reduit
 *
 * De plus il teste toutes les contraintes de le depart
 * */

public class AC3 extends ForwardChecking{


    @Override
    protected void addConstraints(BinaryCSP<?> CSP, LinkedList<BinaryConstraint> constraints, HashSet<BinaryConstraint> setConstraints, BinaryConstraint constraint) {
        // System.out.println("RAJOUT CONTRAINTES");
        //Pour chaque contrainte ayant Xi comme statement, il faut remettre les contraintes inverses
        //,çàd ayant Xi comme destination , afin de verifier si elles restent
        // valide après avoir supprimmer des valeurs du domaine de Xi.
        for (BinaryConstraint binaryConstraint : CSP.getConstraints(constraint.getXi())) {
            // il n'est pas necessaire de rajouter la contrainte (Xj,Xi).
            // Chaque valeur du domaine de Xi trouve un equivalent dans le domaine de Xj
            // Si la contrainte n'a pas encore été traité elle le sera après de toute facon.
            // Si elle a été traité juste avant, le domaine de Xj ne contient que des valeurs qui trouvent une correspondance
            // dans le domaine de Xi. Apres avoir traite la contrainte (xi,xj) les deux domaines sont coherents pour toutes les
            // contraintes qu lient les deux variables.

            if (!binaryConstraint.getXj().equals(constraint.getXj())) {
                //on recupere la contrainte inverse qui part de Xj pour rejoindre Xi
                BinaryConstraint reviewBinaryConstraint = CSP.getConstraint(binaryConstraint.getXj(), binaryConstraint.getXi());
                //si cette contrainte existe et qu'elle n'est pas deja dans la liste on l'ajoute
                if (reviewBinaryConstraint != null && !setConstraints.contains(reviewBinaryConstraint)) {
                    constraints.add(reviewBinaryConstraint);
                    setConstraints.add(reviewBinaryConstraint);
                }
            }

        }
    }
}
