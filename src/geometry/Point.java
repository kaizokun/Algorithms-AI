package geometry;

public class Point {

    protected double x,y;

    public Point() {
    }

    public Point(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public Point clone(){
        return new Point(this.x,this.y);
    }

    public double getX() {
        return x;
    }

    public void setX(double x) {
        this.x = x;
    }

    public double getY() {
        return y;
    }

    public void setY(double y) {
        this.y = y;
    }


    public static double squareDistance(Point p1, Point p2){
        return Math.pow(p1.x - p2.x, 2) + Math.pow(p1.y - p2.y,2);
    }

    public static double distance(Point p1, Point p2){
        return Math.sqrt(squareDistance(p1,p2));
    }

    public static double scalarProduct(Point p0, Point p1, Point p2){

        return  (p0.x - p1.x) * (p0.x - p2.x) + (p0.y - p1.y) * (p0.y - p2.y);

    }

    @Override
    public String toString() {
        return "("+x+","+y+")";
    }

    @Override
    public boolean equals(Object o) {
        // System.out.println("POINT EQUAL "+o+" "+this);
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Point point = (Point) o;

        if (Double.compare(point.x, x) != 0) return false;
        return Double.compare(point.y, y) == 0;
    }

    @Override
    public int hashCode() {
        int result;
        long temp;
        temp = Double.doubleToLongBits(x);
        result = (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(y);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        return result;
    }

}
