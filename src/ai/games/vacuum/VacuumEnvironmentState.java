package ai.games.vacuum;

import ai.State;
import ai.problem.NotDeterministicVacummProblem;

import java.util.ArrayList;

public class VacuumEnvironmentState extends State {

    public static boolean CLEAN = true, DIRTY = false;

    private Vacuum vacuum;

    private ArrayList<Boolean> ground;

    public VacuumEnvironmentState(ArrayList<Boolean> ground, Vacuum vacuum) {
        this.ground = ground;
        this.vacuum = vacuum;
    }

    public int posVacuumDirty(int xLimit){
        return this.ground.get(this.vacuum.getAbsPos(xLimit)) ? 1 : 0;
    }

    public Vacuum getVacuum() {
        return vacuum;
    }

    public void setVacuum(Vacuum vacuum) {
        this.vacuum = vacuum;
    }

    public ArrayList<Boolean> getGround() {
        return ground;
    }

    public void setGround(ArrayList<Boolean> ground) {
        this.ground = ground;
    }

    public VacuumEnvironmentState clone(){
        return new VacuumEnvironmentState(new ArrayList<Boolean>(this.ground), new Vacuum(this.vacuum));
    }

    public void moveVacuum(VacuumAction vacuumAction, int xLimit, int yLimit) throws VacuumActionValue.CannotMoveException {
        this.vacuum.move(vacuumAction, xLimit, yLimit);
    }

    public void moveVacuumAddDirt(VacuumAction vacuumAction, int xLimit, int yLimit) throws VacuumActionValue.CannotMoveException {
        this.addDirtOnVacuumPosition(xLimit);
        this.moveVacuum(vacuumAction, xLimit, yLimit);
    }

    public void addDirtOnVacuumPosition(int xLimit){
        this.ground.set(vacuum.getAbsPos(xLimit), false);
    }

    public void aspireCurrentVacummPos(int xLimit) {
        this.ground.set(this.vacuum.getAbsPos(xLimit), true);
    }


    public void aspireCurrentAndAroundVacummPos(int xLimit, int yLimit) {

        this.aspireCurrentVacummPos(xLimit);

        for( int i = 1 ;  i < VacuumActionValue.values().length ; i ++ ) {

            int[] del = VacuumActionValue.getMove(VacuumActionValue.values()[i]);

            int x = vacuum.getXpos() + del[0];
            int y = vacuum.getYpos() + del[1];

            if(x >= 0 && x < xLimit && y >= 0 && y < yLimit) {
                this.ground.set(getAbsPos(x, y, xLimit), true);
            }

        }

    }

    public int getAbsPos(int x, int y, int yLimit) {
        return ( y * yLimit ) + x;
    }

    @Override
    public int hashCode() {

        int rs = vacuum.getAbsPos(NotDeterministicVacummProblem.getxLimit()) * 10;

        for(boolean c : ground){
            rs += c ? 1 : 0;
            rs *= 10;
        }

        return rs;

    }

    @Override
    public long hashKey(){
        long rs = vacuum.getAbsPos(NotDeterministicVacummProblem.getxLimit()) * 10;

        for(boolean c : ground){
            rs += c ? 1 : 0;
            rs *= 10;
        }

        return rs;
    }

    public boolean isVacummPositionDirty(int limitX) {
       return !this.ground.get(this.vacuum.getAbsPos(limitX));
    }

    @Override
    public String toString() {

        String strGr = "";

        for( int i = 0 ; i < this.ground.size() ; i ++ ){

            if( i != 0 && i % NotDeterministicVacummProblem.getxLimit() == 0 )
                strGr+="\n";

            if(vacuum.getAbsPos(NotDeterministicVacummProblem.getxLimit()) == i) {
                if(this.ground.get(i))
                    strGr += "[-]";
                else
                    strGr+="[+]";
            }else{
                if(!this.ground.get(i))
                    strGr += "[*]";
                else
                    strGr += "[ ]";
            }


        }

        return strGr;
    }

    public String toStringb(int d) {

        String strGr = "";

        for( int i = 0 ; i < this.ground.size() ; i ++ ){

            if( i % NotDeterministicVacummProblem.getxLimit() == 0 )
                strGr+="\n"+ util.Util.getIdent(d);

            if(vacuum.getAbsPos(NotDeterministicVacummProblem.getxLimit()) == i) {
                if(this.ground.get(i))
                    strGr += "[-]";
                else
                    strGr+="[+]";
            }else{
                if(!this.ground.get(i))
                    strGr += "[*]";
                else
                    strGr += "[ ]";
            }


        }

        return strGr;
    }


    public int getPerceptId(int xLimit) {
       return this.getVacuum().getAbsPos(xLimit) * 10 + this.posVacuumDirty(xLimit);
    }

    @Override
    public boolean equals(Object o) {

        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        VacuumEnvironmentState that = (VacuumEnvironmentState) o;

        return this.hashKey() == that.hashKey();
    }
}
