package ai.games.vacuum;

public enum VacuumActionValue {

    ASPIRE, UP, UP_RIGHT, RIGHT, DOWN_RIGHT, DOWN, DOWN_LEFT, LEFT, LEFT_UP;
    //ASPIRE, UP, RIGHT, DOWN, LEFT;

    private static int mov [][] = new int[][]{{0,0},{0,-1},{1,-1},{1,0},{1,1},{0,1},{-1,1},{-1,0},{-1,-1} };
    //private static int mov [][] = new int[][]{{0,0},{0,-1},{1,0},{0,1},{-1,0}};

    public static int[] getMove(VacuumActionValue move){
        return mov[move.ordinal()];
    }

    public static class CannotMoveException extends Exception{ }
}
