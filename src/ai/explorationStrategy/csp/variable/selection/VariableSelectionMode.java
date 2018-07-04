package ai.explorationStrategy.csp.variable.selection;

import ai.problem.csp.BinaryCSP;
import ai.problem.csp.CSPvariable;

public abstract class VariableSelectionMode {

    public abstract CSPvariable selectVariable(BinaryCSP<?> csp);
}
