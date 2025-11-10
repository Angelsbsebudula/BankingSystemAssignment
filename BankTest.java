public class BankTest {
    public static void main(String[] args) {
        // Setup
        IndividualCustomer customer = new IndividualCustomer("C001", "Angie", "Bee", "123 Honeycomb Lane", "Software Developer");

        // Quick tests
        SavingsAccount savings = new SavingsAccount("SA001", 1500.0, "Gaborone Branch", customer);
        InvestmentAccount investment = new InvestmentAccount("IA001", 800.0, "Gaborone Branch", customer);
        ChequeAccount cheque = new ChequeAccount("CA001", 3000.0, "Gaborone Branch", customer, 1200.0);

        // Test deposits
        savings.deposit(200.0);
        investment.deposit(400.0);
        cheque.deposit(1000.0);

        // Test withdrawals
        savings.withdraw(100.0);  // Should fail (no withdrawals allowed)
        investment.withdraw(300.0); // Should succeed
        cheque.withdraw(4500.0); // Should succeed with overdraft

        // Test interest
        savings.applyInterest();
        investment.applyInterest();

        // Show results
        System.out.println("Angie Bee's Savings: BWP " + savings.getBalance());
        System.out.println("Angie Bee's Investment: BWP " + investment.getBalance());
        System.out.println("Angie Bee's Cheque: BWP " + cheque.getBalance());
    }
}