package ajitsingh.com.expensemanager.model;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

import ajitsingh.com.expensemanager.Constants;
import ajitsingh.com.expensemanager.model.Expense;
import ajitsingh.com.expensemanager.model.ExpenseType;
import ajitsingh.com.expensemanager.model.ExpenseDatabase;
import ajitsingh.com.expensemanager.model.table.ExpenseTable;
import ajitsingh.com.expensemanager.model.table.ExpenseTypeTable;
import ajitsingh.com.expensemanager.model.table.ExpenseDatabaseTable;
import ajitsingh.com.expensemanager.utils.DateUtil;

import static ajitsingh.com.expensemanager.utils.DateUtil.getCurrentDate;
import static ajitsingh.com.expensemanager.utils.DateUtil.getCurrentWeeksDates;
import static java.lang.Float.parseFloat;
import static java.lang.Integer.parseInt;
import static java.lang.Long.parseLong;

public class ExpenseDatabaseHelper extends SQLiteOpenHelper {
  private static final int DATABASE_VERSION = 2;
  private static final String DATABASE_NAME = "expense";

  public ExpenseDatabaseHelper(Context context) {
    super(context, DATABASE_NAME, null, DATABASE_VERSION);
  }

  @Override
  public void onCreate(SQLiteDatabase sqLiteDatabase) {
    sqLiteDatabase.execSQL(ExpenseTypeTable.CREATE_TABLE_QUERY);
    sqLiteDatabase.execSQL(ExpenseDatabaseTable.CREATE_TABLE_QUERY);
    sqLiteDatabase.execSQL(ExpenseTable.CREATE_TABLE_QUERY);
    seedExpenseTypes(sqLiteDatabase);
    // Add the default database
    sqLiteDatabase.execSQL(String.format("INSERT INTO %s (%s) VALUES(\"%s\");", ExpenseDatabaseTable.TABLE_NAME, ExpenseDatabaseTable.NAME, Constants.defaultDatabaseName));
  }

  @Override
  // drop, delete and create DB when migrating
  public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
    if(oldVersion == 1 && newVersion == 2) {
      // added table ExpenseDatabaseTable, and table Expense has a new attribute to connect to it
      sqLiteDatabase.execSQL(ExpenseDatabaseTable.CREATE_TABLE_QUERY);
      sqLiteDatabase.execSQL(String.format("ALTER TABLE %s ADD COLUMN %s %s;", ExpenseTable.TABLE_NAME,
              ExpenseTable.EXPENSE_DATABASE_ID, "INTEGER")); // ALTER TABLE name ADD COLUMN COLNew type;
      sqLiteDatabase.execSQL(String.format("ALTER TABLE %s  ADD CONSTRAINT %s FOREIGN KEY (%s) REFERENCES %s(%s);",
              ExpenseTable.TABLE_NAME, "fk_expenses_expense_database", ExpenseTable.EXPENSE_DATABASE_ID,
              ExpenseDatabaseTable.TABLE_NAME, ExpenseDatabaseTable._ID)); // ALTER TABLE name ADD CONSTRAINT fk_child_parent FOREIGN KEY (parent_id) REFERENCES parent(id);
    }
  }

  public List<ExpenseType> getExpenseTypes() {
    List<ExpenseType> expenseTypes;

    SQLiteDatabase database = this.getWritableDatabase();
    Cursor cursor = database.rawQuery(ExpenseTypeTable.SELECT_ALL, null);

    expenseTypes = buildExpenseTypes(cursor);

    return expenseTypes;
  }

  public void deleteAll() {
    SQLiteDatabase database = this.getWritableDatabase();
    database.delete(ExpenseTypeTable.TABLE_NAME, "", new String[]{});
    database.delete(ExpenseTable.TABLE_NAME, "", new String[]{});
    database.delete(ExpenseDatabaseTable.TABLE_NAME, "", new String[]{});
    database.close();
  }

  public boolean addExpense(Expense expense) {
    SQLiteDatabase database = this.getWritableDatabase();
    ContentValues values = new ContentValues();
    values.put(ExpenseTable.AMOUNT, expense.getAmount());
    values.put(ExpenseTable.TYPE, expense.getType());
    values.put(ExpenseTable.DATE, expense.getDate());
    ExpenseDatabase curDB = expense.getExpenseDatabase();
    values.put(ExpenseTable.EXPENSE_DATABASE_ID, 0);
    if(curDB != null) { // Expense not assigned to any particular DB
        values.remove(ExpenseTable.EXPENSE_DATABASE_ID);
        values.put(ExpenseTable.EXPENSE_DATABASE_ID, curDB.getId());
    }

    long res = database.insert(ExpenseTable.TABLE_NAME, null, values);
    return res != -1;
  }

  public ExpenseDatabase getExpenseDatabaseByName(String name) {
    SQLiteDatabase database = this.getReadableDatabase();
    Cursor cursor = database.rawQuery(ExpenseDatabaseTable.SELECT_BY_NAME, new String[] {name});

    List<ExpenseDatabase> expenseDatabases = buildExpenseDatabases(cursor);
    if(expenseDatabases.size() == 0)
      return null;
    return expenseDatabases.get(0);
  }

  public List<Expense> getExpenses() {
    SQLiteDatabase database = this.getWritableDatabase();
    Cursor cursor = database.rawQuery(ExpenseTable.SELECT_ALL, null);

    return buildExpenses(cursor);
  }

  public List<Expense> getTodaysExpenses(String curExpensesDBName) {
    SQLiteDatabase database = this.getWritableDatabase();
    int id = this.getExpenseDatabaseByName(curExpensesDBName).getId(); // ID of current expenses DB
    Cursor cursor = database.rawQuery(ExpenseTable.getExpensesForDate(getCurrentDate(), id), null);

    return buildExpenses(cursor);
  }

  public List<Expense> getCurrentWeeksExpenses(String curExpensesDBName) {
    SQLiteDatabase database = this.getWritableDatabase();
    int id = this.getExpenseDatabaseByName(curExpensesDBName).getId(); // ID of current expenses DB
    Cursor cursor = database.rawQuery(ExpenseTable.getConsolidatedExpensesForDates(getCurrentWeeksDates(), id), null);
    return buildExpenses(cursor);
  }

  public List<Expense> getExpensesForCurrentMonthGroupByCategory(String curExpensesDBName) {
    SQLiteDatabase database = this.getWritableDatabase();
    int id = this.getExpenseDatabaseByName(curExpensesDBName).getId(); // ID of current expenses DB
    Cursor cursor = database.rawQuery(ExpenseTable.getExpenseForCurrentMonth(DateUtil.currentMonthOfYear(), id), null);
    return buildExpenses(cursor);
  }

  public void addExpenseType(ExpenseType type) {
    SQLiteDatabase database = this.getWritableDatabase();
    ContentValues values = new ContentValues();
    values.put(ExpenseTable.TYPE, type.getType());

    database.insert(ExpenseTypeTable.TABLE_NAME, null, values);
  }

  public void truncate(String tableName) {
    SQLiteDatabase database = this.getWritableDatabase();
    database.execSQL("delete from " + tableName);
  }

  public void truncate() {
    truncate(ExpenseTypeTable.TABLE_NAME);
    truncate(ExpenseDatabaseTable.TABLE_NAME);
    truncate(ExpenseTable.TABLE_NAME);
  }

  /* Databases */
  public boolean addExpenseDatabase(ExpenseDatabase db) {
    SQLiteDatabase database = this.getWritableDatabase();
    ContentValues values = new ContentValues();
    values.put(ExpenseDatabaseTable.NAME, db.getName());

    long res = database.insert(ExpenseDatabaseTable.TABLE_NAME, null, values);
    return res != -1;
  }

  public List<ExpenseDatabase> getExpenseDatabases() {
      SQLiteDatabase database = this.getWritableDatabase();
      Cursor cursor = database.rawQuery(ExpenseDatabaseTable.SELECT_ALL, null);

      return buildExpenseDatabases(cursor);
  }

  private List<ExpenseDatabase> buildExpenseDatabases(Cursor cursor) {
    List<ExpenseDatabase> dbs = new ArrayList<>();
    if(isCursorPopulated(cursor)){
      do {
        int id = Integer.parseInt(cursor.getString(cursor.getColumnIndex(ExpenseTable._ID)));
        String name = cursor.getString(cursor.getColumnIndex(ExpenseDatabaseTable.NAME));

        ExpenseDatabase db = new ExpenseDatabase(id, name);
        dbs.add(db);
      } while(cursor.moveToNext());
    }

    return dbs;
  }

  private List<Expense> buildExpenses(Cursor cursor) {
    List<Expense> expenses = new ArrayList<>();
    if(isCursorPopulated(cursor)){
      do {
        String type = cursor.getString(cursor.getColumnIndex(ExpenseTable.TYPE));
        String amount = cursor.getString(cursor.getColumnIndex(ExpenseTable.AMOUNT));
        String date = cursor.getString(cursor.getColumnIndex(ExpenseTable.DATE));
        String id = cursor.getString(cursor.getColumnIndex(ExpenseTable._ID));

        Expense expense = id == null ? new Expense(parseFloat(amount), type, date) :
                new Expense(parseInt(id), parseFloat(amount), type, date);
        expenses.add(expense);
      } while(cursor.moveToNext());
    }

    return expenses;
  }

  private List<ExpenseType> buildExpenseTypes(Cursor cursor) {
    List<ExpenseType> expenses = new ArrayList<>();
    if(isCursorPopulated(cursor)){
      do {
        String type = cursor.getString(cursor.getColumnIndex(ExpenseTypeTable.TYPE));
        String id = cursor.getString(cursor.getColumnIndex(ExpenseTable._ID));

        ExpenseType expenseType = id == null ? new ExpenseType(type) : new ExpenseType(parseInt(id), type);
        expenses.add(expenseType);
      } while(cursor.moveToNext());
    }

    return expenses;
  }

  private boolean isCursorPopulated(Cursor cursor) {
    return cursor != null && cursor.moveToFirst();
  }

  private void seedExpenseTypes(SQLiteDatabase sqLiteDatabase) {
    List<ExpenseType> expenseTypes = ExpenseTypeTable.seedData();
    for (ExpenseType expenseType : expenseTypes) {
      ContentValues contentValues = new ContentValues();
      contentValues.put(ExpenseTypeTable.TYPE, expenseType.getType());

      sqLiteDatabase.insert(ExpenseTypeTable.TABLE_NAME, null, contentValues);
    }
  }
}
