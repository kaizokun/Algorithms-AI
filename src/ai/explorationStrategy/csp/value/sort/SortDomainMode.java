package ai.explorationStrategy.csp.value.sort;

import ai.problem.csp.BinaryCSP;
import ai.problem.csp.CSPvariable;

public abstract class SortDomainMode {

    public abstract void sortDomain(BinaryCSP<?> csp, CSPvariable csPvariable);

}
