package ajitsingh.com.expensemanager.model.table;

import android.provider.BaseColumns;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import ajitsingh.com.expensemanager.utils.DateUtil;

public class ExpenseTable implements BaseColumns {
  public static final String TABLE_NAME = "expenses";
  public static final String AMOUNT = "amount";
  public static final String TYPE = "type";
  public static final String DATE = "date";
  public static final String EXPENSE_DATABASE_ID = "expense_database_id";

  public static final String CREATE_TABLE_QUERY = "create table " + TABLE_NAME + " ("+
                                                      _ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "+
                                                      AMOUNT +" INTEGER,"+
                                                      TYPE +" TEXT, "+ /* TODO: why has he used ExpenseType so? */
                                                      DATE +" DATE, " +
                                                      EXPENSE_DATABASE_ID + " INTEGER," +
                                                      "FOREIGN KEY(" + EXPENSE_DATABASE_ID + ") REFERENCES " + ExpenseDatabaseTable.TABLE_NAME + "(" + ExpenseDatabaseTable._ID + ")" +
                                                  ");";

  public static final String SELECT_ALL = "SELECT * FROM " + TABLE_NAME + " ORDER BY " + _ID + " DESC";

  public static String getExpensesForDate(Date date, int expeseDatabaseID){
    String dateStr = DateUtil.dateToString(date);
    String query = "SELECT " + ExpenseTable.TABLE_NAME + "." + "* FROM " + TABLE_NAME + " JOIN " + ExpenseDatabaseTable.TABLE_NAME +
      " ON " + ExpenseDatabaseTable.TABLE_NAME + "." + ExpenseDatabaseTable._ID + " = " + ExpenseTable.TABLE_NAME  + "." + EXPENSE_DATABASE_ID +
            " WHERE " + ExpenseDatabaseTable.TABLE_NAME + "." + ExpenseDatabaseTable._ID + " = " + expeseDatabaseID +
            " AND date like '" + dateStr + "%' ORDER BY " + _ID + " DESC";
    return query;
  }

  public static String getConsolidatedExpensesForDates(ArrayList<String> dates, int expeseDatabaseID) {
    String dateLike = "";
    for (String date : dates){
      dateLike += "date like '" + date + "%' " + (dates.get(dates.size() - 1) == date ? "" : "or ");
    }

    dateLike = "SELECT expenses." + _ID + ", expenses.date, expenses.type, sum(expenses.amount) as amount FROM " + TABLE_NAME + " JOIN " + ExpenseDatabaseTable.TABLE_NAME +
            " ON " + ExpenseDatabaseTable.TABLE_NAME + "." + ExpenseDatabaseTable._ID + " = " + ExpenseTable.TABLE_NAME  + "." + EXPENSE_DATABASE_ID +
            " WHERE " + ExpenseDatabaseTable.TABLE_NAME + "." + ExpenseDatabaseTable._ID + " = " + expeseDatabaseID +
            " AND " + dateLike + " GROUP BY expenses.date, expenses.type";
    return dateLike;
  }

  // TODO can be simplified if date is of type DATE?
  public static String getExpenseForCurrentMonth(String currentMonthOfYear, int expeseDatabaseID) {
    String currentMonthsExpenses = "(SELECT expenses." + _ID + ", expenses.date, expenses.type, expenses.amount FROM " +
      TABLE_NAME + " JOIN " + ExpenseDatabaseTable.TABLE_NAME + // added by agost
            " ON " + ExpenseDatabaseTable.TABLE_NAME + "." + ExpenseDatabaseTable._ID + " = " + ExpenseTable.TABLE_NAME  + "." + EXPENSE_DATABASE_ID + // added by agost. TODO CORRECT?
            " WHERE " + ExpenseDatabaseTable.TABLE_NAME + "." + ExpenseDatabaseTable._ID + " = " + expeseDatabaseID +
            " AND date like '%-" + currentMonthOfYear + "')";

    return "SELECT expenses." + _ID + ", expenses.date, expenses.type, sum(expenses.amount) as amount FROM " +
      currentMonthsExpenses + " GROUP BY expenses.type";
  }
}
