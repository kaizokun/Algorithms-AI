package ai.agent.logic;

import ai.agent.logic.expressions.Not;
import ai.agent.logic.expressions.Statement;
import ai.agent.logic.expressions.Symbol;
import util.TimeUtil;
import util.Util;

import java.util.*;

public class DPLL_test_components {

    public static String ident;
    private static Hashtable<Statement,Set<Statement>> symbolsClausesIndex;
    public static double totalPure;
    public static double totalUnitaire;
    public static double totalTest;
    public static double totalConflictSymbol;
    private static boolean showLog = false;

    public static boolean explore(KnowledgeBase kb, Statement e ){

        totalPure = 0.0;
        totalTest = 0.0;
        totalUnitaire = 0.0;
        totalConflictSymbol = 0.0;

        //Util.initTime();
        //copy du CNF de la KB et des symboles
        Set<Statement> clauses = new LinkedHashSet(kb.getStatements());

        Set<Symbol> symbols = new LinkedHashSet(kb.getSymbols());

        symbolsClausesIndex = kb.getclausesFromSymbolIndex();

        //AJOUT DE LA CLAUSE DANS LA COPY DE L INDEX DES CLAUSES PAR SYMBOLES
        for(Symbol symbol : e.getSymbols()){
            Set symbolClause = symbolsClausesIndex.get(symbol);
            if(symbolClause == null){
                symbolClause = new LinkedHashSet();
                symbolsClausesIndex.put( symbol, symbolClause);
            }
            symbolClause.add(e);
        }


        //AJOUT DU COMPLEMENT DANS LA LISTE DES CLAUSES
        Statement complement = new Not(e);
        clauses.add(complement);

        //AJOUT DES SYMBOLES DE L ENONCE A PROUVER
        for(Symbol complementSymbol : symbols){
            if(!symbols.contains(complementSymbol)){
                symbols.add(complementSymbol);
            }
        }

        TimeUtil t1 = new TimeUtil();
        boolean rs = explore(clauses , symbols,0,e);
        t1.printTimeDetlaStr();
        System.out.println("PURE TOTAL "+ totalPure);
        System.out.println("TEST TOTAL "+ totalTest);
        System.out.println("UNITAIRE TOTAL "+ totalUnitaire);
        System.out.println("CONFLICT SYMBOL TOTAL "+ totalConflictSymbol);
        System.out.println();

        return !rs;

    }

    private static boolean explore( Set<Statement> clauses, Set<Symbol> symbols, int d, Statement e) {

        if(showLog)
            ident = Util.getIdent(d);
        if(showLog)
            System.out.println(ident+" "+symbols.size());

        Set<Statement> newTrueClauses = new LinkedHashSet<>();

        if(showLog)
            Util.initTime();

       // Util.initTime();

        int cptrClauseTrue = 0;
        //pour chaque clause de la KB

        for(Statement clause : clauses) {

            if(clause.isTrueClause()) {
                cptrClauseTrue++;
                continue;
            }

            int cptrLiteralFalse = 0;
            //récupere la liste des literaux
            for (Statement literal : clause.getLiterals()) {
                //si le literal a déja été assigné
                if (literal.getSymbol().isInitialized()) {
                    if (literal.isTrue()) {
                        //si le literal est vrai, la clause est vrai
                        //on passe à la prochaine clause
                        newTrueClauses.add(clause);
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

                for(Statement trueClause : newTrueClauses) {
                    trueClause.setTrueClause(false);
                }

                return false;
            }

        }

        //totalTest += Util.computeTimeDelta();

        if(showLog)
            System.out.println(ident+" TEST CLAUSES : "+Util.getTimeDeltaStr()+" "+newTrueClauses.size()+"  "+clauses.size());

        if(showLog)
            System.out.println(ident + " SIZE CLAUSES " + clauses.size());

        if(cptrClauseTrue == clauses.size()){
            if(showLog)
                System.out.println(ident+"clause true");

            return true;
        }

        if(showLog)
            Util.initTime();

        //Util.initTime();
        Symbol symbole = findPureSymbol(clauses, symbols, newTrueClauses);
       // totalPure += Util.computeTimeDelta();

        if(showLog)
            System.out.println(ident+" PURE SYMBOL "+Util.getTimeDeltaStr());

        if(symbole != null){

            symbole.setInitialized(true);
            symbols.remove(symbole);

            if(showLog)
                System.out.println(ident+"PURE SYMBOL "+symbole+" "+symbols.contains(symbole));

            boolean rs = explore(clauses,symbols,d,e);

            reset(rs, symbols,symbole,newTrueClauses);

            return rs;
        }

        if(showLog)
            Util.initTime();
        symbole = findUnitaryClause(clauses,newTrueClauses);
        if(showLog)
            System.out.println(ident+"CLAUSE UNITAIRE "+Util.getTimeDeltaStr());

        if(symbole != null){
            if(showLog)
                System.out.println(ident+"UNITAIRE SYMBOL "+symbole);

            symbols.remove(symbole);
            symbole.setInitialized(true);

            boolean rs = explore(clauses, symbols,d,e);

            reset(rs, symbols,symbole,newTrueClauses);

            return rs;
        }

        //symbole = symbols.iterator().next();



        //System.out.println();

        //System.exit(0);

        symbole = getMostConstrainedSymbol(symbols);

        symbols.remove(symbole);
        symbole.setInitialized(true);

        ((Symbol)symbole).setValue(true);
        if(showLog)
            System.out.println(ident+"NEXT SYMBOL "+symbole);


        if(!exploreComponents(symbols,d,e)) {

            ((Symbol) symbole).setValue(false);
            if (showLog)
                System.out.println(ident + "NEXT SYMBOL " + symbole);

            boolean rs = exploreComponents(symbols,d,e);

            reset(rs, symbols,symbole,newTrueClauses);

            return rs;
        }



        reset(true, symbols,symbole,newTrueClauses);

        return true;
    }

    private static boolean exploreComponents(Set<Symbol> symbols, int d, Statement e){

                /*
        int totalClausesNotTrue = 0;

        for(Statement clause : clauses){
            if(!clause.isTrueClause()){
                totalClausesNotTrue ++ ;
            }
        }
*/
        List<KnowledgeBase.KBcomponent> kBcomponents = KnowledgeBase.createComponents(symbols, symbolsClausesIndex);
        /*
        System.out.println("Left clause "+totalClausesNotTrue);
        System.out.println("Left symbols "+symbols.size());
        System.out.println("KB COMPONENTS");
        for(KnowledgeBase.KBcomponent kBcomponent :kBcomponents)
            System.out.println(kBcomponent);
*/

        //System.out.println(Util.getIdent(d)+"EXPLORE "+kBcomponents.size()+" COMPONENTS ");
        int i = 1 ;
        for(KnowledgeBase.KBcomponent kBcomponent : kBcomponents){
           // System.out.println(Util.getIdent(d)+" COMPONENT "+i+" : C = "+kBcomponent.getClausesSet().size()+" - S = "+kBcomponent.getSymbolsSet().size());
/*
            if(!kBcomponent.getSymbolsSet().contains(e))
                continue;
*/
            if(!explore(kBcomponent.getClausesSet(), kBcomponent.getSymbolsSet(), d+1, e)){
                return false;
            }
            i ++;
        }
       // System.out.println(Util.getIdent(d)+"EXPLORE "+kBcomponents.size()+" COMPONENTS END");
        return true;

    }

    private static void reset(boolean rs, Set<Symbol> symbols, Symbol symbole, Set<Statement> newTrueClauses ){

        if(!rs) {
            symbols.add(symbole);

        }

        symbole.setInitialized(false);

        for(Statement trueClause : newTrueClauses) {
            trueClause.setTrueClause(false);
        }

    }

    private static Symbol getMostConstrainedSymbol(Set<Symbol> symbols) {

        //System.out.println("get most\n\n");

        int maxClauses = 0;
        Symbol mostConstrained = symbols.iterator().next();

        for(Symbol symbol : symbols){

           int totalClause = 0;
           for(Statement clause :  symbolsClausesIndex.get(symbol)){
               if (!clause.isTrueClause())
                   totalClause ++;
           }

               if(totalClause > maxClauses){
                   maxClauses = totalClause;
                   mostConstrained = symbol;
               }

        }

        return mostConstrained;

    }



    private static Symbol findUnitaryClause(Set<Statement> clauses, Set<Statement> newTrueClauses) {

        for( Statement clause : clauses ){

            Symbol symbol = clause.isUnitaryClauseTest();
            if(symbol != null){
                newTrueClauses.add(clause);
                clause.setTrueClause(true);
                return symbol;
            }
        }

        return null;
    }

    private static Symbol findPureSymbol(Set<Statement> clauses, Set<Symbol> symbols,
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

                ((Symbol)symbol).setValue((pos > 0));

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
