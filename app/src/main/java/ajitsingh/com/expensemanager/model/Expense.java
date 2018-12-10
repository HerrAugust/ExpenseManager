package ajitsingh.com.expensemanager.model;

import java.util.Date;

public class Expense {
  private String type;
  private Date date;
  private Float amount;
  private Integer id;
  private ExpenseDatabase expenseDatabase; // external key to expense_database

  public Expense(Float amount, String type, Date date) {
    this.type = type;
    this.date = date;
    this.amount = amount;
  }

  public Expense(Integer id, Float amount, String type, Date date) {
    this(amount, type, date);
    this.id = id;
  }

  public Expense(Float aLong, String type, Date currentDate, ExpenseDatabase database) {
    this(aLong, type, currentDate);
    this.expenseDatabase = database;
  }

  public Float getAmount() {
    return amount;
  }

  public String getType() {
    return type;
  }

  public Date getDate() {
    return date;
  }

  public Integer getId() {
    return id;
  }

  public ExpenseDatabase getExpenseDatabase() {
    return this.expenseDatabase;
  }

}
