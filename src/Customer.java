import java.util.ArrayList;

public abstract class Customer {
    protected int customerID;
    protected String name;
    protected String address;
    protected ArrayList<Account> accounts;

    public Customer(int customerID, String name, String address) {
        this.customerID = customerID;
        this.name = name;
        this.address = address;
        accounts = new ArrayList<>();
    }

    public void openAccount(Account account) {
        accounts.add(account);
    }

    public void showCustomerInfo() {
        System.out.println("Customer ID: " + customerID);
        System.out.println("Name: " + name);
        System.out.println("Address: " + address);
        System.out.println("Accounts owned: " + accounts.size());
    }
}

