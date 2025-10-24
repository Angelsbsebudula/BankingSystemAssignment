public class CompanyCustomer extends Customer {
    private String contactPerson;
    private String businessType;
    
    public CompanyCustomer(String customerID, String companyName, String contactPerson, String companyAddress) {
        super(customerID, companyName, companyAddress);
        this.contactPerson = contactPerson;
        this.businessType = "Corporation";
    }
    
    public CompanyCustomer(String customerID, String companyName, String contactPerson, String companyAddress, String businessType) {
        super(customerID, companyName, companyAddress);
        this.contactPerson = contactPerson;
        this.businessType = businessType;
    }
    
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
    
    public boolean canOpenChequeAccount() {
        return true;
    }
    
    @Override
    public String getCustomerType() {
        return "Company";
    }
}
