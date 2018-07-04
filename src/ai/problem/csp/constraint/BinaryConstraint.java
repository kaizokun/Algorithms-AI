package ai.problem.csp.constraint;

import ai.problem.csp.CSPvariable;

public abstract class BinaryConstraint {

    protected CSPvariable Xi, Xj;

    public BinaryConstraint() {
    }

    public BinaryConstraint(CSPvariable xi, CSPvariable xj) {
        Xi = xi;
        Xj = xj;
    }

    public abstract boolean satisfied();

    public CSPvariable getXi() {
        return Xi;
    }

    public void setXi(CSPvariable xi) {
        this.Xi = xi;
    }

    public CSPvariable getXj() {
        return Xj;
    }

    public void setXj(CSPvariable xj) {
        this.Xj = xj;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        BinaryConstraint that = (BinaryConstraint) o;

        if (Xi != null ? !Xi.equals(that.Xi) : that.Xi != null) return false;
        return Xj != null ? Xj.equals(that.Xj) : that.Xj == null;
    }

    @Override
    public int hashCode() {
        return Integer.parseInt(Xi.getIndex()+""+Xj.getIndex());
    }

    @Override
    public String toString() {
        return "BinaryConstraint{" +
                "Xi=" + Xi +
                ", Xj=" + Xj +
                '}';
    }

    public abstract BinaryConstraint getNewBinaryConstraint(CSPvariable xi, CSPvariable xj);
}
