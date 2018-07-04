package ai.agent.logic.expressions;

public class EmptyStatement extends UnaryStatement {

    public EmptyStatement(Statement st1) {
        super(st1);
    }

    public EmptyStatement() {
    }

    @Override
    public boolean isTrue() {
        return false;
    }

    @Override
    public Statement clone(boolean cloneSymbol) {
        return new EmptyStatement();
    }

    @Override
    public String toShortString() {
        return "";
    }

    @Override
    protected String getUniqueString() {
        return "";
    }

    @Override
    public String noVarsSignature() {
        return "";
    }
}
