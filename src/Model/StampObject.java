package Model;

import javafx.scene.control.Label;

public class StampObject {

    public Label stamp;
    public int stamp_no;
    public String imagePath;

    public Label getStamp() {
        return stamp;
    }

    public void setStamp(Label stamp) {
        this.stamp = stamp;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public int getStamp_no() {
        return stamp_no;
    }

    public void setStamp_no(int stamp_no) {
        this.stamp_no = stamp_no;
    }
}
