package ajitsingh.com.expensemanager.presenter;

import java.util.List;

import ajitsingh.com.expensemanager.Constants;
import ajitsingh.com.expensemanager.model.ExpenseDatabaseHelper;
import ajitsingh.com.expensemanager.model.Expense;
import ajitsingh.com.expensemanager.utils.SettingsUtil;
import ajitsingh.com.expensemanager.view.TodaysExpenseView;

public class TodaysExpensePresenter {

  private TodaysExpenseView view;
  private final List<Expense> expenses;

  public TodaysExpensePresenter(TodaysExpenseView view, ExpenseDatabaseHelper expenseDatabaseHelper) {
    this.view = view;
    String curExpensesDB = new SettingsUtil().get(Constants.settingsCurrentDatabase, Constants.defaultDatabaseName);
    expenses = expenseDatabaseHelper.getTodaysExpenses(curExpensesDB);
  }

  public void renderTotalExpense() {
    Float totalExpense = 0.0f;
    for (Expense expense : expenses)
      totalExpense += expense.getAmount();

    view.displayTotalExpense(totalExpense);
  }

  public void renderTodaysExpenses() {
    view.displayTodaysExpenses(expenses);
  }
}
