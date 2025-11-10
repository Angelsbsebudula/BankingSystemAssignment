import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;

public class TransactionHistoryController {
    
    @FXML private TextField txtFilterAccount;
    @FXML private Button btnFilter;
    @FXML private Button btnClearFilter;
    @FXML private TableView<Transaction> transactionTable;
    @FXML private TableColumn<Transaction, String> colAccNumber;
    @FXML private TableColumn<Transaction, String> colAccountType;
    @FXML private TableColumn<Transaction, Double> colAmount;
    @FXML private TableColumn<Transaction, String> colType;
    @FXML private TableColumn<Transaction, String> colDate;
    @FXML private Label lblSummary;
    
    private BankSystem bankSystem;
    private ObservableList<Transaction> transactionList = FXCollections.observableArrayList();
    private FilteredList<Transaction> filteredTransactions;
    
    public void setBankSystem(BankSystem bankSystem) {
        this.bankSystem = bankSystem;
        refreshTransactions();
    }
    
    // ADD THIS MISSING METHOD:
    public void setMainController(MainController mainController) {
        // This method might be needed for coordination
    }
    
    @FXML
    public void initialize() {
        setupTransactionTable();
    }
    
    private void setupTransactionTable() {
        // Configure table columns
        colAccNumber.setCellValueFactory(cellData -> 
            cellData.getValue().accountNumberProperty());
        
        colAccountType.setCellValueFactory(cellData -> 
            cellData.getValue().accountTypeProperty());
        
        colAmount.setCellValueFactory(cellData -> 
            cellData.getValue().amountProperty().asObject());
        
        colType.setCellValueFactory(cellData -> 
            cellData.getValue().typeProperty());
        
        colDate.setCellValueFactory(cellData -> 
            cellData.getValue().dateProperty());
        
        // Initialize filtered list
        filteredTransactions = new FilteredList<>(transactionList);
        transactionTable.setItems(filteredTransactions);
        
        updateSummary();
    }
    
    @FXML
    private void handleFilter() {
        String filterAccount = txtFilterAccount.getText().trim();
        
        if (filterAccount.isEmpty()) {
            filteredTransactions.setPredicate(transaction -> true);
        } else {
            filteredTransactions.setPredicate(transaction -> 
                transaction.getAccountNumber().toLowerCase().contains(filterAccount.toLowerCase()));
        }
        
        updateSummary();
    }
    
    @FXML
    private void handleClearFilter() {
        txtFilterAccount.clear();
        filteredTransactions.setPredicate(transaction -> true);
        updateSummary();
    }
    
    public void refreshTransactions() {
        if (bankSystem != null) {
            transactionList.setAll(bankSystem.getAllTransactions());
            updateSummary();
        }
    }
    
    private void updateSummary() {
        int totalTransactions = filteredTransactions.size();
        
        if (totalTransactions == 0) {
            lblSummary.setText("No transactions found");
            return;
        }
        
        double totalDeposits = filteredTransactions.stream()
            .filter(t -> "Deposit".equals(t.getType()))
            .mapToDouble(Transaction::getAmount)
            .sum();
            
        double totalWithdrawals = filteredTransactions.stream()
            .filter(t -> "Withdraw".equals(t.getType()))
            .mapToDouble(Transaction::getAmount)
            .sum();
        
        lblSummary.setText(String.format(
            "Total Transactions: %d | Total Deposits: BWP %.2f | Total Withdrawals: BWP %.2f",
            totalTransactions, totalDeposits, totalWithdrawals
        ));
    }
}