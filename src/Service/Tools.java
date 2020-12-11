package Service;
import Controllers.workspaceController;
import Model.NotesObject;
import Model.PageObject;
import Model.ShapeObject;
import javafx.geometry.Point2D;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.*;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.*;
import javafx.scene.text.Font;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Tools {

    public PageObject page;
    public workspaceController window;

    Group group;
    String mode;
    Pane pane;
    Canvas canvas;
    Line line;
    Circle circle;
    VBox box;
    ScrollPane scroller;
    Color color;
    ContextMenu contextMenu, stampMenu;
    int pageNumber = 0;
    static boolean canDraw = false;
    boolean issetCanvas = false;

    List<Shape> shapeList = new ArrayList<>();
    ArrayList<Point2D> pointList = new ArrayList<>();
    ArrayList<Shape> stampList = new ArrayList<>();
    ArrayList<PageObject> pageObjects = new ArrayList<>();
    ArrayList<double[][]> snapList = new ArrayList<>();
    public ArrayList<ShapeObject> shapeObjList = new ArrayList<>();

    // indicator
    public int notes_id = 0;

    double total;

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
        this.box = box;
        setMode(this.mode);
    }

    public Tools(workspaceController window) {
        this.window = window;
        this.canvas = window.canvas;
        this.pane = window.pane;
        this.scroller = window.scroller;
        this.group = window.group;
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

    public void open() {
        try {
            File pdf = FileService.open();
            PdfToImageService service = new PdfToImageService(pdf);
            service.valueProperty().addListener((observable, oldValue, newValue) -> {
                if (newValue != null) {
                    pageObjects.add(newValue);
                    if (!issetCanvas) {
                        page = newValue;
                        setPageElements();
                        scroller.setOpacity(1.0);
                        issetCanvas = true;
                    }
                }
            });
            service.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setMode(String mode) {
        switch (mode) {
            case "FREE":
                this.canDraw = false;
                window.pane.setOnMouseMoved(event -> {
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
            case "LENGTH":
                this.canDraw = true;
                Length length = new Length(this);
                break;
            case "NOTES":
                this.canDraw = false;
                pane.setOnMouseClicked(event -> {
                    if (event.getButton() == MouseButton.PRIMARY) {
                        Label notes = new Label();
                        notes.setText(window.NOTES_TXT.getText());
                        notes.setTextFill(window.NOTES_COLOR.getValue());
                        notes.setFont(new Font("Segoe UI", 32));
                        notes.setWrapText(true);
                        notes.setMaxWidth(550);
                        notes.setStyle("-fx-border-color: #ff0000;");
                        notes.setAlignment(Pos.CENTER);
                        notes.setLayoutX(event.getX() - 50);
                        notes.setLayoutY(event.getY() - 40);

                        pane.getChildren().add(notes);

                        NotesObject notesObject = new NotesObject();
                        notesObject.setNotes(notes);
                        notesObject.setNotesNo(notes_id++);

                        notes.setOnMouseClicked(event1 -> {
                            if (event1.getButton() == MouseButton.SECONDARY) {
                                stampMenu = new ContextMenu();
                                stampMenu.hide();

                                MenuItem removeStamp = new MenuItem("Remove Note");
                                removeStamp.setOnAction(event2 -> {
                                    notes.setVisible(false);
                                });
                                stampMenu.getItems().add(removeStamp);
                                stampMenu.show(notes, event1.getScreenX(), event1.getScreenY());
                            }
                        });
                    }
                });
                break;
        }
    }

    public void setColor(Color color) {
        this.color = color;
    }

    public Point2D clamp(double x, double y) {
        double maxX = window.canvas.getWidth();
        double maxY = window.canvas.getHeight();
        double a = Math.max(0, x);
        double b = Math.max(0, y);
        double c = Math.min(a, maxX);
        double d = Math.min(b, maxY);
        Point2D p = new Point2D(c, d);
        return p;
    }

    public void setPageElements() {
        shapeObjList = new ArrayList<>();
        stampList = new ArrayList<>();
        snapList = new ArrayList<>();
        page = pageObjects.get(pageNumber);
        shapeObjList.addAll(page.getShapeList());
        stampList.addAll(page.getStampList());
        snapList.addAll(page.getSnapList());
        setCanvas();
    }

    public void setCanvas() {
        canvas.setWidth(page.getImage().getWidth());
        canvas.setHeight(page.getImage().getHeight());
        pane.setPrefSize(canvas.getWidth(), canvas.getHeight());
        GraphicsContext gc = canvas.getGraphicsContext2D();
        gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
        gc.drawImage(page.getImage(), 0, 0);
        updateWindow();
    }

    //Pagination
    public void nextPage() {
        if (pageNumber < pageObjects.size() - 1) {
            pageNumber++;
            setPageElements();
            setMode("FREE");
        }
    }

    public void previousPage() {
        if (pageNumber > 0) {
            pageNumber--;
            setPageElements();
            setMode("FREE");
        }
    }

    public void updateWindow() {
        pane.getChildren().clear();
        pane.getChildren().add(circle);
        circle.setVisible(false);

        for (ShapeObject sp1 : page.getShapeList()) {
            if (sp1.getType().equals("LINE")) {
                for (Line l : sp1.getLineList()) {
                    Point2D p1 = new Point2D(l.getStartX(), l.getStartY());
                    Point2D p2 = new Point2D(l.getEndX(), l.getEndY());
                    Point2D mid = p1.midpoint(p2);
                    line.getRotate();
                    if (p1.distance(p2) != 0) {
                        double length = (p1.distance(p2) / page.getScale());
                        sp1.setLength(Math.round(length * 100.0) / 100.0);
                        Label lbl = new Label(sp1.getLength() + " mm");
                        double theta = Math.atan2(p2.getY() - p1.getY(), p2.getX() - p1.getX());
                        lbl.setFont(new Font("Arial", 36));
                        lbl.setRotate(theta * 180 / Math.PI);
                        lbl.setLayoutY(mid.getY());
                        lbl.setLayoutX(mid.getX() - (55 / group.getScaleY()));
                        lbl.setOnMouseEntered(event -> {
                            lbl.setStyle("-fx-background-color: white");
                            lbl.setOpacity(1);
                        });
                        lbl.setOnMouseExited(event -> {
                            lbl.setStyle("-fx-background-color: transparent");
                            lbl.setOpacity(.7);
                        });
                        lbl.setTextFill(sp1.getColor());
                        lbl.setOpacity(.7);
                        pane.getChildren().add(lbl);
                        total = total + sp1.getLength();
                    }
                }
                sp1.setLength(Math.round(total * 100.0) / 100.0);
                total = 0.0;
            }
            pane.getChildren().addAll(sp1.getLineList());
            pane.getChildren().addAll(sp1.getBoxList());
        }

        pane.getChildren().add(line);
        line.setVisible(false);
        pane.getChildren().forEach(node -> {
            if (node.getClass().getSuperclass() == Shape.class) {
                Shape sp = (Shape) node;
                if (sp.getClass() == Rectangle.class) {
                    Rectangle rect = (Rectangle) sp;
                    double centerX = rect.getX() + rect.getWidth() / 2;
                    double centerY = rect.getY() + rect.getHeight() / 2;
                    rect.setStrokeWidth(4 / group.getScaleY());
                    rect.setX(centerX - 5 / group.getScaleY());
                    rect.setY(centerY - 5 / group.getScaleY());
                    rect.setWidth(10 / group.getScaleY());
                    rect.setHeight(10 / group.getScaleY());
                } else {
                    sp.setStrokeWidth(5 / group.getScaleY());
                }
            }
            if (node.getClass() == Label.class) {
                Label lbl = (Label) node;
                lbl.setFont(new Font("Arial", 18 / group.getScaleY()));
            }
        });
    }

}
