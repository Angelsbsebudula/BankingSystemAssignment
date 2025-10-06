public class ChequeAccount extends Account implements InterestBearing {
    private double overdraftLimit;

    public ChequeAccount(String accountNumber, double balance, String branch, IndividualCustomer customer, double overdraftLimit) {
        super(accountNumber, balance, branch, customer);
        if (customer.getEmployer() == null || customer.getEmployer().isEmpty()) {
            System.out.println("Cheque Account can only be opened for employed people.");
        }
        this.overdraftLimit = overdraftLimit;
    }

    @Override
    public void withdraw(double amount) {
        if (amount > 0 && balance + overdraftLimit >= amount) {
            balance -= amount;
            System.out.println("Withdrew BWP " + amount + " using Cheque Account.");
        } else {
            System.out.println("Overdraft limit reached. Withdrawal denied.");
        }
    }

    @Override
    public void applyInterest() {
        System.out.println("Cheque Account does not earn interest.");
    }
}
