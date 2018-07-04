package ai.games.taquin;

import ai.State;

import java.util.ArrayList;

/**
 * Created by monsio on 7/18/17.
 */
public class TaquinConfig extends State<ArrayList<Integer>> {

    private int emptyPos;

    public TaquinConfig(ArrayList<Integer> table) {

        this.value = table;

        for(int i = 0 ; i < table.size() ; i ++)
            if(table.get(i) == 0){
                this.emptyPos = i;
                break;
            }
    }

    @Override
    public int hashCode() {

        int key = 0;

        for(int i = 0 ; i  < 9 ; i ++){
            key = key * 10 +  this.value.get(i);
        }

        return key;

    }

    public int getEmptyPos() {
        return emptyPos;
    }

    public void setEmptyPos(int emptyPos) {
        this.emptyPos = emptyPos;
    }

    @Override
    public String toString() {

        String taquin = "\n";

        for( int y = 0 ; y < 3 ; y ++){
            for(int x = 0 ; x < 3 ; x ++){
                taquin+="[ "+value.get(y*3+x)+" ]";
            }
            taquin+="\n";
        }

        return taquin;
    }
}
