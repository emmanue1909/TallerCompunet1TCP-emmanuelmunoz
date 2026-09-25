package co.icesi.buscaminas.client;

import java.io.Serializable;

public class Cell implements Serializable {
    private boolean isLandMine;
    private int value;
    private boolean hide;
    private boolean showAll;
    private boolean isMarked;

    public Cell(boolean isMine, int value) {
        this.isLandMine = isMine;
        this.value = value;
        this.hide = true;
        this.showAll = false;
        this.isMarked = false;
    }

    public boolean isMarked() { return this.isMarked; }
    public void setMarked(boolean marked) { this.isMarked = marked; }
    public int getValue() { return this.value; }
    public void setLandMine(boolean landMine) { this.isLandMine = landMine; }
    public boolean isLandMine() { return this.isLandMine; }
    public void setValue(int value) { this.value = value; }
    public boolean isHide() { return this.hide; }
    public void setHide(boolean hide) { this.hide = hide; }
    public void setShowAll(boolean showAll) { this.showAll = showAll; }
    public boolean isShowAll() { return this.showAll; }

    public String toString() {
        if (this.isMarked) {
            return "\u001b[33mM\u001b[0m";
        } else {
            return this.hide && !this.showAll ? "." : (this.isLandMine ? "\u001b[31m*\u001b[0m" : "" + this.value);
        }
    }
}