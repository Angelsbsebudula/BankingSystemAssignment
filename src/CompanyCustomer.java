public class CompanyCustomer extends Customer {
    private String companyName;
    private String contactPerson;

    public CompanyCustomer(int customerID, String name, String address, String companyName, String contactPerson) {
        super(customerID, name, address);
        this.companyName = companyName;
        this.contactPerson = contactPerson;
    }
}
