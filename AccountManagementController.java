import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.SimpleDoubleProperty;

public class AccountManagementController {
    
    @FXML private TextField txtAccountNumber;
    @FXML private TextField txtBalance;
    @FXML private TextField txtCustomerID;
    @FXML private TextField txtInterestRate;
    @FXML private ComboBox<String> cmbAccountType;
    @FXML private ComboBox<String> cmbBranch;
    @FXML private TableView<Account> tableViewAccounts;
    @FXML private TableColumn<Account, String> colAccNumber;
    @FXML private TableColumn<Account, Double> colBalance;
    @FXML private TableColumn<Account, String> colAccType;
    @FXML private TableColumn<Account, String> colBranch;
    @FXML private TableColumn<Account, String> colCustomerID;
    @FXML private Button btnInsert;
    @FXML private Button btnUpdate;
    @FXML private Button btnDelete;
    @FXML private Button btnSearch;
    @FXML private Button btnList;
    @FXML private Button btnUpdateInterest;
    
    private BankSystem bankSystem;
    private MainController mainController;
    private ObservableList<Account> accountList = FXCollections.observableArrayList();
    
    public void setBankSystem(BankSystem bankSystem) {
        this.bankSystem = bankSystem;
        refreshAccounts();
    }
    
    public void setMainController(MainController mainController) {
        this.mainController = mainController;
    }
    
    @FXML
    public void initialize() {
        // Setup comboboxes
        cmbAccountType.setItems(FXCollections.observableArrayList("Savings", "Cheque", "Investment"));
        cmbBranch.setItems(FXCollections.observableArrayList("Gaborone", "Francistown", "Maun", "Palapye"));
        
        // Setup table
        setupAccountTable();
        
        // Disable interest fields initially
        txtInterestRate.setDisable(true);
        btnUpdateInterest.setDisable(true);
    }
    
    private void setupAccountTable() {
        // Configure table columns
        colAccNumber.setCellValueFactory(cellData -> 
            new SimpleStringProperty(cellData.getValue().getAccountNumber()));
        
        colBalance.setCellValueFactory(cellData -> 
            new SimpleDoubleProperty(cellData.getValue().getBalance()).asObject());
        
        colAccType.setCellValueFactory(cellData -> {
            String type = cellData.getValue().getClass().getSimpleName();
            return new SimpleStringProperty(type.replace("Account", ""));
        });
        
        colBranch.setCellValueFactory(cellData -> 
            new SimpleStringProperty(cellData.getValue().getBranch()));
        
        colCustomerID.setCellValueFactory(cellData -> 
            new SimpleStringProperty(cellData.getValue().getCustomer().getCustomerID()));
        
        // Set table data
        tableViewAccounts.setItems(accountList);
        
        // Add selection listener
        tableViewAccounts.getSelectionModel().selectedItemProperty().addListener(
            (observable, oldValue, newValue) -> {
                if (newValue != null) {
                    displayAccountDetails(newValue);
                }
            });
    }
    
    @FXML
    private void handleInsert() {
        try {
            String accountNumber = txtAccountNumber.getText().trim();
            double balance = Double.parseDouble(txtBalance.getText());
            String accountType = cmbAccountType.getValue();
            String branch = cmbBranch.getValue();
            String customerID = txtCustomerID.getText().trim();
            
            // Validate inputs
            if (accountNumber.isEmpty() || customerID.isEmpty() || accountType == null || branch == null) {
                showAlert(Alert.AlertType.ERROR, "Validation Error", "Please fill in all fields");
                return;
            }
            
            if (bankSystem.getAccounts().containsKey(accountNumber)) {
                showAlert(Alert.AlertType.ERROR, "Error", "Account number already exists");
                return;
            }
            
            Customer customer = bankSystem.getCustomer(customerID);
            if (customer == null) {
                showAlert(Alert.AlertType.ERROR, "Error", "Customer ID not found");
                return;
            }
            
            // Validate investment account minimum balance
            if ("Investment".equals(accountType) && balance < 500.0) {
                showAlert(Alert.AlertType.ERROR, "Error", "Investment account requires minimum balance of BWP 500.00");
                return;
            }
            
            // Create account
            Account newAccount = createAccount(accountType, accountNumber, balance, branch, customer);
            bankSystem.addAccount(newAccount);
            
            // Save to file
            saveAccountToFile(accountNumber, accountType, balance, branch, customerID);
            
            // Refresh table
            refreshAccounts();
            
            // Clear form
            clearForm();
            
            showAlert(Alert.AlertType.INFORMATION, "Success", 
                accountType + " account created successfully for customer " + customerID);
                
        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.ERROR, "Error", "Please enter a valid balance amount");
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to create account: " + e.getMessage());
        }
    }
    
    @FXML
    private void handleUpdate() {
        Account selectedAccount = tableViewAccounts.getSelectionModel().getSelectedItem();
        if (selectedAccount == null) {
            showAlert(Alert.AlertType.ERROR, "Error", "Please select an account to update");
            return;
        }
        
        try {
            double newBalance = Double.parseDouble(txtBalance.getText());
            String newBranch = cmbBranch.getValue();
            
            if (newBranch == null || newBranch.isEmpty()) {
                showAlert(Alert.AlertType.ERROR, "Error", "Please select a branch");
                return;
            }
            
            // Update account details
            selectedAccount.setBalance(newBalance);
            selectedAccount.setBranch(newBranch);
            
            // Refresh table
            tableViewAccounts.refresh();
            
            showAlert(Alert.AlertType.INFORMATION, "Success", "Account updated successfully");
            
        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.ERROR, "Error", "Please enter a valid balance amount");
        }
    }
    
    @FXML
    private void handleUpdateInterest() {
        Account selectedAccount = tableViewAccounts.getSelectionModel().getSelectedItem();
        if (selectedAccount == null || !(selectedAccount instanceof InterestPayable)) {
            showAlert(Alert.AlertType.ERROR, "Error", "Please select an interest-paying account");
            return;
        }
        
        try {
            double newRate = Double.parseDouble(txtInterestRate.getText()) / 100; // Convert from percentage to decimal
            
            if (newRate < 0) {
                showAlert(Alert.AlertType.ERROR, "Error", "Interest rate cannot be negative");
                return;
            }
            
            InterestPayable interestAccount = (InterestPayable) selectedAccount;
            double oldRate = interestAccount.getInterestRate() * 100;
            interestAccount.setInterestRate(newRate);
            
            showAlert(Alert.AlertType.INFORMATION, "Success", 
                String.format("Interest rate updated from %.3f%% to %.3f%%", oldRate, newRate * 100));
                
        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.ERROR, "Error", "Please enter a valid interest rate");
        }
    }
    
    @FXML
    private void handleDelete() {
        Account selectedAccount = tableViewAccounts.getSelectionModel().getSelectedItem();
        if (selectedAccount == null) {
            showAlert(Alert.AlertType.ERROR, "Error", "Please select an account to delete");
            return;
        }
        
        // Confirm deletion
        Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmAlert.setTitle("Confirm Delete");
        confirmAlert.setHeaderText("Delete Account");
        confirmAlert.setContentText("Are you sure you want to delete account " + selectedAccount.getAccountNumber() + "?");
        
        if (confirmAlert.showAndWait().get() == ButtonType.OK) {
            bankSystem.getAccounts().remove(selectedAccount.getAccountNumber());
            accountList.remove(selectedAccount);
            clearForm();
            showAlert(Alert.AlertType.INFORMATION, "Success", "Account deleted successfully");
        }
    }
    
    @FXML
    private void handleSearch() {
        String searchTerm = txtAccountNumber.getText().trim();
        if (searchTerm.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Error", "Please enter an account number to search");
            return;
        }
        
        Account account = bankSystem.getAccount(searchTerm);
        if (account != null) {
            tableViewAccounts.getSelectionModel().select(account);
            tableViewAccounts.scrollTo(account);
            displayAccountDetails(account);
            showAlert(Alert.AlertType.INFORMATION, "Found", "Account found: " + account.getAccountNumber());
        } else {
            showAlert(Alert.AlertType.ERROR, "Not Found", "Account not found");
        }
    }
    
    @FXML
    private void handleList() {
        refreshAccounts();
        showAlert(Alert.AlertType.INFORMATION, "Accounts", 
            "Displaying " + accountList.size() + " accounts");
    }
    
    private Account createAccount(String type, String accountNumber, double balance, String branch, Customer customer) {
        switch (type) {
            case "Savings":
                return new SavingsAccount(accountNumber, balance, branch, customer);
            case "Cheque":
                return new ChequeAccount(accountNumber, balance, branch, customer, 1000.0);
            case "Investment":
                return new InvestmentAccount(accountNumber, balance, branch, customer);
            default:
                throw new IllegalArgumentException("Unknown account type: " + type);
        }
    }
    
    private void displayAccountDetails(Account account) {
        txtAccountNumber.setText(account.getAccountNumber());
        txtBalance.setText(String.format("%.2f", account.getBalance()));
        txtCustomerID.setText(account.getCustomer().getCustomerID());
        
        String accountType = account.getClass().getSimpleName().replace("Account", "");
        cmbAccountType.setValue(accountType);
        cmbBranch.setValue(account.getBranch());
        
        // Display interest rate for interest-paying accounts
        if (account instanceof InterestPayable) {
            InterestPayable interestAccount = (InterestPayable) account;
            double ratePercentage = interestAccount.getInterestRate() * 100;
            txtInterestRate.setText(String.format("%.3f", ratePercentage));
            txtInterestRate.setDisable(false);
            btnUpdateInterest.setDisable(false);
        } else {
            txtInterestRate.setText("N/A");
            txtInterestRate.setDisable(true);
            btnUpdateInterest.setDisable(true);
        }
    }
    
    private void clearForm() {
        txtAccountNumber.clear();
        txtBalance.clear();
        txtCustomerID.clear();
        txtInterestRate.clear();
        cmbAccountType.setValue(null);
        cmbBranch.setValue(null);
        txtInterestRate.setDisable(true);
        btnUpdateInterest.setDisable(true);
    }
    
    public void refreshAccounts() {
        accountList.setAll(bankSystem.getAllAccounts());
    }
    
    private void saveAccountToFile(String accountNumber, String accountType, double balance, String branch, String customerID) {
        try {
            java.io.FileWriter writer = new java.io.FileWriter("accounts.txt", true);
            String timestamp = java.time.LocalDateTime.now().format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            String record = String.format("ACCOUNT|%s|%s|%s|%.2f|%s|%s%n", 
                timestamp, accountNumber, accountType, balance, branch, customerID);
            writer.write(record);
            writer.close();
        } catch (Exception e) {
            System.out.println("Error saving account to file: " + e.getMessage());
        }
    }
    
    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}