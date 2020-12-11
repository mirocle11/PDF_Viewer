package Controllers;

import Main.Main;
import Model.PageObject;
import Model.ShapeObject;
import Service.FileService;
import Service.PdfToImageService;
import Service.Tools;
import com.jfoenix.controls.JFXButton;
import javafx.fxml.Initializable;
import javafx.geometry.Point2D;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.*;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;
import javafx.scene.text.Font;
import javafx.stage.Stage;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class workspaceController implements Initializable {

    public Main main;
    public Stage stage;

    public void setMain(Stage stage, Main main) {
        this.main = main;
        this.stage = stage;
    }

    //components
    public HBox DRAW_OPTIONS, STAMP_OPTIONS, NOTES_OPTIONS;
    public JFXButton FILE, START_PAGE, PREVIOUS_PAGE, NEXT_PAGE, END_PAGE, ROTATE_LEFT, ROTATE_RIGHT,
            DRAW, STAMP, COMMENT, NOTES, LINE;
    public ColorPicker DRAW_COLOR, NOTES_COLOR;
    public TextField PAGE, NOTES_TXT;
    public Label TOTAL_PAGE;
    public Canvas canvas;
    public Pane pane;
    public ScrollPane scroller;
    public StackPane zoomPane;
    public Group scrollContent, group;

    public ContextMenu FILE_MENU = new ContextMenu();
    public MenuItem FILE_OPEN = new MenuItem("Open");
    public MenuItem FILE_SAVE = new MenuItem("Save");

    //collections
    ArrayList<PageObject> pageObjects = new ArrayList<>();
    boolean issetCanvas = false;
    public int notes_indicator;

    //collections (2)
    List<Shape> shapeList = new ArrayList<>();
    ArrayList<Point2D> pointList = new ArrayList<>();
    ArrayList<Shape> stampList = new ArrayList<>();
    ArrayList<double[][]> snapList = new ArrayList<>();
    public ArrayList<ShapeObject> shapeObjList = new ArrayList<>();

    //page
    public PageObject page;
    int pageNumber = 0;

    //scale
    double SCALE_DELTA = 1.1;

    //booleans
    private boolean isNew = true;
    private boolean canDraw = true;

    //shapes
    Line line = new Line();
    Rectangle rect = new Rectangle();
    Circle circle = new Circle();

    //snap
    double snapX = -1;
    double snapY = -1;

    //static classes
    public Tools tools;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        //todo -> all icons color #737373

        tools = new Tools(this);
        tools.setMode("FREE");

        scroller.viewportBoundsProperty().addListener((observable, oldValue, newValue) ->
                zoomPane.setMinSize(newValue.getWidth(), newValue.getHeight()));

        DRAW.setOnAction(event -> {
            DRAW_OPTIONS.setVisible(true);
            STAMP_OPTIONS.setVisible(false);
            NOTES_OPTIONS.setVisible(false);
        });

        STAMP.setOnAction(event -> {
            DRAW_OPTIONS.setVisible(false);
            STAMP_OPTIONS.setVisible(true);
            NOTES_OPTIONS.setVisible(false);
        });

        NOTES.setOnAction(event -> {
            if (!NOTES_OPTIONS.isVisible()) {
                NOTES_OPTIONS.setVisible(true);
                DRAW_OPTIONS.setVisible(false);
                STAMP_OPTIONS.setVisible(false);
                notes_indicator++;
                tools.setMode("NOTES");
            } else {
                NOTES_OPTIONS.setVisible(false);
                DRAW_OPTIONS.setVisible(false);
                STAMP_OPTIONS.setVisible(false);
                notes_indicator--;
                tools.setMode("FREE");
            }
        });

        FILE_MENU.getItems().add(FILE_OPEN);
        FILE_MENU.getItems().add(FILE_SAVE);

        FILE.addEventHandler(javafx.scene.input.MouseEvent.MOUSE_CLICKED, event -> {
            if (event.getButton() == MouseButton.PRIMARY) {
                FILE_MENU.show(FILE, event.getScreenX(), event.getScreenY());
            } else {
                FILE_MENU.hide();
            }
        });

        FILE_OPEN.setOnAction(event -> {
            tools.open();
        });

        FILE_SAVE.setOnAction(event -> {

        });

        PREVIOUS_PAGE.setOnAction(event -> {
            tools.previousPage();
        });

        NEXT_PAGE.setOnAction(event -> {
            tools.nextPage();
        });

    }

    public void lineAction() {
        isNew = true;
        canDraw = true;

        tools.setColor(DRAW_COLOR.getValue());
        tools.setMode("LENGTH");
    }

    public void updateLine(MouseEvent event) {
        try {
            for (double[][] arr : snapList) {

                int a = (int) arr[0][0] - (int) event.getX();
                int b = (int) arr[0][1] - (int) event.getY();

                if ((a >= -3 && a <= 3) && (b >= -3 && b <= 3)) {
                    circle.setCenterX(arr[0][0]);
                    circle.setCenterY(arr[0][1]);
                    circle.setVisible(true);
                    circle.setRadius(3 / group.getScaleY());
                    circle.setOnMouseExited(event1 -> {
                        circle.setVisible(false);
                    });
                    circle.setOnMouseReleased(event1 -> {
                        if (canDraw) {
                            if (snapX == -1 && snapY == -1) {
                                snapX = circle.getCenterX();
                                snapY = circle.getCenterY();
                            }
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

        Point2D cP = clamp(event.getX(), event.getY());
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

    public Point2D clamp(double x, double y) {
        double maxX = canvas.getBoundsInLocal().getMaxX();
        double maxY = canvas.getBoundsInLocal().getMaxY();
        double a = Math.max(0, x);
        double b = Math.max(0, y);
        double c = Math.min(a, maxX);
        double d = Math.min(b, maxY);
        Point2D p = new Point2D(c, d);
        return p;
    }

    public void zoom(ScrollEvent event) {
        if (event.getDeltaY() == 0) {
            return;
        }
        double scaleFactor = (event.getDeltaY() > 0) ? SCALE_DELTA : 1 / SCALE_DELTA;
        // amount of scrolling in each direction in scrollContent coordinate
        // units
        double newScale = group.getScaleX() * scaleFactor;
        if (event.isControlDown()) {
            if (newScale > .2 && newScale < 3.7) {
                group.setScaleX(newScale);
                group.setScaleY(newScale);
                Point2D scrollOffset = figureScrollOffset(scrollContent, scroller);
                repositionScroller(scrollContent, scroller, scaleFactor, scrollOffset);

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
                            sp.setStrokeWidth(8 / group.getScaleY());
                        }
                    }
                    if (node.getClass() == Label.class) {
                        Label lbl = (Label) node;
                        lbl.setFont(new Font("Arial", 18 / group.getScaleY()));
                    }
                });
            }
        }
    }

    private Point2D figureScrollOffset (Node scrollContent, ScrollPane scroller) {
        double extraWidth = scrollContent.getLayoutBounds().getWidth() - scroller.getViewportBounds().getWidth();
        double hScrollProportion = (scroller.getHvalue() - scroller.getHmin()) / (scroller.getHmax() - scroller.getHmin());
        double scrollXOffset = hScrollProportion * Math.max(0, extraWidth);
        double extraHeight = scrollContent.getLayoutBounds().getHeight() - scroller.getViewportBounds().getHeight();
        double vScrollProportion = (scroller.getVvalue() - scroller.getVmin()) / (scroller.getVmax() - scroller.getVmin());
        double scrollYOffset = vScrollProportion * Math.max(0, extraHeight);
        return new Point2D(scrollXOffset, scrollYOffset);
    }

    private void repositionScroller (Node scrollContent, ScrollPane scroller, double scaleFactor, Point2D scrollOffset) {
        double scrollXOffset = scrollOffset.getX();
        double scrollYOffset = scrollOffset.getY();
        double extraWidth = scrollContent.getLayoutBounds().getWidth() - scroller.getViewportBounds().getWidth();
        if (extraWidth > 0) {
            double halfWidth = scroller.getViewportBounds().getWidth() / 2;
            double newScrollXOffset = (scaleFactor - 1) * halfWidth + scaleFactor * scrollXOffset;
            scroller.setHvalue(scroller.getHmin() + newScrollXOffset * (scroller.getHmax() - scroller.getHmin()) / extraWidth);
        } else {
            scroller.setHvalue(scroller.getHmin());
        }
        double extraHeight = scrollContent.getLayoutBounds().getHeight() - scroller.getViewportBounds().getHeight();
        if (extraHeight > 0) {
            double halfHeight = scroller.getViewportBounds().getHeight() / 2;
            double newScrollYOffset = (scaleFactor - 1) * halfHeight + scaleFactor * scrollYOffset;
            scroller.setVvalue(scroller.getVmin() + newScrollYOffset * (scroller.getVmax() - scroller.getVmin()) / extraHeight);
        } else {
            scroller.setHvalue(scroller.getHmin());
        }
    }

    public void handlePan(MouseEvent event) {
        if (event.getButton() == MouseButton.PRIMARY) {
            scroller.setPannable(false);
        } else {
            scroller.setPannable(true);
        }
    }

}
