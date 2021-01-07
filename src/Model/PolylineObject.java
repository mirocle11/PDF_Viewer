package Model;

import javafx.geometry.Point2D;
import javafx.scene.paint.Color;
import javafx.scene.shape.Polyline;

import java.util.ArrayList;
import java.util.List;

public class PolylineObject {

    private Polyline polyline;
    private Color color;
    private ArrayList<Double> pointList = new ArrayList<>();

    public Polyline getPolyline() {
        return polyline;
    }

    public void setPolyline(Polyline polyline) {
        this.polyline = polyline;
    }

    public Color getColor() {
        return color;
    }

    public void setColor(Color color) {
        this.color = color;
    }

    public void setPointList(ArrayList<Double> pointList) {
        this.pointList.clear();
        pointList.forEach(Double -> {
            this.pointList.add(Double);
        });
    }

    public List<Double> getPointList() {
        return this.pointList;
    }
}
