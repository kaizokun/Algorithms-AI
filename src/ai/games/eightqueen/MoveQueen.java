package ai.games.eightqueen;

import ai.Action;

public class MoveQueen extends Action {


    private int colone, from, to;

    public MoveQueen(int colone, int from, int to){
        this.colone = colone;
        this.from = from;
        this.to = to;
    }

    @Override
    public String getActionName() {
        return "Deplacement reine colone "+colone+" de la ligne "+from+" à "+to;
    }

    @Override
    public double getCost() {
        return 1;
    }

    @Override
    public void setCost(double cost) {}

    public int getColone() {
        return colone;
    }

    public void setColone(int colone) {
        this.colone = colone;
    }

    public int getFrom() {
        return from;
    }

    public void setFrom(int from) {
        this.from = from;
    }

    public int getTo() {
        return to;
    }

    public void setTo(int to) {
        this.to = to;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        MoveQueen moveQueen = (MoveQueen) o;

        if (colone != moveQueen.colone) return false;
        if (from != moveQueen.from) return false;
        return to == moveQueen.to;
    }

    @Override
    public int hashCode() {
        int result = colone;
        result = 31 * result + from;
        result = 31 * result + to;
        return result;
    }
}
