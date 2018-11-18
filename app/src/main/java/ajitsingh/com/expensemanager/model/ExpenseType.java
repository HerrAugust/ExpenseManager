package ajitsingh.com.expensemanager.model;

public class ExpenseType {
  private String type;
  private int id;

  public ExpenseType(String type) {
    this.type = type;
  }

  public ExpenseType(int id, String type) {
    this.id = id;
    this.type = type;
  }

  public String getType() {
    return type;
  }

  public int getId() {
    return id;
  }
}
