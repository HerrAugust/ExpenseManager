package ajitsingh.com.expensemanager.view;

import java.util.List;

import ajitsingh.com.expensemanager.model.Expense;

public interface TodaysExpenseView {
  void displayTotalExpense(Float totalExpense);
  void displayTodaysExpenses(List<Expense> expenses);
}
