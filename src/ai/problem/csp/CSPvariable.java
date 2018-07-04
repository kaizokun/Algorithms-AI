package ai.problem.csp;

public class CSPvariable {

    private int index;
    private String label;
    private Object value;

    public CSPvariable(String label, int index) {
        this.index = index;
        this.label = label;
    }

    public CSPvariable(CSPvariable var){
        this.index = var.index;
        this.label = var.label;
        this.value = var.value;
    }

    public int getIndex() {
        return index;
    }

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        CSPvariable that = (CSPvariable) o;

        return index == that.index;
    }

    @Override
    public int hashCode() {
        return index;
    }

    @Override
    public String toString() {
        return "CSPvariable{" +
                "index=" + index +
                ", label='" + label + '\'' +
                ", isTrue=" + value +
                '}';
    }
}
