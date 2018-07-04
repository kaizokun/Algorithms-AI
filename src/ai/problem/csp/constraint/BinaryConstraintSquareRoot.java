package ai.problem.csp.constraint;

import ai.problem.csp.CSPvariable;

public class BinaryConstraintSquareRoot extends BinaryConstraint{

    public BinaryConstraintSquareRoot(CSPvariable xi, CSPvariable xj) {
        super(xi, xj);
    }

    @Override
    public boolean satisfied() {
        return Double.compare(Math.sqrt((Double) this.getXi().getValue()),(Double)this.Xj.getValue()) == 0 ;
    }

    @Override
    public BinaryConstraint getNewBinaryConstraint(CSPvariable xi, CSPvariable xj) {
        return new BinaryConstraintSquareRoot(xi,xj);
    }
}
