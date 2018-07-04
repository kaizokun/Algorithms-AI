package util;

public class TimeUtil {

    private long t1;

    public TimeUtil() {
        this.t1 = System.nanoTime();
    }

    public double getTimeDelta(){
        return (System.nanoTime() - t1) / 1000000.0;
    }

    public String getTimeDetlaStr(){
        return getTimeDelta()+" ms";
    }

    public void printTimeDetlaStr(){
        System.out.println("TEMPS : "+getTimeDetlaStr());
    }

}
