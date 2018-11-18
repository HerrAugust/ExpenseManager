package ajitsingh.com.expensemanager.model;

public class ExpenseDatabase {
  private String name;
  private Integer id;

  public ExpenseDatabase(String name) {
    this.name = name;
  }

  public ExpenseDatabase(int id, String name) {
    this.id = id;
    this.name = name;
  }

  public String getName() {
    return name;
  }

  public int getId() {
    return this.id;
  }
}
