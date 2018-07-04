package ai.agent;

import ai.agent.logic.KnowledgeBase;
import ai.agent.logic.expressions.*;

import java.util.*;

public class BackTracking {

    public static boolean backTrack(KnowledgeBase kb, Statement statement){

        return backTrack(kb.getStatements(), statement);
    }

    public static boolean backTrack(Collection<Statement> kb, Statement statement){

        List<Statement> statements = new ArrayList<>();

        statements.add(new Not(statement));
        statements.addAll(kb);

        return backTrackStatement(statements, 0,0);

    }

    private static boolean backTrackStatement(List<Statement> statements, int st, int d) {

      // String ident = Util.getIdent(d);

        //on a initialisé tout les enoncé ont retourne faux
        //pour signifier que l'on à trouvé un énoncé ou KB => P === KB AND NOT P est satisfiable
        if(st == statements.size()){
            System.out.println(d+" TEST ENONCE FIN "+statements.get(st-1));
            return false;
        }

        //System.out.println(d+" TEST ENONCE "+statements.get(st));


        //on charge les symboles de l'enoncé
        ArrayList<Symbol> symbols = new ArrayList<>(selectStatement(statements, st).getSymbols());
        //on teste les combinaisons de valeurs pour un enoncé
        return backTrackSymbol(statements, symbols, st, 0,d + 1);

    }

    private static boolean backTrackSymbol(List<Statement> statements, List<Symbol> kbStatementSymbols, int st, int syCurrent, int d){

        //String ident = Util.getIdent(d);

        //si un symbole est déja initialisé ont passe au suivant
        while( syCurrent < kbStatementSymbols.size() && kbStatementSymbols.get(syCurrent).isInit()){

            //System.out.println(ident+"TEST SYMBOLE "+syCurrent+"/"+ kbStatementSymbols.size());

            syCurrent ++;
        }


        //Un enoncé à été completement initialisé
        if(syCurrent == kbStatementSymbols.size()) {
            //System.out.println(ident+"ENONCE INITIALISE");

            //on recupere l'enoncé et on verifie si il est faux
            //si c'est le cas inutile d'essayer des assignations pour un autre enoncé
            //les assignations de symboles précédents rendent (KB AND NOT P) faux
            if(!statements.get(st).isTrue()){
                //System.out.println("ENONCE FAUX "+statements.get(st));
                return true;
            }else{
                //System.out.println("ENONCE FAUX "+statements.get(st));
                //si l'enoncé est vrai on teste le prochain
                return backTrackStatement(statements, st + 1, d+1);
            }

        }

        //on assigne un symbole à vrai et on le marque comme initialisé
        kbStatementSymbols.get(syCurrent).setValue(true);
        kbStatementSymbols.get(syCurrent).setInit(true);

        //System.out.println(ident+"SYMBOLE "+kbStatementSymbols.get(syCurrent)+" TRUE" );

        //si l'enonce est faux avec ce symbole initialisé à vrai
        if(backTrackSymbol(statements, kbStatementSymbols, st,syCurrent + 1, d+1) == true) {

            //System.out.println(ident+"SYMBOLE "+kbStatementSymbols.get(syCurrent)+" FALSE" );

            //on teste avec le symbole à faux
            kbStatementSymbols.get(syCurrent).setValue(false);

            boolean rs =  backTrackSymbol(statements, kbStatementSymbols, st, syCurrent + 1,d+1);
            //on demarque le symbole comme initialisé
            kbStatementSymbols.get(syCurrent).setInit(false);

            return rs;

        }

        kbStatementSymbols.get(syCurrent).setInit(false);

        return false;

    }

    private static Statement selectStatement(List<Statement> statements, int st ) {



        return statements.get(st);



    }

    public static void main(String[] args) {

        List<Statement> statements = new LinkedList<>();

        Symbol P11 = new Symbol("P(1,1)");
        Symbol P12 = new Symbol("P(1,2)");
        Symbol P13 = new Symbol("P(1,3)");
        Symbol P21 = new Symbol("P(2,1)");
        Symbol P22 = new Symbol("P(2,2)");
        Symbol P31 = new Symbol("P(3,1)");
        Symbol B11 = new Symbol("B(1,1)");
        Symbol B21 = new Symbol("B(2,1)");
        Symbol B12 = new Symbol("B(1,2)");

        /*------------------ REGLES ------------------*/

        statements.add(new Not(P11));
        statements.add(new DoubleImply(B11, new Or(P12, P21)));
        statements.add(new DoubleImply(B21, new Or(P11, new Or( P31, P22))));
        statements.add(new Not(B11));
        statements.add(B21);
        statements.add(new DoubleImply(B12, new Or(P11, new Or(P22, P13))));
        statements.add(new Not(B12));

        Statement RP = new Not(P13);

        long t1 = System.currentTimeMillis();

        if(BackTracking.backTrack(statements, RP)) {
            System.out.println(RP+" VRAI");
        }else{
            System.out.println(RP+" FAUX");
        }

        System.out.println(System.currentTimeMillis() - t1);

    }


}
