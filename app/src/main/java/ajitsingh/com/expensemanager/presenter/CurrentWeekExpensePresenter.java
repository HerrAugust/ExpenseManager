package ajitsingh.com.expensemanager.presenter;

import ajitsingh.com.expensemanager.Constants;
import ajitsingh.com.expensemanager.model.ExpenseDatabaseHelper;
import ajitsingh.com.expensemanager.utils.ExpenseCollection;
import ajitsingh.com.expensemanager.utils.SettingsUtil;
import ajitsingh.com.expensemanager.view.CurrentWeekExpenseView;

public class CurrentWeekExpensePresenter {

  private CurrentWeekExpenseView view;
  private ExpenseDatabaseHelper database;
  private ExpenseCollection expenseCollection;

  public CurrentWeekExpensePresenter(ExpenseDatabaseHelper database, CurrentWeekExpenseView view) {
    this.database = database;
    this.view = view;
    String curExpensesDB = new SettingsUtil().get(Constants.settingsCurrentDatabase, Constants.defaultDatabaseName);
    expenseCollection = new ExpenseCollection(this.database.getCurrentWeeksExpenses(curExpensesDB));
  }

  public void renderTotalExpenses() {
    view.displayTotalExpenses(expenseCollection.getTotalExpense());
  }

  public void renderCurrentWeeksExpenses() {
    view.displayCurrentWeeksExpenses(expenseCollection.groupByDate());
  }
}
