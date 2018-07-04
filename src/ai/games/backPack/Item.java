package ai.games.backPack;

public class Item {

    private char id;
    private double weigh, value;

    public Item(char id, double weigh, double value) {
        this.weigh = weigh;
        this.value = value;
        this.id = id;
    }

    public double getWeigh() {
        return weigh;
    }

    public void setWeigh(double weigh) {
        this.weigh = weigh;
    }

    public double getValue() {
        return value;
    }

    public void setValue(double value) {
        this.value = value;
    }

    public char getId() {
        return id;
    }

    public void setId(char id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return "Item{" +
                "id=" + id +
                ", weigh=" + weigh +
                ", isTrue=" + value +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Item item = (Item) o;

        return id == item.id;
    }

    @Override
    public int hashCode() {
        return (int) id;
    }
}
