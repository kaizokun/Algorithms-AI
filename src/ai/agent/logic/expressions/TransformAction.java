package ai.agent.logic.expressions;

import ai.Action;

public class TransformAction extends Action{

    protected Statement statement;

    private TRANFORMATION action;

    public TransformAction(TRANFORMATION action, Statement statement) {
        this.action = action;
        this.statement = statement;
    }

    @Override
    public String getActionName() {
        return this.action.toString()+" ON "+statement.toString();
    }

    public enum TRANFORMATION{

        MODUS_PONENS,
        REMOVE_CONJONCTION_A,
        REMOVE_CONJONCTION_B,
        COMMUTATIVITY,
        ASSOCIATIVITY_AND,
        ASSOCIATIVITY_AND_INV,
        ASSOCIATIVITY_OR,
        ASSOCIATIVITY_OR_INV,
        REMOVE_DOUBLE_NEGATION,
        ADD_DOUBLE_NEGATION,
        CONTRAPOSITION,
        CONTRAPOSITION_INV,
        REMOVE_IMPLICATION,
        REMOVE_IMPLICATION_INV,
        REMOVE_BICONDITIONAL,
        REMOVE_BICONDITIONAL_INV,
        MORGAN_AND,
        MORGAN_OR,
        MORGAN_AND_INV,
        MORGAN_OR_INV,
        DISTRIBUTIVE_AND,
        DISTRIBUTIVE_OR,
        DISTRIBUTIVE_AND_INV,
        DISTRIBUTIVE_OR_INV,

    }

    public Statement getStatement() {
        return statement;
    }

    public void setStatement(Statement statement) {
        this.statement = statement;
    }

    public TRANFORMATION getAction() {
        return action;
    }

    public void setAction(TRANFORMATION action) {
        this.action = action;
    }
}
