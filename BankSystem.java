import java.io.*;
import java.util.*;

public class BankSystem {

    private Map<String, User> users = new HashMap<>();
    private List<Customer> customers = new ArrayList<>();
    private List<Account> accounts = new ArrayList<>();
    private List<Transaction> transactions = new ArrayList<>();
    private User currentUser;

    public BankSystem() {
        loadUsersFromFile();
        loadCustomersFromFile();
        loadAccountsFromFile();
    }

    // ===== LOGIN / LOGOUT =====
    public boolean authenticateUser(String username, String password) {
        User user = users.get(username);
        if (user != null && user.getPassword().equals(password)) {
            currentUser = user;
            return true;
        }
        return false;
    }

    public void logout() {
        currentUser = null;
    }

    public User getCurrentUser() {
        return currentUser;
    }

    // ===== CREATE CUSTOMER =====
    public String createCustomer(String firstName, String lastName, String address,
                                 String occupation, String accountType, String branch) {
        String customerID = "C" + (customers.size() + 1);
        String username = firstName.toLowerCase() + customerID;
        String password = "pass" + (1000 + new Random().nextInt(9000));

        Customer customer = new IndividualCustomer(customerID, firstName, lastName, address, occupation);
        customers.add(customer);

        User user = new User(username, password, customerID);
        users.put(username, user);

        String accountNumber = "A" + (accounts.size() + 1);
        Account acc = null;

        switch (accountType.toLowerCase()) {
            case "savings" -> acc = new SavingsAccount(accountNumber, 0.0, branch, customer);
            case "cheque" -> acc = new ChequeAccount(accountNumber, 0.0, branch, customer, 1000);
            case "investment" -> acc = new InvestmentAccount(accountNumber, 500.0, branch, customer);
            default -> throw new IllegalArgumentException("Invalid account type.");
        }

        if (acc != null) accounts.add(acc);

        saveUserToFile(username, password, customerID);
        saveCustomerToFile(customer);
        saveAccountToFile(acc);

        return String.format("""
                ✅ Customer Created Successfully!
                Customer ID: %s
                Username: %s
                Password: %s
                Account Number: %s
                Account Type: %s
                Branch: %s
                """, customerID, username, password, accountNumber, accountType, branch);
    }

    // ===== CREATE ACCOUNT =====
    public void createAccount(String accNum, String type, double balance, String branch, String customerID) {
        if (balance < 0) {
            throw new IllegalArgumentException("Balance cannot be negative.");
        }
        
        Customer c = findCustomerByID(customerID);
        if (c == null) throw new IllegalArgumentException("Customer not found.");

        Account acc;
        switch (type.toLowerCase()) {
            case "savings" -> acc = new SavingsAccount(accNum, balance, branch, c);
            case "cheque" -> acc = new ChequeAccount(accNum, balance, branch, c, 1000);
            case "investment" -> acc = new InvestmentAccount(accNum, balance, branch, c);
            default -> throw new IllegalArgumentException("Invalid account type.");
        }

        accounts.add(acc);
        saveAccountToFile(acc);
    }

    // ===== DEPOSIT & WITHDRAW =====
    public boolean deposit(String accNum, double amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException("Amount must be positive.");
        }
        
        Account acc = findAccount(accNum);
        if (acc == null) return false;
        
        boolean success = acc.deposit(amount);
        if (success) {
            Transaction t = new Transaction(accNum, amount, "Deposit", acc.getClass().getSimpleName());
            transactions.add(t);
            saveTransactionToFile(t);
        }
        return success;
    }

    public boolean withdraw(String accNum, double amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException("Amount must be positive.");
        }
        
        Account acc = findAccount(accNum);
        if (acc == null) return false;
        
        boolean success = acc.withdraw(amount);
        if (success) {
            Transaction t = new Transaction(accNum, amount, "Withdraw", acc.getClass().getSimpleName());
            transactions.add(t);
            saveTransactionToFile(t);
        }
        return success;
    }

    // ===== INTEREST =====
    public List<InterestResult> applyInterest(String type) {
        List<InterestResult> results = new ArrayList<>();
        for (Account acc : accounts) {
            double oldBalance = acc.getBalance();
            double interest = 0;

            if (acc instanceof SavingsAccount s && (type.equalsIgnoreCase("savings") || type.equalsIgnoreCase("all"))) {
                double newBal = s.getBalance() * 1.05; // 5% interest
                interest = newBal - s.getBalance();
                s.setBalance(newBal);
            }

            if (acc instanceof InvestmentAccount i && (type.equalsIgnoreCase("investment") || type.equalsIgnoreCase("all"))) {
                double newBal = i.getBalance() * 1.10; // 10% interest
                interest = newBal - i.getBalance();
                i.setBalance(newBal);
            }

            if (interest > 0)
                results.add(new InterestResult(acc.getAccountNumber(),
                        acc.getClass().getSimpleName().replace("Account", ""),
                        oldBalance, interest, acc.getBalance()));
        }
        return results;
    }

    // ===== FILE SAVE / LOAD =====
    private void saveUserToFile(String username, String password, String customerID) {
        try (FileWriter fw = new FileWriter("users.txt", true)) {
            fw.write("USER|" + username + "|" + password + "|" + customerID + "\n");
        } catch (IOException e) {
            System.err.println("Error saving user: " + e.getMessage());
        }
    }

    private void saveCustomerToFile(Customer c) {
        try (FileWriter fw = new FileWriter("customers.txt", true)) {
            fw.write(c.getCustomerID() + "," + c.getAddress() + "\n");
        } catch (IOException e) {
            System.err.println("Error saving customer: " + e.getMessage());
        }
    }

    private void saveAccountToFile(Account a) {
        try (FileWriter fw = new FileWriter("accounts.txt", true)) {
            fw.write(a.getAccountNumber() + "," + a.getBranch() + "," + a.getCustomer().getCustomerID() + "," + a.getBalance() + "\n");
        } catch (IOException e) {
            System.err.println("Error saving account: " + e.getMessage());
        }
    }

    private void saveTransactionToFile(Transaction t) {
        try (FileWriter fw = new FileWriter("transactions.txt", true)) {
            fw.write(t.getAccountNumber() + "," + t.getAmount() + "," + 
                     t.getType() + "," + t.getDate() + "\n");
        } catch (IOException e) {
            System.err.println("Error saving transaction: " + e.getMessage());
        }
    }

    public void loadUsersFromFile() {
        users.put("teller", new User("teller", "123"));
        users.put("manager", new User("manager", "123"));

        try (Scanner sc = new Scanner(new File("users.txt"))) {
            while (sc.hasNextLine()) {
                String line = sc.nextLine();
                if (line.startsWith("USER|")) {
                    String[] parts = line.split("\\|");
                    if (parts.length >= 4) {
                        String username = parts[1];
                        String password = parts[2];
                        String customerID = parts[3];
                        users.put(username, new User(username, password, customerID));
                    }
                }
            }
        } catch (FileNotFoundException e) {
            System.out.println("No users.txt found, will create when new users register.");
        }
    }

    // ADDED: Load customers from file
    private void loadCustomersFromFile() {
        try (Scanner sc = new Scanner(new File("customers.txt"))) {
            while (sc.hasNextLine()) {
                String line = sc.nextLine().trim();
                if (!line.isEmpty()) {
                    String[] parts = line.split(",");
                    if (parts.length >= 2) {
                        String customerID = parts[0];
                        String address = parts[1];
                        // FIX: Use empty strings to match your customer creation
                        Customer customer = new IndividualCustomer(customerID, "", "", address, "");
                        customers.add(customer);
                    }
                }
            }
        } catch (FileNotFoundException e) {
            System.out.println("No customers.txt found yet");
        }
    }

    // ADDED: Load accounts from file
    private void loadAccountsFromFile() {
        try (Scanner sc = new Scanner(new File("accounts.txt"))) {
            while (sc.hasNextLine()) {
                String line = sc.nextLine().trim();
                if (!line.isEmpty()) {
                    String[] parts = line.split(",");
                    if (parts.length >= 4) {
                        String accountNumber = parts[0];
                        String branch = parts[1];
                        String customerID = parts[2];
                        double balance = Double.parseDouble(parts[3]);
                        
                        Customer customer = findCustomerByID(customerID);
                        if (customer != null) {
                            Account account = new SavingsAccount(accountNumber, balance, branch, customer);
                            accounts.add(account);
                        }
                    }
                }
            }
        } catch (FileNotFoundException e) {
            System.out.println("No accounts.txt found yet");
        }
    }

    // ===== FINDERS =====
    private Account findAccount(String accNum) {
        for (Account a : accounts)
            if (a.getAccountNumber().equals(accNum)) return a;
        return null;
    }

    private Customer findCustomerByID(String id) {
        for (Customer c : customers)
            if (c.getCustomerID().equals(id)) return c;
        return null;
    }

    public String getAccountsByCustomerID(String customerID) {
        StringBuilder sb = new StringBuilder();
        for (Account acc : accounts)
            if (acc.getCustomer().getCustomerID().equals(customerID))
                sb.append(acc.getAccountNumber()).append(" ");
        return sb.toString().trim();
    }

    public List<Account> getAllAccounts() {
        return new ArrayList<>(accounts);
    }

    // ===== NEW METHOD FOR TRANSACTION HISTORY =====
    public List<Transaction> getAllTransactions() {
        return new ArrayList<>(transactions);
    }

    // ===== INTEREST RESULT CLASS =====
    public static class InterestResult {
        private String accountNumber;
        private String accountType;
        private double oldBalance;
        private double interestAmount;
        private double newBalance;

        public InterestResult(String accNo, String accType, double oldBal, double interest, double newBal) {
            this.accountNumber = accNo;
            this.accountType = accType;
            this.oldBalance = oldBal;
            this.interestAmount = interest;
            this.newBalance = newBal;
        }

        public String getAccountNumber() { return accountNumber; }
        public String getAccountType() { return accountType; }
        public double getOldBalance() { return oldBalance; }
        public double getInterestAmount() { return interestAmount; }
        public double getNewBalance() { return newBalance; }
    }
}