package ajitsingh.com.expensemanager.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import ajitsingh.com.expensemanager.R;
import ajitsingh.com.expensemanager.database.ExpenseDatabaseHelper;
import ajitsingh.com.expensemanager.model.ExpenseDatabase;
import ajitsingh.com.expensemanager.presenter.CategoryPresenter;
import ajitsingh.com.expensemanager.view.AddCategoryView;
import ajitsingh.com.expensemanager.view.AddDatabaseView;

import static ajitsingh.com.expensemanager.activity.MainActivity.ADD_NEW_CAT;


public class AddDatabaseActivity extends FragmentActivity implements AddDatabaseView {

  @Override
  protected void onCreate(Bundle savedInstanceState){
    super.onCreate(savedInstanceState);
    setContentView(R.layout.new_database);

    findViewById(R.id.add_database).setOnClickListener(new View.OnClickListener(){
      public void onClick(View v) {
        addDatabase(v);
      }
    });
  }

  public void addDatabase(View view) {
    ExpenseDatabaseHelper expenseDatabaseHelper = new ExpenseDatabaseHelper(this);
    if(expenseDatabaseHelper.addExpenseDatabase(new ExpenseDatabase(getDatabase())))
      Toast.makeText(this, getString(R.string.add_category_success), Toast.LENGTH_LONG).show();

    expenseDatabaseHelper.close();

    // finish. finish() wouldn't refresh the list in DatabaseActivity
    Intent i = new Intent(this, DatabasesActivity.class);
    startActivity(i);
  }

  @Override
  public String getDatabase() {
    TextView databaseInput = findViewById(R.id.new_database_name);
    return databaseInput.getText().toString();
  }
}
