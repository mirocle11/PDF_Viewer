package Controllers;

import Main.Main;
import Model.PageObject;
import Model.ShapeObject;
import Service.Tools;
import com.jfoenix.controls.JFXButton;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Point2D;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;
import javafx.scene.text.Font;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.WindowEvent;

import java.io.IOException;
import java.io.InputStream;
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
    public HBox DRAW_OPTIONS, STAMP_OPTIONS, NOTES_OPTIONS, WINDOW_BOX, DOOR_BOX, STAMP_BOX;
    public VBox ACCOUNT_DETAILS;
    public JFXButton FILE, PREVIOUS_PAGE, NEXT_PAGE, ROTATE_LEFT, ROTATE_RIGHT,
            DRAW, STAMP, NOTES, LINE, STAMP_DRAG;
    public ColorPicker DRAW_COLOR, NOTES_COLOR, WINDOWS_COLOR, DOORS_COLOR;
    public TextField PAGE, NOTES_TXT, WINDOWS_NO, DOORS_NO;
    public ComboBox<String> STAMP_TYPE, STAMP_COLOR;
    public Label TOTAL_PAGE, NAME, EMAIL;
    public Canvas canvas, pane_canvas;
    public Pane pane;
    public ScrollPane scroller;
    public StackPane zoomPane;
    public Group scrollContent, group;
    public ListView<ImageView> STAMP_LIST;

    public static Stage addDocumentStage; //modal
    public static Stage loginStage;

    public ContextMenu FILE_MENU = new ContextMenu();
    public MenuItem FILE_OPEN = new MenuItem("Open");
    public MenuItem FILE_SAVE = new MenuItem("Save");
    public MenuItem FILE_CREATE_DOC = new MenuItem("Create Doc");
    public MenuItem FILE_LOGIN = new MenuItem("Login");

    private double xOffset = 0;
    private double yOffset = 0;

    public Image[] iconImages;

    //collections
    ArrayList<PageObject> pageObjects = new ArrayList<>();
    boolean issetCanvas = false;
    public int notes_indicator;
    public static int stamp_drag_indicator = 0;
    public static boolean login_identifier = false;

    //collections (2)
    List<Shape> shapeList = new ArrayList<>();
    ArrayList<Point2D> pointList = new ArrayList<>();
    ArrayList<Shape> stampList = new ArrayList<>();
    ArrayList<double[][]> snapList = new ArrayList<>();
    public ArrayList<ShapeObject> shapeObjList = new ArrayList<>();
    private final ObservableList<String> stamp_color = FXCollections.observableArrayList("Blue", "Red", "Green");
    private final ObservableList<String> stamp_type = FXCollections.observableArrayList("Misc", "Windows", "Doors");

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

        STAMP_COLOR.setItems(stamp_color);
        STAMP_TYPE.setItems(stamp_type);

        scroller.viewportBoundsProperty().addListener((observable, oldValue, newValue) ->
                zoomPane.setMinSize(newValue.getWidth(), newValue.getHeight()));

        DRAW.setOnAction(event -> {
            DRAW_OPTIONS.setVisible(true);
            STAMP_OPTIONS.setVisible(false);
            NOTES_OPTIONS.setVisible(false);
            if (!DRAW_OPTIONS.isVisible()) {
                tools.setMode("FREE");
            }
        });

        STAMP.setOnAction(event -> {
            if (!STAMP_OPTIONS.isVisible()) {
                DRAW_OPTIONS.setVisible(false);
                STAMP_OPTIONS.setVisible(true);
                NOTES_OPTIONS.setVisible(false);
//                tools.setMode("STAMP");
                tools.setMode("FREE");
            } else {
                DRAW_OPTIONS.setVisible(false);
                STAMP_OPTIONS.setVisible(false);
                NOTES_OPTIONS.setVisible(false);
                tools.setMode("FREE");
            }
        });
//
//        WINDOW_AUTO.setOnAction(event -> {
//            if (!WINDOW_AUTO.isSelected()) {
//                WINDOWS_NO.setDisable(false);
//            } else {
//                WINDOWS_NO.setDisable(true);
//            }
//        });

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
        FILE_MENU.getItems().add(FILE_CREATE_DOC);
//        FILE_MENU.getItems().add(FILE_LOGIN);

        FILE.addEventHandler(javafx.scene.input.MouseEvent.MOUSE_CLICKED, event -> {
            if (event.getButton() == MouseButton.PRIMARY) {
                FILE_MENU.show(FILE, event.getScreenX(), event.getScreenY());
            } else {
                FILE_MENU.hide();
            }

        });

        FILE_OPEN.setOnAction(event -> {
            try {
                tools.open();
//                loadSaveButton();
            } catch (IOException exception) {
//                exception.printStackTrace();
            }
        });

        FILE_SAVE.setDisable(true);
//       loadSaveButton();

        FILE_SAVE.setOnAction(event -> {
            tools.save();
        });

        PREVIOUS_PAGE.setOnAction(event -> {
            tools.previousPage();
        });

        NEXT_PAGE.setOnAction(event -> {
            tools.nextPage();
        });

        STAMP_TYPE.setOnMouseClicked(event -> {
            if (!STAMP_TYPE.getSelectionModel().isEmpty()) {
                if (STAMP_TYPE.getSelectionModel().getSelectedItem().equals("Misc")) {
                    STAMP_BOX.setVisible(true);
                    WINDOW_BOX.setVisible(false);
                    DOOR_BOX.setVisible(false);
                    tools.setMode("STAMP");
                } else if (STAMP_TYPE.getSelectionModel().getSelectedItem().equals("Windows")) {
                    STAMP_BOX.setVisible(false);
                    WINDOW_BOX.setVisible(true);
                    DOOR_BOX.setVisible(false);
                    tools.setMode("STAMP_WINDOWS");
                } else if (STAMP_TYPE.getSelectionModel().getSelectedItem().equals("Doors")) {
                    STAMP_BOX.setVisible(false);
                    WINDOW_BOX.setVisible(false);
                    DOOR_BOX.setVisible(true);
                }
            }
        });

        STAMP_TYPE.setOnAction(event -> {
            if (!STAMP_TYPE.getSelectionModel().isEmpty()) {
                if (STAMP_TYPE.getSelectionModel().getSelectedItem().equals("Misc")) {
                    STAMP_BOX.setVisible(true);
                    WINDOW_BOX.setVisible(false);
                    DOOR_BOX.setVisible(false);
                    tools.setMode("STAMP");
                } else if (STAMP_TYPE.getSelectionModel().getSelectedItem().equals("Windows")) {
                    STAMP_BOX.setVisible(false);
                    WINDOW_BOX.setVisible(true);
                    DOOR_BOX.setVisible(false);
                    tools.setMode("STAMP_WINDOWS");
                } else if (STAMP_TYPE.getSelectionModel().getSelectedItem().equals("Doors")) {
                    STAMP_BOX.setVisible(false);
                    WINDOW_BOX.setVisible(false);
                    DOOR_BOX.setVisible(true);
                    tools.setMode("STAMP_DOORS");
                }
            }
        });

        STAMP_COLOR.setOnAction(event -> {
            if (!STAMP_COLOR.getSelectionModel().isEmpty()) {
                createIconList(STAMP_COLOR.getSelectionModel().getSelectedItem());
            }
        });

        STAMP_DRAG.setOnAction(event -> {
            if (stamp_drag_indicator == 0) {
                stamp_drag_indicator = 1;
            } else {
                stamp_drag_indicator = 1;
            }
        });

        STAMP_LIST.setOnMouseClicked(event -> {
            tools.setMode("STAMP");
        });

        FILE_CREATE_DOC.setOnAction(event -> {
            try {
                FXMLLoader loader = new FXMLLoader(Main.class.getResource("/Views/addDocument.fxml"));
                AnchorPane pane = loader.load();

                //draggable pop up
                pane.setOnMousePressed(event1 -> {
                    xOffset = event1.getSceneX();
                    yOffset = event1.getSceneY();
                });

                pane.setOnMouseDragged(event1 -> {
                    addDocumentStage.setX(event1.getScreenX() - xOffset);
                    addDocumentStage.setY(event1.getScreenY() - yOffset);
                });

                Scene scene = new Scene(pane);
                scene.setFill(Color.TRANSPARENT);
                addDocumentStage = new Stage();
                addDocumentStage.setScene(scene);
                addDocumentStage.initStyle(StageStyle.UNDECORATED);
                addDocumentStage.initModality(Modality.APPLICATION_MODAL);
                addDocumentStage.initStyle(StageStyle.TRANSPARENT);

                addDocumentStage.showAndWait();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        FILE_LOGIN.setOnAction(event -> {
            try {
                FXMLLoader loader = new FXMLLoader(Main.class.getResource("/Views/loginForm.fxml"));
                AnchorPane pane = loader.load();

                //draggable pop up
                pane.setOnMousePressed(event1 -> {
                    xOffset = event1.getSceneX();
                    yOffset = event1.getSceneY();
                });

                pane.setOnMouseDragged(event1 -> {
                    loginStage.setX(event1.getScreenX() - xOffset);
                    loginStage.setY(event1.getScreenY() - yOffset);
                });

                Scene scene = new Scene(pane);
                scene.setFill(Color.TRANSPARENT);
                loginStage = new Stage();
                loginStage.setScene(scene);
                loginStage.initStyle(StageStyle.UNDECORATED);
                loginStage.initModality(Modality.APPLICATION_MODAL);
                loginStage.initStyle(StageStyle.TRANSPARENT);
                loginStage.setOnHidden(new EventHandler<WindowEvent>() {
                    @Override
                    public void handle(WindowEvent event) {
                        if (login_identifier = true) {
                            setAccountDetails(loginController.name, loginController.email);
                        }
                    }
                });
                loginStage.showAndWait();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    public void loadSaveButton() {
        new Thread(()->{try {
            Thread.sleep(35000);} catch (InterruptedException ex) { ex.printStackTrace();}
            Platform.runLater(() -> FILE_SAVE.setDisable(false));
        }).start();
    }

    public void setAccountDetails(String name, String email) {
        NAME.setText(name);
        EMAIL.setText(email);
    }

    public void createIconList(String color) {
        try {
            STAMP_LIST.getItems().clear();
            if (color.equals("Blue")) {
                String[] iconNames = new String[] { // names of image resource file, in directory stamper_icons
                        "blue_right.png", "blue_down.png", "blue_left.png", "blue_up.png"
                };

                iconImages = new Image[iconNames.length];

                String icon_url = "/Views/stamper_icons/";

                for (int i = 0; i < iconNames.length; i++) {
                    InputStream inputStream = getClass().getResourceAsStream(icon_url + iconNames[i]);
                    Image icon = new Image(inputStream);
                    iconImages[i] = icon;
                    STAMP_LIST.getItems().add(new ImageView(icon));
                }
            }
            else if (color.equals("Red")) {
                String[] iconNames = new String[] { // names of image resource file, in directory stamper_icons
                        "red_right.png", "red_down.png", "red_left.png", "red_up.png"
                };

                iconImages = new Image[iconNames.length];

                String icon_url = "/Views/stamper_icons/";

                for (int i = 0; i < iconNames.length; i++) {
                    InputStream inputStream = getClass().getResourceAsStream(icon_url + iconNames[i]);
                    Image icon = new Image(inputStream);
                    iconImages[i] = icon;
                    STAMP_LIST.getItems().add(new ImageView(icon));
                }
            }
            else if (color.equals("Green")) {
                String[] iconNames = new String[] { // names of image resource file, in directory stamper_icons
                        "green_right.png", "green_down.png", "green_left.png", "green_up.png"
                };

                iconImages = new Image[iconNames.length];

                String icon_url = "/Views/stamper_icons/";

                for (int i = 0; i < iconNames.length; i++) {
                    InputStream inputStream = getClass().getResourceAsStream(icon_url + iconNames[i]);
                    Image icon = new Image(inputStream);
                    iconImages[i] = icon;
                    STAMP_LIST.getItems().add(new ImageView(icon));
                }
            }

            STAMP_LIST.getSelectionModel().select(0);  //The first item in the list is currently selected.
        } catch (Exception e) {
        }
    }

    public void lineAction() {
        isNew = true;
        canDraw = true;

        tools.setColor(DRAW_COLOR.getValue());
        tools.setMode("LENGTH");
    }

    public void freeFormAction() {
        isNew = true;
        canDraw = true;

        tools.setColor(DRAW_COLOR.getValue());
        tools.setMode("FREE_FORM");
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

    static void closeDocumentStage() {
        addDocumentStage.close();
    }

    static void closeLoginStage() {
        loginStage.close();
    }

}
