public class User {
    private String username;
    private String password;
    private boolean isTeller;  // true = Bank Teller, false = Customer
    private String customerID; // only for customers

    // Constructor for CUSTOMERS
    public User(String username, String password, String customerID) {
        this.username = username;
        this.password = password;
        this.isTeller = false;  // Always false for customers
        this.customerID = customerID;
    }

    // Constructor for BANK TELLERS  
    public User(String username, String password) {
        this.username = username;
        this.password = password;
        this.isTeller = true;   // Always true for tellers
        this.customerID = null; // Tellers don't have customer IDs
    }

    // Getters
    public String getUsername() { return username; }
    public String getPassword() { return password; }
    public boolean isTeller() { return isTeller; }
    public boolean isCustomer() { return !isTeller; } // If not teller, then customer
    public String getCustomerID() { return customerID; }
}