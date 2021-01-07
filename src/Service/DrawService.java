package Service;

import javafx.geometry.Point2D;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.StrokeLineCap;
import javafx.scene.text.Font;

import java.util.*;

/**
 * Created by User on 18/02/2020.
 */
public class DrawService {
    static List box = new ArrayList();

    public static void drawLine(Line line, Pane pane, double scale) {
        box.add(new Line(line.getStartX(), line.getStartY(), line.getEndX(), line.getEndY()));
        Line l = (Line) box.get(0);

        l.setStroke(line.getStroke());
        l.setStrokeLineCap(StrokeLineCap.BUTT);
        l.setStrokeWidth(7 / scale);
        l.setOpacity(.7);

        l.setOnMouseEntered(event -> {
            l.setStroke(Color.RED);
        });
        l.setOnMouseExited(event -> {
            l.setStroke(line.getStroke());
        });
        pane.getChildren().add(l);
        box.clear();
    }

    public static void drawBox(double x, double y, Pane pane, double scale) {
        box.add(new Rectangle());
        Rectangle r = (Rectangle) box.get(0);
        r.setX(x - 5 / scale);
        r.setY(y - 5 / scale);
        r.setWidth(10 / scale);
        r.setHeight(10 / scale);
        r.setStroke(Color.GREEN);
        r.setOpacity(.7);
        r.setStrokeWidth(4 / scale);
        r.setFill(Color.TRANSPARENT);
        r.setOnMouseReleased(event -> {
            if (!Tools.canDraw) {
                return;
            }
        });
        pane.getChildren().add(r);
        box.clear();
    }

    public static void drawLabel(Line line,Pane pane,double m_Scale,double scale) {
        Point2D p1 = new Point2D(line.getStartX(), line.getStartY());
        Point2D p2 = new Point2D(line.getEndX(), line.getEndY());
        Point2D mid = p1.midpoint(p2);
        line.getRotate();
        if (p1.distance(p2) != 0) {
            double length = (p1.distance(p2) / m_Scale);
            Label lbl = new Label(Math.round(length * 100.0) / 100.0 + " mm");
            double theta = Math.atan2(p2.getY() - p1.getY(), p2.getX() - p1.getX());
            lbl.setFont(new Font("Arial", 36 / scale));
            lbl.setRotate(theta * 180 / Math.PI);
            lbl.setLayoutY(mid.getY());
            lbl.setLayoutX(mid.getX() - (55 / scale));
            lbl.setOnMouseEntered(event1 -> {
                lbl.setStyle("-fx-background-color: white");
                lbl.setOpacity(1);
            });
            lbl.setOnMouseExited(event1 -> {
                lbl.setStyle("-fx-background-color: transparent");
                lbl.setOpacity(.7);
            });
            lbl.setTextFill(line.getStroke());
            lbl.setOpacity(.7);
            pane.getChildren().add(lbl);
        }
    }
}
