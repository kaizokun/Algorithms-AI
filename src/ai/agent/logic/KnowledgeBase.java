package ai.agent.logic;

import ai.State;
import ai.agent.logic.expressions.Not;
import ai.agent.logic.expressions.And;
import ai.agent.logic.expressions.Statement;
import ai.agent.logic.expressions.Symbol;

import java.util.*;

public class KnowledgeBase extends State<List<Statement>> {

    private Set<Statement> statements = new LinkedHashSet<>();
    private Set<Symbol> symbols = new LinkedHashSet<>();
    private Hashtable<Statement,Set<Statement>> symbolsClausesIndex;
    private Set<Statement> unitaryStatements = new LinkedHashSet<>();
    private Set<Symbol> pureSymbols;
    private Set<Statement> statementsToCheck;

    public KnowledgeBase() {
    }

    public KnowledgeBase(List<Statement> statements) {
       this.addStatements(statements);
    }


    public Hashtable<Statement, Set<Statement>> getclausesFromSymbolIndex() {

        if(this.symbolsClausesIndex == null) {

            this.symbolsClausesIndex = new Hashtable<>();
            //plus necessaire si le reset fonctionne bien
/*
            for(Statement symbol : this.getSymbols()){
                symbol.initLiteralsSet();
            }
*/
            for (Statement statement : this.getStatements()) {
                this.addStatementToIndex(statement, this.symbolsClausesIndex);
            }
        }

        return symbolsClausesIndex;

    }

    private void addStatementToIndex(Statement statement, Hashtable<Statement, Set<Statement>> index ){

        for (Statement symbol : statement.getSymbols()) {

            Set<Statement> symbolStatements = index.get(symbol);

            if (symbolStatements == null) {
                symbolStatements = new LinkedHashSet<>();
                index.put(symbol, symbolStatements);
            }

            symbolStatements.add(statement);

        }

    }

    /*
    public Hashtable<Statement, Set<Statement>> getclausesFromSymbolIndexCopy() {
        Hashtable<Statement, Set<Statement>> indexCopy = new Hashtable<>();
        for(Map.Entry<Statement,Set<Statement>> entry : this.getclausesFromSymbolIndex().entrySet()){
            indexCopy.put(entry.getKey(),  new LinkedHashSet<>(entry.getObjectRef()));
        }
        return indexCopy;
    }
*/



    public void addStatements(List<Statement> statements){

        for(Statement statement : statements){
            this.addStatement(statement);
        }
    }

    public void addStatement(Statement statement){

        if(!this.statements.contains(statement)){
            this.statements.add(statement);

            //on ajoute egalement les symboles du statements
            for(Symbol symbol : statement.getSymbols()){
                this.addSymbol(symbol);
            }

            //on compte le nombre de literaux des symboles
            for (Statement literal : statement.getLiterals()) {
                if(literal instanceof Not) {literal.getSymbol().addNeg();}
                else{literal.getSymbol().addPos();}
            }

            if(statement.getLiterals().size() == 1){
                this.unitaryStatements.add(statement);
            }

        }
    }

    public void addSymbol(Symbol symbol) {
        if(!this.getSymbols().contains(symbol)){
            this.symbols.add(symbol);
        }
    }

    public Set<Symbol> getSymbols(){
        return this.symbols;
    }


    public Collection<Statement> getStatementsList(){
        return this.statements;
    }


    public Set<Statement> getStatements() {
        return statements;
    }


    public Set<Symbol> getPureSymbols(){

        if(this.pureSymbols == null){

            this.pureSymbols = new LinkedHashSet<>();

            for(Symbol symbol : this.getSymbols()) {

                if (symbol.isPure()) {
                    //initialise la valeur du symbole rendant toutes les énoncés sous forme de clauses
                    //auquels il appartient vrai
                    symbol.setPurevalue();
                    symbol.setInitialized(true);
                    this.pureSymbols.add(symbol);
                }
            }

        }

        return this.pureSymbols;

    }


    public Set<Statement> getStatementsToCheck(){

        if(this.statementsToCheck == null) {

            this.statementsToCheck = new LinkedHashSet<>();
            //recupere les symboles pures
            for (Symbol symbol : this.getPureSymbols()) {
                this.statementsToCheck.addAll(this.getclausesFromSymbolIndex().get(symbol));
            }
            //ajout des clauses unitaire qui sont les seuls à ne pas avoir été impacté
            //par la modification d'un symbole donc n'ont pas besoin d'être vérifié
            //mais necessaire pour la recherche de clauses réélement unitaire
            //les autres clauses unitaires étant decouverte au fur et à mesure que des literaux sont
            //initialisé à faux, dans ces cas l'exploration ajoute ces clauses dans cette liste pendant l'exploration
            this.statementsToCheck.addAll(this.unitaryStatements);

            //retire les symboles pures
            this.getSymbols().removeAll(this.getPureSymbols());
        }

        return this.statementsToCheck;

    }


    private Statement statementToProove;
    private boolean containStatementToProove = false;
    private Set<Symbol> statementToProoveNewSymbols = new LinkedHashSet<>();

    public void addStatementToProove(Statement statement){

        //si la kb contient deja l'enonce que l'on cherche a prouver il ne faudra
        //pas le retirer à la fin de l'exploration
        this.containStatementToProove = this.getStatements().contains(statement);

        if(!this.containStatementToProove) {

            this.statementToProove = statement;

            this.addStatement(statement);
            //sauvegarde des symboles inconnus
            for (Symbol symbol : statement.getSymbols()) {
                if (!this.getSymbols().contains(symbol)) {
                    this.statementToProoveNewSymbols.add(symbol);
                }
            }
            //ajout des symboles pures
            for(Symbol symbol : statement.getSymbols()){
                if(symbol.isPure()){
                    this.getPureSymbols().add(symbol);
                }
            }

            this.addStatementToIndex(statement, this.getclausesFromSymbolIndex());
        }

    }

    public void resetKB(){

        //retirer l enonce à prouver des listes
        //statements, unitaryStatements
        //ainsi que les symbols des listes symbols, pureSymbols
        //de l index
        //vider la liste statementsToCheck

        if(!this.containStatementToProove){

            this.getStatements().remove(this.statementToProove);
            this.unitaryStatements.remove(this.statementToProove);

            //retrait de la clause de l index
            for(Symbol symbol : this.statementToProove.getSymbols()){
                this.getclausesFromSymbolIndex().get(symbol).remove(this.statementToProove);
            }

            //retire les symboles qui n'étaient pas present dans la KB
            this.getSymbols().removeAll(this.statementToProoveNewSymbols);
            this.getPureSymbols().remove(this.statementToProoveNewSymbols);

            //retrait des symboles de l index
            for(Symbol symbol : this.statementToProoveNewSymbols) {
                this.getclausesFromSymbolIndex().remove(symbol);
            }

        }

        this.statementsToCheck = null;

    }

    @Override
    public boolean equals(Object obj) {
        KnowledgeBase b2 = (KnowledgeBase) obj;
        return this.toString().equals(b2.toString());
    }

    @Override
    public int hashCode() {
        return this.toString().hashCode();
    }

    @Override
    public String toString() {
        String rs = "";
        for(Statement statement : statements) {
            rs += statement.toString()+" + ";
        }
        return rs;
    }

    public KnowledgeBase clone(){

        List<Statement> statements = new LinkedList<>();

        for(Statement statement : this.statements){
            statements.add(statement.clone(true));
        }

        return new KnowledgeBase(statements);

    }

    public Statement getStatement(){
        And and = new And();
        return and.getStatement(new LinkedList(this.statements));
    }


    public void createComponents(){
        createComponents(this.getSymbols(), this.getclausesFromSymbolIndex());
        //System.out.println(" CNF : "+this.getStatements().size());
        //System.out.println(" SYMBOLS : "+this.getSymbols().size());
    }


    public static List<KBcomponent> createComponents( Set<Symbol> symbolsSet,  Hashtable<Statement, Set<Statement>> index ){

        Set<Symbol> symbols = new LinkedHashSet<>(symbolsSet);

        //List<Set<Statement>> symbolsSets = new LinkedList<>();
       // List<Set<Statement>> clausesSets = new LinkedList<>();

        List<KBcomponent> kBcomponents = new LinkedList<>();

        while(!symbols.isEmpty()){

            Symbol symbol = symbols.iterator().next();
            symbols.remove(symbol);

            if(symbol.isInitialized()) {
                continue;
            }

            //kBcomponents.add(new KBcomponent());
            KBcomponent kBcomponent = new KBcomponent();

            //Set<Statement> setSymbols = new LinkedHashSet<>();
            //Set<Statement> setClauses = new LinkedHashSet<>();

            kBcomponent.getSymbolsSet().add(symbol);

            Set<Symbol> newSymbols = new LinkedHashSet<>();

            do {

                Set<Statement> newClauses = new LinkedHashSet<>(index.get(symbol));
                newClauses.removeAll(kBcomponent.getClausesSet());
                for(Statement newClause : newClauses) {
                    if(!newClause.isTrueClause()) {
                        kBcomponent.getClausesSet().add(newClause);
                    }
                }

                for(Statement clause : newClauses) {
                    for(Symbol sym : clause.getSymbols()){
                        if(!kBcomponent.getSymbolsSet().contains(sym)){
                            if(!sym.isInitialized()) {
                                newSymbols.add(sym);
                            }
                        }
                    }
                }

                kBcomponent.getSymbolsSet().addAll(newSymbols);
                symbols.removeAll(newSymbols);

                if(newSymbols.iterator().hasNext()) {
                    symbol = newSymbols.iterator().next();
                    newSymbols.remove(symbol);
                }else{
                    symbol = null;
                }

            }while(symbol != null);

            kBcomponents.add(kBcomponent);

        }

        //System.out.println(symbolsSets.size()+" "+clausesSets.size());

        int totalClauses = 0, totalSymbols = 0;

        for(int i = 0 ; i < kBcomponents.size() ; i ++){
/*
            System.out.println();
            System.out.println("SET SYMBOL CLAUSES");
            System.out.println();

            System.out.println("SYMBOLS "+kBcomponents.get(i).getSymbolsSet().size());
            for( Statement symbol : kBcomponents.get(i).getSymbolsSet()){
                System.out.println(symbol);
            }

            System.out.println();
            System.out.println("CLAUSES "+kBcomponents.get(i).getClausesSet().size());
            for( Statement clause : kBcomponents.get(i).getClausesSet()){
                System.out.println(clause);
            }

            System.out.println("SYMBOLS "+kBcomponents.get(i).getSymbolsSet().size());
            System.out.println("CLAUSES "+kBcomponents.get(i).getClausesSet().size());
            */
            totalClauses += kBcomponents.get(i).getClausesSet().size();
            totalSymbols += kBcomponents.get(i).getSymbolsSet().size();

        }

       // System.out.println("total clauses : "+totalClauses);
       // System.out.println("total symbols : "+totalSymbols);


        return kBcomponents;
    }

    public static class KBcomponent{

        private Set<Symbol> symbolsSet = new LinkedHashSet<>();
        private Set<Statement> clausesSet = new LinkedHashSet<>();

        public Set<Symbol> getSymbolsSet() {
            return symbolsSet;
        }

        public void setSymbolsSet(Set<Symbol> symbolsSet) {
            this.symbolsSet = symbolsSet;
        }

        public Set<Statement> getClausesSet() {
            return clausesSet;
        }

        public void setClausesSet(Set<Statement> clausesSet) {
            this.clausesSet = clausesSet;
        }

        @Override
        public String toString() {
            return "Symboles : "+symbolsSet.size()+"\n" +
                    "Clauses : "+clausesSet.size();
        }
    }

}
