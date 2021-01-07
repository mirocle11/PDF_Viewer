package Service;

import Model.PageObject;
import Model.ShapeObject;
import javafx.geometry.Point2D;
import javafx.scene.Group;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by User on 17/02/2020.
 */
public class Length implements IMeasurement {
    Tools tools;
    boolean isNew = true;
    Line line;
    Circle circle;
    Pane pane;
    Group group;
    PageObject page;
    static List box = new ArrayList();
    ArrayList<double[][]> snapList;
    ArrayList<Point2D> pointList = new ArrayList<>();
    Rectangle rect;
    double snapX = -1, snapY = -1;
    ContextMenu contextMenu;

    public Length(Tools tools) {
        this.tools = tools;
        this.line = tools.line;
        this.line.setStroke(tools.color);
        this.line.setStrokeWidth(8 / tools.group.getScaleY());
        this.circle = tools.circle;
        this.pane = tools.pane;
        this.group = tools.group;
        this.page = tools.page;
        this.snapList = tools.page.getSnapList();

        this.tools.pane.setOnMouseClicked(event -> {
            handleClick(event);
        });

        this.tools.pane.setOnMouseMoved(event -> {
            handleMouseMove(event);
        });
    }

    @Override
    public void handleClick(MouseEvent event) {
        if (event.getButton() == MouseButton.SECONDARY) {
            handleShowContextMenu(event);
            return;
        }
        Point2D clamp = tools.clamp(event.getX(), event.getY());
        if (isNew) {
            line.setVisible(true);
            line.setStartX(clamp.getX());
            line.setStartY(clamp.getY());
            line.setEndX(clamp.getX());
            line.setEndY(clamp.getY());
            if (snapX != -1 && snapY != -1) {
                line.setStartX(snapX);
                line.setStartY(snapY);
                line.setEndX(snapX);
                line.setEndY(snapY);
            }
            line.setStrokeWidth(8 / group.getScaleY());
            Point2D end = new Point2D(line.getEndX(), line.getEndY());
            pointList.add(end);
            drawBox(line.getEndX(), line.getEndY(), pane, group.getScaleX());
            isNew = false;
            snapX = snapY = -1;
        } else {
            if (snapX != -1 && snapY != -1) {
                line.setEndX(snapX);
                line.setEndY(snapY);
            }
            Point2D end = new Point2D(line.getEndX(), line.getEndY());
            pointList.add(end);

            DrawService.drawLine(line, pane, group.getScaleX());
            drawBox(line.getEndX(), line.getEndY(), pane, group.getScaleX());
//            DrawService.drawLabel(line, pane, page.getScale(), group.getScaleX());

            line.setStartX(line.getEndX());
            line.setStartY(line.getEndY());
            snapX = snapY = -1;
        }
    }

    @Override
    public void handleMouseMove(MouseEvent event) {
        try {
            pane.getChildren().contains(circle);
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
                    circle.setOnMouseReleased(event1 -> {
                        if (snapX == -1 && snapY == -1) {
                            snapX = circle.getCenterX();
                            snapY = circle.getCenterY();
                        }
                    });
                    pane.getChildren().add(circle);
                    break;
                } else {
                    circle.setVisible(false);
                }
            }
        } catch (Exception ex) {

        }

        Point2D cP = tools.clamp(event.getX(), event.getY());
        if (!isNew) {
            if (event.isShiftDown()) {
                if (cP.getX() < cP.getY()) {
                    line.setEndX(line.getStartX());
                    line.setEndY(cP.getY());
                } else {
                    line.setEndX(cP.getX());
                    line.setEndY(line.getStartY());
                }
                int xdiff = (int) Math.abs(line.getStartX() - cP.getX());
                int ydiff = (int) Math.abs(line.getStartY() - cP.getY());
                if (xdiff < ydiff) {
                    line.setEndX(line.getStartX());
                    line.setEndY(cP.getY());
                } else {
                    line.setEndX(cP.getX());
                    line.setEndY(line.getStartY());
                }
            } else {
                line.setEndX(cP.getX());
                line.setEndY(cP.getY());
            }
        }
    }

    @Override
    public void handleFinish() {
        ShapeObject shapeObj = new ShapeObject();
        shapeObj.setPane(pane);
        shapeObj.setStrokeWidth(7 / group.getScaleY());
        shapeObj.setColor(tools.color);

        try {
            Point2D p2d = new Point2D(rect.getX() + rect.getHeight() / 2, rect.getY() + rect.getHeight() / 2);
            if (p2d != pointList.get(pointList.size() - 1)) {
                pointList.add(p2d);
            }
        } catch (Exception ex) {
            ex.getSuppressed();
        }

        shapeObj.setPointList(pointList);
        shapeObj.setType("FREE");
        shapeObj.setTools(tools);

        page.getShapeList().add(shapeObj);

//        if (tools.window.stud_height == 0.0) {
//            tools.studPopup();
//        } else {
//            tools.stud_height = tools.window.stud_height;
//        }

        tools.updateWindow();
//        System.out.println("Workspace stud height is: " + tools.window.stud_height);

        pointList.clear();
    }

    @Override
    public void handleShowContextMenu(MouseEvent event) {
        tools.setMode("FREE");
        if (event.getButton() == MouseButton.SECONDARY) {
            contextMenu = new ContextMenu();
            contextMenu.hide();

            MenuItem removeLength = new MenuItem("Remove Length");
            removeLength.setOnAction(event1 -> {
                tools.page.shapeObjList.remove(this);
                tools.updateWindow();
            });

            MenuItem finish = new MenuItem("Complete Length");
            finish.setOnAction(event1 -> {
                if (pointList.size() >= 2) {
                    handleFinish();
                }
                tools.updateWindow();
            });

            if (pointList.size() >= 2) {
                contextMenu.getItems().add(finish);
            }
            if (pointList.size() >= 1) {
                contextMenu.getItems().add(removeLength);
            }
            contextMenu.show(line, event.getScreenX(), event.getScreenY());
        }
    }

    public void drawBox(double x, double y, Pane pane, double scale) {
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
        r.setOnMouseEntered(event -> {
            r.setStroke(Color.RED);
        });
        r.setOnMouseExited(event -> {
            r.setStroke(Color.GREEN);
        });
        r.setOnMouseReleased(event -> {
            if (!Tools.canDraw || event.getButton() == MouseButton.SECONDARY) {
                return;
            }
            this.rect = r;
            handleFinish();
        });
        pane.getChildren().add(r);
        box.clear();
    }
}
