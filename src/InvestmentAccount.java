public class InvestmentAccount extends Account implements InterestBearing {
    private double interestRate = 0.05; // 5%

    public InvestmentAccount(String accountNumber, double balance, String branch, Customer customer) {
        super(accountNumber, balance, branch, customer);
        if (balance < 500) {
            System.out.println("Warning: Investment Account should start with at least BWP 500.");
        }
    }

    @Override
    public void applyInterest() {
        double interest = balance * interestRate;
        balance += interest;
        System.out.println("Interest of " + interest + " added to Investment Account.");
    }
}
