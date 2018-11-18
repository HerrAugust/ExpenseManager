package ajitsingh.com.expensemanager.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import ajitsingh.com.expensemanager.Constants;

public class SettingsUtil {

    private final Context context;

    public SettingsUtil(Context context) {
        this.context = context;
    }

    public SettingsUtil() {
        this.context = Constants.defaultAppContext;
    }

    public boolean add(String name, String val) {
        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(this.context).edit();
        editor.putString(name, val);
        return editor.commit();
    }

    public boolean remove(String name) {
        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(this.context).edit();
        editor.remove(name);
        return editor.commit();
    }

    public String get(String name, String defaultVal) {
        SharedPreferences sh = PreferenceManager.getDefaultSharedPreferences(this.context);
        return sh.getString(name, defaultVal);
    }

    public boolean update(String name, String newVal) {
        boolean res = this.remove(name);
        if(!res) return res;
        return this.add(name, newVal);
    }
}
