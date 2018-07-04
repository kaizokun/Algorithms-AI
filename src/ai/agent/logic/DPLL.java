package ai.agent.logic;

import ai.agent.logic.expressions.Not;
import ai.agent.logic.expressions.Statement;
import ai.agent.logic.expressions.Symbol;
import util.TimeUtil;
import util.Util;

import java.util.*;

public class DPLL {

    public static int totalremove = 0 , totaladd = 0;
    public static String ident;
    private static Hashtable<Statement,Set<Statement>> symbolsClausesIndex;
    public static double totalPure;
    public static double totalReset;
    public static double totalPureAppel;
    public static double totalUnitaire;
    public static double totalTest;
    public static double totalTestCTP;
    public static double totalConflictSymbol;
    public static double totallitDec = 0;
    public static double totallitInc = 0;

    private static boolean showLog = false;

    public static boolean explore(KnowledgeBase kb, Statement e){

        totalPureAppel = 0.0;
        totalPure = 0.0;
        totalTest = 0.0;
        totalTestCTP = 0.0;
        totalUnitaire = 0.0;
        totalConflictSymbol = 0.0;
        totalremove = 0;
        totaladd = 0;
        totalReset = 0;

        //Util.initTime();
        //copy du CNF de la KB et des symboles

       // Set<Statement> clauses = new LinkedHashSet(kb.getStatements());

        //kb.initclausesFromSymbolIndex();

        //System.exit(0);


        Set<Symbol> symbols = new LinkedHashSet(kb.getSymbols());

        symbolsClausesIndex = kb.getclausesFromSymbolIndex();

        List<Symbol> rm = new LinkedList<>();

        Set<Statement> modifiedClauses = new LinkedHashSet();

        for(Symbol symbol : symbols){
            if(symbol.isPure()){

                if(symbol.isNeg()){
                    symbol.setValue(false);
                }else{
                    symbol.setValue(true);
                }

                System.out.println("PURE "+symbol);
                modifiedClauses.addAll(symbolsClausesIndex.get(symbol));

                rm.add(symbol);
            }
        }

        //ajout des clauses unitaires
        int unit = 0 ;
        for(Statement clause : kb.getStatements()){
            if(clause.getLiterals().size() == 1){
                modifiedClauses.add(clause);
                unit++;
            }
        }

        System.out.println("UNIT "+unit);

        symbols.removeAll(rm);

/*
        for(Statement clause : clauses){
            System.out.println(clause);
        }

        System.out.println();
*/
/*
        System.out.println("SYMBOLS");
        for(Symbol symbol : symbols){
            System.out.println(symbol+" "+symbol.getTotalPos()+" "+symbol.getTotaNeg());
        }
*/

        // System.exit(0);



/*
        for(Symbol symbol : symbols){
            System.out.println(symbol+" "+symbol.getTotalPos()+" "+symbol.getTotaNeg());
        }
*/
        // System.exit(0);

        //AJOUT DU COMPLEMENT DANS LA LISTE DES CLAUSES
        Statement cpm = new Not(e);
       // clauses.add(cpm);

        //COMPTER LES LITERAUX DU COMPLEMENT

        for(Statement literal : cpm.getLiterals()){
            if(literal instanceof Not){
                literal.getSymbol().addNeg();
            }else{
                literal.getSymbol().addPos();
            }
        }

        //AJOUT DE LA CLAUSE COMPLEMENTAIRE A PROUVER DANS LA COPY DE L INDEX DES CLAUSES PAR SYMBOLES
        //AJOUT DU SYMBOLE EGALEMENT
        for(Symbol symbol : cpm.getSymbols()){
            Set symbolClause = symbolsClausesIndex.get(symbol);
            if(symbolClause == null){
                symbolClause = new LinkedHashSet();
                symbolsClausesIndex.put( symbol, symbolClause);
            }
            symbolClause.add(cpm);
            symbols.add(symbol);
        }

        System.out.println();
        System.out.println("SYMBOLES "+symbols.size());

        totallitDec = 0;
        totallitInc = 0;
/*
        for(Symbol symbol : symbols){
            System.out.println(symbol+" "+symbol.getTotalPos()+" "+symbol.getTotaNeg());
        }
*/
        TimeUtil t1 = new TimeUtil();
        DPLLrs rs = explore(modifiedClauses, symbols,0);
        t1.printTimeDetlaStr();
        System.out.println("PURE TOTAL "+ totalPure+" "+totalPureAppel);
        System.out.println("TEST TOTAL "+ totalTest+" "+totalTestCTP);
        System.out.println("UNITAIRE TOTAL "+ totalUnitaire);
        System.out.println("CONFLICT SYMBOL TOTAL "+ totalConflictSymbol);
        System.out.println("RESET TOTAL "+ totalReset);
        System.out.println("TOTAL AD RM "+totaladd+" "+totalremove);
        System.out.println("TOTAL INC DEC LITE - DEC "+totallitDec+" - INC "+totallitInc);

/*
        boolean allTrue = true;
        System.out.println("CLAUSES "+clauses.size());
        for(Statement clause : clauses){
            if(!clause.isTrue())
                allTrue = false;
        }

        System.out.println("ALLTRUE :"+allTrue);

        */
/*
        for(Symbol symbol : symbols){
            System.out.println(symbol+" "+symbol.getTotalPos()+" "+symbol.getTotaNeg());
        }
*/
       // System.exit(0);

        return !rs.rs;

    }

    private static DPLLrs explore(Set<Statement> modifiedClauses/*, Set<Statement> clauses*/, Set<Symbol> symbols, int d) {


       // String ident = "";
        if(showLog)
            ident = Util.getIdent(d);
        if(showLog)
            System.out.println(ident+" SYMBOLES : "+symbols.size()+" "+symbols);

        if(showLog)
            Util.initTime();

        Util.initTime();

        Set<Statement> lastNewTrueClauses = new LinkedHashSet<>();

        int cptrClauseTrue = 0;
        //pour chaque clause de la KB

        //la premiere fois aucun symbole n'est initialisé

        //for (Statement clause : clauses) {

        /*------------------ TEST DE PARCOUR DES CLAUSES NON VRAI ----------------*/

        // à la base les clauses modifié contiennent aussi les clauses unitaire
        // detectés à l'avance. ce qui evite de gerer une liste contenant aussi les clauses
        // qui n'ont pas encore été modifie rien que pour
        //trouver ce quelques clauses qui ne contiennt qu'un seul literals
        //les autres clauses unitaire detectés pendant le deroulement du processus
        //ne sont engendré que par des clauses modifié et non rendu vrai

        for(Statement clause : modifiedClauses){

            //System.out.println(ident+clause);
/*
                if (clause.isTrueClause()) {
                    cptrClauseTrue++;
                    continue;
                }
*/

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
                if (showLog)
                    System.out.println(ident + "clause false " + clause + " " + cptrLiteralFalse + " " + clause.getLiterals().size());

                resetTrueClauses(lastNewTrueClauses);

                return new DPLLrs(false);
            }

            totalTestCTP++;

        }

        // }



        totalTest += Util.computeTimeDelta();
/*
        if(showLog)
            System.out.println(ident+" TEST CLAUSES : "+Util.getTimeDeltaStr()+" "+lastNewTrueClauses.size()+"  "+clauses.size());

        if(showLog)
            System.out.println(ident + " SIZE CLAUSES " + clauses.size()+" <> "+cptrClauseTrue);
        */


        if(modifiedClauses.size() == lastNewTrueClauses.size()){
        //if(clauses.isEmpty()){
            //if(cptrClauseTrue == clauses.size()){
            if(showLog)
                System.out.println(ident+"clause true");

            resetTrueClauses(lastNewTrueClauses);

            return new DPLLrs(true);
        }

        /*---------------RETRAIT DES NOUVELLES CLAUSES----------------*/
        //clauses.removeAll(lastNewTrueClauses);
        totalremove+=lastNewTrueClauses.size();

        modifiedClauses.removeAll(lastNewTrueClauses);

        /*-------------------------------*/


        if(showLog)
            Util.initTime();

        Set<Statement> conflicts;

        Set<Symbol> pureSymbolsInit = null;
        Set<Statement> nextNewTrueClauses = null;
        boolean resetDecLiteral = false;
        if(!lastNewTrueClauses.isEmpty()) {

            pureSymbolsInit = new LinkedHashSet<>();
            nextNewTrueClauses = new LinkedHashSet<>();

            //trouve recursivement des symboles pures en les initialisant avec la bonne valeur
            //et en eliminant les clauses vrai
            TimeUtil t1 = new TimeUtil();
            addPureSymbols(lastNewTrueClauses, nextNewTrueClauses, pureSymbolsInit);
            totalPure += t1.getTimeDelta();
            resetDecLiteral = true;

            //Util.initTime();
            //Symbol symbole = findPureSymbolV2(lastPureSymbols, lastNewTrueClauses);
            //totalPure += Util.computeTimeDelta();

            if (showLog)
                System.out.println(ident + " PURE SYMBOL ");

            if (!pureSymbolsInit.isEmpty()) {

                if (showLog)
                    System.out.println(ident+" PURE SYMBOLS "+pureSymbolsInit.size());
/*
            conflicts = new LinkedHashSet<>();
            for(Symbol symbol : symbolsInit){
                conflicts.addAll(getConflictualSymbols(symbol));
            }
*/

/*
                for(Symbol symbol : symbols)
                    System.out.println(ident+" symbol : "+symbol);

                for(Symbol symbol : pureSymbolsInit)
                    System.out.println(ident+" init symbol : "+symbol);
*/

        /*---------------RETRAIT DES NOUVELLES CLAUSES DE L INDEX----------------*/

                //clauses.removeAll(nextNewTrueClauses);
                totalremove+=nextNewTrueClauses.size();

                modifiedClauses.removeAll(nextNewTrueClauses);

                symbols.removeAll(pureSymbolsInit);

                if(!symbols.iterator().hasNext()){

                    //System.out.println("NO NEXT "+clauses.size());

                    resetLiterals(lastNewTrueClauses);
                    resetLiterals(nextNewTrueClauses);
                    resetTrueClauses(lastNewTrueClauses);
                    resetTrueClauses(nextNewTrueClauses);
                    resetSymbols(symbols, pureSymbolsInit);
                    //resetModifiedClausesInsertNew(lastNewTrueClauses, clauses);
                    //resetModifiedClausesInsertNew(nextNewTrueClauses, clauses);


                    //retourner vrai rend la remise en place de l'ensemble
                   // resetModifiedClausesInsertNew(lastNewTrueClauses, modifiedClauses);
                   // resetModifiedClausesInsertNew(nextNewTrueClauses, modifiedClauses);


                    return new DPLLrs(true);
                }

            }

        }


        if(showLog)
            Util.initTime();

        Symbol symbole;

        Util.initTime();
        symbole = findUnitaryClause(modifiedClauses);

        totalUnitaire += Util.computeTimeDelta();

        if(showLog)
            System.out.println(ident+"CLAUSE UNITAIRE "+Util.getTimeDeltaStr());

        if(symbole != null){

            conflicts = getConflictualSymbols(symbole);

            if(showLog)
                System.out.println(ident+"UNITAIRE SYMBOL "+symbole);

            symbols.remove(symbole);
            symbole.setInitialized(true);

            //les clauses impactés par la modification de ce symbole
            //peuvent être potentiellement fausses ou vrai
            //ces clauses devraient être ajoutés aux précédentes qui n'ont pas été detecté comme fausses
            //et celles detectés comme vrai devraient être retirées

            for(Statement st : symbolsClausesIndex.get(symbole)){
                if(!st.isTrueClause()){
                    modifiedClauses.add(st);
                }
            }

            DPLLrs rs = explore(modifiedClauses, symbols,d+1);

            //le resultat contient un liste de conflits et ce symbole en fait parti
            if(!rs.rs && rs.conflicts != null ){
                if(rs.conflicts.contains(symbole)){
                    if(showLog)
                        System.out.println(ident+" "+symbole+" peut permettre de regler le problème");
                    //on ajoute les conflicts retourné à ceux de ce symbole
                    conflicts.addAll(rs.conflicts);
                    conflicts.remove(symbole);
                }else{
                    if(showLog)
                        System.out.println(ident+" "+symbole+" ne permet pas de regler le problème");

                    if(resetDecLiteral){
                        resetLiterals(lastNewTrueClauses);
                        resetLiterals(nextNewTrueClauses);
                        resetTrueClauses(nextNewTrueClauses);
                        resetSymbols(symbols, pureSymbolsInit);
                       // resetModifiedClausesInsertNew(nextNewTrueClauses,clauses);
                        resetModifiedClausesInsertNew(nextNewTrueClauses, modifiedClauses);
                    }

                    resetModifiedClausesRemoveClauses(modifiedClauses, symbole);
                    resetModifiedClausesInsertNew(lastNewTrueClauses,modifiedClauses);
                   // resetModifiedClausesInsertNew(lastNewTrueClauses,clauses);
                    resetTrueClauses(lastNewTrueClauses);
                    resetSymbol(symbols, symbole);

                    //ce symbole ne permettrait pas de resoudre le probleme
                    return rs;
                }
            }

            if(resetDecLiteral){
                resetLiterals(lastNewTrueClauses);
                resetLiterals(nextNewTrueClauses);
                resetTrueClauses(nextNewTrueClauses);
                resetSymbols(symbols, pureSymbolsInit);
                //resetModifiedClausesInsertNew(nextNewTrueClauses,clauses);
                if(!rs.rs)
                    resetModifiedClausesInsertNew(nextNewTrueClauses,modifiedClauses);
            }

            if(!rs.rs) {
                resetModifiedClausesRemoveClauses(modifiedClauses, symbole);
                resetModifiedClausesInsertNew(lastNewTrueClauses, modifiedClauses);
            }
           // resetModifiedClausesInsertNew(lastNewTrueClauses,clauses);
            resetTrueClauses(lastNewTrueClauses);
            resetSymbol(symbols, symbole);

            rs.conflicts = conflicts;

            return rs;
        }

        symbole = getMostConstrainedSymbol(symbols);

        symbols.remove(symbole);
        symbole.setInitialized(true);

        (symbole).setValue(true);
        if(showLog)
            System.out.println(ident+"NEXT SYMBOL "+symbole+" TRUE ");

        for(Statement st : symbolsClausesIndex.get(symbole)){
            if(!st.isTrueClause()){
                modifiedClauses.add(st);
            }
        }

        DPLLrs rs = explore(modifiedClauses, symbols,d+1);

        Util.initTime();
        conflicts = getConflictualSymbols(symbole);
        totalConflictSymbol += Util.computeTimeDelta();

        if(!rs.rs) {

            //le resultat contient un liste de conflits et ce symbole en fait parti
            if(rs.conflicts != null ){
                if(rs.conflicts.contains(symbole)){
                    if(showLog)
                        System.out.println(ident+" "+symbole+" peut permettre de regler le problème");
                    //on ajoute les conflicts retourné à ceux de ce symbole
                    conflicts.addAll(rs.conflicts);
                    conflicts.remove(symbole);
                }else{
                    if(showLog)
                        System.out.println(ident+" "+symbole+" ne permet pas de regler le problème");

                    if(resetDecLiteral){
                        resetLiterals(lastNewTrueClauses);
                        resetLiterals(nextNewTrueClauses);
                        resetTrueClauses(nextNewTrueClauses);
                        resetSymbols(symbols, pureSymbolsInit);
                        //resetModifiedClausesInsertNew(nextNewTrueClauses,clauses);
                        resetModifiedClausesInsertNew(nextNewTrueClauses,modifiedClauses);
                    }

                    resetModifiedClausesRemoveClauses(modifiedClauses, symbole);
                    resetModifiedClausesInsertNew(lastNewTrueClauses,modifiedClauses);
                    //resetModifiedClausesInsertNew(lastNewTrueClauses,clauses);
                    resetTrueClauses(lastNewTrueClauses);
                    resetSymbol(symbols, symbole);

                    //ce symbole ne permettrait pas de resoudre le probleme
                    return rs;
                }
            }

            (symbole).setValue(false);

            if (showLog)
                System.out.println(ident + "NEXT SYMBOL " + symbole+" false");

            DPLLrs rs2 = explore(modifiedClauses, symbols,d + 1);

            if (!rs2.rs && rs2.conflicts != null) {
                if (rs2.conflicts.contains(symbole)) {
                    if(showLog)
                        System.out.println(ident+" "+symbole+" peut permettre de regler le problème");
                    //on ajoute les conflicts retourné à ceux de ce symbole
                    conflicts.addAll(rs2.conflicts);
                    conflicts.remove(symbole);
                } else {
                    if(showLog)
                        System.out.println(ident+" "+symbole+" ne permet pas de regler le problème");
                    //ce symbole ne permettrait pas de resoudre le probleme

                    if(resetDecLiteral){
                        resetLiterals(lastNewTrueClauses);
                        resetLiterals(nextNewTrueClauses);
                        resetTrueClauses(nextNewTrueClauses);
                        resetSymbols(symbols, pureSymbolsInit);
                       // resetModifiedClausesInsertNew(nextNewTrueClauses,clauses);
                        resetModifiedClausesInsertNew(nextNewTrueClauses,modifiedClauses);
                    }

                    resetModifiedClausesRemoveClauses(modifiedClauses, symbole);
                    resetModifiedClausesInsertNew(lastNewTrueClauses,modifiedClauses);
                   // resetModifiedClausesInsertNew(lastNewTrueClauses,clauses);
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
                //resetModifiedClausesInsertNew(nextNewTrueClauses,clauses);
                if(!rs.rs)
                    resetModifiedClausesInsertNew(nextNewTrueClauses,modifiedClauses);
            }

            if(!rs.rs) {
                resetModifiedClausesRemoveClauses(modifiedClauses, symbole);
                resetModifiedClausesInsertNew(lastNewTrueClauses, modifiedClauses);
            }
            // resetModifiedClausesInsertNew(lastNewTrueClauses,clauses);
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
            //resetModifiedClausesInsertNew(nextNewTrueClauses,clauses);
            if(!rs.rs)
                resetModifiedClausesInsertNew(nextNewTrueClauses,modifiedClauses);
        }

        if(!rs.rs) {
            resetModifiedClausesRemoveClauses(modifiedClauses, symbole);
            resetModifiedClausesInsertNew(lastNewTrueClauses, modifiedClauses);
        }
        // resetModifiedClausesInsertNew(lastNewTrueClauses,clauses);
        resetTrueClauses(lastNewTrueClauses);
        resetSymbol(symbols, symbole);

        return new DPLLrs(true);
    }

    private static void resetModifiedClausesRemoveClauses(Set<Statement> modifiedClauses, Symbol symbole) {

        for(Statement clauses : symbolsClausesIndex.get(symbole)){
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
        totaladd+=newClauses.size();
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

                totallitInc ++;
            }
        }

        totalReset++;

    }


    public static class DPLLrs{

        public DPLLrs(boolean rs) {
            this.rs = rs;
        }

        boolean rs;
        Set<Statement> conflicts;
    }

    private static Set<Statement> getConflictualSymbols(Statement symbole) {

        Set<Statement> conflict = new LinkedHashSet<>();

        for(Statement clause : symbolsClausesIndex.get(symbole)){
            for(Statement symbol : clause.getSymbols()) {
                if(symbol.isInitialized()) {
                    conflict.addAll(clause.getSymbols());
                }
            }
        }

        return conflict;
    }



    private static Symbol getMostConstrainedSymbol(Set<Symbol> symbols) {

        //System.out.println("get most\n\n");

        int maxClauses = 0;

        Symbol mostConstrained = symbols.iterator().hasNext() ? symbols.iterator().next() : null;

        for(Symbol symbol : symbols){

            //le nombre de clauses total pourrait aussi etre donné par le nombre
            //de literaux dans lequels on peut trouver le symbole
            //les literaux se trouvant dans des clauses rendu vrai sont deja decrementés
            // via la recherche de symboles pures
            int totalClause = symbol.getTotalPos() + symbol.getTotaNeg();

            /*
            int totalClause = 0;
            for(Statement clause : symbolsClausesIndex.get(symbol)){
                if (!clause.isTrueClause())
                    totalClause ++;
            }
*/
            if(totalClause > maxClauses){
                maxClauses = totalClause;
                mostConstrained = symbol;
            }

        }

        return mostConstrained;

    }

    private static Symbol findUnitaryClause( Set<Statement> clauses ) {

        for (Statement clause : clauses) {

/*
            if(clause.isTrueClause()) {
                continue;
            }
*/
            Symbol newSymbol = clause.isUnitaryClauseTest();
            if (newSymbol != null) {

                if(showLog)
                    System.out.println(ident+" CLAUSE UNITAIRE "+clause);
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

    /*
        private static void resetPureSymbols(Symbol symbol, Set<Statement> newTrueClauses) {

            for( Statement clause : newTrueClauses ){
                //pour chaque literaux de la clause vrai
                for( Statement literal : clause.getLiterals() ){
                    //on remet en l'etat le nombre de literaux positif ou negatif du symbole
                    if(literal instanceof Not){
                        literal.getSymbol().addNeg();
                    }else{
                        literal.getSymbol().addPos();
                    }
                }

            }


        }
    */
    private static Symbol findPureSymbolV2(Set<Symbol> pureSymbols, Set<Statement> newTrueClauses){

        if(pureSymbols.isEmpty()){
            return null;
        }else{


            System.out.println("A PURE : "+pureSymbols);

            Symbol pureSymbol = pureSymbols.iterator().next();
            pureSymbols.remove(pureSymbol);

            //rend le symbole positif en fonction de la positivité des literaux qui le contiennent
            if(pureSymbol.isNeg()){
                pureSymbol.setValue(false);

            }else{
                pureSymbol.setValue(true);
            }

            for( Statement clause : symbolsClausesIndex.get(pureSymbol) ){

                if(clause.isTrueClause())
                    continue;

                //toutes les clauses ou ce litera pure est rendu vrai deviennent vrai
                newTrueClauses.add(clause);
                clause.setTrueClause(true);

            }

            return pureSymbol;

        }

    }

    private static void addPureSymbols(Set<Statement> lastNewTrueClauses, Set<Statement> nextNewTrueClauses,
                                       Set<Symbol> initSymbols){

        totalPureAppel++;

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
/*
                if(literal.getSymbol().isInitialized()) {
                    continue;
                }
*/
                //on decremente le nombre de literaux positif ou negatif du symbole
                if(literal instanceof Not){
                    literal.getSymbol().remNeg();
                }else{
                    literal.getSymbol().remPos();
                }

                totallitDec ++;

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
                    for(Statement newClause : symbolsClausesIndex.get(literal.getSymbol())){

                        if(newClause.isTrueClause()){
                            continue;
                        }

                        newClause.setTrueClause(true);
                        newTrueClauses.add(newClause);

                    }

                    nextNewTrueClauses.addAll(newTrueClauses);
                    addPureSymbols(newTrueClauses, nextNewTrueClauses, initSymbols);

                }
            }
        }

    }


    private static Symbol findPureSymbol( Set<Symbol> symbols,
                                          Set<Statement> newTrueClauses) {

        List<Statement> cl;

        //pour chaque symbole
        for( Symbol symbol : symbols ){
            cl = new LinkedList<>();

            int neg = 0, pos = 0;
            //on crée son complement
            Not notSymbol = new Not(symbol);
            //pour chaque clause on l'on trouve le symbole
            for(Statement clause : symbolsClausesIndex.get(symbol)){

                if(clause.isTrueClause()){
                    continue;
                }

                //si la clause contient le symbole on compte un literal positif
                if(clause.getLiterals().contains(symbol)){
                    cl.add(clause);
                    pos ++;
                }
                //si la clause contient le complement du symbole on compte un literal negatif
                if(clause.getLiterals().contains(notSymbol)){
                    cl.add(clause);
                    neg ++;
                }

                //la clause pourrait contenir à fois un literal et son complement, dans ce cas la clause est vrai
                //si à un moment donné les deux ont été incrementé c'est que le symbole n'est pas pur
                //sauf au cas ou une clause contiendrait à la fois le positif et le négatif ce qui ne devrait normalement
                //jamais être le cas dans la liste des clauses.
                if(neg > 0 && pos > 0) {
                    //on passe au prochain symbole
                    break;
                }
            }


            //si l'un des deux vaut zero on possède un symbole pur
            if((neg == 0 && pos > 0) || (pos == 0 && neg > 0 )){
/*
                if(showLog) {

                    System.out.println(ident + symbol + " " + notSymbol + " " + (pos > 0) + " " + pos + " " + neg);
                    System.out.println(ident + "CLAUSES CONTENANT LE SYMBOLE PURE ");
                    for (Statement c : cl) {
                        System.out.println(ident + c);
                    }

                    System.out.println(ident + "ALL CLAUSES ");
                    for (Statement c : symbolsClausesIndex.get(symbol)) {
                        System.out.println(ident + c + " IS TRUE : " + !clauses.contains(c) + " - CONTAIN NOT : "
                                + c.getLiterals().contains(notSymbol) + " " + c.getLiterals());
                    }
                }
*/
                symbol.setValue((pos > 0));

                newTrueClauses.addAll(cl);

                for(Statement statement : cl) {
                    statement.setTrueClause(true);
                }

                return symbol;
            }

        }

        return null;

    }


}
