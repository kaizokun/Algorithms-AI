package ai.problem.csp.constraint;

import ai.problem.csp.CSPvariable;

public class BinaryConstraintSquare extends BinaryConstraint{

    public BinaryConstraintSquare(CSPvariable xi, CSPvariable xj) {
        super(xi, xj);
    }

    @Override
    public boolean satisfied() {
        return Double.compare(Math.pow((Double) this.getXi().getValue(),2),(Double)this.Xj.getValue()) == 0 ;
    }

    @Override
    public BinaryConstraint getNewBinaryConstraint(CSPvariable xi, CSPvariable xj) {
        return new BinaryConstraintSquare(xi,xj);
    }
}
