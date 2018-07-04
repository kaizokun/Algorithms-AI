package ai.agent.logic.expressions;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;

public class Not extends UnaryStatement {

    public Not(Statement st1) {
        super(st1);
    }

    @Override
    public boolean isTrue() {
        return !this.st1.isTrue();
    }

    @Override
    public List<TransformAction> getActions() {

        List<TransformAction> actions = new LinkedList<>();

        if(this.st1 instanceof And) {
            actions.add(new TransformAction(TransformAction.TRANFORMATION.MORGAN_AND,this));
        }else if(this.st1 instanceof Or){
            actions.add(new TransformAction(TransformAction.TRANFORMATION.MORGAN_OR,this));
        }else if(this.st1 instanceof Not && ((Not)this.st1).st1 instanceof Not){
            actions.add(new TransformAction(TransformAction.TRANFORMATION.REMOVE_DOUBLE_NEGATION,this));
        }

        return actions;

    }

    @Override
    public Statement getTransformationResult(TransformAction.TRANFORMATION action, boolean cloneSymbol) {

        switch (action){
            case MORGAN_AND :
                And a1 = (And) this.st1;
                return new Or(
                        new Not(a1.st1.clone(cloneSymbol)),
                        new Not(a1.st2.clone(cloneSymbol)));
            case MORGAN_OR :
                Or o1 = (Or) this.st1;
                return new And(
                        new Not(o1.st1.clone(cloneSymbol)),
                        new Not(o1.st2.clone(cloneSymbol)));
            case REMOVE_DOUBLE_NEGATION :
                Not n1 = (Not) this.st1;
                Not n2 = (Not) n1.st1;
                return n2.st1.clone(cloneSymbol);

        }

        return null;
    }

    @Override
    public Statement clone(boolean cloneSymbol) {
        return new Not(this.st1.clone(cloneSymbol));
    }

    @Override
    public String toString() {
        return " ¬ "+this.st1.toString()+" ";
    }

    @Override
    public String noVarsSignature() {
        return " ¬ "+this.st1.noVarsSignature()+" ";
    }

    @Override
    public String toShortString() {
        return "!"+this.st1.toShortString();
    }

    @Override
    protected void addLiterals(List<Statement> literals) {

        if(this.st1 instanceof Symbol) {
            literals.add(this);
        } else {
            this.st1.addLiterals(literals);
        }
    }

    @Override
    protected void addOrClauses(List<Statement> orClauses) {

        if(this.st1 instanceof Symbol) {
            orClauses.add(this);
        }else{
            this.st1.addOrClauses(orClauses);
        }

    }

    @Override
    public Symbol getSymbol() {

        if(this.st1 instanceof Symbol)
            return (Symbol) this.st1;

        return super.getSymbol();
    }

    /**
     * Doit renvoyer directement la chaine unique du literal négatif
     *
     * ! la méthode parent récuperer une liste de literaux de l'expression
     *  pour un literal cette liste le contiendrait lui même ce qui pose problème
     *  avec cet implementation
     *
     * */

    @Override
    protected String getUniqueString() {
        return  this.toShortString();
    }

    /**
     *
     * Un literal ne peut contenir sont opposés ...
     * */

    @Override
    public boolean containOpposite(){
        return false;
    }

    /**
     *
     * pour un literal il suffit de comparer les deux literaux
     * */

    @Override
    public boolean containOpposite(Statement opposite) {
        return this.equals(opposite);
    }

    @Override
    public HashSet<Statement> getLiterals() {
        HashSet<Statement> literals = new HashSet<>();
        literals.add(this);
        return literals;
    }
}
