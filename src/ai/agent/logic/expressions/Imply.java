package ai.agent.logic.expressions;

import java.util.LinkedList;
import java.util.List;

public class Imply extends BinaryStatement {



    public Imply(Statement st1, Statement st2) {
        super(st1, st2);
    }

    public Imply() {
    }


    public void incCptrFacts(){
        if(this.cptrFacts < this.getLiterals().size() - 1)
            this.cptrFacts ++;
    }

    public void decCptrFacts(){
        this.cptrFacts --;
    }

    public boolean isActive(){
        return this.cptrFacts == this.getLiterals().size() - 1;
    }


    @Override
    protected BinaryStatement getNewStatement() {
        return new Imply();
    }

    @Override
    public boolean isTrue() {
        return (!st1.isTrue() && !st2.isTrue())
                || (st1.isTrue() && st2.isTrue())
                || (!st1.isTrue() && st2.isTrue());
    }

    @Override
    public String toString() {
        return " ( "+this.st1+" ⇒ "+this.st2+" ) ";
    }

    @Override
    public String noVarsSignature() {
        return " ( "+this.st1.noVarsSignature()+" ⇒ "+this.st2.noVarsSignature()+" ) ";
    }

    @Override
    public String toShortString() {
        return this.st1.toShortString()+"=>"+this.st2.toShortString();
    }

    @Override
    public List<TransformAction> getActions() {

        List<TransformAction> actions = new LinkedList<>();

        if(this.st1 instanceof Not && this.st2 instanceof Not){
            actions.add(new TransformAction(TransformAction.TRANFORMATION.CONTRAPOSITION_INV,this));
        }else if( !(this.st1 instanceof Not) && !(this.st2 instanceof Not)){
            actions.add(new TransformAction(TransformAction.TRANFORMATION.CONTRAPOSITION,this));
        }

        actions.add(new TransformAction(TransformAction.TRANFORMATION.REMOVE_IMPLICATION,this));

        actions.add(new TransformAction(TransformAction.TRANFORMATION.MODUS_PONENS,this));

        return actions;
    }

    @Override
    public Statement getTransformationResult(TransformAction.TRANFORMATION action, boolean cloneSymbol) {
        switch (action){

            case CONTRAPOSITION_INV:
                Not n1 = (Not) this.st1, n2 = (Not) this.st2;
                return new Imply(
                        n2.st1.clone(cloneSymbol),
                        n1.st1.clone(cloneSymbol));
            case CONTRAPOSITION:
                return new Imply(
                        new Not(this.st2.clone(cloneSymbol)),
                        new Not(this.st1.clone(cloneSymbol)));
            case REMOVE_IMPLICATION:
               return new Or(
                       new Not(this.st1.clone(cloneSymbol)),
                       this.st2.clone(cloneSymbol));
            case MODUS_PONENS:
                return this.st2.clone(cloneSymbol);
        }

        return null;
    }

    @Override
    public Statement clone(boolean cloneSymbol) {
        return new Imply(this.st1.clone(cloneSymbol),this.st2.clone(cloneSymbol));
    }

}
