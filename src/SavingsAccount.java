public class SavingsAccount extends Account implements InterestBearing {
    private double interestRate = 0.0005; // 0.05%

    public SavingsAccount(String accountNumber, double balance, String branch, Customer customer) {
        super(accountNumber, balance, branch, customer);
    }

    @Override
    public void withdraw(double amount) {
        System.out.println("You cannot withdraw from a Savings Account.");
    }

    @Override
    public void applyInterest() {
        double interest = balance * interestRate;
        balance += interest;
        System.out.println("Interest of " + interest + " added to Savings Account.");
    }
}
