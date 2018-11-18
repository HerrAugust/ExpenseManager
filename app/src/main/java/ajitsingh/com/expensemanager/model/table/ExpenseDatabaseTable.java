package ajitsingh.com.expensemanager.model.table;

import android.provider.BaseColumns;

public class ExpenseDatabaseTable implements BaseColumns {
  public static final String TABLE_NAME = "expense_database";
  public static final String NAME = "name";

  public static final String CREATE_TABLE_QUERY = "create table " + TABLE_NAME + " ("+ _ID +" INTEGER PRIMARY KEY AUTOINCREMENT, "+ NAME +" TEXT)";
  public static final String SELECT_ALL = "SELECT * FROM " + TABLE_NAME;
  public static final String SELECT_BY_NAME = "SELECT * FROM " + TABLE_NAME + " WHERE " + NAME + " = ?";
}
