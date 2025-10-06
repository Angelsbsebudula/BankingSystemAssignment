public class IndividualCustomer extends Customer {
    private String firstName;
    private String surname;
    private String employer;

    public IndividualCustomer(int customerID, String name, String address, String firstName, String surname, String employer) {
        super(customerID, name, address);
        this.firstName = firstName;
        this.surname = surname;
        this.employer = employer;
    }

    public String getEmployer() {
        return employer;
    }
}
