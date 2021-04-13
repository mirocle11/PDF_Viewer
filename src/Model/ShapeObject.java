package Model;

import javafx.geometry.Point2D;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.*;
import Service.Tools;

import java.util.ArrayList;
import java.util.List;

public class ShapeObject {
    Polygon polygon;
    Color color;
    double strokeWidth;
    List<Line> lineList = new ArrayList<>();
    public Polyline polyline;
    private ArrayList<Point2D> pointList = new ArrayList<>();
    ArrayList<Shape> stampList = new ArrayList<>();
    List<Rectangle> boxList = new ArrayList<>();
    String type;
    Pane pane;
    Tools tools;
    ContextMenu contextMenu;
    double area = 0;
    double length = 0;
    String structure;
    String wallType;
    String wall;
    String material;
    String stud_height;
    String unit;
    String labour;
    String cladding_name;

    public ShapeObject() {
        java.awt.Color c = new java.awt.Color(0, true);
    }

    public String getType() {
        return type;
    }

    public void setTools(Tools tools) {
        this.tools = tools;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Color getColor() {
        return color;
    }

    public void setColor(Color color) {
        this.color = color;
    }

    public void setPane(Pane pane) {
        this.pane = pane;
    }

    public Polygon getPolygon() {
        return polygon;
    }

    public void setStrokeWidth(double strokeWidth) {
        this.strokeWidth = strokeWidth;
    }

    public List<Line> getLineList() {
        return lineList;
    }

    public List<Point2D> getPointList() {
        return this.pointList;
    }

    public ArrayList<Shape> getStampList() {
        return stampList;
    }

    public void setStampList(ArrayList<Shape> stampLists) {
        this.stampList.clear();
        stampLists.forEach(shape1 -> {
            this.stampList.add(shape1);
        });
    }

    public double getArea() {
        return area;
    }

    public void setArea(double area) {
        this.area = area;
    }

    public double getLength() {
        return length;
    }

    public void setLength(double length) {
        this.length = length;
    }

    public void setPointList(ArrayList<Point2D> pointLists) {
        this.pointList.clear();
        pointLists.forEach(point2D -> {
            this.pointList.add(point2D);
        });
        createLines(this.pointList);
        creatBoxes(this.pointList);
        createPolygon(this.pointList);
    }

    public List<Rectangle> getBoxList() {
        return boxList;
    }

    public void createLines(List<Point2D> point2DS) {
        lineList.clear();
        for (int x = 0; x < point2DS.size() - 1; x++) {
            lineList.add(new Line(point2DS.get(x).getX(), point2DS.get(x).getY(), point2DS.get(x + 1).getX(), point2DS.get(x + 1).getY()));
            Line l = lineList.get(lineList.size() - 1);
            l.setStrokeLineCap(StrokeLineCap.BUTT);
            l.setStroke(color);
            l.setStrokeWidth(strokeWidth);
            l.setOpacity(.8);
            l.setOnMouseEntered(event -> {
                l.setStroke(Color.RED);
            });
            l.setOnMouseExited(event -> {
                l.setStroke(color);
            });
            l.setOnMousePressed(event -> {
                if (event.getButton() == MouseButton.SECONDARY) {
                    contextMenu = new ContextMenu();
                    contextMenu.hide();
                    if (type == "LENGTH") {
                        MenuItem removeLength = new MenuItem("Remove Line");
                        removeLength.setOnAction(event1 -> {
                            tools.page.shapeObjList.remove(this);
                            tools.updateWindow();
                        });
                        contextMenu.getItems().add(removeLength);
                    }
//                    else {
//                        MenuItem removeArea = new MenuItem("Remove Line");
//                        removeArea.setOnAction(event1 -> {
//                            tools.page.shapeObjList.remove(this);
//                            tools.updateWindow();
//                        });
//
//                        contextMenu.getItems().add(removeArea);
//                    }
                    contextMenu.show(l, event.getScreenX(), event.getScreenY());
                }
            });
        }
    }

    public void creatBoxes(List<Point2D> point2DS) {
        boxList.clear();
        double boxW = strokeWidth * 3;
        int ctr = point2DS.size();
        for (int x = 0; x < ctr; x++) {

            boxList.add(new Rectangle(point2DS.get(x).getX() - boxW / 2, point2DS.get(x).getY() - boxW / 2, boxW, boxW));
            Rectangle r = boxList.get(boxList.size() - 1);
            r.setStroke(Color.GREEN);
            r.setOpacity(.5);
            r.setStrokeWidth(boxW / 5);
            r.setFill(Color.TRANSPARENT);
            r.setOnMouseEntered(event -> {
                r.setStroke(Color.RED);
            });
            r.setOnMouseExited(event -> {
                r.setStroke(Color.GREEN);
            });
            r.setOnMousePressed(event -> {
                if (event.getButton() == MouseButton.SECONDARY) {
                    contextMenu = new ContextMenu();
                    if (type.equals("LENGTH")) {
                        MenuItem removeLength = new MenuItem("Remove Length");
                        removeLength.setOnAction(event1 -> {
                            tools.page.shapeObjList.remove(this);
                            tools.updateWindow();
                        });
                        MenuItem continueLength = new MenuItem("Continue Length");
                        continueLength.setOnAction(event1 -> {
                            tools.page.shapeObjList.remove(this);
                            tools.updateWindow();
                        });

                        contextMenu.getItems().add(removeLength);
                        contextMenu.getItems().add(continueLength);
                    } else {
                        MenuItem removeArea = new MenuItem("Remove Area");
                        removeArea.setOnAction(event1 -> {
                            tools.page.shapeObjList.remove(this);
                            tools.updateWindow();
                        });
                        contextMenu.getItems().add(removeArea);
                    }
                    contextMenu.show(r, event.getScreenX(), event.getScreenY());
                }
            });
        }
    }

    public void createPolygon(List<Point2D> point2DS) {
        this.polygon = new Polygon();

        for (int x = 0; x < point2DS.size() - 1; x++) {
            double px = point2DS.get(x).getX();
            double py = point2DS.get(x).getY();
            polygon.getPoints().add(px);
            polygon.getPoints().add(py);


        }
        polygon.setFill(color);
        polygon.setOpacity(.3);
    }

    public String getStructure() {
        return structure;
    }

    public void setStructure(String structure) {
        this.structure = structure;
    }

    public String getWallType() {
        return wallType;
    }

    public void setWallType(String wallType) {
        this.wallType = wallType;
    }

    public String getWall() {
        return wall;
    }

    public void setWall(String wall) {
        this.wall = wall;
    }

    public String getMaterial() {
        return material;
    }

    public void setMaterial(String material) {
        this.material = material;
    }

    public String getStud_height() {
        return stud_height;
    }

    public void setStud_height(String stud_height) {
        this.stud_height = stud_height;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public String getLabour() {
        return labour;
    }

    public void setLabour(String labour) {
        this.labour = labour;
    }

    public Polyline getPolyline() {
        return polyline;
    }

    public void setPolyline(Polyline polyline) {
        this.polyline = polyline;
    }
}

