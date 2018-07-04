package ai.agent.logic;

import ai.agent.logic.expressions.Statement;
import ai.agent.logic.expressions.Symbol;

import java.util.List;

public class TT_Entails {

    public static boolean check(Statement KB, Statement statement, List<Symbol> symbols){

       return check(KB, statement, symbols,0);

    }

    private static boolean check(Statement KB, Statement statement, List<Symbol> symbols, int i){

        if( i == symbols.size()){

            if( KB.isTrue() ) {
                return statement.isTrue();
            }else{
                return true;
            }

        }else{

            symbols.get(i).setValue(true);
            boolean rs1 = check(KB,statement,symbols,i+1);

            symbols.get(i).setValue(false);
            boolean rs2 = check(KB,statement,symbols,i+1);

            return rs1 && rs2;

        }


    }

}
