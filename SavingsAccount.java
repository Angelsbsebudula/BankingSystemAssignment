public class SavingsAccount extends Account implements InterestPayable {
    private double interestRate = 0.0005;

    public SavingsAccount(String accountNumber, double initialBalance, String branch, Customer customer) {
        super(accountNumber, initialBalance, branch, customer);
    }

    public SavingsAccount(String accountNumber, double initialBalance, String branch,
                          Customer customer, double interestRate) {
        super(accountNumber, initialBalance, branch, customer);
        this.interestRate = interestRate;
    }

    @Override
    public boolean withdraw(double amount) {
        if (hasSufficientFunds(amount)) {
            setBalance(getBalance() - amount);
            return true;
        }
        return false;
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
}