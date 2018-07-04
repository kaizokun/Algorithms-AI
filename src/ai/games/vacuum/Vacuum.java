package ai.games.vacuum;

public class Vacuum {


    private XYPos position;

    public Vacuum(XYPos pôsition) {
        this.position = pôsition;
    }

    public Vacuum(int x, int y) {
        this.position = new XYPos(x,y);
    }

    public Vacuum(Vacuum vacuum){
        this(vacuum.position.x,vacuum.position.y);
    }

    public void move(VacuumAction move, int xLimit, int yLimit) throws VacuumActionValue.CannotMoveException {
        this.position.x += VacuumActionValue.getMove(move.getActionValue())[0];
        this.position.y += VacuumActionValue.getMove(move.getActionValue())[1];

        if(this.position.x < 0 || this.position.x >= xLimit || this.position.y < 0 || this.position.y >= yLimit  )
            throw new VacuumActionValue.CannotMoveException();

    }

    public int getXpos() {
        return position.x;
    }

    public int getYpos() {
        return position.y;
    }

    public int getAbsPos(int xLimit) {

        return (this.getYpos() * xLimit) + this.getXpos();
    }

    public static class XYPos {

        private int x, y;

        public XYPos(int x, int y) {
            this.x = x;
            this.y = y;
        }

        public int getX() {
            return x;
        }

        public void setX(int x) {
            this.x = x;
        }

        public int getY() {
            return y;
        }

        public void setY(int y) {
            this.y = y;
        }
    }

}
