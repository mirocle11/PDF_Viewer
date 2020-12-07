package Service;

import Main.Main;
import javafx.stage.FileChooser;

import javax.swing.*;
import java.io.File;

public class FileService {

    public static FileChooser chooser = new FileChooser();
    public static File pdf, tempPdf;

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

}
