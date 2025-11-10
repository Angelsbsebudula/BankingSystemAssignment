public class ChequeAccount extends Account implements Withdrawable {
    private double chequeOverdraft;

    public ChequeAccount(String accountNumber, double initialBalance, String branch,
                         Customer customer, double chequeOverdraft) {
        super(accountNumber, initialBalance, branch, customer);
        this.chequeOverdraft = chequeOverdraft;

        if (customer instanceof IndividualCustomer) {
            IndividualCustomer individual = (IndividualCustomer) customer;
            if (!individual.canOpenChequeAccount()) {
                throw new IllegalArgumentException("Cheque account can only be opened for employed individuals");
            }
        }
    }

    @Override
    public boolean withdraw(double amount) {
        if (amount > 0 && (getBalance() + chequeOverdraft) >= amount) {
            setBalance(getBalance() - amount);
            return true;
        }
        return false;
    }

    @Override
    public boolean canWithdraw(double amount) {
        return amount > 0 && (getBalance() + chequeOverdraft) >= amount;
    }

    public double getChequeOverdraft() {
        return chequeOverdraft;
    }

    public void setChequeOverdraft(double chequeOverdraft) {
        this.chequeOverdraft = chequeOverdraft;
    }
}