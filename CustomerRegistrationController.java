import javafx.fxml.FXML;
import javafx.scene.control.*;
import java.io.File;

public class CustomerRegistrationController {

    @FXML private TextField txtFirstName, txtLastName, txtAddress, txtOccupation;
    @FXML private ComboBox<String> cmbAccountType, cmbBranch;
    @FXML private Button btnRegister;

    private BankSystem bankSystem;

    @FXML
    public void initialize() {
        bankSystem = new BankSystem();
        bankSystem.loadUsersFromFile();
        cmbAccountType.getItems().addAll("Savings", "Cheque", "Investment");
        cmbBranch.getItems().addAll("Gaborone", "Francistown", "Maun", "Palapye");
    }

    @FXML
    private void handleRegister() {
        String first = txtFirstName.getText();
        String last = txtLastName.getText();
        String username = "C" + (int)(Math.random() * 10000);
        String password = "123";

        saveUserToFile(username, password, username);
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Customer Created");
        alert.setHeaderText("Registration successful");
        alert.setContentText("Username: " + username + "\nPassword: 123");
        alert.showAndWait();
    }

    private void saveUserToFile(String username, String password, String customerID) {
        try {
            String filePath = System.getProperty("user.dir") + File.separator + "users.txt";
            java.io.FileWriter writer = new java.io.FileWriter(filePath, true);
            writer.write(String.format("USER|%s|%s|%s|%s%n", java.time.LocalDateTime.now(), username, password, customerID));
            writer.close();
            System.out.println("Saved customer user: " + username);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
