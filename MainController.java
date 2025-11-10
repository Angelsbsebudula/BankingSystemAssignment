import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.collections.*;
import javafx.beans.property.*;
import java.util.*;

public class MainController {

    @FXML private TabPane tabPane;

    // === GENERAL BUTTONS AND FIELDS ===
    @FXML private Button btnLogin, btnLogout, btnInsert, btnDeposit, btnWithdraw;
    @FXML private Button btnApplySavingsInterest, btnApplyInvestmentInterest, btnApplyAllInterest;
    @FXML private TextField txtFilterAccount;
    @FXML private Button btnFilter;
    @FXML private TableView<Transaction> transactionTable;
    @FXML private TableColumn<Transaction, String> colTransAccNumber, colTransType, colTransDate;
    @FXML private TableColumn<Transaction, Double> colTransAmount;
    private ObservableList<Transaction> transactionList = FXCollections.observableArrayList();

    // === LOGIN ===
    @FXML private TextField txtUsername;
    @FXML private PasswordField txtPassword;

    // === CUSTOMER REGISTRATION ===
    @FXML private TextField txtFirstName, txtLastName, txtAddress, txtOccupation;
    @FXML private ComboBox<String> cmbCustAccountType, cmbCustBranch;
    @FXML private Button btnCreateCustomer;

    // === ACCOUNT MANAGEMENT ===
    @FXML private TextField txtAccountNumber, txtBalance, txtCustomerID;
    @FXML private ComboBox<String> cmbAccAccountType, cmbAccBranch;
    @FXML private TableView<Account> tableViewAccounts;
    @FXML private TableColumn<Account, String> colAccNumber, colAccType, colBranch, colCustomerID;
    @FXML private TableColumn<Account, Double> colBalance;

    // === TRANSACTIONS ===
    @FXML private TextField txtTransAccountNumber, txtTransAmount;
    @FXML private Label lblTransResult;

    // === INTEREST ===
    @FXML private TableView<BankSystem.InterestResult> tblInterestResults;
    @FXML private TableColumn<BankSystem.InterestResult, String> colIntAccount, colIntType;
    @FXML private TableColumn<BankSystem.InterestResult, Double> colIntInterest;
    @FXML private Label lblInterestResult;

    private BankSystem bankSystem;
    private ObservableList<Account> accountList = FXCollections.observableArrayList();
    private ObservableList<BankSystem.InterestResult> interestResults = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        bankSystem = new BankSystem();
        bankSystem.loadUsersFromFile();

        cmbCustAccountType.setItems(FXCollections.observableArrayList("Savings", "Cheque", "Investment"));
        cmbCustBranch.setItems(FXCollections.observableArrayList("Gaborone", "Francistown", "Maun", "Palapye"));
        cmbAccAccountType.setItems(FXCollections.observableArrayList("Savings", "Cheque", "Investment"));
        cmbAccBranch.setItems(FXCollections.observableArrayList("Gaborone", "Francistown", "Maun", "Palapye"));

        setupAccountTable();
        setupInterestTable();
        setupTransactionTable();
        refreshTransactions();
        disableAllTabsExceptLogin();
    }

    // === LOGIN ===
    @FXML
    private void handleLogin() {
        String username = txtUsername.getText().trim();
        String password = txtPassword.getText().trim();

        if (username.isEmpty() || password.isEmpty()) {
            showAlert("Error", "Please enter username and password.");
            return;
        }

        boolean success = bankSystem.authenticateUser(username, password);
        if (success) {
            User currentUser = bankSystem.getCurrentUser();

            if (currentUser.isTeller()) {
                showAlert("Login Successful", "Welcome Teller!");
            } else {
                showCustomerAccountInfo(currentUser);
            }
            onUserLoggedIn();
        } else {
            showAlert("Login Failed", "Invalid username or password.");
        }
    }

    @FXML
    private void handleLogout() {
        bankSystem.logout();
        showAlert("Logout", "You have been logged out successfully.");
        disableAllTabsExceptLogin();
        tabPane.getSelectionModel().select(0);
        btnLogout.setDisable(true);
    }

    private void onUserLoggedIn() {
        User currentUser = bankSystem.getCurrentUser();

        for (Tab tab : tabPane.getTabs()) {
            String tabName = tab.getText();

            if (currentUser.isTeller()) {
                tab.setDisable(tabName.equals("Login"));
            } else {
                boolean allowed = tabName.equals("Login") || tabName.equals("Transactions") || tabName.equals("Transaction History");
                tab.setDisable(!allowed);
            }
        }

        if (currentUser.isTeller()) {
            tabPane.getSelectionModel().select(1);
        } else {
            tabPane.getSelectionModel().select(3);
        }

        btnLogout.setDisable(false);
        refreshAccounts();
    }

    private void showCustomerAccountInfo(User loggedUser) {
        String customerID = loggedUser.getCustomerID();
        String accounts = bankSystem.getAccountsByCustomerID(customerID);
        showAlert("Login Successful",
                "Welcome, " + loggedUser.getUsername() + "!\n\n" +
                "Customer ID: " + customerID + "\n" +
                "Account Number(s): " + accounts);
    }

    // === CUSTOMER REGISTRATION ===
    @FXML
    private void handleCreateCustomer() {
        try {
            String firstName = txtFirstName.getText().trim();
            String lastName = txtLastName.getText().trim();
            String address = txtAddress.getText().trim();
            String occupation = txtOccupation.getText().trim();
            String accountType = cmbCustAccountType.getValue();
            String branch = cmbCustBranch.getValue();

            if (firstName.isEmpty() || lastName.isEmpty() || address.isEmpty() || occupation.isEmpty()
                    || accountType == null || branch == null) {
                showAlert("Error", "Please fill in all fields.");
                return;
            }
            
            String result = bankSystem.createCustomer(firstName, lastName, address, occupation, accountType, branch);
            showAlert("Success", result);
            clearCustomerForm();

        } catch (Exception e) {
            showAlert("Error", "Failed to create customer: " + e.getMessage());
        }
    }

    // === ACCOUNT CREATION ===
    @FXML
    private void handleInsert() {
        try {
            String accNum = txtAccountNumber.getText().trim();
            String custID = txtCustomerID.getText().trim();
            String accType = cmbAccAccountType.getValue();
            String branch = cmbAccBranch.getValue();
            String balanceText = txtBalance.getText().trim();

            if (accNum.isEmpty() || custID.isEmpty() || accType == null || branch == null || balanceText.isEmpty()) {
                showAlert("Error", "Please fill in all fields before adding an account.");
                return;
            }

            double balance = Double.parseDouble(balanceText);

            bankSystem.createAccount(accNum, accType, balance, branch, custID);
            refreshAccounts();
            showAlert("Success", "Account created successfully!");
            
            // Clear form after success
            txtAccountNumber.clear();
            txtCustomerID.clear();
            txtBalance.clear();
            cmbAccAccountType.setValue(null);
            cmbAccBranch.setValue(null);
            
        } catch (NumberFormatException e) {
            showAlert("Error", "Please enter a valid number for balance.");
        } catch (Exception e) {
            showAlert("Error", "Failed to create account: " + e.getMessage());
        }
    }

    // === TRANSACTIONS ===
    @FXML private void handleDeposit() { processTransaction("Deposit"); }
    @FXML private void handleWithdraw() { processTransaction("Withdraw"); }

    private void processTransaction(String type) {
        try {
            String accNum = txtTransAccountNumber.getText().trim();
            if (accNum.isEmpty()) {
                showResult("Please enter account number", false);
                return;
            }
            
            String amountText = txtTransAmount.getText().trim();
            if (amountText.isEmpty()) {
                showResult("Please enter amount", false);
                return;
            }
            
            double amount = Double.parseDouble(amountText);
            if (amount <= 0) {
                showResult("Amount must be positive", false);
                return;
            }

            // Get current user and validate account ownership
            User currentUser = bankSystem.getCurrentUser();
            if (currentUser == null) {
                showResult("Please login first", false);
                return;
            }

            // If customer, verify they own this account
            if (!currentUser.isTeller()) {
                String customerAccounts = bankSystem.getAccountsByCustomerID(currentUser.getCustomerID());
                // FIX: Split the space-separated accounts and check if the entered account is in the list
                String[] ownedAccounts = customerAccounts.split(" ");
                boolean ownsAccount = false;
                for (String ownedAcc : ownedAccounts) {
                    if (ownedAcc.equals(accNum)) {
                        ownsAccount = true;
                        break;
                    }
                }
                if (!ownsAccount) {
                    showResult("Access denied: You don't own this account", false);
                    return;
                }
            }

            boolean success = type.equals("Deposit")
                    ? bankSystem.deposit(accNum, amount)
                    : bankSystem.withdraw(accNum, amount);

            showResult(success ? type + " successful!" : type + " failed!", success);
            
            if (success) {
                txtTransAccountNumber.clear();
                txtTransAmount.clear();
                refreshAccounts();
                refreshTransactions();
            }
            
        } catch (NumberFormatException e) {
            showResult("Please enter a valid number", false);
        } catch (Exception e) {
            showResult("Error: " + e.getMessage(), false);
        }
    }

    // === INTEREST ===
    @FXML private void handleApplySavingsInterest() { applyInterest("Savings"); }
    @FXML private void handleApplyInvestmentInterest() { applyInterest("Investment"); }
    @FXML private void handleApplyAllInterest() { applyInterest("All"); }

    private void applyInterest(String type) {
        // Add confirmation dialog
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Apply Interest");
        confirm.setHeaderText("Apply " + type + " Interest?");
        confirm.setContentText("This will modify account balances. Continue?");
        
        Optional<ButtonType> result = confirm.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            List<BankSystem.InterestResult> results = bankSystem.applyInterest(type);
            interestResults.setAll(results);
            showInterestResult(
                    results.isEmpty() ? "No eligible accounts." : "Applied interest to " + results.size() + " accounts.",
                    !results.isEmpty()
            );
            refreshAccounts();
        }
    }

    // === TRANSACTION HISTORY ===
    @FXML
    private void handleFilter() {
        String filter = txtFilterAccount.getText().trim().toLowerCase();
        if (filter.isEmpty()) {
            transactionTable.setItems(transactionList);
        } else {
            ObservableList<Transaction> filtered = FXCollections.observableArrayList();
            for (Transaction t : transactionList) {
                if (t.getAccountNumber().toLowerCase().contains(filter)) {
                    filtered.add(t);
                }
            }
            transactionTable.setItems(filtered);
        }
    }

    // === TABLE SETUP ===
    private void setupAccountTable() {
        colAccNumber.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getAccountNumber()));
        colBalance.setCellValueFactory(c -> new SimpleDoubleProperty(c.getValue().getBalance()).asObject());
        colAccType.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getClass().getSimpleName().replace("Account", "")));
        colBranch.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getBranch()));
        colCustomerID.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getCustomer().getCustomerID()));
        tableViewAccounts.setItems(accountList);
    }

    private void setupInterestTable() {
        colIntAccount.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getAccountNumber()));
        colIntType.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getAccountType()));
        colIntInterest.setCellValueFactory(c -> new SimpleDoubleProperty(c.getValue().getInterestAmount()).asObject());
        tblInterestResults.setItems(interestResults);
    }

    private void setupTransactionTable() {
        colTransAccNumber.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getAccountNumber()));
        colTransAmount.setCellValueFactory(c -> new SimpleDoubleProperty(c.getValue().getAmount()).asObject());
        colTransType.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getType()));
        colTransDate.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getDate().toString()));
        transactionTable.setItems(transactionList);
    }

    private void refreshAccounts() {
        accountList.setAll(bankSystem.getAllAccounts());
    }

    private void refreshTransactions() {
        transactionList.setAll(bankSystem.getAllTransactions());
    }

    private void clearCustomerForm() {
        txtFirstName.clear();
        txtLastName.clear();
        txtAddress.clear();
        txtOccupation.clear();
        cmbCustAccountType.setValue(null);
        cmbCustBranch.setValue(null);
    }

    // === ALERT + LABEL HELPERS ===
    private void showAlert(String t, String m) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(t);
        alert.setHeaderText(null);
        alert.setContentText(m);
        alert.showAndWait();
    }

    private void showResult(String msg, boolean success) {
        lblTransResult.setText(msg);
        lblTransResult.setStyle(success ? "-fx-text-fill: green;" : "-fx-text-fill: red;");
    }

    private void showInterestResult(String msg, boolean success) {
        lblInterestResult.setText(msg);
        lblInterestResult.setStyle(success ? "-fx-text-fill: green;" : "-fx-text-fill: red;");
    }

    private void disableAllTabsExceptLogin() {
        for (Tab t : tabPane.getTabs()) {
            t.setDisable(!t.getText().equals("Login"));
        }
    }
}