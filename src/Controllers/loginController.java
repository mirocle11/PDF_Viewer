package Controllers;

import com.jfoenix.controls.JFXButton;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import org.json.JSONObject;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ResourceBundle;

public class loginController implements Initializable {

    public JFXButton LOGIN, CLOSE;
    public TextField USERNAME;
    public PasswordField PASSWORD;
    public static String name = "";
    public static String email = "";

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        CLOSE.setOnAction(event -> {
            workspaceController.closeLoginStage();
        });

        LOGIN.setOnAction(event -> {
            String charset = "UTF-8";

            try {
                URL url = new URL("https://thedraftingzone.com/api/login");
                HttpURLConnection http = (HttpURLConnection)url.openConnection();
                http.setRequestMethod("POST");
                http.setDoOutput(true);
                http.setRequestProperty("Accept", "application/json");
                http.setRequestProperty("Authorization", "Bearer dRCxVMeCrR4IzDGx1VjMBgUQqVmKNXjYeJ0zfjgm");
                http.setRequestProperty("Content-Type", "application/json");

                String data = "{\n  \"username\": \""+USERNAME.getText()+"\",\n  \"password\": "+PASSWORD.getText()+"\n}";

                byte[] out = data.getBytes(StandardCharsets.UTF_8);

                OutputStream stream = http.getOutputStream();
                stream.write(out);

                OutputStream outputStream = http.getOutputStream();
                PrintWriter writer = new PrintWriter(new OutputStreamWriter(outputStream, charset),
                        true);

//                List<String> response = new ArrayList<String>();
//
//                BufferedReader reader = new BufferedReader(new InputStreamReader(http.getInputStream()));
//                String line = null;
//                while ((line = reader.readLine()) != null) {
//                    response.add(line);
//                }

                BufferedReader in = new BufferedReader(new InputStreamReader(http.getInputStream()));
                String inputLine;
                StringBuffer response = new StringBuffer();
                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
                in.close();
                //print in String
                System.out.println(response.toString());
                System.out.println(http.getResponseCode() + " " + http.getResponseMessage());
                http.disconnect();

                if (http.getResponseCode() == 200) {
                    workspaceController.login_identifier = true;

                    JSONObject myResponse = new JSONObject(response.toString());
                    name = myResponse.getString("name");
                    email = myResponse.getString("email");

                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("Success");
                    alert.setHeaderText("Welcome " + name + "!");
                    alert.setContentText("Login successful, you may now proceed.");
                    alert.showAndWait();
                    workspaceController.closeLoginStage();
                }

//            } catch (JSONException exception) {
//                exception.printStackTrace();
            } catch (Exception exception) {
                workspaceController.login_identifier = false;
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error");
                alert.setHeaderText("Login Failed!");
                alert.setContentText("There was an error logging in "+ USERNAME.getText() + "." +
                        " Make sure the user exists and try again.");
                alert.showAndWait();

                name = "";
                email = "";
            }
        });
    }

}
