package ajitsingh.com.expensemanager.view;

import java.util.Date;
import java.util.List;

public interface ExpenseView {
  String getAmount();
  String getType();
  void renderExpenseTypes(List<String> expenseTypes);
  void displayError();
  Date getDate();
}
