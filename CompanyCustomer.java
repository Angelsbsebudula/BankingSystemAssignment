public class CompanyCustomer extends Customer {
    private String contactPerson;
    private String businessType;

    // Constructor
    public CompanyCustomer(String customerID, String companyName, String contactPerson, String companyAddress) {
        super(customerID, companyName, companyAddress);
        this.contactPerson = contactPerson;
        this.businessType = "Corporation"; // default value
    }

    // Constructor with business type
    public CompanyCustomer(String customerID, String companyName, String contactPerson,
                           String companyAddress, String businessType) {
        super(customerID, companyName, companyAddress);
        this.contactPerson = contactPerson;
        this.businessType = businessType;
    }

    // Getters and Setters
    public String getContactPerson() {
        return contactPerson;
    }

    public void setContactPerson(String contactPerson) {
        this.contactPerson = contactPerson;
    }

    public String getBusinessType() {
        return businessType;
    }

    public void setBusinessType(String businessType) {
        this.businessType = businessType;
    }

    // Business methods
    public boolean canOpenChequeAccount() {
        // Companies can always open cheque accounts
        return true;
    }

    @Override
    public String getCustomerType() {
        return "Company";
    }
}