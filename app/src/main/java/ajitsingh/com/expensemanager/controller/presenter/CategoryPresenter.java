package ajitsingh.com.expensemanager.controller.presenter;

import ajitsingh.com.expensemanager.model.ExpenseDatabaseHelper;
import ajitsingh.com.expensemanager.model.ExpenseType;
import ajitsingh.com.expensemanager.view.interfaces.AddCategoryView;

public class CategoryPresenter {
  private final AddCategoryView view;
  private final ExpenseDatabaseHelper database;

  public CategoryPresenter(AddCategoryView view, ExpenseDatabaseHelper database) {
    this.view = view;
    this.database = database;
  }

  public boolean addCategory() {
    String newCategory = view.getCategory();
    if(newCategory.isEmpty()){
      view.displayError();
      return false;
    }

    database.addExpenseType(new ExpenseType(newCategory));
    return true;
  }
}
