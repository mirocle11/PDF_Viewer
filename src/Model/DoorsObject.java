package Model;

import javafx.scene.control.Label;
import javafx.scene.paint.Color;

public class DoorsObject {

    private int doorsNo;
    private Label doors;
    private Color color;


    public int getDoorsNo() {
        return doorsNo;
    }

    public void setDoorsNo(int doorsNo) {
        this.doorsNo = doorsNo;
    }

    public Label getDoors() {
        return doors;
    }

    public void setDoors(Label doors) {
        this.doors = doors;
    }

    public Color getColor() {
        return color;
    }

    public void setColor(Color color) {
        this.color = color;
    }
}
