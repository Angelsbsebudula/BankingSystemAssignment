public abstract class Account {
    private String accountNumber;
    private double balance;
    private String branch;
    private Customer customer;
    private static final double MIN_DEPOSIT_AMOUNT = 10.00;

    public Account(String accountNumber, double balance, String branch, Customer customer) {
        this.accountNumber = accountNumber;
        this.balance = balance;
        this.branch = branch;
        this.customer = customer;
    }

    public boolean deposit(double amount) {
        if (amount >= MIN_DEPOSIT_AMOUNT) {
            balance += amount;
            return true;
        }
        return false;
    }

    public abstract boolean withdraw(double amount);

    // Getters and Setters
    public String getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    public double getBalance() {
        return balance;
    }

    protected void setBalance(double balance) {
        this.balance = balance;
    }

    public String getBranch() {
        return branch;
    }

    public void setBranch(String branch) {
        this.branch = branch;
    }

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    public static double getMinDepositAmount() {
        return MIN_DEPOSIT_AMOUNT;
    }

    protected boolean hasSufficientFunds(double amount) {
        return amount > 0 && balance >= amount;
    }

    protected boolean isValidDepositAmount(double amount) {
        return amount >= MIN_DEPOSIT_AMOUNT;
    }
}


