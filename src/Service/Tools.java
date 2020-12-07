package Service;
import Controllers.workspaceController;
import Model.PageObject;
import Model.ShapeObject;
import javafx.geometry.Insets;
import javafx.geometry.Point2D;
import javafx.scene.Group;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.*;
import javafx.scene.text.Font;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Locale;

public class Tools {

    public PageObject page;
    public workspaceController window;

    Group group;
    String mode;
    Pane pane;
    Canvas canvas;
    Line line;
    Circle circle;
    Label noScale;
    VBox box;
    ScrollPane scroller;
    AnchorPane mainPane, frontPane, loadingPane;
    GridPane gridPane, innerGridPane;
    Color color;
    ContextMenu contextMenu, stampMenu;
    int pageNumber = 0;
    static boolean canDraw = false;
    boolean issetCanvas = false;

    ArrayList<double[][]> snapList = new ArrayList<>();

    public Tools(PageObject page, Group group, String mode, Line line, Circle circle, VBox box) {
        this.page = page;
        this.group = group;
        this.mode = mode;
        this.line = line;
        this.circle = circle;
        group.getChildren().forEach(node -> {
            if (node.getClass().equals(Pane.class)) {
                this.pane = (Pane) node;
            } else if (node.getClass().equals(Canvas.class)) {
                this.canvas = (Canvas) node;
            }
        });
        this.noScale = new Label();
        this.noScale.setText("The page is not scaled. No measurements can be taken.");
        this.noScale.setTextFill(Color.RED);
        this.box = box;
        setMode(this.mode);
    }

    public Tools(Group group, String mode, Line line, Circle circle, VBox box) {
        this.group = group;
        this.mode = mode;
        this.line = line;
        this.circle = circle;
        group.getChildren().forEach(node -> {
            if (node.getClass().equals(Pane.class)) {
                this.pane = (Pane) node;
            } else if (node.getClass().equals(Canvas.class)) {
                this.canvas = (Canvas) node;
            }
        });
        this.noScale = new Label();
        this.noScale.setText("The page is not scaled. No measurements can be taken.");
        this.noScale.setTextFill(Color.RED);
        this.box = box;
        setMode(this.mode);
    }

    public Tools(workspaceController window) {
        this.window = window;
        this.canvas = window.CANVAS;
        this.pane = window.PANE;
        this.scroller = window.SCROLL_PANE;
        this.group = window.GROUP;
        this.contextMenu = new ContextMenu();
        this.stampMenu = new ContextMenu();

        this.circle = new Circle();
        this.circle.setFill(Color.RED);
        this.circle.setRadius(5);

        this.line = new Line();
        this.line.setVisible(false);
        this.line.setOpacity(.7);
        this.line.setStrokeLineCap(StrokeLineCap.BUTT);

        this.circle.setFill(Color.RED);
        this.circle.setRadius(5);
        this.pane.getChildren().add(circle);

        //stud
//        this.workspace_stud_height = window.stud_height;
    }

    public void setMode(String mode) {
        switch (mode) {
            case "FREE":
                this.canDraw = false;
                window.PANE.setOnMouseMoved(event -> {
                    try {
                        for (double[][] arr : snapList) {
                            int a = (int) arr[0][0] - (int) event.getX();
                            int b = (int) arr[0][1] - (int) event.getY();
                            if ((a >= -3 && a <= 3) && (b >= -3 && b <= 3)) {
                                circle.setCenterX(arr[0][0]);
                                circle.setCenterY(arr[0][1]);
                                circle.setVisible(true);
                                circle.setRadius(5);
                                circle.setOnMouseExited(event1 -> {
                                    circle.setVisible(false);
                                });
                                pane.getChildren().add(circle);
                                break;
                            } else {
                                circle.setVisible(false);
                            }
                        }
                    } catch (Exception ex) {

                    }
                });
                break;
            case "LINE":
                this.canDraw = true;
                Lines lines = new Lines(this);
                break;
        }
    }

    public void setColor(Color color) {
        this.color = color;
    }

    public Point2D clamp(double x, double y) {
        double maxX = window.CANVAS.getWidth();
        double maxY = window.CANVAS.getHeight();
        double a = Math.max(0, x);
        double b = Math.max(0, y);
        double c = Math.min(a, maxX);
        double d = Math.min(b, maxY);
        Point2D p = new Point2D(c, d);
        return p;
    }

    public void updateWindow() {
        pane.getChildren().clear();
        pane.getChildren().add(circle);
        circle.setVisible(false);

        for (ShapeObject sp : page.getShapeList()) {
            pane.getChildren().addAll(sp.getLineList());
            pane.getChildren().addAll(sp.getBoxList());
        }
    }

}
