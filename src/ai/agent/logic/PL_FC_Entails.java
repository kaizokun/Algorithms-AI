package ai.agent.logic;

import ai.agent.logic.expressions.And;
import ai.agent.logic.expressions.Imply;
import ai.agent.logic.expressions.Statement;
import ai.agent.logic.expressions.Symbol;
import ai.agent.logic.expressions.*;

import java.util.*;

public class PL_FC_Entails {

    public boolean forwardTracking(KnowledgeBase KB, Statement q){

        Hashtable<Statement,Integer> compteur = new Hashtable<>();
        Hashtable<Statement,Boolean> inferred = new Hashtable<>();
        LinkedList<Statement> agenda = new LinkedList<>();

        Collection<Statement> statementsList = KB.getStatementsList();

        for( Statement statement : statementsList ){

            if(statement instanceof Symbol){
                agenda.add(statement);
            }else{
                Imply defineClause = (Imply) statement;
                compteur.put(statement, defineClause.getSt1().getSymbols().size());

                for(Statement symbol : statement.getSymbols()){
                    //if(!inferred.containsKey(symbol)) {
                    inferred.put(symbol, false);
                    //}
                }
            }

        }


        //System.out.println(compteur);
        //System.out.println(inferred);
        //System.out.println(agenda);

        // //System.exit(0);

        while(!agenda.isEmpty()){

            Statement p = agenda.removeFirst();
            //System.out.println();
            //System.out.println("DEFILER "+p +" "+agenda);

            if(p.equals(q)){
                //System.out.println(q+" <> "+p);
                return true;
            }

            if(!inferred.get(p)){

                //System.out.println("INFERER "+p);

                inferred.put(p,true);

                for(Statement statement : statementsList){

                    //System.out.println("CLAUSE DEFINI "+statement);

                    if(statement instanceof Imply && ((Imply)statement).getSt1().getSymbols().contains(p)){

                        //System.out.println("CLAUSE CONTIENT "+p);
                        //System.out.println("COMPTEUR AVANT : "+compteur.get(statement));
                        compteur.put(statement, compteur.get(statement) - 1);
                        //System.out.println("COMPTEUR APRES : "+compteur.get(statement));

                        if(compteur.get(statement) == 0){
                            //System.out.println("COMPTEUR ZERO : "+compteur.get(statement));
                            agenda.add(((Imply)statement).getSt2());
                            //System.out.println("AGENDA "+agenda);
                        }
                    }

                }

            }


        }

        return false;

    }

    public boolean backTracking(KnowledgeBase KB, Statement q){
        return this.backTracking(KB,q,0, new LinkedHashSet<Statement>());
    }

    private boolean backTracking(KnowledgeBase KB, Statement q, int d, HashSet<Statement> visited){

        //String ident = Util.getIdent(d);
        //System.out.println(ident+"PROUVER "+q);

        //Si la base de conaissance possède le symbole
        if(KB.getStatements().contains(q)){
            return true;
        }

        //pour chaque énoncé de la base de conaissance
        for(Statement statement : KB.getStatementsList()){
            //Si l'enonce est une implication ( une clause définie obligatoirement )
            //Si l'enoncé n'a pas déja été choisi plus haut
            //Si la partie droite de l'énoncé contient le symbole à prouver
            if( statement instanceof Imply
                    && !visited.contains(statement)
                    && ((Imply)statement).getSt2().equals(q)){

                //Ajout de l'énoncé dans la liste des énoncés visités
                visited.add(statement);
                //Considére par defaut que q sera validé
                boolean valid = true;
                //System.out.println(ident+"PROUVER "+ statement);
                //récupère tout les symboles appartenants à la partie gauche de l'implication
                for(Statement symbol :  ((Imply) statement).getSt1().getSymbols()){
                    //tente de verifiersi chaque symbole fait partie de la base de conaissance ou peut être prouvé
                    //récursivement

                    if(!backTracking(KB,symbol,d+1,visited)){
                        //si un des symboles de la partie gauche ne peut être vérifié
                        //l'enoncé ne le sera pas
                        valid = false;
                        //donc passer au suivant
                        break;
                    }

                    //Si un enoncé à été validé ajouter le symbole à la base de conaissance.
                    //Evite de le prouver à nouveau si il fait partie des symboles d'un autre enoncé
                    //à prouver plus tard
                    KB.getStatements().add(symbol);

                }
                //retirer l'énoncé de la liste de ceux déja testé
                visited.remove(statement);
                //si le symbole est valide retourner vrai pour le valider
                if (valid) {
                    return true;
                }

            }
        }
        //si aucun enoncé n'a été validé retourner faux
        return false;
    }


    public static void main(String[] args) {



        Symbol A = new Symbol("A");
        Symbol B = new Symbol("B");
        Symbol L = new Symbol("L");
        Symbol M = new Symbol("M");
        Symbol P = new Symbol("P");
        Symbol Q = new Symbol("Q");

        List<Statement> statements = new LinkedList<>();

        statements.add( new Imply(P,Q) );
        statements.add( new Imply(new And(L,M),P));
        statements.add( new Imply(new And(B,L),M));
        statements.add( new Imply(new And(A,P),L));
        statements.add( new Imply(new And(A,B),L));

        statements.add( A);
        statements.add( B);

        Statement proof = Q;

        long t1;

        KnowledgeBase kb1 = new KnowledgeBase(statements);

        PL_FC_Entails pl_fc_entails = new PL_FC_Entails();

        for(int i = 0 ; i < 1 ; i ++) {

            t1 = System.currentTimeMillis();

            System.out.println(pl_fc_entails.forwardTracking(kb1, proof));

            System.out.println("TEMPS FORWARD : " + (System.currentTimeMillis() - t1));

            KnowledgeBase kb2 = new KnowledgeBase(statements);

            t1 = System.currentTimeMillis();

            System.out.println(pl_fc_entails.backTracking(kb2, proof, 0, new LinkedHashSet<Statement>()));

            System.out.println("TEMPS BACK : " + (System.currentTimeMillis() - t1));

        }

    }

}
