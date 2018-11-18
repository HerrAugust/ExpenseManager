package ajitsingh.com.expensemanager.model.table;

import android.provider.BaseColumns;

import java.util.ArrayList;

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
                                                      DATE +" TEXT, " + /* TODO: why text and not date? */
                                                      EXPENSE_DATABASE_ID + " INTEGER," +
                                                      "FOREIGN KEY(" + EXPENSE_DATABASE_ID + ") REFERENCES " + ExpenseDatabaseTable.TABLE_NAME + "(" + ExpenseDatabaseTable._ID + ")" +
                                                  ");";

  public static final String SELECT_ALL = "SELECT * FROM " + TABLE_NAME + " ORDER BY " + _ID + " DESC";

  public static String getExpensesForDate(String date, int expeseDatabaseID){
    String query = "SELECT " + ExpenseTable.TABLE_NAME + "." + "* FROM " + TABLE_NAME + " JOIN " + ExpenseDatabaseTable.TABLE_NAME +
      " ON " + ExpenseDatabaseTable.TABLE_NAME + "." + ExpenseDatabaseTable._ID + " = " + ExpenseTable.TABLE_NAME  + "." + EXPENSE_DATABASE_ID +
            " WHERE " + ExpenseDatabaseTable.TABLE_NAME + "." + ExpenseDatabaseTable._ID + " = " + expeseDatabaseID +
            " AND date like '"+date+"%' ORDER BY " + _ID + " DESC";
    return query;
  }

  public static String getConsolidatedExpensesForDates(ArrayList<String> dates) {
    String dateLike = "";
    for (String date : dates){
      dateLike += "date like '" + date + "%' " + (dates.get(dates.size() - 1) == date ? "" : "or ");
    }

    dateLike = "SELECT "+ _ID +", date, type, sum(amount) as amount FROM " + TABLE_NAME + " JOIN " + ExpenseDatabaseTable.TABLE_NAME +
            " ON " + ExpenseDatabaseTable.TABLE_NAME + "." + ExpenseDatabaseTable._ID + " = " + ExpenseTable.TABLE_NAME  + "." + EXPENSE_DATABASE_ID +
            " WHERE " + dateLike + " GROUP BY date, type";
    return dateLike;
  }

  public static String getExpenseForCurrentMonth(String currentMonthOfYear) {
    String currentMonthsExpenses = "(SELECT " + _ID + ", date, type, amount FROM " +
      TABLE_NAME + " JOIN " + ExpenseDatabaseTable.TABLE_NAME + // added by agost
            " ON " + ExpenseDatabaseTable.TABLE_NAME + "." + ExpenseDatabaseTable._ID + " = " + ExpenseTable.TABLE_NAME  + "." + EXPENSE_DATABASE_ID + // added by agost. TODO CORRECT?
            " WHERE date like '%-" + currentMonthOfYear + "')";

    return "SELECT " + _ID + ", date, type, sum(amount) as amount FROM " +
      currentMonthsExpenses + " GROUP BY type";
  }
}
