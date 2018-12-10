package ajitsingh.com.expensemanager.view.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import ajitsingh.com.expensemanager.controller.activity.ExpenseFragment;
import ajitsingh.com.expensemanager.controller.activity.TodaysExpenseFragment;

public class HomeViewPagerAdapter extends FragmentStatePagerAdapter {
  public HomeViewPagerAdapter(FragmentManager supportFragmentManager) {
    super(supportFragmentManager);
  }

  @Override
  public Fragment getItem(int position) {
    switch (position){
      case 0:
        return new ExpenseFragment();
      case 1:
        return new TodaysExpenseFragment();
    }

    return null;
  }

  @Override
  public int getCount() {
    return 2;
  }
}
