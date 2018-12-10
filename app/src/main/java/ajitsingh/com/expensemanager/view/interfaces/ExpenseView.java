package ajitsingh.com.expensemanager.view.interfaces;

import java.util.Date;
import java.util.List;

public interface ExpenseView {
  String getAmount();
  String getType();
  void renderExpenseTypes(List<String> expenseTypes);
  void displayError();
  Date getDate();
}
