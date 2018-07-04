package ai.explorationStrategy.csp;

import ai.problem.csp.BinaryCSP;
import ai.problem.csp.CSPvariable;

import java.util.HashSet;

public interface Inference {

    boolean isCoherent(BinaryCSP<?> csp, CSPvariable variable);
    HashSet<CSPvariable> getConflicts();
}
