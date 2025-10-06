public class BankTest {
    public static void main(String[] args) {
        IndividualCustomer cust1 = new IndividualCustomer(1, "John Doe", "Gaborone", "John", "Doe", "ABC Ltd");
        CompanyCustomer cust2 = new CompanyCustomer(2, "ABC Ltd", "Francistown", "ABC Ltd", "Mary Smith");

        SavingsAccount savings = new SavingsAccount("S001", 1000, "Main Branch", cust1);
        InvestmentAccount invest = new InvestmentAccount("I001", 5000, "Main Branch", cust2);
        ChequeAccount cheque = new ChequeAccount("C001", 200, "Main Branch", cust1, 500);

        cust1.openAccount(savings);
        cust1.openAccount(cheque);
        cust2.openAccount(invest);

        System.out.println("\n--- Initial Balances ---");
        System.out.println("Savings: " + savings.getBalance());
        System.out.println("Investment: " + invest.getBalance());
        System.out.println("Cheque: " + cheque.getBalance());

        savings.deposit(200);
        invest.withdraw(300);
        cheque.withdraw(600);

        savings.applyInterest();
        invest.applyInterest();
        cheque.applyInterest();

        System.out.println("\n--- Final Balances ---");
        System.out.println("Savings: " + savings.getBalance());
        System.out.println("Investment: " + invest.getBalance());
        System.out.println("Cheque: " + cheque.getBalance());
    }
}
