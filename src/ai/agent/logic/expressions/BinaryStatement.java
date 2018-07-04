package ai.agent.logic.expressions;

import java.util.*;

public abstract class BinaryStatement extends Statement {

    protected Statement st1, st2;

    public BinaryStatement() { }

    public BinaryStatement(Statement st1, Statement st2) {
        this.st1 = st1;
        this.st2 = st2;
    }

    public Statement getSt1() {
        return st1;
    }

    public void setSt1(Statement st1) {
        this.st1 = st1;
    }

    public Statement getSt2() {
        return st2;
    }

    public void setSt2(Statement st2) {
        this.st2 = st2;
    }
/*
    @Override
    public List<Statement> getStatements() {
        LinkedList<Statement> statements = new LinkedList<>();
        statements.add(st1);
        statements.add(st2);
        return statements;
    }
*/

    @Override
    protected void addLiterals(List<Statement> literalList) {
        this.st1.addLiterals(literalList);
        this.st2.addLiterals(literalList);
    }

    @Override
    protected void addSymbols(List<Symbol> symbols) {
        this.st1.addSymbols(symbols);
        this.st2.addSymbols(symbols);
    }


    @Override
    protected void addOrClauses(List<Statement> orClauses) {
        this.st1.addOrClauses(orClauses);
        this.st2.addOrClauses(orClauses);
    }

    @Override
    protected void addExistentials(List<Existential> existentials) {
        this.st1.addExistentials(existentials);
        this.st2.addExistentials(existentials);
    }

    @Override
    protected void addConsts(HashSet<FonctionalSymbol> consts) {
        this.st1.addConsts(consts);
        this.st2.addConsts(consts);
    }

    @Override
    protected void replaceVarBySkolemConst(Hashtable<String, FonctionalSymbol> vars) {
        this.st1.replaceVarBySkolemConst(vars);
        this.st2.replaceVarBySkolemConst(vars);
    }

    @Override
    protected void addVariables(Set<Variable> variables) {
        this.st1.addVariables(variables);
        this.st2.addVariables(variables);
    }

    @Override
    public Symbol getSymbol() {
        throw new UnsupportedOperationException();
    }

    /**
     * Retourne un nouveau statement contenant un ensemble deliteraux
     *
     * **/
    public Statement getStatement(Collection<Statement> p_sts){

        LinkedList<Statement> sts = new LinkedList(p_sts);

        if(sts.isEmpty()){
            return new EmptyStatement();
        }

        if(sts.size() == 1 ) {
            return sts.get(0).clone(false);
        }

        //crée un enonce de type AND
        BinaryStatement statement = this.getNewStatement();
        BinaryStatement lastStatement = statement;
        //place le premier enonce en premiere partie du AND
        lastStatement.setSt1(sts.removeFirst().clone(false));

        //Tand qu'il y a des enoncés
        while(!sts.isEmpty()){
            //récupere le prochain enonce
            Statement st2 = sts.removeFirst().clone(false);
            //si c'était le dernier on l'ajoute en derniere partie du AND
            if(sts.isEmpty()){
                lastStatement.setSt2(st2);
            }else{
                //sinon il reste des enoncé à ajouter par la suite
                //on recrée un AND on y place l'enonce en premiere partie
                //on place le nouveau AND en deuxieme partie du precedent
                //et on remplace le and de depart
                BinaryStatement statement2 = this.getNewStatement();
                statement2.setSt1(st2);
                lastStatement.setSt2(statement2);
                lastStatement = statement2;
            }

        }


        return statement;

    }

    protected abstract BinaryStatement getNewStatement();

}
