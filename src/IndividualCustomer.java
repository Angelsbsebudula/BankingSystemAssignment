public class IndividualCustomer extends Customer {
    private String firstName;
    private String surname;
    private String sourceOfIncome;

    // Constructor
    public IndividualCustomer(String customerID, String firstName, String surname, String address, String sourceOfIncome) {
        super(customerID, firstName + " " + surname, address);
        this.firstName = firstName;
        this.surname = surname;
        this.sourceOfIncome = sourceOfIncome;
    }

    // Getters and Setters
    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
        // Update the name in parent class when first name changes
        setName(firstName + " " + surname);
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
        // Update the name in parent class when surname changes
        setName(firstName + " " + surname);
    }

    public String getSourceOfIncome() {
        return sourceOfIncome;
    }

    public void setSourceOfIncome(String sourceOfIncome) {
        this.sourceOfIncome = sourceOfIncome;
    }

    // Business methods
    public boolean isEmployed() {
        return sourceOfIncome != null && !sourceOfIncome.trim().isEmpty();
    }

    public boolean canOpenChequeAccount() {
        return isEmployed();
    }

    @Override
    public String getCustomerType() {
        return "Individual";
    }
}