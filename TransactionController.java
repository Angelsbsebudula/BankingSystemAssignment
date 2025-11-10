import javafx.fxml.FXML;
import javafx.scene.control.*;

public class TransactionController {
    
    @FXML private TextField txtAccountNumber;
    @FXML private TextField txtAmount;
    @FXML private Button btnDeposit;
    @FXML private Button btnWithdraw;
    @FXML private Label lblResult;
    
    private BankSystem bankSystem;
    private MainController mainController;
    
    public void setBankSystem(BankSystem bankSystem) {
        this.bankSystem = bankSystem;
    }
    
    public void setMainController(MainController mainController) {
        this.mainController = mainController;
    }
    
    @FXML
    public void initialize() {
        lblResult.setVisible(false);
    }
    
    @FXML
    private void handleDeposit() {
        processTransaction("Deposit");
    }
    
    @FXML
    private void handleWithdraw() {
        processTransaction("Withdraw");
    }
    
    private void processTransaction(String transactionType) {
        try {
            String accountNumber = txtAccountNumber.getText().trim();
            double amount = Double.parseDouble(txtAmount.getText());
            
            // Validate inputs
            if (accountNumber.isEmpty()) {
                showResult("Please enter an account number", false);
                return;
            }
            
            if (amount <= 0) {
                showResult("Please enter a positive amount", false);
                return;
            }
            
            Account account = bankSystem.getAccount(accountNumber);
            if (account == null) {
                showResult("Account not found", false);
                return;
            }
            
            // Check access permissions for customers
            if (!canAccessAccount(account)) {
                showResult("You don't have permission to access this account", false);
                return;
            }
            
            boolean success = false;
            String message = "";
            
            if ("Deposit".equals(transactionType)) {
                success = account.deposit(amount);
                message = success ? "Deposit successful!" : "Deposit failed - minimum deposit is BWP 10.00";
            } else {
                success = account.withdraw(amount);
                message = success ? "Withdrawal successful!" : "Withdrawal failed - insufficient funds or invalid amount";
            }
            
            if (success) {
                // Create and save transaction
                String accountType = account.getClass().getSimpleName().replace("Account", "");
                Transaction transaction = new Transaction(accountNumber, amount, transactionType, accountType);
                bankSystem.addTransaction(transaction);
                
                // Save to file
                saveTransactionToFile(accountNumber, amount, transactionType, accountType);
                
                // Refresh other controllers
                if (mainController != null) {
                    mainController.refreshAllData();
                }
                
                // Clear amount field
                txtAmount.clear();
                
                showResult(message + " New balance: BWP " + account.getBalance(), true);
            } else {
                showResult(message, false);
            }
            
        } catch (NumberFormatException e) {
            showResult("Please enter a valid amount", false);
        } catch (Exception e) {
            showResult("Transaction failed: " + e.getMessage(), false);
            e.printStackTrace();
        }
    }
    
    private boolean canAccessAccount(Account account) {
        User currentUser = bankSystem.getCurrentUser();
        if (currentUser == null) return false;
        
        if (currentUser.isTeller()) {
            return true; // Tellers can access all accounts
        }
        
        // Customers can only access their own accounts
        return account.getCustomer().getCustomerID().equals(currentUser.getCustomerID());
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
        lblResult.setText(message);
        lblResult.setStyle(isSuccess ? 
            "-fx-text-fill: #27ae60; -fx-font-weight: bold;" : 
            "-fx-text-fill: #e74c3c; -fx-font-weight: bold;");
        lblResult.setVisible(true);
    }
    
    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}