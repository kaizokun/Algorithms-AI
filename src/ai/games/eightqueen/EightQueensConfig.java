package ai.games.eightqueen;

import ai.State;

import java.util.ArrayList;

public class EightQueensConfig extends State<ArrayList<Integer>> {


    public EightQueensConfig(ArrayList<Integer> queens) {
        this.value = queens;
    }

    @Override
    public int hashCode() {

        int key = 0;

        for(int i = 0 ; i  < 8 ; i ++){
            key = key * 10 +  this.value.get(i);
        }

        return key;

    }


    @Override
    public boolean equals(Object obj) {

        EightQueensConfig q2 = (EightQueensConfig) obj;

        for( int i = 0 ; i < 8 ; i ++ ){

            if(!this.value.get(i).equals(q2.value.get(i)))
                return false;

        }

        return true;
    }

    @Override
    public String toString() {

        return ""+this.hashCode();
    }



}
