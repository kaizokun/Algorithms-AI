package ai.agent.logic.expressions;

import java.util.LinkedList;
import java.util.List;

public class DoubleImply extends BinaryStatement {

    public DoubleImply(Statement st1, Statement st2) {
        super(st1, st2);
    }


    public DoubleImply() {
    }

    @Override
    protected BinaryStatement getNewStatement() {
        return new DoubleImply();
    }

    @Override
    public boolean isTrue() {
        return (this.st1.isTrue() && this.st2.isTrue())
                || (!this.st1.isTrue() && !this.st2.isTrue()) ;
    }

    @Override
    public List<TransformAction> getActions() {

        List<TransformAction> actions = new LinkedList<>();

        actions.add(new TransformAction(TransformAction.TRANFORMATION.REMOVE_BICONDITIONAL,this));

        return actions;
    }

    @Override
    public String toString() {
        return " ( "+this.st1+" ⇔ "+this.st2+" ) ";
    }

    @Override
    public String noVarsSignature() {
        return " ( "+this.st1.noVarsSignature()+" ⇔ "+this.st2.noVarsSignature()+" ) ";
    }

    @Override
    public String toShortString() {
        return this.st1.toShortString()+"<>"+this.st2.toShortString();
    }

    @Override
    public Statement getTransformationResult(TransformAction.TRANFORMATION action, boolean cloneSymbol) {
        switch(action) {
            case REMOVE_BICONDITIONAL:
            return new And(
                    new Imply(this.st1.clone(cloneSymbol), this.st2.clone(cloneSymbol)),
                    new Imply(this.st2.clone(cloneSymbol), this.st1.clone(cloneSymbol)));
        }

        return null;
    }


    @Override
    public Statement clone(boolean cloneSymbol) {
        return new DoubleImply(this.st1.clone(cloneSymbol),this.st2.clone(cloneSymbol));
    }

}
