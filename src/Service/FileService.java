package Service;

import Controllers.loginController;
import Main.Main;
import Model.*;
import com.spire.pdf.PdfCompressionLevel;
import com.spire.pdf.PdfDocument;
import com.spire.pdf.PdfPageBase;
import com.spire.pdf.graphics.*;
import javafx.embed.swing.SwingFXUtils;
import javafx.geometry.Point2D;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.Alert;
import javafx.stage.FileChooser;
import org.apache.pdfbox.pdmodel.PDDocument;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Dimension2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static Service.Tools.job_id;

public class FileService {

    public static FileChooser chooser = new FileChooser();
    public static File pdf, tempPdf;
    private static String file_name;
    public Canvas canvas;
    public static Tools tools;

    public static File open() {
        chooser.setTitle("Select File");
        if (pdf != null) {
            tempPdf = pdf;
        }
        tempPdf = chooser.showOpenDialog(Main.loginStage);
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
        PdfDocument doc = new PdfDocument();
        chooser.setTitle("Save PDF");

        doc.setCompressionLevel(PdfCompressionLevel.None);
        doc.loadFromFile(pdf.getAbsolutePath());

        BufferedImage bufferedImage;
        ArrayList<ShapeObject> shapeObjList;
        ArrayList<StampObject> stampObjectList;
        ArrayList<NotesObject> notesObjectList;
        ArrayList<WindowsObject> windowsObjectList;
        ArrayList<DoorsObject> doorsObjectList;
        ArrayList<PolylineObject> polylineObjectList;

        File file = chooser.showSaveDialog(Main.loginStage);
        if (file != null) {
            for (int i = 0; i < doc.getPages().getCount(); i++) {
                PageObject pageObject = pageObjects.get(i);
                PdfPageBase page = doc.getPages().get(i);
                page.getCanvas().setTransparency(0.5f, 0.5f, PdfBlendMode.Normal);
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
                    if (shapeObject.getType().equals("LENGTH")) {
                        PdfPen pens = new PdfPen(pdfRGBColor, 1);
                        shapeObject.getLineList().forEach(line1 -> {
                            Point2D p1 = new Point2D(line1.getStartX(), line1.getStartY());
                            Point2D p2 = new Point2D(line1.getEndX(), line1.getEndY());
                            Point2D mid = p1.midpoint(p2);
                            double length = (p1.distance(p2) / pageObject.getScale());
//                            page.getCanvas().drawString(Math.round(length * 100.0) / 100.0 + " mm", font, pens, (float) mid.getX() / subX, (float) mid.getY() / subY);
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
                            (notesObject.getNotes().getLayoutY() / subY) - 1.8, notesObject.getNotes().getWidth() / 2 - 10,
                            (notesObject.getNotes().getHeight() / 2) - 5);
                    page.getCanvas().drawString(notesObject.getNotes().getText(), font, pen,
                        (notesObject.getNotes().getLayoutX() / subX) + 2,
                        (notesObject.getNotes().getLayoutY() / subY) - 1.8 + 2);
            });

                windowsObjectList = pageObjects.get(i).getWindowsObjectList();
                windowsObjectList.forEach(windowsObject -> {
                    PdfFont font = new PdfFont(PdfFontFamily.Helvetica, 10);
                    PdfRGBColor pdfRGBColor = new PdfRGBColor(new Color((float) windowsObject.getColor().getRed(),
                            (float) windowsObject.getColor().getGreen(), (float) windowsObject.getColor().getBlue()));
                    PdfPen pen = new PdfPen(pdfRGBColor);
                    page.getCanvas().drawRectangle(pen, windowsObject.getWindows().getLayoutX() / subX,
                            (windowsObject.getWindows().getLayoutY() / subY) - 1.8, windowsObject.getWindows().getWidth() / 2 - 10,
                            (windowsObject.getWindows().getHeight() / 2) - 5);

                    page.getCanvas().drawString(windowsObject.getWindows().getText(), font, pen,
                            (windowsObject.getWindows().getLayoutX() / subX) + 2,
                            (windowsObject.getWindows().getLayoutY() / subY) - 1.8 + 2);
                });

                doorsObjectList = pageObjects.get(i).getDoorsObjectList();
                doorsObjectList.forEach(doorsObject -> {
                    PdfFont font = new PdfFont(PdfFontFamily.Helvetica, 10);
                    PdfRGBColor pdfRGBColor = new PdfRGBColor(new Color((float) doorsObject.getColor().getRed(),
                            (float) doorsObject.getColor().getGreen(), (float) doorsObject.getColor().getBlue()));
                    PdfPen pen = new PdfPen(pdfRGBColor);
                    page.getCanvas().drawRectangle(pen, doorsObject.getDoors().getLayoutX() / subX,
                            (doorsObject.getDoors().getLayoutY() / subY) - 1.8, doorsObject.getDoors().getWidth() / 2 - 10,
                            (doorsObject.getDoors().getHeight() / 2) - 5);
                    page.getCanvas().drawString(doorsObject.getDoors().getText(), font, pen,
                            (doorsObject.getDoors().getLayoutX() / subX) + 2,
                            (doorsObject.getDoors().getLayoutY() / subY) - 1.8 + 2);
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

                //Restore graphics
                page.getCanvas().restore(state);
            }

            // Save pdf file
            PdfPageBase page1 = doc.getPages().get(0);
            Dimension2D size = page1.getActualSize();

            doc.getPages().add(size, new PdfMargins(0));

            if (!file.getName().contains(".")) {
                file = new File(file.getAbsolutePath() + ".pdf");
                doc.saveToFile(file.getAbsolutePath());
                try {
                    PDDocument doc1 = PDDocument.load(file);
                    doc1.removePage(doc1.getNumberOfPages() - 1);
                    doc1.save(file);
                    doc1.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                String charset = "UTF-8";
                String requestURL = "https://thedraftingzone.com/api/plan-editor-file";

                try {
                    MultipartUtility multipart = new MultipartUtility(requestURL, charset);
                    multipart.addFormField("tdz_ref", job_id);
                    multipart.addFormField("user", loginController.name);
                    multipart.addFilePart("file", file);

                    List<String> response = multipart.finish();

                    System.out.println("SERVER REPLIED:");

                    for (String line : response) {
                        System.out.println(line);
                    }

                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("Success");
                    alert.setHeaderText("File Upload!");
                    alert.setContentText("File uploaded successfully.");
                    alert.showAndWait();
                } catch (IOException exception) {
                    exception.printStackTrace();
                }
            }
        } else {
            JOptionPane.showMessageDialog(null, "No File selected");
        }
        doc.close();
    }
}
