package ai.agent.logic;

import ai.agent.logic.expressions.Not;
import ai.agent.logic.expressions.Statement;
import ai.agent.logic.expressions.Symbol;


import java.util.*;

public class DPLL_CLEAN {

    public static boolean explore(KnowledgeBase kb, Statement e){

       // System.out.println();
       // System.out.println("EXPLORATION "+e);
        //AJOUT DU COMPLEMENT DANS LA LISTE DES CLAUSES
        Statement cpm = new Not(e);

        kb.addStatementToProove(cpm);

      //  TimeUtil timer = new TimeUtil();
        DPLLrs rs = explore(kb, kb.getStatementsToCheck(), kb.getSymbols(),0);
       //timer.printTimeDetlaStr();

        kb.resetKB();

        return !rs.rs;

    }

    private static DPLLrs explore(KnowledgeBase kb, Set<Statement> modifiedClauses, Set<Symbol> symbols, int d) {


        Set<Statement> lastNewTrueClauses = new LinkedHashSet<>();

        /*------------------ TEST DE PARCOUR DES CLAUSES NON VRAI ----------------*/



        for(Statement clause : modifiedClauses){

            int cptrLiteralFalse = 0;
            //récupere la liste des literaux
            for (Statement literal : clause.getLiterals()) {
                //si le literal a déja été assigné
                if (literal.getSymbol().isInitialized()) {
                    if (literal.isTrue()) {
                        //si le literal est vrai, la clause est vrai
                        //on passe à la prochaine clause
                        lastNewTrueClauses.add(clause);
                        clause.setTrueClause(true);
                        break;
                    } else {
                        cptrLiteralFalse++;
                    }
                }
            }

            if (cptrLiteralFalse == clause.getLiterals().size()) {

                resetTrueClauses(lastNewTrueClauses);
                return new DPLLrs(false);
            }

        }


        if(modifiedClauses.size() == lastNewTrueClauses.size()){

            resetTrueClauses(lastNewTrueClauses);
            return new DPLLrs(true);
        }

        /*---------------RETRAIT DES NOUVELLES CLAUSES----------------*/

        modifiedClauses.removeAll(lastNewTrueClauses);


        Set<Statement> conflicts;

        Set<Symbol> pureSymbolsInit = null;

        Set<Statement> nextNewTrueClauses = null;

        boolean resetDecLiteral = false;

        if(!lastNewTrueClauses.isEmpty()) {

            pureSymbolsInit = new LinkedHashSet<>();
            nextNewTrueClauses = new LinkedHashSet<>();

            //trouve recursivement des symboles pures en les initialisant avec la bonne valeur
            //et en sauvegardant les clauses vrai

            addPureSymbols(kb,lastNewTrueClauses, nextNewTrueClauses, pureSymbolsInit);

            resetDecLiteral = true;

            if (!pureSymbolsInit.isEmpty()) {

        /*---------------RETRAIT DES NOUVELLES CLAUSES DE L INDEX----------------*/

                modifiedClauses.removeAll(nextNewTrueClauses);

                symbols.removeAll(pureSymbolsInit);

                if(!symbols.iterator().hasNext()){

                    resetLiterals(lastNewTrueClauses);
                    resetLiterals(nextNewTrueClauses);
                    resetTrueClauses(lastNewTrueClauses);
                    resetTrueClauses(nextNewTrueClauses);
                    resetSymbols(symbols, pureSymbolsInit);

                    return new DPLLrs(true);
                }

            }

        }

        Symbol symbole = findUnitaryClause(modifiedClauses);

        if(symbole != null){

            conflicts = getConflictualSymbols(kb, symbole);

            symbols.remove(symbole);
            symbole.setInitialized(true);

            //les clauses impactés par la modification de ce symbole
            //peuvent être potentiellement fausses ou vrai
            //ces clauses devraient être ajoutés aux précédentes qui n'ont pas été detecté comme fausses
            //et celles detectés comme vrai devraient être retirées

            for(Statement st : kb.getclausesFromSymbolIndex().get(symbole)){
                if(!st.isTrueClause()){
                    modifiedClauses.add(st);
                }
            }

            DPLLrs rs = explore(kb, modifiedClauses, symbols,d+1);

            //le resultat contient un liste de conflits et ce symbole en fait parti
            if(!rs.rs && rs.conflicts != null ){

                if(rs.conflicts.contains(symbole)){

                    //on ajoute les conflicts retourné à ceux de ce symbole
                    conflicts.addAll(rs.conflicts);
                    conflicts.remove(symbole);

                }else{

                    if(resetDecLiteral){
                        resetLiterals(lastNewTrueClauses);
                        resetLiterals(nextNewTrueClauses);
                        resetTrueClauses(nextNewTrueClauses);
                        resetSymbols(symbols, pureSymbolsInit);
                        resetModifiedClausesInsertNew(nextNewTrueClauses, modifiedClauses);
                    }

                    resetModifiedClausesRemoveClauses(kb,modifiedClauses, symbole);
                    resetModifiedClausesInsertNew(lastNewTrueClauses,modifiedClauses);
                    resetTrueClauses(lastNewTrueClauses);
                    resetSymbol(symbols, symbole);

                    return rs;
                }
            }

            if(resetDecLiteral){
                resetLiterals(lastNewTrueClauses);
                resetLiterals(nextNewTrueClauses);
                resetTrueClauses(nextNewTrueClauses);
                resetSymbols(symbols, pureSymbolsInit);

                if(!rs.rs)
                    resetModifiedClausesInsertNew(nextNewTrueClauses,modifiedClauses);
            }

            if(!rs.rs) {
                resetModifiedClausesRemoveClauses(kb,modifiedClauses, symbole);
                resetModifiedClausesInsertNew(lastNewTrueClauses, modifiedClauses);
            }

            resetTrueClauses(lastNewTrueClauses);
            resetSymbol(symbols, symbole);

            rs.conflicts = conflicts;

            return rs;
        }

        symbole = getMostConstrainedSymbol(symbols);

        symbols.remove(symbole);
        symbole.setInitialized(true);

        (symbole).setValue(true);

        for(Statement st : kb.getclausesFromSymbolIndex().get(symbole)){
            if(!st.isTrueClause()){
                modifiedClauses.add(st);
            }
        }

        DPLLrs rs = explore(kb, modifiedClauses, symbols,d+1);

        conflicts = getConflictualSymbols(kb, symbole);

        if(!rs.rs) {

            //le resultat contient un liste de conflits et ce symbole en fait parti
            if(rs.conflicts != null ){

                if(rs.conflicts.contains(symbole)){

                    //on ajoute les conflicts retourné à ceux de ce symbole
                    conflicts.addAll(rs.conflicts);
                    conflicts.remove(symbole);

                }else{

                    if(resetDecLiteral){
                        resetLiterals(lastNewTrueClauses);
                        resetLiterals(nextNewTrueClauses);
                        resetTrueClauses(nextNewTrueClauses);
                        resetSymbols(symbols, pureSymbolsInit);
                        resetModifiedClausesInsertNew(nextNewTrueClauses,modifiedClauses);
                    }

                    resetModifiedClausesRemoveClauses(kb,modifiedClauses, symbole);
                    resetModifiedClausesInsertNew(lastNewTrueClauses,modifiedClauses);
                    resetTrueClauses(lastNewTrueClauses);
                    resetSymbol(symbols, symbole);

                    //ce symbole ne permettrait pas de resoudre le probleme
                    return rs;
                }
            }

            (symbole).setValue(false);

            DPLLrs rs2 = explore(kb, modifiedClauses, symbols,d + 1);

            if (!rs2.rs && rs2.conflicts != null) {

                if (rs2.conflicts.contains(symbole)) {

                    //on ajoute les conflicts retourné à ceux de ce symbole
                    conflicts.addAll(rs2.conflicts);
                    conflicts.remove(symbole);
                } else {

                    //ce symbole ne permettrait pas de resoudre le probleme
                    if(resetDecLiteral){
                        resetLiterals(lastNewTrueClauses);
                        resetLiterals(nextNewTrueClauses);
                        resetTrueClauses(nextNewTrueClauses);
                        resetSymbols(symbols, pureSymbolsInit);
                        resetModifiedClausesInsertNew(nextNewTrueClauses,modifiedClauses);
                    }

                    resetModifiedClausesRemoveClauses(kb,modifiedClauses, symbole);
                    resetModifiedClausesInsertNew(lastNewTrueClauses,modifiedClauses);
                    resetTrueClauses(lastNewTrueClauses);
                    resetSymbol(symbols, symbole);

                    return rs2;
                }
            }

            if(resetDecLiteral){
                resetLiterals(lastNewTrueClauses);
                resetLiterals(nextNewTrueClauses);
                resetTrueClauses(nextNewTrueClauses);
                resetSymbols(symbols, pureSymbolsInit);
                if(!rs.rs)
                    resetModifiedClausesInsertNew(nextNewTrueClauses,modifiedClauses);
            }

            if(!rs.rs) {
                resetModifiedClausesRemoveClauses(kb,modifiedClauses, symbole);
                resetModifiedClausesInsertNew(lastNewTrueClauses, modifiedClauses);
            }
            resetTrueClauses(lastNewTrueClauses);
            resetSymbol(symbols, symbole);

            rs2.conflicts = conflicts;

            return rs2;
        }


        if(resetDecLiteral){
            resetLiterals(lastNewTrueClauses);
            resetLiterals(nextNewTrueClauses);
            resetTrueClauses(nextNewTrueClauses);
            resetSymbols(symbols, pureSymbolsInit);
            if(!rs.rs)
                resetModifiedClausesInsertNew(nextNewTrueClauses,modifiedClauses);
        }

        if(!rs.rs) {
            resetModifiedClausesRemoveClauses(kb, modifiedClauses, symbole);
            resetModifiedClausesInsertNew(lastNewTrueClauses, modifiedClauses);
        }
        resetTrueClauses(lastNewTrueClauses);
        resetSymbol(symbols, symbole);

        return new DPLLrs(true);
    }

    private static void resetModifiedClausesRemoveClauses(KnowledgeBase kb, Set<Statement> modifiedClauses, Symbol symbole) {

        for(Statement clauses : kb.getclausesFromSymbolIndex().get(symbole)){
            modifiedClauses.remove(clauses);
        }

    }


    private static void resetSymbol(Set<Symbol> symbols, Symbol symbole){
        symbols.add(symbole);
        symbole.setInitialized(false);
    }

    private static void resetSymbols(Set<Symbol> symbols, Set<Symbol> symbolsAdd){
        symbols.addAll(symbolsAdd);
        for(Symbol symbol : symbolsAdd){
            symbol.setInitialized(false);
        }
    }

    private static void resetTrueClauses(Set<Statement> clauses){
        for(Statement trueClause : clauses) {
            trueClause.setTrueClause(false);
        }

    }

    private static void resetModifiedClausesInsertNew(Set<Statement> newClauses, Set<Statement> clauses){
        /*---------------REMISE DES NOUVELLES CLAUSES DANS L'ENSEMBLE ----------------*/
        clauses.addAll(newClauses);
        /*-------------------------------*/
    }

    private static void resetLiterals(Set<Statement> clauses) {

        for(Statement clause : clauses){
            for(Statement literal : clause.getLiterals()){
                if(literal instanceof Not){
                    literal.getSymbol().addNeg();
                }else{
                    literal.getSymbol().addPos();
                }
            }
        }

    }


    public static class DPLLrs{

        public DPLLrs(boolean rs) {
            this.rs = rs;
        }

        boolean rs;
        Set<Statement> conflicts;
    }

    private static Set<Statement> getConflictualSymbols(KnowledgeBase kb, Statement symbole) {

        Set<Statement> conflict = new LinkedHashSet<>();

        for(Statement clause : kb.getclausesFromSymbolIndex().get(symbole)){
            for(Statement symbol : clause.getSymbols()) {
                if(symbol.isInitialized()) {
                    conflict.addAll(clause.getSymbols());
                }
            }
        }

        return conflict;
    }



    private static Symbol getMostConstrainedSymbol(Set<Symbol> symbols) {

        int maxClauses = 0;

        Symbol mostConstrained = symbols.iterator().hasNext() ? symbols.iterator().next() : null;

        for(Symbol symbol : symbols){

            //le nombre de clauses total peut aussi etre donné par le nombre
            //de literaux dans lequels on peut trouver le symbole
            //les literaux se trouvant dans des clauses rendu vrai sont deja decrementés
            // via la recherche de symboles pures
            int totalClause = symbol.getTotalPos() + symbol.getTotaNeg();

            if(totalClause > maxClauses){
                maxClauses = totalClause;
                mostConstrained = symbol;
            }

        }

        return mostConstrained;

    }

    private static Symbol findUnitaryClause( Set<Statement> clauses ) {

        // à la base les clauses modifié contiennent aussi les clauses unitaire
        // detectés à l'avance. ce qui evite de gerer une liste contenant aussi les clauses
        // qui n'ont pas encore été modifie rien que pour
        //trouver ce quelques clauses qui ne contiennt qu'un seul literal
        //les autres clauses unitaire detectés pendant le deroulement du processus
        //ne sont engendré que par des clauses modifié et non rendu vrai

        for (Statement clause : clauses) {

            Symbol newSymbol = clause.isUnitaryClauseTest();
            if (newSymbol != null) {

                //sera detecté au rappel de fonction
                //et permettra de trouver de nouveaux symboles purs
                // pourrait aussi etre executé en cascade ???????
                // newTrueClauses.add(clause);
                // clause.setTrueClause(true);
                return newSymbol;
            }
        }

        return null;
    }



    private static void addPureSymbols(KnowledgeBase kb, Set<Statement> lastNewTrueClauses, Set<Statement> nextNewTrueClauses,
                                       Set<Symbol> initSymbols){


        for(Statement clause : lastNewTrueClauses){
            //pour chaque literaux de la clause rendu vrai
            for( Statement literal : clause.getLiterals() ){

                 /*
                    * Si le symbole est deja initialise il doit etre ignoré
                    * un symbole ne peut etre detecte comme pure que si il n'est pas initialisé
                    *
                    * Par exemple, un symbole contenant deux literaux opposés dans des clauses differentes
                    * le premier literal positif est choisi car dans une clause unitaire ou il est seul ou que tout les autres literaux sont faux
                    * il est rendu vrai pour rendre la clause vrai.
                    * Si par la suite on considere le literal opposé comme pure est qu'on choisi de le rendre faux
                    * la clause unitaire precedente se retrouve fausse.
                    * */

                //on decremente le nombre de literaux positif ou negatif du symbole
                if(literal instanceof Not){
                    literal.getSymbol().remNeg();
                }else{
                    literal.getSymbol().remPos();
                }


                //si le symbole est pure
                if(literal.getSymbol().isPure() && !literal.getSymbol().isInitialized()){


                    //si le symbole a déja été initialisé plus haut on ne doit pas
                    //lui donner un autre valeur ni le retirer des symboles

                    //le symbole est pure on l'initialise pour rendre les clauses auquels il appartient vrai
                    if (literal.getSymbol().isNeg()) {
                        literal.getSymbol().setValue(false);
                    } else {
                        literal.getSymbol().setValue(true);
                    }
                    //si il a déja été initialisé on ne le retire pas des symboles
                    //mais on modifie tout de même sa valeur

                    literal.getSymbol().setInitialized(true);
                    initSymbols.add(literal.getSymbol());


                    Set<Statement> newTrueClauses = new LinkedHashSet<>();

                    //pour chaque clauses de se literals rendu vrai par son assignation
                    for(Statement newClause : kb.getclausesFromSymbolIndex().get(literal.getSymbol())){

                        if(newClause.isTrueClause()){
                            continue;
                        }

                        newClause.setTrueClause(true);
                        newTrueClauses.add(newClause);

                    }

                    nextNewTrueClauses.addAll(newTrueClauses);
                    addPureSymbols(kb, newTrueClauses, nextNewTrueClauses, initSymbols);

                }
            }
        }

    }


}
