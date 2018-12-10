package ajitsingh.com.expensemanager.controller.activity;

import android.content.Intent;
import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.LinkedList;
import java.util.List;

import ajitsingh.com.expensemanager.Constants;
import ajitsingh.com.expensemanager.R;
import ajitsingh.com.expensemanager.model.ExpenseDatabaseHelper;
import ajitsingh.com.expensemanager.model.ExpenseDatabase;
import ajitsingh.com.expensemanager.utils.SettingsUtil;

public class DatabasesActivity extends Activity {
    private final String defaultDatabaseName = "Default database";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_databases);

        List<ExpenseDatabase> databases = new ExpenseDatabaseHelper(this).getExpenseDatabases();
        List<String> databasesString = new LinkedList<>();
        for(ExpenseDatabase db : databases)
            databasesString.add(db.getName());
        if(databases.isEmpty())
            findViewById(R.id.databases_id).setVisibility(View.VISIBLE);

        final ListView databases_listview = findViewById(R.id.databases_listview_id);
        final ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, databasesString);
        databases_listview.setAdapter(adapter);

        final DatabasesActivity _this = this;
        // When the user selects the new expenses database:
        databases_listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position,
                                    long id) {
                TextView db = (TextView) view;
                boolean res = new SettingsUtil(_this).update(Constants.settingsCurrentDatabase, db.getText().toString());
                if(res)
                    Toast.makeText(_this, "Database changed to " + db.getText().toString(), Toast.LENGTH_LONG).show();
                else
                    Toast.makeText(_this, "ERROR", Toast.LENGTH_SHORT).show();

                Intent i = new Intent(_this, MainActivity.class);
                _this.startActivity(i);

            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_databases, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent intent;

        switch(item.getItemId()) {
            case R.id.databases_add_database:
                intent = new Intent(this, AddDatabaseActivity.class);
                startActivity(intent);
                return true;
            default:
                return true;
        }

    }

}
