package Service;
import Controllers.workspaceController;
import Model.*;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Point2D;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.*;
import javafx.scene.text.Font;

import javax.swing.*;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Tools {

    public PageObject page;
    public workspaceController window;

    Group group;
    String mode;
    Pane pane;
    Canvas canvas, pane_canvas;
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
    public static String job_id = "";

    // indicator
    public int notes_id = 0;
    public int stamp_id = 0;
    public int windows_id = 0;
    public int doors_id = 0;
    public int stamp_drag = 0;

    double total;

//    public Color windows_color = new Color(0,0,0,0);
    int w_num = 0;
    int d_num = 0;

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
        this.pane_canvas = window.pane_canvas;
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
    }

    public void open() throws IOException {
//        URL url = new URL("https://drafty-public.s3.ap-southeast-1.amazonaws.com/documents/jobs/00001-House+1/2604-AFH_ALEX+HILLS_vc1.0.2+18+Apr+20.pdf");
//        try (InputStream in = url.openStream()) {
//            Files.copy(in, Paths.get("C:/Users/User/Documents/someFile.pdf"), StandardCopyOption.REPLACE_EXISTING);
//        } catch (IOException e) {
//            // handle exception
//        }

        try {
//            File pdf = new File("C:/Users/User/Documents/someFile.pdf");
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
                        pane_canvas.setWidth(pane.getWidth());
                        pane_canvas.setHeight(pane.getHeight());

                        Label lbl = new Label();
                        lbl.setBackground(new Background(new BackgroundFill(Color.WHITE, CornerRadii.EMPTY, Insets.EMPTY)));
                        lbl.setMinWidth(1700);
                        lbl.setMinHeight(50);
                        lbl.setLayoutX(10);
                        lbl.setLayoutY(-5);
                        pane.getChildren().add(lbl);

                        try {
                            job_id = JOptionPane.showInputDialog("Enter Job Id", "");
                        } catch (Exception e) {
                            JOptionPane.showMessageDialog(null, "Please enter a valid number.",
                                    "Invalid job id value", JOptionPane.ERROR_MESSAGE);
                        }

                        window.PAGE.setText(String.valueOf(pageNumber + 1));
                        window.TOTAL_PAGE.setText("of " + pageObjects.size());

                        window.loadSaveButton();
                    }
                }
            });
            service.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void save() {
        FileService.save(pageObjects);
    }

    public void setMode(String mode) {
        EventHandler<MouseEvent> onMouseClickedEventHandlerStamp = event -> {
            if (event.getButton() == MouseButton.PRIMARY && mode.equals("STAMP")) {
                ImageView stamp_img = new ImageView();
                StampObject stampObject = new StampObject();

                int selected_icon = window.STAMP_LIST.getSelectionModel().getSelectedIndex();

                switch (window.STAMP_COLOR.getSelectionModel().getSelectedItem()) {
                    case "Blue":
                        switch (selected_icon) {
                            case 0:
                                stamp_img.setImage(new Image(getClass().getResourceAsStream("/Views/stamper_icons/blue_right1.png")));
                                stampObject.setImagePath("/Views/stamper_icons/blue_right1.png");
                                break;
                            case 1:
                                stamp_img.setImage(new Image(getClass().getResourceAsStream("/Views/stamper_icons/blue_down1.png")));
                                stampObject.setImagePath("/Views/stamper_icons/blue_down1.png");
                                break;
                            case 2:
                                stamp_img.setImage(new Image(getClass().getResourceAsStream("/Views/stamper_icons/blue_left1.png")));
                                stampObject.setImagePath("/Views/stamper_icons/blue_left1.png");
                                break;
                            case 3:
                                stamp_img.setImage(new Image(getClass().getResourceAsStream("/Views/stamper_icons/blue_up1.png")));
                                stampObject.setImagePath("/Views/stamper_icons/blue_up1.png");
                                break;
                        }
                        break;
                    case "Red":
                        switch (selected_icon) {
                            case 0:
                                stamp_img.setImage(new Image(getClass().getResourceAsStream("/Views/stamper_icons/red_right1.png")));
                                stampObject.setImagePath("/Views/stamper_icons/red_right1.png");
                                break;
                            case 1:
                                stamp_img.setImage(new Image(getClass().getResourceAsStream("/Views/stamper_icons/red_down1.png")));
                                stampObject.setImagePath("/Views/stamper_icons/red_down1.png");
                                break;
                            case 2:
                                stamp_img.setImage(new Image(getClass().getResourceAsStream("/Views/stamper_icons/red_left1.png")));
                                stampObject.setImagePath("/Views/stamper_icons/red_left1.png");
                                break;
                            case 3:
                                stamp_img.setImage(new Image(getClass().getResourceAsStream("/Views/stamper_icons/red_up1.png")));
                                stampObject.setImagePath("/Views/stamper_icons/red_up1.png");
                                break;
                        }
                        break;
                    case "Green":
                        switch (selected_icon) {
                            case 0:
                                stamp_img.setImage(new Image(getClass().getResourceAsStream("/Views/stamper_icons/green_right1.png")));
                                stampObject.setImagePath("/Views/stamper_icons/green_right1.png");
                                break;
                            case 1:
                                stamp_img.setImage(new Image(getClass().getResourceAsStream("/Views/stamper_icons/green_down1.png")));
                                stampObject.setImagePath("/Views/stamper_icons/green_down1.png");
                                break;
                            case 2:
                                stamp_img.setImage(new Image(getClass().getResourceAsStream("/Views/stamper_icons/green_left1.png")));
                                stampObject.setImagePath("/Views/stamper_icons/green_left1.png");
                                break;
                            case 3:
                                stamp_img.setImage(new Image(getClass().getResourceAsStream("/Views/stamper_icons/green_up1.png")));
                                stampObject.setImagePath("/Views/stamper_icons/green_up1.png");
                                break;
                        }
                        break;
                }

                Label stamp = new Label();
                stamp.setLayoutX(event.getX());
                stamp.setLayoutY(event.getY());
                stamp.setGraphic(stamp_img);

                pane.getChildren().add(stamp);

                System.out.println("stamp path: " + stampObject.getImagePath());
                stampObject.setStamp(stamp);
                stamp_id++;
                stampObject.setStamp_no(stamp_id);

                stamp.setOnMouseClicked(event1 -> {
                    if (event1.getButton() == MouseButton.SECONDARY) {
                        stampMenu = new ContextMenu();
                        stampMenu.hide();

                        MenuItem removeStamp = new MenuItem("Remove Stamp");
                        removeStamp.setOnAction(event2 -> {
                            stamp.setVisible(false);
                        });
                        stampMenu.getItems().add(removeStamp);
                        stampMenu.show(stamp, event1.getScreenX(), event1.getScreenY());
                    }
                });
                MouseGestures mg = new MouseGestures();
                mg.makeDraggable(stamp);

                page.getStampObjectList().add(stampObject);
            }
        };
        EventHandler<MouseEvent> onMouseClickedEventHandlerNotes = event -> {
            if (event.getButton() == MouseButton.PRIMARY && mode.equals("NOTES")) {
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
                notesObject.setColor(window.NOTES_COLOR.getValue());
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
                MouseGestures mg = new MouseGestures();
                mg.makeDraggable(notes);

                page.getNotesObjectList().add(notesObject);
            }
        };
        EventHandler<MouseEvent> onMouseClickedEventHandlerWindows = event -> {
            if (event.getButton() == MouseButton.PRIMARY && mode.equals("STAMP_WINDOWS")) {

                if (!window.WINDOWS_NO.getText().equals("")) {
                    w_num = Integer.parseInt(window.WINDOWS_NO.getText());
                }

                String content = "";
                if (!window.WINDOWS_WIDTH.getText().equals("") || !window.WINDOWS_HEIGHT.getText().equals("")) {
                    content = "       W" + w_num + "\n        " + window.WINDOWS_WIDTH.getText() +
                            " x " + window.WINDOWS_HEIGHT.getText() + "        ";
                } else {
                    content = "       W" + w_num;
                }

                Label window_lbl = new Label();
                window_lbl.setText(content);
                window_lbl.setTextFill(window.WINDOWS_COLOR.getValue());
                window_lbl.setFont(new Font("Segoe UI", 32));
                window_lbl.setWrapText(true);
                window_lbl.setMaxWidth(550);
                window_lbl.setStyle("-fx-border-color: gray;");
                window_lbl.setAlignment(Pos.CENTER);
                window_lbl.setLayoutX(event.getX() - 50);
                window_lbl.setLayoutY(event.getY() - 40);

                pane.getChildren().add(window_lbl);

                WindowsObject windowsObject = new WindowsObject();
                windowsObject.setWindows(window_lbl);
                windowsObject.setColor(window.WINDOWS_COLOR.getValue());
                windowsObject.setWindowsNo(windows_id++);

                window_lbl.setOnMouseClicked(event1 -> {
                    if (event1.getButton() == MouseButton.SECONDARY) {
                        stampMenu = new ContextMenu();
                        stampMenu.hide();

                        MenuItem removeStamp = new MenuItem("Remove Stamp");
                        removeStamp.setOnAction(event2 -> {
                            window_lbl.setVisible(false);
                        });
                        stampMenu.getItems().add(removeStamp);
                        stampMenu.show(window_lbl, event1.getScreenX(), event1.getScreenY());
                    }
                });
                MouseGestures mg = new MouseGestures();
                mg.makeDraggable(window_lbl);

                page.getWindowsObjectList().add(windowsObject);
            }
        };
        EventHandler<MouseEvent> onMouseClickedEventHandlerDoors = event -> {
            if (event.getButton() == MouseButton.PRIMARY && mode.equals("STAMP_DOORS")) {

                if (!window.DOORS_NO.getText().equals("")) {
                    d_num = Integer.parseInt(window.DOORS_NO.getText());
                }

                String content = "";
                if (!window.DOORS_WIDTH.getText().equals("") || !window.DOORS_HEIGHT.getText().equals("")) {
                    content = "       D" + d_num + "\n        " + window.DOORS_WIDTH.getText() +
                            " x " + window.DOORS_HEIGHT.getText() + "        ";
                } else {
                    content = "       D" + d_num;
                }

                Label doors = new Label();
                doors.setText(content);
                doors.setTextFill(window.DOORS_COLOR.getValue());
                doors.setFont(new Font("Segoe UI", 32));
                doors.setWrapText(true);
                doors.setMaxWidth(550);
                doors.setStyle("-fx-border-color: #ff0000;");
                doors.setAlignment(Pos.CENTER);
                doors.setLayoutX(event.getX() - 50);
                doors.setLayoutY(event.getY() - 40);

                pane.getChildren().add(doors);

                DoorsObject doorsObject = new DoorsObject();
                doorsObject.setDoors(doors);
                doorsObject.setColor(window.DOORS_COLOR.getValue());
                doorsObject.setDoorsNo(doors_id++);

                doors.setOnMouseClicked(event1 -> {
                    if (event1.getButton() == MouseButton.SECONDARY) {
                        stampMenu = new ContextMenu();
                        stampMenu.hide();

                        MenuItem removeStamp = new MenuItem("Remove Stamp");
                        removeStamp.setOnAction(event2 -> {
                            doors.setVisible(false);
                        });
                        stampMenu.getItems().add(removeStamp);
                        stampMenu.show(doors, event1.getScreenX(), event1.getScreenY());
                    }
                });
                MouseGestures mg = new MouseGestures();
                mg.makeDraggable(doors);

                page.getDoorsObjectList().add(doorsObject);
            }
        };

        switch (mode) {
            case "FREE":
                this.canDraw = false;
                pane_canvas.setVisible(false);
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
                this.pane.setOnMouseReleased(event -> {
                    //do nothing
                });
                pane_canvas.setVisible(false);
                Length length = new Length(this);
                break;
            case "NOTES":
                this.canDraw = false;
                pane_canvas.setVisible(false);
                pane.setOnMouseReleased(onMouseClickedEventHandlerNotes);
                break;
            case "STAMP":
                this.canDraw = false;
                pane_canvas.setVisible(false);
                pane.setOnMouseReleased(onMouseClickedEventHandlerStamp);
                break;
            case "STAMP_WINDOWS":
                this.canDraw = false;
                pane_canvas.setVisible(false);
                pane.setOnMouseReleased(onMouseClickedEventHandlerWindows);
                break;
            case "STAMP_DOORS":
                this.canDraw = false;
                pane_canvas.setVisible(false);
                pane.setOnMouseReleased(onMouseClickedEventHandlerDoors);
                break;

            case "FREE_FORM":
                this.pane.setOnMouseReleased(event -> {
                    //do nothing
                });
                pane_canvas.setVisible(true);
                GraphicsContext g = pane_canvas.getGraphicsContext2D();
                ArrayList<Double> points = new ArrayList<>();
                pane_canvas.setOnMousePressed(event -> {
                    points.clear();
                    if (event.getButton() == MouseButton.PRIMARY) {
                        double size = 7;
                        double x = event.getX() - size / 2;
                        double y = event.getY() - size / 2;

                        g.beginPath();
                        g.moveTo(x, y);
                        g.stroke();
                        g.setLineWidth(size);
                        g.setStroke(window.DRAW_COLOR.getValue());

                        points.add(x);
                        points.add(y);
                    }
                });
                pane_canvas.setOnMouseDragged(event -> {
                    if (event.getButton() == MouseButton.PRIMARY) {
                        double size = 7;
                        double x = event.getX() - size / 2;
                        double y = event.getY() - size / 2;

                        g.lineTo(x, y);
                        g.stroke();
                        points.add(x);
                        points.add(y);
                    }
                });

                pane_canvas.setOnMouseClicked(event -> {
                    if (event.getButton() == MouseButton.PRIMARY) {
                        double size = 7;
                        double x = event.getX() - size / 2;
                        double y = event.getY() - size / 2;
                        points.add(x);
                        points.add(y);

                        Polyline polyline = new Polyline();
                        for (Double point : points) {
                            polyline.getPoints().add(point);
                        }

                        polyline.setStroke(window.DRAW_COLOR.getValue());
                        polyline.setStrokeWidth(size);

                        pane.getChildren().add(polyline);
                        g.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());

                        PolylineObject polylineObject = new PolylineObject();
                        polylineObject.setPolyline(polyline);
                        polylineObject.setColor(window.DRAW_COLOR.getValue());
                        polylineObject.setPointList(points);
                        page.getPolyLineObjectList().add(polylineObject);

                        polyline.setOnMouseClicked(event1 -> {
                            if (event1.getButton().equals(MouseButton.SECONDARY)) {
                                stampMenu = new ContextMenu();
                                stampMenu.hide();

                                MenuItem removeLine = new MenuItem("Remove Line");
                                removeLine.setOnAction(event2 -> {
                                    polyline.setVisible(false);
                                });
                                stampMenu.getItems().add(removeLine);
                                stampMenu.show(polyline, event1.getScreenX(), event1.getScreenY());
                            }
                        });

                        pane_canvas.setOnMouseReleased(event1 -> {
                            if (event.getButton().equals(MouseButton.PRIMARY)) {
                                setMode("FREE");
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
            window.PAGE.setText(String.valueOf(pageNumber + 1));
            window.TOTAL_PAGE.setText("of " + pageObjects.size());
            setPageElements();
            setMode("FREE");

            Label lbl = new Label();
            lbl.setBackground(new Background(new BackgroundFill(Color.WHITE, CornerRadii.EMPTY, Insets.EMPTY)));
            lbl.setMinWidth(1700);
            lbl.setMinHeight(50);
            lbl.setLayoutX(10);
            lbl.setLayoutY(-5);
            pane.getChildren().add(lbl);
        }
    }

    public void previousPage() {
        if (pageNumber > 0) {
            pageNumber--;
            window.PAGE.setText(String.valueOf(pageNumber + 1));
            window.TOTAL_PAGE.setText("of " + pageObjects.size());
            setPageElements();
            setMode("FREE");

            Label lbl = new Label();
            lbl.setBackground(new Background(new BackgroundFill(Color.WHITE, CornerRadii.EMPTY, Insets.EMPTY)));
            lbl.setMinWidth(1700);
            lbl.setMinHeight(50);
            lbl.setLayoutX(10);
            lbl.setLayoutY(-5);
            pane.getChildren().add(lbl);
        }
    }

    public void updateWindow() {
        pane.getChildren().clear();
        pane.getChildren().add(circle);
        circle.setVisible(false);

        for (ShapeObject sp1 : page.getShapeList()) {
            if (sp1.getType().equals("LENGTH")) {
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
//                        pane.getChildren().add(lbl);
                        total = total + sp1.getLength();
                    }
                }
                sp1.setLength(Math.round(total * 100.0) / 100.0);
                total = 0.0;
            }
            pane.getChildren().addAll(sp1.getLineList());
//            pane.getChildren().addAll(sp1.getBoxList());
        }

        for (PolylineObject polylineObject : page.getPolyLineObjectList()) {
            pane.getChildren().addAll(polylineObject.getPolyline());
        }

        for (StampObject stampObject : page.getStampObjectList()) {
            pane.getChildren().addAll(stampObject.getStamp());
        }

        for (NotesObject notesObject : page.getNotesObjectList()) {
            pane.getChildren().addAll(notesObject.getNotes());
        }

        for (WindowsObject windowsObject : page.getWindowsObjectList()) {
            pane.getChildren().addAll(windowsObject.getWindows());
        }

        for (DoorsObject doorsObject : page.getDoorsObjectList()) {
            pane.getChildren().addAll(doorsObject.getDoors());
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

        Label lbl = new Label();
        lbl.setBackground(new Background(new BackgroundFill(Color.WHITE, CornerRadii.EMPTY, Insets.EMPTY)));
        lbl.setMinWidth(1700);
        lbl.setMinHeight(50);
        lbl.setLayoutX(10);
        lbl.setLayoutY(-5);
        pane.getChildren().add(lbl);
    }

    public class MouseGestures {

        class DragContext {
            double x;
            double y;
        }

        DragContext dragContext = new DragContext();

        public void makeDraggable(Node node) {
            node.setOnMousePressed(onMousePressedEventHandler);
            node.setOnMouseDragged(onMouseDraggedEventHandler);
        }

        EventHandler<MouseEvent> onMousePressedEventHandler = event -> {
            if (event.getButton().equals(MouseButton.PRIMARY)) {
                pane.setOnMouseReleased(event1 -> {

                });
                Node node = ((Node) (event.getSource()));

                dragContext.x = node.getTranslateX() - event.getSceneX();
                dragContext.y = node.getTranslateY() - event.getSceneY();
            }
        };

        EventHandler<MouseEvent> onMouseDraggedEventHandler = event -> {
            if (event.getButton().equals(MouseButton.PRIMARY)) {
                Node node = ((Node) (event.getSource()));

                node.setTranslateX(dragContext.x + event.getSceneX());
                node.setTranslateY(dragContext.y + event.getSceneY());
            }

            window.NOTES_OPTIONS.setVisible(false);
            window.STAMP_BOX.setVisible(false);
            window.WINDOW_BOX.setVisible(false);
        };
    }

//    public Object getLastElement(Collection collection) {
//        Iterator itr = collection.iterator();
//        Object lastElement = itr.next();
//        while (itr.h
//        sNext()) {
//            lastElement = itr.next();
//        }
//        return lastElement;
//    }
}
