package ai.problem.csp.constraint;

import ai.problem.csp.CSPvariable;

public class BinaryConstraintDif extends BinaryConstraint{

    public BinaryConstraintDif(CSPvariable xi, CSPvariable xj) {
        super(xi, xj);
    }

    @Override
    public boolean satisfied() {
        return !this.Xi.getValue().equals(this.getXj().getValue());
    }

    @Override
    public BinaryConstraint getNewBinaryConstraint(CSPvariable xi, CSPvariable xj) {
        return new BinaryConstraintDif(xi,xj);
    }

}
