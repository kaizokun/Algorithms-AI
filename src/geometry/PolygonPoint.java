package geometry;



public class PolygonPoint extends Point   {

    protected Polygon polygon;

    public PolygonPoint(double x, double y) {
      super(x,y);
    }


    public Polygon getPolygon() {
        return polygon;
    }

    public void setPolygon(Polygon polygon) {
        this.polygon = polygon;
    }
}
