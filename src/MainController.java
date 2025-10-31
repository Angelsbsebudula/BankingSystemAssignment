import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import java.util.HashMap;

public class MainController {

    // Login Tab
    @FXML private TextField txtUsername, txtPassword;
    @FXML private Button btnLogin, btnRegister;
    @FXML private Button btnLogout;
    
    //CREATE CUSTOMER TAB
    @FXML private TextField txtFirstName, txtLastName, txtAddress, txtOccupation;
    @FXML private Button btnCreateCustomer;
    @FXML private ComboBox<String> cmbCustomerAccountType;
    @FXML private ComboBox<String> cmbCustomerBranch;

    // Account Tab  
    @FXML private TextField txtAccountNumber, txtBalance, txtCustomerID;
    @FXML private ComboBox<String> cmbAccountType, cmbBranch;
    @FXML private TableView<Account> tableViewAccounts;
    @FXML private TableColumn<Account, String> colAccNumber;
    @FXML private TableColumn<Account, Double> colBalance;
    @FXML private TableColumn<Account, String> colAccType;
    @FXML private TableColumn<Account, String> colBranch;
    @FXML private TableColumn<Account, String> colCustomerID;
    @FXML private Button btnInsert, btnDelete, btnUpdate, btnSearch, btnList;

    // Deposit/Withdraw Tab
    @FXML private TextField txtTransAccountNumber, txtTransAmount;
    @FXML private Button btnWithdraw, btnDeposit;

    // Transaction Tab
    @FXML private TableView<Transaction> transactionTable;
    @FXML private TableColumn<Transaction, String> colTransAccNumber;
    @FXML private TableColumn<Transaction, String> colTransAccountType;
    @FXML private TableColumn<Transaction, Double> colTransAmount;
    @FXML private TableColumn<Transaction, String> colTransType;
    @FXML private TableColumn<Transaction, String> colTransDate;

    // TabPane for controlling tabs
    @FXML private TabPane tabPane;

    private HashMap<String, User> users = new HashMap<>();
    private HashMap<String, Customer> customers = new HashMap<>();
    private HashMap<String, Account> accounts = new HashMap<>();
    private ObservableList<Account> accountList = FXCollections.observableArrayList();
    private ObservableList<Transaction> transactionList = FXCollections.observableArrayList();
    private User currentUser;
    
     public MainController() {
        System.out.println("=== DEBUG: Constructor called ===");
        // Manually call initialize since @FXML initialize isn't working
        javafx.application.Platform.runLater(() -> {
            System.out.println("=== DEBUG: Manual initialize called ===");
            initializeManually();
        });
    }
    
    private void initializeManually() {
        System.out.println("=== DEBUG: Manual initialize started ===");
        System.out.println("DEBUG: tabPane is null: " + (tabPane == null));
        
        if (tabPane != null) {
            System.out.println("DEBUG: Number of tabs: " + tabPane.getTabs().size());
            for (int i = 0; i < tabPane.getTabs().size(); i++) {
                System.out.println("DEBUG: Tab " + i + " - " + tabPane.getTabs().get(i).getText());
            }
            
            // Setup your components
            cmbAccountType.getItems().addAll("Savings", "Cheque", "Investment");
            cmbBranch.getItems().addAll("Gaborone", "Francistown");
            cmbCustomerAccountType.getItems().addAll("Savings", "Cheque", "Investment");
            cmbCustomerBranch.getItems().addAll("Gaborone", "Francistown");
            setupAccountTable();
            setupTransactionTable();
            setupTestData();
            disableAllTabsExceptLogin();
            
        } else {
            System.out.println("ERROR: tabPane is still null!");
        }
        
        System.out.println("=== DEBUG: Manual initialize completed ===");
    }


    @FXML
public void initialize() {
       
    if (tabPane != null) {
        System.out.println("DEBUG: Number of tabs: " + tabPane.getTabs().size());
        // Print tab names for debugging
        for (int i = 0; i < tabPane.getTabs().size(); i++) {
            System.out.println("DEBUG: Tab " + i + " - " + tabPane.getTabs().get(i).getText());
        }
    } else {
        System.out.println("ERROR: tabPane is null in initialize() - FXML injection failed!");
        // Don't continue with setup if tabPane is null
        return;
    }
    
    // Only setup the rest if tabPane is working
    System.out.println("DEBUG: Setting up application components...");
    
    // Setup comboboxes
    cmbAccountType.getItems().addAll("Savings", "Cheque", "Investment");
    cmbBranch.getItems().addAll("Gaborone", "Francistown");
    
    cmbCustomerAccountType.getItems().addAll("Savings", "Cheque", "Investment");
    cmbCustomerBranch.getItems().addAll("Gaborone", "Francistown");
    
    // Setup account table
    setupAccountTable();
    
    // Setup transaction table
    setupTransactionTable();
    
    // Create test data - INCLUDING ANGIE BEE!
    setupTestData();
    
    // Start with only login enabled
    disableAllTabsExceptLogin();
    
    System.out.println("DEBUG: Initialize completed successfully");
}
   
    private void setupAccountTable() {
        // Link table columns to account properties
        colAccNumber.setCellValueFactory(cellData -> {
            try {
                return new javafx.beans.property.SimpleStringProperty(cellData.getValue().getAccountNumber());
            } catch (Exception e) {
                return new javafx.beans.property.SimpleStringProperty("Error");
            }
        });
        
        colBalance.setCellValueFactory(cellData -> {
            try {
                return new javafx.beans.property.SimpleDoubleProperty(cellData.getValue().getBalance()).asObject();
            } catch (Exception e) {
                return new javafx.beans.property.SimpleDoubleProperty(0.0).asObject();
            }
        });
        
        colAccType.setCellValueFactory(cellData -> {
            try {
                String type = cellData.getValue().getClass().getSimpleName();
                return new javafx.beans.property.SimpleStringProperty(type.replace("Account", ""));
            } catch (Exception e) {
                return new javafx.beans.property.SimpleStringProperty("Unknown");
            }
        });
        
        colBranch.setCellValueFactory(cellData -> {
            try {
                return new javafx.beans.property.SimpleStringProperty(cellData.getValue().getBranch());
            } catch (Exception e) {
                return new javafx.beans.property.SimpleStringProperty("Unknown");
            }
        });
        
        colCustomerID.setCellValueFactory(cellData -> {
            try {
                return new javafx.beans.property.SimpleStringProperty(cellData.getValue().getCustomer().getCustomerID());
            } catch (Exception e) {
                return new javafx.beans.property.SimpleStringProperty("Unknown");
            }
        });
        
        // Connect the observable list to table
        tableViewAccounts.setItems(accountList);
    }

    private void setupTransactionTable() {
        colTransAccNumber.setCellValueFactory(cellData -> cellData.getValue().accountNumberProperty());
        colTransAccountType.setCellValueFactory(cellData -> cellData.getValue().accountTypeProperty());
        colTransAmount.setCellValueFactory(cellData -> cellData.getValue().amountProperty().asObject());
        colTransType.setCellValueFactory(cellData -> cellData.getValue().typeProperty());
        colTransDate.setCellValueFactory(cellData -> cellData.getValue().dateProperty());
        
        transactionTable.setItems(transactionList);
    }

    private void setupTestData() {
        // Create BANK TELLER users
        users.put("teller", new User("teller", "123"));
        users.put("manager", new User("manager", "123"));
        
        // Create CUSTOMER users - INCLUDING ANGIE BEE!
        users.put("angie", new User("angie", "123", "C001"));
        users.put("john", new User("john", "123", "C002"));
        
        // Create actual customer records - ANGIE BEE FROM  TEST!
        Customer angie = new IndividualCustomer("C001", "Angie", "Bee", "123 Honeycomb Lane", "Software Developer");
        Customer john = new IndividualCustomer("C002", "John", "Doe", "456 Main St", "Engineer");
        
        customers.put("C001", angie);
        customers.put("C002", john);
        
        // Create test accounts - ANGIE'S ACCOUNTS FROM  BANKTEST!
        Account angieSavings = new SavingsAccount("SA001", 1500.0, "Gaborone Branch", angie);
        Account angieInvestment = new InvestmentAccount("IA001", 800.0, "Gaborone Branch", angie);
        Account angieCheque = new ChequeAccount("CA001", 3000.0, "Gaborone Branch", angie, 1200.0);
        
        Account johnSavings = new SavingsAccount("SA002", 2000.0, "Francistown", john);
        
        // Add all accounts to the system
        accounts.put("SA001", angieSavings);
        accounts.put("IA001", angieInvestment);
        accounts.put("CA001", angieCheque);
        accounts.put("SA002", johnSavings);
        
        accountList.addAll(angieSavings, angieInvestment, angieCheque, johnSavings);
    }

 private void disableAllTabsExceptLogin() {
    if (tabPane != null) {
        // Enable login tab
        tabPane.getTabs().get(0).setDisable(false);
        
        // Disable all other tabs
        for (int i = 1; i < tabPane.getTabs().size(); i++) {
            tabPane.getTabs().get(i).setDisable(true);
        }
    }
    
    // Reset buttons - enable login, disable logout
    btnLogout.setDisable(true);
    btnLogin.setDisable(false);
}
@FXML 
private void handleLogin() {
    String username = txtUsername.getText();
    String password = txtPassword.getText();
    
    User user = users.get(username);
    
    if (user != null && user.getPassword().equals(password)) {
        currentUser = user;
        showMessage("Welcome " + (user.isTeller() ? "Bank Teller" : "Customer"));
        setupTabsForUser();
    } else {
        showMessage("Wrong username or password");
    }
}
@FXML 
private void handleLogout() {
    currentUser = null;
    txtUsername.clear();
    txtPassword.clear();
    disableAllTabsExceptLogin();
    btnLogout.setDisable(true);
    btnLogin.setDisable(false);
    showMessage("Logged out successfully");
    tabPane.getSelectionModel().select(0); // Go to login tab
}
     private void setupTabsForUser() {
    if (tabPane == null || currentUser == null) return;
    
    // First, enable ALL tabs
    for (Tab tab : tabPane.getTabs()) {
        tab.setDisable(false);
    }
    
    if (currentUser.isCustomer()) {
        // Customer: Only Deposit/Withdraw and Transactions enabled
        tabPane.getTabs().get(1).setDisable(true); // Account Management ❌
        tabPane.getSelectionModel().select(2); // Go to deposit/withdraw
        
    } else if (currentUser.isTeller()) {
        // Teller: All tabs except Login enabled
        tabPane.getTabs().get(0).setDisable(true); // Login ❌ only
        tabPane.getSelectionModel().select(1); // Go to account management
    }
    
    // Enable logout button and disable login button after successful login
    btnLogout.setDisable(false);
    btnLogin.setDisable(true);
}
    @FXML 
    private void handleRegister() {
        showMessage("See bank teller to register");
    }
    @FXML 
private void handleCreateCustomer() {
    System.out.println("=== DEBUG: handleCreateCustomer called ===");
    
    try {
        String firstName = txtFirstName.getText();
        String lastName = txtLastName.getText();
        String address = txtAddress.getText();
        String occupation = txtOccupation.getText();
        
        System.out.println("DEBUG: Fields - " + firstName + ", " + lastName + ", " + address + ", " + occupation);
        
        if (firstName.isEmpty() || lastName.isEmpty() || address.isEmpty() || occupation.isEmpty()) {
            showMessage("Please fill all customer details");
            return;
        }
        
        // Generate unique customer ID
        String customerID = "C" + (customers.size() + 1000);
        System.out.println("DEBUG: Generated customerID: " + customerID);
        
        // Generate username
        String username = firstName.toLowerCase() + lastName.toLowerCase().charAt(0);
        System.out.println("DEBUG: Generated username: " + username);
        
        // Default password
        String password = "123";
        
        System.out.println("DEBUG: About to create IndividualCustomer...");
        
        // Create new customer
        Customer newCustomer = new IndividualCustomer(customerID, firstName, lastName, address, occupation);
        System.out.println("DEBUG: Customer created successfully");
        
        User newUser = new User(username, password, customerID);
        System.out.println("DEBUG: User created successfully");
        // === CREATE ACCOUNT BASED ON CUSTOMER'S SELECTION ===
           String selectedAccountType = cmbCustomerAccountType.getValue();
           String selectedBranch = cmbCustomerBranch.getValue();
           String accountNumber = generateAccountNumber(selectedAccountType);
          
// === Handle Investment account minimum balance ===
double openingBalance = 0.0;
if ("Investment".equals(selectedAccountType)) {
    openingBalance = 500.0; // Minimum for investment accounts
}
Account newAccount = createAccount(selectedAccountType, accountNumber, openingBalance, selectedBranch, newCustomer);
           System.out.println("DEBUG: Account created: " + accountNumber + " (" + selectedAccountType + ")");

         // Add to system
        customers.put(customerID, newCustomer);
         users.put(username, newUser);
         accounts.put(accountNumber, newAccount);
        accountList.add(newAccount);

       // SAVE TO FILES - USING CUSTOMER'S SELECTIONS
        saveCustomerToFile(customerID, firstName, lastName, address, occupation, username);
       saveAccountToFile(accountNumber, selectedAccountType, openingBalance, selectedBranch, customerID);
             
        System.out.println("DEBUG: Added to system - customers: " + customers.size() + 
                          ", users: " + users.size() + ", accounts: " + accounts.size());
        
        // Clear fields
        txtFirstName.clear();
        txtLastName.clear();
        txtAddress.clear();
        txtOccupation.clear();
        
        // === SHOW ACCOUNT NUMBER TO USER ===
        showMessage("Customer created successfully!\n" +
                   "Username: " + username + "\n" +
                   "Password: 123\n" +
                   "Customer ID: " + customerID + "\n" +
                   "Account Number: " + accountNumber + "\n" +
                   "Use this account number for deposits/withdrawals!");
        
    } catch (Exception e) {
        System.out.println("ERROR in handleCreateCustomer: " + e.getMessage());
        e.printStackTrace();
        showMessage("Error creating customer: " + e.getMessage());
    }
}    
          @FXML 
    private void handleInsert() {
        if (!isTeller()) return;
        
        try {
            String accNum = txtAccountNumber.getText();
            double balance = Double.parseDouble(txtBalance.getText());
            String type = cmbAccountType.getValue();
            String branch = cmbBranch.getValue();
            String custID = txtCustomerID.getText();
            
            if (accNum.isEmpty() || custID.isEmpty() || type == null || branch == null) {
                showMessage("Fill all fields");
                return;
            }
            
            if (accounts.containsKey(accNum)) {
                showMessage("Account number already exists");
                return;
            }
            
            Customer customer = customers.get(custID);
            if (customer == null) {
                showMessage("Customer " + custID + " not found. Use C001 or C002 for testing.");
                return;
            }
            
            Account newAccount = createAccount(type, accNum, balance, branch, customer);
            accounts.put(accNum, newAccount);
            accountList.add(newAccount); // Add to table
            // SAVE ACCOUNT TO FILE
            saveAccountToFile(accNum, type, balance, branch, custID);
            
            showMessage(type + " account created successfully");
            clearFields();
            
        } catch (NumberFormatException e) {
            showMessage("Enter valid balance amount");
        } catch (Exception e) {
            showMessage("Error: " + e.getMessage());
        }
    }

    private Account createAccount(String type, String num, double balance, String branch, Customer cust) {
        switch (type) {
            case "Savings": return new SavingsAccount(num, balance, branch, cust);
            case "Cheque": return new ChequeAccount(num, balance, branch, cust, 1000.0);
            case "Investment": return new InvestmentAccount(num, balance, branch, cust);
            default: throw new RuntimeException("Unknown account type");
        }
    }

    @FXML 
    private void handleDelete() {
        if (!isTeller()) return;
        
        Account selectedAccount = tableViewAccounts.getSelectionModel().getSelectedItem();
        if (selectedAccount == null) {
            showMessage("Select an account to delete");
            return;
        }
        
        accounts.remove(selectedAccount.getAccountNumber());
        accountList.remove(selectedAccount);
        showMessage("Account deleted");
    }

    @FXML 
    private void handleUpdate() {
        if (!isTeller()) return;
        
        Account selectedAccount = tableViewAccounts.getSelectionModel().getSelectedItem();
        if (selectedAccount == null) {
            showMessage("Select an account to update");
            return;
        }
        
        // For now, just show current details
        txtAccountNumber.setText(selectedAccount.getAccountNumber());
        txtBalance.setText(String.valueOf(selectedAccount.getBalance()));
        txtCustomerID.setText(selectedAccount.getCustomer().getCustomerID());
        showMessage("Edit details and click Insert to update");
    }

    @FXML 
    private void handleSearch() {
        String searchTerm = txtAccountNumber.getText();
        if (searchTerm.isEmpty()) {
            showMessage("Enter account number to search");
            return;
        }
        
        Account account = accounts.get(searchTerm);
        if (account != null) {
            // Select the account in table
            tableViewAccounts.getSelectionModel().select(account);
            tableViewAccounts.scrollTo(account);
            showMessage("Account found: " + account.getAccountNumber());
        } else {
            showMessage("Account not found");
        }
    }

    @FXML 
    private void handleList() {
        // Table automatically shows all accounts from accountList
        showMessage("Showing " + accountList.size() + " accounts");
    }

    @FXML 
    private void handleWithdraw() {
        processTransaction("Withdraw");
    }

    @FXML 
    private void handleDeposit() {
        processTransaction("Deposit");
    }

    private void processTransaction(String type) {
        try {
            String accNum = txtTransAccountNumber.getText();
            double amount = Double.parseDouble(txtTransAmount.getText());
            
            Account account = accounts.get(accNum);
            if (account == null) {
                showMessage("Account not found");
                return;
            }
            
            if (!canAccessAccount(account)) {
                showMessage("You can't access this account");
                return;
            }
            
            boolean success = false;
            if ("Deposit".equals(type)) {
                success = account.deposit(amount);
            } else {
                success = account.withdraw(amount);
            }
            
            if (success) {
                // Refresh table to show updated balance
                tableViewAccounts.refresh();
                
                // Add to transaction history
               String accountType = account.getClass().getSimpleName().replace("Account", "");
                Transaction transaction = new Transaction(accNum, amount, type, accountType);
                  transactionList.add(transaction);  
                  // SAVE TRANSACTION TO FILE
               saveTransactionToFile(accNum, amount, type, accountType);   
                          
                showMessage(type + " successful! Balance: BWP " + account.getBalance());
                txtTransAmount.clear();
            } else {
                showMessage(type + " failed");
            }
            
        } catch (Exception e) {
            showMessage("Enter valid amount");
        }
    }

    private boolean canAccessAccount(Account account) {
        if (currentUser.isTeller()) return true;
        if (currentUser.isCustomer()) {
            return account.getCustomer().getCustomerID().equals(currentUser.getCustomerID());
        }
        return false;
    }

    private boolean isTeller() {
        if (currentUser == null || !currentUser.isTeller()) {
            showMessage("Tellers only");
            return false;
        }
        return true;
    }

    private void showMessage(String text) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Bank System");
        alert.setHeaderText(null);
        alert.setContentText(text);
        alert.showAndWait();
    }

    private void clearFields() {
        txtAccountNumber.clear();
        txtBalance.clear();
        txtCustomerID.clear();
        cmbAccountType.setValue(null);
        cmbBranch.setValue(null);
    }
        // ===  FILE SAVING METHODS ===

    private void saveCustomerToFile(String customerID, String firstName, String lastName, String address, String occupation, String username) {
        try {
            java.io.FileWriter writer = new java.io.FileWriter("customers.txt", true); // true for append mode
            java.time.LocalDateTime now = java.time.LocalDateTime.now();
            String timestamp = now.format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            
            String record = String.format("CUSTOMER|%s|%s|%s|%s|%s|%s|%s%n", 
                timestamp, customerID, firstName, lastName, address, occupation, username);
            
            writer.write(record);
            writer.close();
            System.out.println("DEBUG: Customer saved to file: " + customerID);
            
        } catch (Exception e) {
            System.out.println("ERROR saving customer to file: " + e.getMessage());
        }
    }

    private void saveTransactionToFile(String accountNumber, double amount, String type, String accountType) {
        try {
            java.io.FileWriter writer = new java.io.FileWriter("transactions.txt", true); // true for append mode
            java.time.LocalDateTime now = java.time.LocalDateTime.now();
            String timestamp = now.format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            
            String record = String.format("TRANSACTION|%s|%s|%.2f|%s|%s%n", 
                timestamp, accountNumber, amount, type, accountType);
            
            writer.write(record);
            writer.close();
            System.out.println("DEBUG: Transaction saved to file: " + accountNumber + " " + type);
            
        } catch (Exception e) {
            System.out.println("ERROR saving transaction to file: " + e.getMessage());
        }
    }

    private void saveAccountToFile(String accountNumber, String accountType, double balance, String branch, String customerID) {
        try {
            java.io.FileWriter writer = new java.io.FileWriter("accounts.txt", true);
            java.time.LocalDateTime now = java.time.LocalDateTime.now();
            String timestamp = now.format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            
            String record = String.format("ACCOUNT|%s|%s|%s|%.2f|%s|%s%n", 
                timestamp, accountNumber, accountType, balance, branch, customerID);
            
            writer.write(record);
            writer.close();
            System.out.println("DEBUG: Account saved to file: " + accountNumber);
            
        } catch (Exception e) {
            System.out.println("ERROR saving account to file: " + e.getMessage());
        }
    }
          // === HELPER METHODS ===

    private String generateAccountNumber(String accountType) {
        String prefix;
        switch (accountType) {
            case "Savings": prefix = "SA"; break;
            case "Cheque": prefix = "CA"; break;
            case "Investment": prefix = "IA"; break;
            default: prefix = "AC";
        }
        return prefix + (accounts.size() + 1000);
    }
}