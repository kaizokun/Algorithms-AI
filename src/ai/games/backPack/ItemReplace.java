package ai.games.backPack;

import ai.Action;
import ai.State;

public class ItemReplace extends Action {


    private Item itema, itemb;
    private int iItemA, iItemB;

    public ItemReplace(Item itema, Item itemb, int iItemA, int iItemB) {
        this.itema = itema;
        this.itemb = itemb;
        this.iItemA = iItemA;
        this.iItemB = iItemB;
    }

    @Override
    public String getActionName() {
        return /*result.getStatement()+*/"Replace "+itema+" par "+itemb+" => "+result;
    }

    public Item getItema() {
        return itema;
    }

    public void setItema(Item itema) {
        this.itema = itema;
    }

    public Item getItemb() {
        return itemb;
    }

    public void setItemb(Item itemb) {
        this.itemb = itemb;
    }

    public int getiItemA() {
        return iItemA;
    }

    public void setiItemA(int iItemA) {
        this.iItemA = iItemA;
    }

    public int getiItemB() {
        return iItemB;
    }

    public void setiItemB(int iItemB) {
        this.iItemB = iItemB;
    }
}

