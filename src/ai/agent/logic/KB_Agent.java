package ai.agent.logic;

import ai.agent.logic.expressions.Statement;

import java.util.List;

public abstract class KB_Agent {

    protected KnowledgeBase KB;

    protected int time = 0, nextTime = 1;

    public KB_Agent(){ }

    public Statement getNextAction(List<Statement> percepts){

        tell(createStatement(percepts));

        Statement action = ask(createRequestAction());

        tell(createStatementAction(action));

        this.time++;

        return action;

    }

    protected void tell(List<Statement> percepts) {

        if(percepts != null) {

            for (Statement percept : percepts) {
                this.KB.addStatement(percept);
            }
        }

    }

    protected void showKb(){
        System.out.println();
        System.out.println("================== KBpercepts =================== "+this.KB.getStatements().size());
        for(Statement kbs : this.KB.getStatements() )
            System.out.println(kbs);

    }

    protected abstract List<Statement> createStatementAction(Statement action);

    protected abstract Statement ask(Statement request);

    protected abstract Statement createRequestAction();

    protected abstract List<Statement> createStatement(List<Statement> percept);

    public int getTime() {
        return time;
    }
}
