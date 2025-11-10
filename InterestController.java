import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.SimpleDoubleProperty;

public class InterestController {
    
    @FXML private Button btnApplySavingsInterest;
    @FXML private Button btnApplyInvestmentInterest;
    @FXML private Button btnApplyAllInterest;
    @FXML private TableView<InterestResult> tblInterestResults;
    @FXML private TableColumn<InterestResult, String> colIntAccount;
    @FXML private TableColumn<InterestResult, String> colIntType;
    @FXML private TableColumn<InterestResult, Double> colIntOldBalance;
    @FXML private TableColumn<InterestResult, Double> colIntInterest;
    @FXML private TableColumn<InterestResult, Double> colIntNewBalance;
    @FXML private Label lblInterestResult;
    
    private BankSystem bankSystem;
    private MainController mainController;
    private ObservableList<InterestResult> interestResults = FXCollections.observableArrayList();
    
    public void setBankSystem(BankSystem bankSystem) {
        this.bankSystem = bankSystem;
    }
    
    public void setMainController(MainController mainController) {
        this.mainController = mainController;
    }
    
    @FXML
    public void initialize() {
        setupInterestTable();
        lblInterestResult.setVisible(false);
    }
    
    private void setupInterestTable() {
        colIntAccount.setCellValueFactory(cellData -> 
            new SimpleStringProperty(cellData.getValue().getAccountNumber()));
        
        colIntType.setCellValueFactory(cellData -> 
            new SimpleStringProperty(cellData.getValue().getAccountType()));
        
        colIntOldBalance.setCellValueFactory(cellData -> 
            new SimpleDoubleProperty(cellData.getValue().getOldBalance()).asObject());
        
        colIntInterest.setCellValueFactory(cellData -> 
            new SimpleDoubleProperty(cellData.getValue().getInterestAmount()).asObject());
        
        colIntNewBalance.setCellValueFactory(cellData -> 
            new SimpleDoubleProperty(cellData.getValue().getNewBalance()).asObject());
        
        tblInterestResults.setItems(interestResults);
    }
    
    @FXML
    private void handleApplySavingsInterest() {
        applyInterestToAccounts("Savings");
    }
    
    @FXML
    private void handleApplyInvestmentInterest() {
        applyInterestToAccounts("Investment");
    }
    
    @FXML
    private void handleApplyAllInterest() {
        applyInterestToAccounts("All");
    }
    
    private void applyInterestToAccounts(String accountType) {
        try {
            interestResults.clear();
            int processedCount = 0;
            double totalInterest = 0;
            
            for (Account account : bankSystem.getAllAccounts()) {
                if (account instanceof InterestPayable) {
                    InterestPayable interestAccount = (InterestPayable) account;
                    
                    // Filter by account type if specified
                    if (!"All".equals(accountType)) {
                        String actualType = account.getClass().getSimpleName().replace("Account", "");
                        if (!actualType.equals(accountType)) {
                            continue;
                        }
                    }
                    
                    double oldBalance = account.getBalance();
                    double interestRate = interestAccount.getInterestRate();
                    double interestAmount = oldBalance * interestRate;
                    double newBalance = oldBalance + interestAmount;
                    
                    // Apply interest
                    interestAccount.applyInterest();
                    
                    // Record the result
                    InterestResult result = new InterestResult(
                        account.getAccountNumber(),
                        account.getClass().getSimpleName().replace("Account", ""),
                        oldBalance,
                        interestAmount,
                        newBalance
                    );
                    interestResults.add(result);
                    
                    processedCount++;
                    totalInterest += interestAmount;
                    
                    // Save interest transaction
                    Transaction interestTransaction = new Transaction(
                        account.getAccountNumber(),
                        interestAmount,
                        "Interest",
                        account.getClass().getSimpleName().replace("Account", "")
                    );
                    bankSystem.addTransaction(interestTransaction);
                    saveTransactionToFile(account.getAccountNumber(), interestAmount, "Interest", 
                                         account.getClass().getSimpleName().replace("Account", ""));
                }
            }
            
            if (processedCount > 0) {
                showResult(String.format(
                    "Successfully applied interest to %d accounts. Total interest: BWP %.2f",
                    processedCount, totalInterest
                ), true);
                
                // Refresh other controllers
                if (mainController != null) {
                    mainController.refreshAllData();
                }
            } else {
                showResult("No eligible accounts found for interest application", false);
            }
            
        } catch (Exception e) {
            showResult("Error applying interest: " + e.getMessage(), false);
            e.printStackTrace();
        }
    }
    
    private void saveTransactionToFile(String accountNumber, double amount, String type, String accountType) {
        try {
            java.io.FileWriter writer = new java.io.FileWriter("transactions.txt", true);
            String timestamp = java.time.LocalDateTime.now().format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            String record = String.format("TRANSACTION|%s|%s|%.2f|%s|%s%n", 
                timestamp, accountNumber, amount, type, accountType);
            writer.write(record);
            writer.close();
        } catch (Exception e) {
            System.out.println("Error saving transaction to file: " + e.getMessage());
        }
    }
    
    private void showResult(String message, boolean isSuccess) {
        lblInterestResult.setText(message);
        lblInterestResult.setStyle(isSuccess ? 
            "-fx-text-fill: #27ae60; -fx-font-weight: bold;" : 
            "-fx-text-fill: #e74c3c; -fx-font-weight: bold;");
        lblInterestResult.setVisible(true);
    }
    
    // Inner class to store interest application results
    public static class InterestResult {
        private String accountNumber;
        private String accountType;
        private double oldBalance;
        private double interestAmount;
        private double newBalance;
        
        public InterestResult(String accountNumber, String accountType, double oldBalance, double interestAmount, double newBalance) {
            this.accountNumber = accountNumber;
            this.accountType = accountType;
            this.oldBalance = oldBalance;
            this.interestAmount = interestAmount;
            this.newBalance = newBalance;
        }
        
        // Getters
        public String getAccountNumber() { return accountNumber; }
        public String getAccountType() { return accountType; }
        public double getOldBalance() { return oldBalance; }
        public double getInterestAmount() { return interestAmount; }
        public double getNewBalance() { return newBalance; }
    }
}