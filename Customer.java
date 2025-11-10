public abstract class Customer {
    private String customerID;
    private String name;
    private String address;

    public Customer(String customerID, String name, String address) {
        this.customerID = customerID;
        this.name = name;
        this.address = address;
    }

    // Getters and Setters
    public String getCustomerID() {
        return customerID;
    }

    public void setCustomerID(String customerID) {
        this.customerID = customerID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public abstract String getCustomerType();
}