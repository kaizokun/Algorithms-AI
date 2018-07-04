package ai.problem;

import ai.Action;
import ai.State;
import ai.games.eightqueen.EightQueensConfig;
import ai.games.eightqueen.MoveQueen;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

public class EightQueenProblem extends SimpleProblem {
    
    @Override
    public List<Action> getActions(State state) {


        List<Action> moves = new LinkedList<Action>();

        EightQueensConfig config = (EightQueensConfig) state;

        ArrayList<Integer> queensPositions = config.getValue();

        for( int x = 0 ; x  < 8 ; x ++ ){

            for( int y = 0 ; y < 8 ; y ++ ){
                if(queensPositions.get(x) != y+1){
                    moves.add(new MoveQueen(x+1, queensPositions.get(x), y+1));
                }
            }

        }

        return moves;
    }

    @Override
    public State getResult(State state, Action action) {

        EightQueensConfig config = (EightQueensConfig) state;

        ArrayList<Integer> queensPositions = config.getValue();
        ArrayList<Integer> copyQueensPositions = new ArrayList<Integer>(queensPositions);

        MoveQueen moveQueen = (MoveQueen) action;

        copyQueensPositions.set(moveQueen.getColone()-1, moveQueen.getTo());

        EightQueensConfig newConfig = new EightQueensConfig(copyQueensPositions);

        return newConfig;
    }


    @Override
    public double getGoalCostEstimation(State state) {
        return countConflicts(state);
    }

    private int countConflicts(State state) {

        int total = 0;

        EightQueensConfig config = (EightQueensConfig) state;

        ArrayList<Integer> queensPositions = config.getValue();

        for( int x = 0 ; x < 8 ; x ++ ){

            int xVal = queensPositions.get(x).intValue();

            //Test ligne gauche
            for( int xa = 0 ;  xa < x ; xa ++)
                if(xVal == queensPositions.get(xa).intValue())
                    total ++;
            //Test ligne droite
            for( int xb = x+1 ; xb < 8 ; xb ++)
                if(xVal == queensPositions.get(xb).intValue())
                    total ++;


            //Test diagonale gauche droite bas/haut + haut/haut
            for( int xb = x+1, dec = 1 ; xb < 8 ; xb ++, dec ++)
                if( (xVal - dec ) == queensPositions.get(xb).intValue()
                        || (xVal + dec ) == queensPositions.get(xb).intValue())
                    total ++;

            //Test diagonale droite gauche bas/haut + haut/haut
            for( int xb = x-1, dec = 1 ; xb >= 0 ; xb --, dec ++)
                if( (xVal - dec ) == queensPositions.get(xb).intValue()
                        || (xVal + dec ) == queensPositions.get(xb).intValue())
                    total ++;

        }

        return total;

    }

    @Override
    public boolean isGoal(State state) {

        EightQueensConfig config = (EightQueensConfig) state;

        ArrayList<Integer> queensPositions = config.getValue();

        for( int x = 0 ; x < 8 ; x ++ ){

            int xVal = queensPositions.get(x).intValue();

            //Test ligne gauche
            for( int xa = 0 ;  xa < x ; xa ++)
                if(xVal == queensPositions.get(xa).intValue())
                    return false;
            //Test ligne droite
            for( int xb = x+1 ; xb < 8 ; xb ++)
                if(xVal == queensPositions.get(xb).intValue())
                    return false;


            //Test diagonale gauche droite bas/haut + haut/haut
            for( int xb = x+1, dec = 1 ; xb < 8 ; xb ++, dec ++)
                if( (xVal - dec ) == queensPositions.get(xb).intValue()
                        || (xVal + dec ) == queensPositions.get(xb).intValue())
                    return false;

            //Test diagonale droite gauche bas/haut + haut/haut
            for( int xb = x-1, dec = 1 ; xb >= 0 ; xb --, dec ++)
                if( (xVal - dec ) == queensPositions.get(xb).intValue()
                        || (xVal + dec ) == queensPositions.get(xb).intValue())
                    return false;

        }


        return true;

    }

    @Override
    public double getStateValue(State state) {
        return - this.getGoalCostEstimation(state);
    }

    @Override
    public State rdmState() {

        Integer []config = new Integer[8];

        for(int i = 0 ; i < config.length ; i ++){
            config[i] = ThreadLocalRandom.current().nextInt(1, 8 + 1);
        }

        return new EightQueensConfig(new ArrayList(Arrays.asList(config)));

    }
}
