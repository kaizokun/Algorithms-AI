package ai.agent.logic.expressions;


import java.util.LinkedList;
import java.util.List;

public class Or extends BinaryStatement {

    public Or(Statement st1, Statement st2) {
        super(st1, st2);
    }

    public Or() {
    }

    @Override
    protected BinaryStatement getNewStatement() {
        return new Or();
    }

    @Override
    public boolean isTrue() {
        return this.st1.isTrue() || this.st2.isTrue();
    }

    @Override
    public Statement clone(boolean cloneSymbol) {
        return new Or(this.st1.clone(cloneSymbol), this.st2.clone(cloneSymbol));
    }


    @Override
    protected void addOrClauses(List<Statement> orClauses) {
       orClauses.add(this);
    }

    @Override
    public List<TransformAction> getActions() {

        List<TransformAction> actions = new LinkedList<>();

        actions.add(new TransformAction(TransformAction.TRANFORMATION.COMMUTATIVITY,this));
       // actions.add(new TransformAction(TransformAction.TRANFORMATION.ADD_DOUBLE_NEGATION,this));

        if(this.st1 instanceof Or) {
            actions.add(new TransformAction(TransformAction.TRANFORMATION.ASSOCIATIVITY_OR, this));
        }

        if(this.st2 instanceof Or) {
            actions.add(new TransformAction(TransformAction.TRANFORMATION.ASSOCIATIVITY_OR_INV, this));
        }

        if(this.st2 instanceof And){
            actions.add(new TransformAction(TransformAction.TRANFORMATION.DISTRIBUTIVE_OR,this));
        }

        if(this.st1 instanceof And && this.st2 instanceof And){
            And a1 = (And) this.st1;
            And a2 = (And) this.st2;

            if(a1.st1.equals(a2.st1)) {
                actions.add(new TransformAction(TransformAction.TRANFORMATION.DISTRIBUTIVE_AND_INV, this));
            }
        }

        if(this.st1 instanceof Not && this.st2 instanceof Not){
            actions.add(new TransformAction(TransformAction.TRANFORMATION.MORGAN_AND_INV,this));
        }

        if(this.st1 instanceof Not && !(this.st2 instanceof Not)){
            actions.add(new TransformAction(TransformAction.TRANFORMATION.REMOVE_IMPLICATION_INV,this));
        }

        return actions;
    }

    @Override
    public String toString() {
        return " ( "+this.st1+" ∨ "+this.st2+" ) ";
    }

    @Override
    public String noVarsSignature() {
        return " ( "+this.st1.noVarsSignature()+" ∨ "+this.st2.noVarsSignature()+" ) ";
    }

    @Override
    public String toShortString() {
        return this.st1.toShortString()+"|"+this.st2.toShortString();
    }

    @Override
    public Statement getTransformationResult(TransformAction.TRANFORMATION action, boolean cloneSymbol) {

        switch (action){

            case COMMUTATIVITY:
                return new Or(
                        this.st2.clone(cloneSymbol), this.st1.clone(cloneSymbol));
            case ASSOCIATIVITY_OR:
                Or a1 = (Or) this.st1;
                return new Or(
                        a1.st1.clone(cloneSymbol),
                        new Or(a1.st2.clone(cloneSymbol), this.st2.clone(cloneSymbol)));
            case ASSOCIATIVITY_OR_INV:
                Or a2 = (Or) this.st2;
                return new Or(
                        new Or(this.st1.clone(cloneSymbol), a2.st1.clone(cloneSymbol)),
                        a2.st2.clone(cloneSymbol));
            case DISTRIBUTIVE_OR:
                And o1 = (And) this.st2;
                return new And(
                        new Or(this.st1.clone(cloneSymbol),o1.st1.clone(cloneSymbol)),
                        new Or(this.st1.clone(cloneSymbol),o1.st2.clone(cloneSymbol)));
            case DISTRIBUTIVE_AND_INV:
                And o2 = (And) this.st1, o3 = (And) this.st2;

                return new And(o2.st1.clone(cloneSymbol),
                        new Or(o2.st2.clone(cloneSymbol),o3.st2.clone(cloneSymbol)));
            case MORGAN_AND_INV:
                Not n1 = (Not) this.st1, n2 = (Not) this.st2;
                return new Not(
                        new And(n1.st1.clone(cloneSymbol), n2.st1.clone(cloneSymbol)));
            case REMOVE_IMPLICATION_INV:
                Not n3 = (Not) this.st1;
                return new Imply(n3.st1.clone(cloneSymbol), this.st2.clone(cloneSymbol));
/*
            case ADD_DOUBLE_NEGATION:

                return new Not(new Not(this.clone(cloneSymbol)));*/
        }

        return null;
    }


}
