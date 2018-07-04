package dataStructure.tas;

/**
 * Created by monsio on 3/23/16.
 */
public class DataTest implements Handle {

    int indexTas;
    String data;

    public DataTest(String data) {
        this.data = data;
    }

    public int getIndex() {
        return indexTas;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    @Override
    public void setIndex(int tasId) {
            indexTas = tasId;
    }
}
