package Service;

import Controllers.Loader;
import Model.PageObject;
import com.spire.pdf.FileFormat;
import com.spire.pdf.PdfDocument;
import com.spire.pdf.graphics.PdfImageType;
import com.spire.pdf.widget.PdfPageCollection;
import javafx.concurrent.Service;
import javafx.concurrent.Task;

import java.awt.image.BufferedImage;
import java.io.File;

public class PdfToImageService extends Service<PageObject> {
    File pdf;
    int length;
    PdfDocument document;
    PdfPageCollection pages;

    File tempDirectory, imgDir, svgDir, pdfDir, file, pdfFile;
    File[] pdfArr, imgArr, svgArr;

    public PdfToImageService(File param) {
        createDirectories();
        this.pdf = param;
        document = new PdfDocument();
        document.loadFromFile(pdf.getAbsolutePath());
        length = document.getPages().getCount();
    }

    @Override
    protected Task<PageObject> createTask() {
        return new Task<PageObject>() {
            @Override
            protected PageObject call() throws Exception {
                for (int i = 0; i < length; i++) {
                    BufferedImage bufferedImage = document.saveAsImage(i, PdfImageType.Bitmap, 300, 300);
                    document.saveToFile(tempDirectory.getAbsolutePath() + "/svg_0.svg", i, i, FileFormat.SVG);
                    Loader load = new Loader(tempDirectory.getAbsolutePath() + "/svg_0.svg");
                    load.width = bufferedImage.getWidth();
                    load.height = bufferedImage.getHeight();
                    PageObject obj = new PageObject(i, bufferedImage);
                    obj.setSnapList(load.getSnap());
                    updateValue(obj);
                }
                return null;
            }
        };
    }

    public void createDirectories() {
        tempDirectory = new File("temp/"); //temp directory address
        if (!tempDirectory.exists()) {
            try {
                tempDirectory.mkdir();
            } catch (SecurityException se) {

            }

        }

        imgDir = new File(tempDirectory, "images");
        if (!imgDir.exists()) {
            try {
                imgDir.mkdir();
            } catch (SecurityException se) {

            }
        }

        svgDir = new File(tempDirectory, "svg");
        if (!svgDir.exists()) {
            try {
                svgDir.mkdir();
            } catch (SecurityException se) {

            }
        }
        pdfDir = new File(tempDirectory, "pdf");
        if (!pdfDir.exists()) {
            try {
                pdfDir.mkdir();
            } catch (SecurityException se) {

            }
        }
        for (File file1 : pdfDir.listFiles()) file1.delete();
        for (File file1 : imgDir.listFiles()) file1.delete();
        for (File file1 : svgDir.listFiles()) file1.delete();

    }
}
