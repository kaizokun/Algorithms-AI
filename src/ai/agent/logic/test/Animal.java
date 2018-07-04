package ai.agent.logic.test;

import ai.agent.logic.CNF_convert;
import ai.agent.logic.PlSolveFirstOrder;
import ai.agent.logic.expressions.Statement;

import java.util.LinkedList;
import java.util.List;

public class Animal {


    public static void main(String[] args) {

        LinkedList<Statement> statementList = new LinkedList<>();


        statementList.add(Statement.getStatementFromString("_A[x]( (_E[z](Animal(z)&(Tuer(x,z)))) > (_A[y](!Aimer(y,x))) )"));

       // statementList.add(Statement.getStatementFromString("_A[x]( ( ( _A[y]( Animal(y) > Aimer(x,y) ) ) > ( _E[z]( Aimer(z,x) ) ) ) )"));


        /*
        statementList.add(Statement.getStatementFromString("Animal(F(x))|Aimer(G(x),x)"));
        statementList.add(Statement.getStatementFromString("!Aimer(x,F(x))|Aimer(G(x),x)"));
        statementList.add(Statement.getStatementFromString("(!Aimer(y,x)|!Animal(z))|!Tuer(x,z)"));
        statementList.add(Statement.getStatementFromString("!Animal(x)|Aimer(Jacques,x)"));
        statementList.add(Statement.getStatementFromString("Tuer(Jacques,Azrael)|Tuer(Curiosite,Azrael)"));
        statementList.add(Statement.getStatementFromString("Chat(Azrael)"));
        statementList.add(Statement.getStatementFromString("!Chat(x)|Animal(x)"));
        statementList.add(Statement.getStatementFromString("!Tuer(Curiosite,Azrael)"));
*/
        List<Statement> CNF = CNF_convert.convertAndClone(new LinkedList<Statement>(statementList));

        for(Statement statement : statementList)
            System.out.println(statement);

        System.out.println();

        for(Statement cnf : CNF) {
            System.out.println(cnf);
        }



       // System.out.println(PlSolveFirstOrder.valid(CNF));


    }




}
