package ajitsingh.com.expensemanager.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Type;
import java.text.ParseException;
import java.util.List;

import ajitsingh.com.expensemanager.R;
import ajitsingh.com.expensemanager.database.ExpenseDatabaseHelper;
import ajitsingh.com.expensemanager.model.Expense;
import ajitsingh.com.expensemanager.model.ExpenseDatabase;
import ajitsingh.com.expensemanager.model.ExpenseType;
import ajitsingh.com.expensemanager.model.table.ExpenseDatabaseTable;
import ajitsingh.com.expensemanager.model.table.ExpenseTable;
import ajitsingh.com.expensemanager.model.table.ExpenseTypeTable;

public class BackupActivity extends Activity {
    // TODO: USE INTERNAL STORAGE
    // TODO: INDICATE THE USED FOLDER FOR BACKUP (USEFUL IF USER WANT TO RESTORE DB)
    // TODO: DEAL WITH DIFFERENT DB VERSIONS

    private final String BACKUP_FILENAME_EXPENSE = ExpenseTable.TABLE_NAME + ".txt"; // the file that will be created or searched
    private final String BACKUP_FILENAME_EXPENSEDATABASE = ExpenseDatabaseTable.TABLE_NAME + ".txt"; // the file that will be created or searched
    private final String BACKUP_FILENAME_EXPENSETYPE = ExpenseTypeTable.TABLE_NAME + ".txt"; // the file that will be created or searched

    private final File FILE_EXPENSE = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), BACKUP_FILENAME_EXPENSE);
    private final File FILE_EXPENSEDATABASE = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), BACKUP_FILENAME_EXPENSEDATABASE);
    private final File FILE_EXPESETYPE = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), BACKUP_FILENAME_EXPENSETYPE);

    private Button todo;
    private Switch switch_backuprestore;
    private CheckBox checkbox_erase;
    private TextView text_filefound;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_backup);

        final BackupActivity _this = this;
        this.todo = findViewById(R.id.backup_button_backup);
        this.switch_backuprestore = findViewById(R.id.backup_switch);
        this.checkbox_erase = findViewById(R.id.backup_checkbox_erase);
        this.text_filefound = findViewById(R.id.backup_text_filefound);

        this.switch_backuprestore.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(b) {
                    _this.todo.setText("Backup");
                    _this.checkbox_erase.setEnabled(false);
                    _this.text_filefound.setVisibility(View.INVISIBLE);
                }
                else {
                    _this.todo.setText("Restore");
                    _this.text_filefound.setVisibility(View.VISIBLE);
                    _this.checkbox_erase.setEnabled(true);
                    File fileExpense = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS), BACKUP_FILENAME_EXPENSE);
                    File fileExpenseType = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS), BACKUP_FILENAME_EXPENSETYPE);
                    File fileExpenseDatabase = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS), BACKUP_FILENAME_EXPENSEDATABASE);
                    if(fileExpense.exists() && fileExpenseDatabase.exists() && fileExpenseType.exists())
                        _this.text_filefound.setText("Files to restore found!");
                }
            }
        });

        todo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean backup = _this.switch_backuprestore.isChecked();
                try {
                    if (backup) {
                        _this.backup();
                        Toast.makeText(_this, "Backup complete. You have 3 files in MicroSD/Downloads/. It's NOT ENCRYPTED!", Toast.LENGTH_LONG).show();
                    } else {
                        _this.restore();
                        Toast.makeText(_this, "Restoration complete", Toast.LENGTH_SHORT).show();

                        // Restart app
                        Intent i = getBaseContext().getPackageManager().getLaunchIntentForPackage( getBaseContext().getPackageName() );
                        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(i);
                    }
                }
                catch(IOException e) {
                    Log.w("ERROR",e.getMessage());
                    Toast.makeText(_this, "Error with backup/restore. " + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void backup() throws IOException {
        if(FILE_EXPENSE.exists())
            FILE_EXPENSE.delete();
        FILE_EXPENSE.createNewFile();
        if(FILE_EXPESETYPE.exists())
            FILE_EXPESETYPE.delete();
        FILE_EXPESETYPE.createNewFile();
        if(FILE_EXPENSEDATABASE.exists())
            FILE_EXPENSEDATABASE.delete();
        FILE_EXPENSEDATABASE.createNewFile();

        // Get & write DB tables
        String towrite = "";
        List<ExpenseType> types = new ExpenseDatabaseHelper(BackupActivity.this).getExpenseTypes();
        towrite = towrite.concat(new Gson().toJson(types));
        FileOutputStream outputStream = new FileOutputStream(FILE_EXPESETYPE);
        outputStream.write(towrite.getBytes());
        outputStream.close();

        towrite = "";
        List<ExpenseDatabase> dbs = new ExpenseDatabaseHelper(BackupActivity.this).getExpenseDatabases();
        towrite = towrite.concat(new Gson().toJson(dbs));
        outputStream = new FileOutputStream(FILE_EXPENSEDATABASE);
        outputStream.write(towrite.getBytes());
        outputStream.close();

        towrite = "";
        List<Expense> remembered = new ExpenseDatabaseHelper(BackupActivity.this).getExpenses();
        towrite = towrite.concat(new Gson().toJson(remembered));
        outputStream = new FileOutputStream(FILE_EXPENSE);
        outputStream.write(towrite.getBytes());
        outputStream.close();
    }

    private void restore() throws IOException {
        // TODO: THIS CODE CAN BE WRITTEN IN A MORE MODULAR WAY
        if(this.checkbox_erase.isChecked())
            new ExpenseDatabaseHelper(BackupActivity.this).truncate();

        BufferedReader br;
        String allfile = "";
        String line = "";
        Gson gson;
        Type listType;

        // read entire file of types
        if(FILE_EXPESETYPE.exists()) {
            br = new BufferedReader(new FileReader(FILE_EXPESETYPE));
            allfile = "";
            line = "";
            while ((line = br.readLine()) != null) {
                allfile = allfile.concat(line);
            }
            br.close();

            // Convert to model
            gson = new Gson();
            listType = new TypeToken<List<ExpenseType>>() {
            }.getType();
            List<ExpenseType> types = (List<ExpenseType>) gson.fromJson(allfile, listType);

            // Add to database
            for (ExpenseType t : types)
                new ExpenseDatabaseHelper(BackupActivity.this).addExpenseType(t);
        }

// -----------------------------------------------------

        // read entire file of databases
        if(FILE_EXPENSEDATABASE.exists()) {
            br = new BufferedReader(new FileReader(FILE_EXPENSEDATABASE));
            allfile = "";
            while ((line = br.readLine()) != null) {
                allfile = allfile.concat(line);
            }
            br.close();

            // Convert to model
            gson = new Gson();
            listType = new TypeToken<List<ExpenseDatabase>>() {
            }.getType();
            List<ExpenseDatabase> dbs = (List<ExpenseDatabase>) gson.fromJson(allfile, listType);

            // Add to database
            for (ExpenseDatabase t : dbs)
                new ExpenseDatabaseHelper(BackupActivity.this).addExpenseDatabase(t);
        }

// -------------------------------------------------------

        // read entire file of expenses
        if(FILE_EXPENSE.exists()) {
            br = new BufferedReader(new FileReader(FILE_EXPENSE));
            allfile = "";
            while ((line = br.readLine()) != null) {
                allfile = allfile.concat(line);
            }
            br.close();

            // Convert to model remembered
            gson = new Gson();
            listType = new TypeToken<List<Expense>>() {
            }.getType();
            List<Expense> expenses = (List<Expense>) gson.fromJson(allfile, listType);

            // Add to database
            for (Expense t : expenses)
                new ExpenseDatabaseHelper(BackupActivity.this).addExpense(t);
        }

    }


}
