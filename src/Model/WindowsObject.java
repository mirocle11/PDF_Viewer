package Model;

import javafx.scene.control.Label;
import javafx.scene.paint.Color;

public class WindowsObject {

    private int windowsNo;
    private Label windows;
    private Color color;


    public int getWindowsNo() {
        return windowsNo;
    }

    public void setWindowsNo(int windowsNo) {
        this.windowsNo = windowsNo;
    }

    public Label getWindows() {
        return windows;
    }

    public void setWindows(Label windows) {
        this.windows = windows;
    }

    public Color getColor() {
        return color;
    }

    public void setColor(Color color) {
        this.color = color;
    }
}
