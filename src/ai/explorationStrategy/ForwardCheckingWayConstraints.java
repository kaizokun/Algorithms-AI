package ai.explorationStrategy;

import ai.explorationStrategy.csp.PC2;
import ai.problem.csp.BinaryCSP;
import ai.problem.csp.constraint.BinaryConstraint;

import java.util.HashSet;
import java.util.LinkedList;
/**
 * Etend PC2 pour faire la coherence de chemin mais à nouveau sans propager
 * */
public class ForwardCheckingWayConstraints extends PC2 {

    @Override
    protected void addConstraints(BinaryCSP<?> CSP, LinkedList<BinaryConstraint> constraints,
                                  HashSet<BinaryConstraint> setConstraints, BinaryConstraint constraint) {
    }

}
