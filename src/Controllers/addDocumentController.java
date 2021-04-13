package Controllers;

import Main.Main;
import Service.MultipartUtility;
import com.jfoenix.controls.JFXButton;
import javafx.fxml.Initializable;
import javafx.scene.control.TextArea;
import javafx.stage.FileChooser;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;

import javax.swing.*;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

import static Service.Tools.job_id;

public class addDocumentController implements Initializable {

    public JFXButton SAVE_NOTES, CLOSE;
    public TextArea TEXT_AREA;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        SAVE_NOTES.setOnAction(event -> {
            XWPFDocument document = new XWPFDocument();

            XWPFParagraph paragraph = document.createParagraph();
            XWPFRun run = paragraph.createRun();
            run.setText(TEXT_AREA.getText());

            try {
                FileChooser fileChooser = new FileChooser();
                fileChooser.setTitle("Save Notes Document");

                File file = fileChooser.showSaveDialog(workspaceController.addDocumentStage);

                if (file != null) {
                    if (!file.getName().contains(".docx")) {
                        file = new File(file.getAbsolutePath() + ".docx");
                    }
                    FileOutputStream out = new FileOutputStream(file);
                    document.write(out);
                    out.close();

                    String charset = "UTF-8";
                    String requestURL = "https://staging.ptbcsitest.net/api/plan-editor-file";

                    try {
                        MultipartUtility multipart = new MultipartUtility(requestURL, charset);
                        multipart.addFormField("tdz_ref", job_id);
                        multipart.addFilePart("file", file);


                        List<String> response = multipart.finish();

                        System.out.println("SERVER REPLIED:");

                        for (String line : response) {
                            System.out.println(line);
                        }
                    } catch (IOException exception) {
                        exception.printStackTrace();
                    }

                    JOptionPane.showMessageDialog(null, "Notes document exported.",
                            "Success", JOptionPane.INFORMATION_MESSAGE);
                }
            } catch (Exception e) {
                JOptionPane.showMessageDialog(null, "Export error. Document is currently open.",
                        "Error", JOptionPane.ERROR_MESSAGE);
                e.printStackTrace();
            }
        });

        CLOSE.setOnAction(event -> {
            workspaceController.closeDocumentStage();
        });
    }

}
