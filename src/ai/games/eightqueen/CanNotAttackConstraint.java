package ai.games.eightqueen;

import ai.problem.csp.CSPvariable;
import ai.problem.csp.constraint.BinaryConstraint;
import geometry.Point;

public class CanNotAttackConstraint extends BinaryConstraint {

    public CanNotAttackConstraint(CSPvariable xi, CSPvariable xj) {
        super(xi, xj);
    }

    @Override
    public boolean satisfied() {

        Point Qi = (Point) this.Xi.getValue();
        Point Qj = (Point) this.Xj.getValue();

        if(Qi.getY() == Qj.getY())
            return false;

        if(Math.abs(Qi.getX()-Qj.getX()) == Math.abs(Qi.getY() - Qj.getY()))
            return false;

        return true;
    }

    @Override
    public BinaryConstraint getNewBinaryConstraint(CSPvariable xi, CSPvariable xj) {
        return new CanNotAttackConstraint(xi,xj);
    }

}
