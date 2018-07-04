package ai.agent.logic.expressions;

import java.util.LinkedList;
import java.util.List;

public class And extends BinaryStatement {

    @Override
    public Statement clone(boolean cloneSymbol) {
        return new And(st1.clone(cloneSymbol), st2.clone(cloneSymbol));
    }

    public And(Statement st1, Statement st2) {
        super(st1, st2);
    }

    public And() {
    }

    @Override
    protected BinaryStatement getNewStatement() {
        return new And();
    }

    @Override
    public boolean isTrue() {
        return this.st1.isTrue() && this.st2.isTrue();
    }

    @Override
    public String toString() {
        return " ( "+this.st1+" ∧ "+this.st2+" ) ";
    }

    @Override
    public String noVarsSignature() {
        return " ( "+this.st1.noVarsSignature()+" ∧ "+this.st2.noVarsSignature()+" ) ";
    }

    @Override
    public String toShortString() {
        return this.st1.toShortString()+"&"+this.st2.toShortString();
    }

    @Override
    public List<TransformAction> getActions() {

        List<TransformAction> actions = new LinkedList<>();

        actions.add(new TransformAction(TransformAction.TRANFORMATION.COMMUTATIVITY,this));
        actions.add(new TransformAction(TransformAction.TRANFORMATION.REMOVE_CONJONCTION_A,this));
        actions.add(new TransformAction(TransformAction.TRANFORMATION.REMOVE_CONJONCTION_B,this));
        //actions.add(new TransformAction(TRANFORMATION.ADD_DOUBLE_NEGATION,this));

        if(this.st1 instanceof And) {
            actions.add(new TransformAction(TransformAction.TRANFORMATION.ASSOCIATIVITY_AND, this));
        }

        if(this.st2 instanceof And) {
            actions.add(new TransformAction(TransformAction.TRANFORMATION.ASSOCIATIVITY_AND_INV, this));
        }

        if(this.st2 instanceof Or){
            actions.add(new TransformAction(TransformAction.TRANFORMATION.DISTRIBUTIVE_AND,this));
        }

        if(this.st1 instanceof Or && this.st2 instanceof Or){
            //les premiers elements des operations OU doivent être égaux
            Or o1 = (Or) this.st1;
            Or o2 = (Or) this.st2;

            if(o1.st1.equals(o2.st1)) {
                actions.add(new TransformAction(TransformAction.TRANFORMATION.DISTRIBUTIVE_OR_INV, this));
            }
        }

        if(this.st1 instanceof Not && this.st2 instanceof Not){
            actions.add(new TransformAction(TransformAction.TRANFORMATION.MORGAN_OR_INV,this));
        }

        if(this.st1 instanceof Imply && this.st2 instanceof Imply){
            //le premier element de la premiere implication doit etre egale au deuxieme element de la deux ieme implication
            //et inversement

            Imply i1 = (Imply) this.st1, i2 = (Imply) this.st2;
            if(i1.st1.equals(i2.st2) && i1.st2.equals(i2.st1)) {
                actions.add(new TransformAction(TransformAction.TRANFORMATION.REMOVE_BICONDITIONAL_INV, this));
            }
        }

        return actions;
    }

    @Override
    public Statement getTransformationResult(TransformAction.TRANFORMATION action, boolean cloneSymbol) {

        switch (action){

            case COMMUTATIVITY:

                return new And(
                        this.st2.clone(cloneSymbol), this.st1.clone(cloneSymbol));

            case ASSOCIATIVITY_AND:

                And a1 = (And) this.st1;

                return new And(
                        a1.st1.clone(cloneSymbol),
                        new And(a1.st2.clone(cloneSymbol), this.st2.clone(cloneSymbol)));

            case ASSOCIATIVITY_AND_INV:

                And a2 = (And) this.st2;

                return new And(
                        new And(this.st1.clone(cloneSymbol), a2.st1.clone(cloneSymbol)),
                        a2.st2.clone(cloneSymbol));

            case DISTRIBUTIVE_AND:

                Or o1 = (Or) this.st2;

                return new Or(
                        new And(this.st1.clone(cloneSymbol),o1.st1.clone(cloneSymbol)),
                        new And(this.st1.clone(cloneSymbol),o1.st2.clone(cloneSymbol)));

            case DISTRIBUTIVE_OR_INV:

                Or o2 = (Or) this.st1, o3 = (Or) this.st2;

                return new Or(o2.st1.clone(cloneSymbol),
                       new And(o2.st2.clone(cloneSymbol),o3.st2.clone(cloneSymbol)));

            case MORGAN_OR_INV:

                Not n1 = (Not) this.st1, n2 = (Not) this.st2;

                return new Not(
                        new Or(n1.st1.clone(cloneSymbol), n2.st1.clone(cloneSymbol)));

            case REMOVE_BICONDITIONAL_INV:

                Imply i1 = (Imply) this.st1;

                return new DoubleImply(i1.st1.clone(cloneSymbol),i1.st2.clone(cloneSymbol));

            case REMOVE_CONJONCTION_A:

                return this.st1.clone(cloneSymbol);

            case REMOVE_CONJONCTION_B:

                return this.st2.clone(cloneSymbol);
/*
            case ADD_DOUBLE_NEGATION:

                return new Not(new Not(this.clone(cloneSymbol)));
*/
        }

        return null;
    }


}
