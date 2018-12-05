package ajitsingh.com.expensemanager.presenter;

import android.content.Context;

import java.util.LinkedList;
import java.util.List;

import ajitsingh.com.expensemanager.Constants;
import ajitsingh.com.expensemanager.model.ExpenseDatabaseHelper;
import ajitsingh.com.expensemanager.model.Expense;
import ajitsingh.com.expensemanager.model.ExpenseDatabase;
import ajitsingh.com.expensemanager.model.ExpenseType;
import ajitsingh.com.expensemanager.view.ExpenseView;

import static ajitsingh.com.expensemanager.utils.DateUtil.getCurrentDate;

public class ExpensePresenter {

  private ExpenseDatabaseHelper database;
  private ExpenseView view;
  private Context context;

  public ExpensePresenter(ExpenseDatabaseHelper expenseDatabaseHelper, ExpenseView view, Context context) {
    this.database = expenseDatabaseHelper;
    this.view = view;
    this.context = context;
  }

  public boolean addExpense(String expenseDatabaseName) {
    String amount = view.getAmount();

    if(amount.isEmpty()) {
      view.displayError();
      return false;
    }

    ExpenseDatabase expenseDatabase = new ExpenseDatabaseHelper(Constants.defaultAppContext).getExpenseDatabaseByName(expenseDatabaseName);
    Expense expense = new Expense(Float.valueOf(amount), view.getType(), getCurrentDate(), expenseDatabase);
    return database.addExpense(expense);
  }

  public void setExpenseTypes() {
    List<ExpenseType> expenseTypes = database.getExpenseTypes();
    List<String> s = new LinkedList<>();
    for(ExpenseType e : expenseTypes)
      s.add(e.getType());

    view.renderExpenseTypes(s);
  }
}
