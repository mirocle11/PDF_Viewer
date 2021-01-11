package Service;

import Main.Main;
import Model.*;
import com.spire.pdf.PdfDocument;
import com.spire.pdf.PdfPageBase;
import com.spire.pdf.graphics.*;
import javafx.embed.swing.SwingFXUtils;
import javafx.geometry.Point2D;
import javafx.scene.canvas.Canvas;
import javafx.scene.shape.Line;
import javafx.stage.FileChooser;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Dimension2D;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

import org.apache.pdfbox.pdmodel.PDDocument;

public class FileService {

    public static FileChooser chooser = new FileChooser();
    public static File pdf, tempPdf;
    public Canvas canvas;

    public static File open() {
        chooser.setTitle("Select File");
        if (pdf != null) {
            tempPdf = pdf;
        }
        tempPdf = chooser.showOpenDialog(Main.mainStage);
        if (tempPdf != null) {
            pdf = tempPdf;
            return pdf;
        } else {
            JOptionPane.showMessageDialog(null, "No File selected", "Error",
                    0, UIManager.getIcon("OptionPane.errorIcon"));
            return null;
        }
    }

    public static void save(ArrayList<PageObject> pageObjects) {
        PdfDocument document;

        chooser.setTitle("Save PDF");

        document = new PdfDocument();
        document.loadFromFile(pdf.getAbsolutePath());

        BufferedImage bufferedImage;
        ArrayList<ShapeObject> shapeObjList;
        ArrayList<StampObject> stampObjectList;
        ArrayList<NotesObject> notesObjectList;
        ArrayList<PolylineObject> polylineObjectList;

        File file = chooser.showSaveDialog(Main.mainStage);
        if (file != null) {
            System.out.println("page object : " + pageObjects.size());
            for (int i = 0; i < pageObjects.size(); i++) {
                PageObject pageObject = pageObjects.get(i);
                PdfPageBase page = document.getPages().get(i);

//                page.getCanvas().setTransparency(0.5f, 0.5f, PdfBlendMode.Normal);
                PdfGraphicsState state = page.getCanvas().save();
                page.getCanvas().translateTransform(0, 0);

                bufferedImage = SwingFXUtils.fromFXImage(pageObject.getImage(), null);

                double subX = bufferedImage.getWidth() / page.getSize().getWidth();
                double subY = bufferedImage.getHeight() / page.getSize().getHeight();

                shapeObjList = pageObjects.get(i).getShapeList();
                shapeObjList.forEach(shapeObject -> {
                    PdfRGBColor pdfRGBColor = new PdfRGBColor(new Color((float) shapeObject.getColor().getRed(),
                            (float) shapeObject.getColor().getGreen(), (float) shapeObject.getColor().getBlue()));
                    PdfFont font = new PdfFont(PdfFontFamily.Helvetica, 8);
                    if (shapeObject.getType().equals("AREA")) {
                        int ndx = 0;
                        PdfBrush brush = new PdfSolidBrush(new PdfRGBColor(pdfRGBColor));
                        java.awt.geom.Point2D[] points = new java.awt.geom.Point2D[shapeObject.getPointList().size()];
                        for (Point2D p2d : shapeObject.getPointList()) {
                            points[ndx] = new java.awt.geom.Point2D.Double(p2d.getX() / subX, (p2d.getY() / subY) - 1.8);
                            ndx++;
                        }
                        page.getCanvas().drawPolygon(brush, points);

                        PdfPen pens = new PdfPen(pdfRGBColor, 1);
                        double layX = shapeObject.getPolygon().getBoundsInParent().getMinX() + (shapeObject.getPolygon().getBoundsInParent().getWidth()) / 2;
                        double layY = shapeObject.getPolygon().getBoundsInParent().getMinY() + (shapeObject.getPolygon().getBoundsInParent().getHeight()) / 2;
                        page.getCanvas().drawString(shapeObject.getArea() + " m²", font, pens, (float) layX / subX, (float) layY / subY);

                    } else if (shapeObject.getType().equals("LENGTH")) {
                        PdfPen pens = new PdfPen(pdfRGBColor, 1);
                        shapeObject.getLineList().forEach(line1 -> {
                            Point2D p1 = new Point2D(line1.getStartX(), line1.getStartY());
                            Point2D p2 = new Point2D(line1.getEndX(), line1.getEndY());
                            Point2D mid = p1.midpoint(p2);
                            double length = (p1.distance(p2) / pageObject.getScale());
                            page.getCanvas().drawString(Math.round(length * 100.0) / 100.0 + " mm", font, pens, (float) mid.getX() / subX, (float) mid.getY() / subY);
                        });
                    }
                    shapeObject.getLineList().forEach(line1 -> {
                        PdfPen pen = new PdfPen(pdfRGBColor, 2);
                        page.getCanvas().drawLine(pen, line1.getStartX() / subX, (line1.getStartY() / subY) - 1.8, line1.getEndX() / subX, (line1.getEndY() / subY) - 1.8);
                    });
                });

                stampObjectList = pageObjects.get(i).getStampObjectList();
                stampObjectList.forEach(stampObject -> {
                    PdfImage pdfImage = PdfImage.fromStream(FileService.class.getResourceAsStream(stampObject.getImagePath()));
                    page.getCanvas().drawImage(pdfImage, stampObject.getStamp().getLayoutX() / subX,
                            (stampObject.getStamp().getLayoutY() / subY) - 1.8, 30, 30);
                });

                notesObjectList = pageObjects.get(i).getNotesObjectList();
                notesObjectList.forEach(notesObject -> {
                    PdfFont font = new PdfFont(PdfFontFamily.Helvetica, 10);
                    PdfRGBColor pdfRGBColor = new PdfRGBColor(new Color((float) notesObject.getColor().getRed(),
                            (float) notesObject.getColor().getGreen(), (float) notesObject.getColor().getBlue()));
                    PdfPen pen = new PdfPen(pdfRGBColor);
                    page.getCanvas().drawRectangle(pen, notesObject.getNotes().getLayoutX() / subX,
                            (notesObject.getNotes().getLayoutY() / subY) -1.8, notesObject.getNotes().getWidth() / 2 - 10,
                            (notesObject.getNotes().getHeight() / 2) - 5);
                    page.getCanvas().drawString(notesObject.getNotes().getText(), font, pen,
                            (notesObject.getNotes().getLayoutX() / subX) + 2,
                            (notesObject.getNotes().getLayoutY() / subY) - 1.8 + 2);
                });

                polylineObjectList = pageObjects.get(i).getPolyLineObjectList();
                polylineObjectList.forEach(polylineObject -> {
                    PdfRGBColor pdfRGBColor = new PdfRGBColor(new Color((float) polylineObject.getColor().getRed(),
                            (float) polylineObject.getColor().getGreen(), (float) polylineObject.getColor().getBlue()));
                    PdfPen pen = new PdfPen(pdfRGBColor);
                    try {
                        for (int ii = 0; ii < polylineObject.getPolyline().getPoints().size(); ii += 2) {
                            page.getCanvas().drawLine(pen, polylineObject.getPointList().get(ii) / subX,
                                    polylineObject.getPointList().get(ii + 1) / subY,
                                    polylineObject.getPointList().get(ii + 2) / subX,
                                    polylineObject.getPointList().get(ii + 3) / subY);
                        }
                    } catch (IndexOutOfBoundsException ignored) {
                    }
                });

                PdfPageBase page1 = document.getPages().get(0);
                Dimension2D size = page1.getActualSize();

                document.getPages().add(size, new PdfMargins(0));

                if (!file.getName().contains(".")) {
                    file = new File(file.getAbsolutePath() + ".pdf");
                    document.saveToFile(file.getAbsolutePath());
                    try {
                        PDDocument doc = PDDocument.load(file);
                        doc.removePage(doc.getNumberOfPages() - 1);
                        doc.save(file);
                        doc.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    String charset = "UTF-8";
                    String requestURL = "https://kylecastillon.dev/~kyle/drafty/api/file";

                    try {
                        MultipartUtility multipart = new MultipartUtility(requestURL, charset);

                        multipart.addFormField("job_id", "1");
                        multipart.addFilePart("file", file);

                        List<String> response = multipart.finish();

                        System.out.println("SERVER REPLIED:");

                        for (String line : response) {
                            System.out.println(line);
                        }
                    } catch (IOException exception) {
                        System.err.println(exception);
                    }
                }
                page.getCanvas().restore(state);
                System.out.println("page " + i);
            }
        } else {
            JOptionPane.showMessageDialog(null, "No File selected");
        }
        document.close();
    }


}
