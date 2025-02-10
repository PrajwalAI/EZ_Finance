package EZ_Finance;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PiePlot;
import org.jfree.data.general.DefaultPieDataset;
import org.jfree.data.category.DefaultCategoryDataset;

import javax.swing.*;
import java.time.LocalDate;
import java.util.*;

public class BudgetManager {
    public static void main(String[] args) {
        UserAccount[] userAccounts = new UserAccount[100];
        Scanner inputScanner = new Scanner(System.in);
        Authentication authentication = new Authentication();
        Dashboard dashboard = new Dashboard();
        
        int userSelection;
        int totalUsers = 0;

        do {
            System.out.println("Enter 1 for SignUp");
            System.out.println("Enter 2 for Login");
            System.out.println("Enter 3 To Exit");
            System.out.print("Enter Number: ");
            userSelection = inputScanner.nextInt();
            inputScanner.nextLine();

            switch (userSelection) {
                case 1:
                    userAccounts[totalUsers] = new UserAccount();
                    userAccounts[totalUsers].collectUserDetails(userAccounts, totalUsers);
                    totalUsers++;
                    break;
                case 2:
                    int activeUser = authentication.validateLogin(userAccounts, totalUsers);
                    dashboard.showDashboard(userAccounts[activeUser]);
                    break;
                case 3:
                    break;
                default:
                    System.out.println("\nERROR Press from 1 to 3");
                    break;
            }
        } while (userSelection != 3);
    }
}

class UserAccount {
    String givenName, surname, contactNumber, securityCode;
    long accountBalance = 0;
    int expenseCounter = 0, incomeCounter = 0;
    Spending[] spendingRecords = new Spending[1000];
    Earnings[] earningRecords = new Earnings[1000];
    Scanner inputScanner = new Scanner(System.in);

    void collectUserDetails(UserAccount[] users, int userCount) {
        System.out.print("Enter First Name: ");
        givenName = inputScanner.nextLine();
        System.out.print("Enter Last Name: ");
        surname = inputScanner.nextLine();
        System.out.print("Enter Mobile Number: ");
        contactNumber = inputScanner.nextLine();
        System.out.print("Enter 4 Digit Security Pin: ");
        securityCode = inputScanner.nextLine();
    }
}

class Authentication {
    Scanner inputScanner = new Scanner(System.in);
    int validateLogin(UserAccount[] users, int userCount) {
        String mobile, pin;
        while (true) {
            System.out.print("Enter Mobile Number: ");
            mobile = inputScanner.nextLine();
            for (int i = 0; i < userCount; i++) {
                if (mobile.equals(users[i].contactNumber)) {
                    System.out.print("Enter Pin: ");
                    pin = inputScanner.nextLine();
                    if (pin.equals(users[i].securityCode)) {
                        return i;
                    }
                }
            }
            System.out.println("Invalid Credentials, Try Again!");
        }
    }
}

class Dashboard {
    Scanner inputScanner = new Scanner(System.in);
    void showDashboard(UserAccount user) {
        int option;
        do {
            System.out.println("1) Add Expense");
            System.out.println("2) Add Income");
            System.out.println("3) Show Expense Chart");
            System.out.println("4) Show Income vs Expense Chart");
            System.out.println("5) Exit");
            System.out.print("Enter Number: ");
            option = inputScanner.nextInt();

            switch (option) {
                case 1:
                    user.spendingRecords[user.expenseCounter] = new Spending();
                    user.spendingRecords[user.expenseCounter].recordExpense(user);
                    user.expenseCounter++;
                    break;
                case 2:
                    user.earningRecords[user.incomeCounter] = new Earnings();
                    user.earningRecords[user.incomeCounter].recordIncome(user);
                    user.incomeCounter++;
                    break;
                case 3:
                    DataVisualization.displayExpenseChart(user);
                    break;
                case 4:
                    DataVisualization.displayIncomeVsExpenseChart(user);
                    break;
                case 5:
                    break;
                default:
                    System.out.println("Invalid choice, try again.");
            }
        } while (option != 5);
    }
}

class Spending {
    long cost;
    String details, type;
    LocalDate recordDate;
    Scanner inputScanner = new Scanner(System.in);

    void recordExpense(UserAccount user) {
        System.out.print("Enter Expense: ");
        cost = inputScanner.nextLong();
        inputScanner.nextLine();
        System.out.print("Enter Description: ");
        details = inputScanner.nextLine();
        System.out.print("Enter Category: ");
        type = inputScanner.nextLine();
        recordDate = LocalDate.now();
        user.accountBalance -= cost;
    }
}

class Earnings {
    long revenue;
    String details, type;
    LocalDate recordDate;
    Scanner inputScanner = new Scanner(System.in);

    void recordIncome(UserAccount user) {
        System.out.print("Enter Income: ");
        revenue = inputScanner.nextLong();
        inputScanner.nextLine();
        System.out.print("Enter Description: ");
        details = inputScanner.nextLine();
        System.out.print("Enter Category: ");
        type = inputScanner.nextLine();
        recordDate = LocalDate.now();
        user.accountBalance += revenue;
    }
}

class DataVisualization {
    public static void displayExpenseChart(UserAccount user) {
        Map<String, Long> categorySummary = new HashMap<>();
        for (int i = 0; i < user.expenseCounter; i++) {
            Spending exp = user.spendingRecords[i];
            categorySummary.put(exp.type, categorySummary.getOrDefault(exp.type, 0L) + exp.cost);
        }
        DefaultPieDataset dataset = new DefaultPieDataset();
        for (Map.Entry<String, Long> entry : categorySummary.entrySet()) {
            dataset.setValue(entry.getKey(), entry.getValue());
        }
        JFreeChart chart = ChartFactory.createPieChart("Expense Distribution", dataset, true, true, false);
        JFrame frame = new JFrame("Expense Chart");
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.add(new ChartPanel(chart));
        frame.pack();
        frame.setVisible(true);
    }

    public static void displayIncomeVsExpenseChart(UserAccount user) {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        long totalIncome = Arrays.stream(user.earningRecords).filter(Objects::nonNull).mapToLong(i -> i.revenue).sum();
        long totalExpense = Arrays.stream(user.spendingRecords).filter(Objects::nonNull).mapToLong(e -> e.cost).sum();
        dataset.addValue(totalIncome, "Income", "Total");
        dataset.addValue(totalExpense, "Expense", "Total");
        JFreeChart barChart = ChartFactory.createBarChart("Income vs Expense", "Category", "Amount", dataset);
        JFrame frame = new JFrame("Income vs Expense Chart");
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.add(new ChartPanel(barChart));
        frame.pack();
        frame.setVisible(true);
    }
}
