import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.TextField;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableView;
import javafx.scene.control.TableColumn;


public class MainController {

    // Login Tab Components
    @FXML private TextField txtUsername;
    @FXML private TextField txtPassword;
    @FXML private Button btnLogin;
    @FXML private Button btnRegister;

    // Account Tab Components
    @FXML private TextField txtAccountNumber;
    @FXML private TextField txtBalance;
    @FXML private ComboBox<String> cmbAccountType;
    @FXML private ComboBox<String> cmbBranch;
    @FXML private TextField txtCustomerID;
    @FXML private TableView<?> tableViewAccounts;
    @FXML private TableColumn<?, ?> colAccNumber;
    @FXML private TableColumn<?, ?> colBalance;
    @FXML private TableColumn<?, ?> colAccType;
    @FXML private TableColumn<?, ?> colBranch;
    @FXML private TableColumn<?, ?> colCustomerID;
    @FXML private Button btnInsert;
    @FXML private Button btnDelete;
    @FXML private Button btnUpdate;
    @FXML private Button btnSearch;
    @FXML private Button btnList;

    // Deposit/Withdraw Tab Components
    @FXML private TextField txtTransAccountNumber;
    @FXML private TextField txtTransAmount;
    @FXML private Button btnWithdraw;
    @FXML private Button btnDeposit;

    // Transaction Tab Components
    @FXML private TableView<?> transactionTable;
    @FXML private TableColumn<?, ?> colTransAccNumber;
    @FXML private TableColumn<?, ?> colTransAmount;
    @FXML private TableColumn<?, ?> colTransType;
    @FXML private TableColumn<?, ?> colTransDate;

    @FXML
    public void initialize() {
        System.out.println("MainController initialized!");
        
        // Set up Account Type combo box
        cmbAccountType.getItems().addAll("Savings", "Cheque", "Investment");
        
        // Set up Branch combo box  
        cmbBranch.getItems().addAll("Gaborone", "Francistown");
    }

    // Login methods
    @FXML private void handleLogin() {
        System.out.println("Login button clicked!");
        String username = txtUsername.getText();
        String password = txtPassword.getText();
        System.out.println("Username: " + username + ", Password: " + password);
    }
    
    @FXML private void handleRegister() {
        System.out.println("Register button clicked!");
    }

    // Account methods
    @FXML private void handleInsert() {
        System.out.println("Insert button clicked!");
        // You can access the selected values like this:
        String accountType = cmbAccountType.getValue();
        String branch = cmbBranch.getValue();
        System.out.println("Account Type: " + accountType + ", Branch: " + branch);
    }
    
    @FXML private void handleDelete() {
        System.out.println("Delete button clicked!");
    }
    
    @FXML private void handleUpdate() {
        System.out.println("Update button clicked!");
    }
    
    @FXML private void handleSearch() {
        System.out.println("Search button clicked!");
    }
    
    @FXML private void handleList() {
        System.out.println("List button clicked!");
    }

    // Deposit/Withdraw methods
    @FXML private void handleWithdraw() {
        System.out.println("Withdraw button clicked!");
        System.out.println("Account: " + txtTransAccountNumber.getText() + ", Amount: " + txtTransAmount.getText());
    }
    
    @FXML private void handleDeposit() {
        System.out.println("Deposit button clicked!");
        System.out.println("Account: " + txtTransAccountNumber.getText() + ", Amount: " + txtTransAmount.getText());
    }
}