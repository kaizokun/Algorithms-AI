package ai.agent.logic.expressions;

import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;
import java.util.Set;

public abstract class UnaryStatement extends Statement {

    protected Statement st1;

    public UnaryStatement(Statement st1) {
        this.st1 = st1;
    }

    public UnaryStatement() { }

    public Statement getSt1() {
        return st1;
    }

    public void setSt1(Statement st1) {
        this.st1 = st1;
    }
/*
    @Override
    public List<Statement> getStatements() {
        LinkedList<Statement> statements = new LinkedList<>();
        statements.add(st1);
        return statements;
    }
*/

    @Override
    protected void addVariables(Set<Variable> variables) {
        this.st1.addVariables(variables);
    }

    @Override
    protected void addExistentials(List<Existential> existentials) {
        this.st1.addExistentials(existentials);
    }

    @Override
    protected void replaceVarBySkolemConst(Hashtable<String, FonctionalSymbol> vars) {
        this.st1.replaceVarBySkolemConst(vars);
    }

    @Override
    protected void addLiterals(List<Statement> literalList) {
        this.st1.addLiterals(literalList);
    }

    @Override
    protected void addSymbols(List<Symbol> symbols) {
        this.st1.addSymbols(symbols);
    }

    @Override
    protected void addOrClauses(List<Statement> orClauses) {
        this.st1.addOrClauses(orClauses);
    }


    @Override
    protected void addConsts(HashSet<FonctionalSymbol> consts) {
        this.st1.addConsts(consts);
    }

    @Override
    public List<TransformAction> getActions() {
        throw new UnsupportedOperationException();
    }

    @Override
    public Statement getTransformationResult(TransformAction.TRANFORMATION action, boolean cloneSymbol) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Symbol getSymbol() {
        throw new UnsupportedOperationException();
    }

}
