public class InvestmentAccount extends Account implements Withdrawable, InterestPayable {
    private double interestRate = 0.05;
    private static final double MIN_OPENING_BALANCE = 500.00;

    public InvestmentAccount(String accountNumber, double initialBalance, String branch, Customer customer) {
        super(accountNumber, initialBalance, branch, customer);
        validateOpeningBalance(initialBalance);
    }

    private void validateOpeningBalance(double initialBalance) {
        if (initialBalance < MIN_OPENING_BALANCE) {
            throw new IllegalArgumentException("Investment account requires minimum opening balance of BWP " + MIN_OPENING_BALANCE);
        }
    }

    @Override
    public boolean withdraw(double amount) {
        if (canWithdraw(amount)) {
            setBalance(getBalance() - amount);
            return true;
        }
        return false;
    }

    @Override
    public boolean canWithdraw(double amount) {
        return amount > 0 && (getBalance() - amount) >= MIN_OPENING_BALANCE;
    }

    @Override
    public void applyInterest() {
        double interest = getBalance() * interestRate;
        setBalance(getBalance() + interest);
    }

    @Override
    public double getInterestRate() {
        return interestRate;
    }

    @Override
    public void setInterestRate(double interestRate) {
        this.interestRate = interestRate;
    }

    public static double getMinOpeningBalance() {
        return MIN_OPENING_BALANCE;
    }
}