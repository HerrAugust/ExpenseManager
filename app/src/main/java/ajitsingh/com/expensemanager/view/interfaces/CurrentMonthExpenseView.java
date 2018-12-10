package ajitsingh.com.expensemanager.view.interfaces;

import com.echo.holographlibrary.Bar;

import java.util.List;

public interface CurrentMonthExpenseView {
  void displayGraph(List<Bar> points);

  void displayTotalExpense(Float totalExpense);

  int getGraphColor();
}
