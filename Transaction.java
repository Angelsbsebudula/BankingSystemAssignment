import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Transaction {
    private SimpleStringProperty accountNumber;
    private SimpleDoubleProperty amount;
    private SimpleStringProperty type;
    private SimpleStringProperty accountType;
    private SimpleStringProperty date;

    public Transaction(String accountNumber, double amount, String type, String accountType) {
        this.accountNumber = new SimpleStringProperty(accountNumber);
        this.amount = new SimpleDoubleProperty(amount);
        this.type = new SimpleStringProperty(type);
        this.accountType = new SimpleStringProperty(accountType);
        this.date = new SimpleStringProperty(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")));
    }

    // Property methods for TableView
    public SimpleStringProperty accountNumberProperty() { return accountNumber; }
    public SimpleDoubleProperty amountProperty() { return amount; }
    public SimpleStringProperty typeProperty() { return type; }
    public SimpleStringProperty accountTypeProperty() { return accountType; }
    public SimpleStringProperty dateProperty() { return date; }

    // Getters
    public String getAccountNumber() { return accountNumber.get(); }
    public double getAmount() { return amount.get(); }
    public String getType() { return type.get(); }
    public String getAccountType() { return accountType.get(); }
    public String getDate() { return date.get(); }
}