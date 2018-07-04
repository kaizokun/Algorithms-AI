package ai.agent.logic;

import ai.Action;
import ai.State;
import ai.agent.logic.expressions.Imply;
import ai.agent.logic.expressions.Statement;
import ai.agent.logic.expressions.Symbol;
import ai.agent.logic.expressions.TransformAction;
import ai.problem.SimpleProblem;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;

public class PropositionalLogicProblem extends SimpleProblem {

    private Statement statementToFind;

    public Statement getStatementToFind() {
        return statementToFind;
    }

    public void setStatementToFind(Statement statementToFind) {
        this.statementToFind = statementToFind;
    }

    @Override
    public List<Action> getActions(State state) {

        KnowledgeBase knowledgeBase = (KnowledgeBase) state;

        //System.out.println(((KnowledgeBase) state).getStatements());

        List<Action> actions = new LinkedList<>();

        for(Statement statement : knowledgeBase.getStatements()){
            actions.addAll(statement.getActions());
        }

        return actions;
    }

    @Override
    public State getResult(State state, Action action) {

        KnowledgeBase knowledgeBase = (KnowledgeBase) state;

       // System.out.println("KBpercepts "+knowledgeBase);

        KnowledgeBase knowledgeBaseClone = knowledgeBase.clone();

        //System.out.println("KBpercepts "+knowledgeBaseClone);


        TransformAction transformAction = (TransformAction) action;

        Statement rsStatement;

        if( transformAction.getAction() == TransformAction.TRANFORMATION.MODUS_PONENS){
            //récupere l'implication
            Imply enonce = (Imply) transformAction.getStatement();
            //récupere la partie gauche de l'implication
            Statement leftPart = enonce.getSt1();
            /*
            System.out.println("LEFT PART "+leftPart+" "+leftPart.hashCode());
            //verifie si la base de conaissance contient cette partie gauche entant qu'enoncé
            //sinon l'action ne produit aucun resultat (ou nul)
            for(Map.Entry<Statement,Statement> k : knowledgeBaseClone.getStatements().entrySet()){
                System.out.println(k.getKey()+" "+k.getObjectRef());
                System.out.println(k.getKey().hashCode()+" "+k.getObjectRef().hashCode());
            }
*/
            if(!knowledgeBaseClone.getStatements().contains(leftPart)){
               // System.out.println("KBpercepts contient pas ");
                return null;
            }else{
               // System.out.println("KBpercepts contient ");
            }

        }

        rsStatement = transformAction.getStatement().getTransformationResult(transformAction.getAction(),true);
        //System.out.println("A : "+knowledgeBaseClone);
        knowledgeBaseClone.getStatements().remove(transformAction.getStatement());
        //System.out.println("B : "+knowledgeBaseClone);
        knowledgeBaseClone.getStatements().add(rsStatement);
       // System.out.println("C : "+knowledgeBaseClone);

        return knowledgeBaseClone;

    }

    public static KnowledgeBase removeUselessStatements(KnowledgeBase kb, Statement e){

        //recupere une liste d' enonce utile dans la KBpercepts à partir de l'enonce à prouver
        HashSet<Statement> usefullStatements = usefullStatements(kb,e);
        HashSet<Statement> nextUsefullStatements;
        //crée une base de conaissance à partir de la liste d'enonce utiles
        KnowledgeBase kbClone = new KnowledgeBase(new LinkedList(usefullStatements));
        KnowledgeBase kbCloneNext;

        do{
            //System.out.println("KBpercepts ACTUELLE "+kbClone);
            //crée une nouvelle liste d'enonce utile
            nextUsefullStatements = new HashSet<>();
            //pour chaque enonce utile certains symboles peuvent toujours se trouver dans d'autres enoncés
            //de la base d'origine
            //pour chaque enoncé utile
            for(Statement statement : usefullStatements){
                //on récupere les énoncés de la KBpercepts qui contiennet des symboles similaires
                nextUsefullStatements.addAll(usefullStatements(kb, statement));
            }
            //on crée une nouvelle base de conaissance à partir des énoncés utile ajoutés
            kbCloneNext = new KnowledgeBase(new LinkedList(nextUsefullStatements));

            //System.out.println("KBpercepts NEXT "+kbCloneNext);

            //si la base de conaissance a changé par rapport à celle precedemment créée
            //la methode equal est trop dependante de l'ordre des enonce dans la base
            //il faudrait un containOpposite all dans les deux sens
            if(!kbClone.equals(kbCloneNext)){
                //elles sont interverties et le processus recommence
                usefullStatements = nextUsefullStatements;
                kbClone = kbCloneNext;
            }else{
                //si elle n'a pas evoluée on s'arette
                break;
            }

        }while (true);

        return kbClone;

    }

    public static HashSet usefullStatements(KnowledgeBase kb, Statement e){

        HashSet<Statement> enonceUtiles = new HashSet<>();
        //recupere tout les symboles de l'enonce à prouver
        List<Symbol> eSymbols = e.getSymbols();

        //pour chaque énoncé de la base de conaissance
        for(Statement statement : kb.getStatements()){
            //System.out.println("STATEMENT : "+statement);
            //recuepre les symbole d'un enonce de la KBpercepts
            List<Symbol> statementSymbols = statement.getSymbols();
            //pour chaque symbole de l'enonce a prouver
            for(Symbol symbol : eSymbols) {
               // System.out.println("SYMBOL "+symbol);
                //si l'enonce de la KBpercepts contient un des symbole de l'enonce à prouver
                if(statementSymbols.contains(symbol)){
                    //System.out.println("CONTAIN");
                    enonceUtiles.add(statement);
                    break;
                }
            }

        }

        return enonceUtiles;

    }

    @Override
    public boolean isGoal(State state) {
        KnowledgeBase knowledgeBase = (KnowledgeBase) state;
        return knowledgeBase.getStatements().contains(this.statementToFind);
    }



}
