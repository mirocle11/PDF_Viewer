package Controllers;

import Main.Main;
import Model.PageObject;
import Service.FileService;
import Service.PdfToImageService;
import Service.Tools;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextField;
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
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;
import javafx.scene.text.Font;
import javafx.stage.Stage;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class workspaceController implements Initializable {

    public Main main;
    public Stage stage;

    public void setMain(Stage stage, Main main) {
        this.main = main;
        this.stage = stage;
    }

    //components
    public HBox DRAW_OPTIONS, STAMP_OPTIONS;
    public JFXButton FILE, START_PAGE, PREVIOUS_PAGE, NEXT_PAGE, END_PAGE, ROTATE_LEFT, ROTATE_RIGHT,
            DRAW, STAMP, COMMENT, NOTES, LINE;
    public ColorPicker DRAW_COLOR;
    public TextField PAGE;
    public Label TOTAL_PAGE;
    public Canvas CANVAS;
    public Pane PANE;
    public ScrollPane SCROLL_PANE;
    public StackPane ZOOM_PANE;
    public Group SCROLL_CONTENT, GROUP;

    public ContextMenu FILE_MENU = new ContextMenu();
    public MenuItem FILE_OPEN = new MenuItem("Open");
    public MenuItem FILE_SAVE = new MenuItem("Save");

    //collections
    ArrayList<PageObject> pageObjects = new ArrayList<>();
    boolean issetCanvas = false;

    //page
    public PageObject page;
    int pageNumber = 0;
    ArrayList<double[][]> snapList = new ArrayList<>();

    //scale
    double SCALE_DELTA = 1.1;

    //booleans
    private boolean isNew = true;
    private boolean canDraw = true;

    //static classes
    public Tools tools;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        tools = new Tools(this);
        tools.setMode("FREE");

        SCROLL_PANE.viewportBoundsProperty().addListener((observable, oldValue, newValue) ->
                ZOOM_PANE.setMinSize(newValue.getWidth(), newValue.getHeight()));

        DRAW.setOnAction(event -> {
            DRAW_OPTIONS.setVisible(true);
            STAMP_OPTIONS.setVisible(false);
        });

        STAMP.setOnAction(event -> {
            DRAW_OPTIONS.setVisible(false);
            STAMP_OPTIONS.setVisible(true);
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
            try {
                File pdf = FileService.open();
                PdfToImageService service = new PdfToImageService(pdf);
                service.valueProperty().addListener((observable, oldValue, newValue) -> {
                    if (newValue != null) {
                        pageObjects.add(newValue);
                        if (!issetCanvas) {
                            page = newValue;
                            setPageElements();
                            SCROLL_PANE.setOpacity(1.0);
                            issetCanvas = true;
                        }
                    }
                });
                service.start();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        FILE_SAVE.setOnAction(event -> {

        });

        PREVIOUS_PAGE.setOnAction(event -> {
            if (pageNumber > 0) {
                pageNumber--;
                setPageElements();
                tools.setMode("FREE");
            }
        });

        NEXT_PAGE.setOnAction(event -> {
            if (pageNumber < pageObjects.size() - 1) {
                pageNumber++;
                setPageElements();
                tools.setMode("FREE");
            }
        });
    }

    public void lineAction() {
        isNew = true;
        canDraw = true;

        tools.setColor(DRAW_COLOR.getValue());
        tools.setMode("LINE");
    }

    public void zoom(ScrollEvent event) {
        if (event.getDeltaY() == 0) {
            return;
        }
        double scaleFactor = (event.getDeltaY() > 0) ? SCALE_DELTA : 1 / SCALE_DELTA;
        // amount of scrolling in each direction in scrollContent coordinate
        // units
        double newScale = GROUP.getScaleX() * scaleFactor;
        if (event.isControlDown()) {
            if (newScale > .2 && newScale < 3.7) {
                GROUP.setScaleX(newScale);
                GROUP.setScaleY(newScale);
                Point2D scrollOffset = figureScrollOffset(SCROLL_CONTENT, SCROLL_PANE);
                repositionScroller(SCROLL_CONTENT, SCROLL_PANE, scaleFactor, scrollOffset);

                PANE.getChildren().forEach(node -> {
                    if (node.getClass().getSuperclass() == Shape.class) {
                        Shape sp = (Shape) node;
                        if (sp.getClass() == Rectangle.class) {
                            Rectangle rect = (Rectangle) sp;
                            double centerX = rect.getX() + rect.getWidth() / 2;
                            double centerY = rect.getY() + rect.getHeight() / 2;
                            rect.setStrokeWidth(4 / GROUP.getScaleY());
                            rect.setX(centerX - 5 / GROUP.getScaleY());
                            rect.setY(centerY - 5 / GROUP.getScaleY());
                            rect.setWidth(10 / GROUP.getScaleY());
                            rect.setHeight(10 / GROUP.getScaleY());
                        } else {
                            sp.setStrokeWidth(8 / GROUP.getScaleY());
                        }
                    }
                    if (node.getClass() == Label.class) {
                        Label lbl = (Label) node;
                        lbl.setFont(new Font("Arial", 18 / GROUP.getScaleY()));
                    }
                });
            }
        }
    }

    public void setPageElements() {
//        shapeObjList = new ArrayList<>();
//        stampList = new ArrayList<>();
        snapList = new ArrayList<>();
        page = pageObjects.get(pageNumber);
//        shapeObjList.addAll(page.getShapeList());
//        stampList.addAll(page.getStampList());
        snapList.addAll(page.getSnapList());
        setCanvas();
    }

    public void setCanvas() {
        CANVAS.setWidth(page.getImage().getWidth());
        CANVAS.setHeight(page.getImage().getHeight());
        PANE.setPrefSize(CANVAS.getWidth(), CANVAS.getHeight());
        GraphicsContext gc = CANVAS.getGraphicsContext2D();
        gc.clearRect(0, 0, CANVAS.getWidth(), CANVAS.getHeight());
        gc.drawImage(page.getImage(), 0, 0);
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
            SCROLL_PANE.setPannable(false);
        } else {
            SCROLL_PANE.setPannable(true);
        }
    }

}
