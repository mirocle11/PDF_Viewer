package Model;

import javafx.scene.control.Label;

public class StampObject {

    public Label stamp;
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
}
