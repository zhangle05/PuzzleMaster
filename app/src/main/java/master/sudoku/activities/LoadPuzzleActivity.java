package master.sudoku.activities;

import android.os.Bundle;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

import master.sudoku.R;
import master.sudoku.adapters.LoadPuzzlePagerAdapter;
import master.sudoku.fragments.LoadPuzzleFragment;
import master.sudoku.fragments.MainGameFragment;
import master.sudoku.model.Sudoku;

public class LoadPuzzleActivity extends AppCompatActivity implements LoadPuzzleFragment.Callback {

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a {@link FragmentPagerAdapter}
     * derivative, which will keep every loaded fragment in memory. If this
     * becomes too memory intensive, it may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    LoadPuzzlePagerAdapter mSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_load_puzzle);

        // Set up the action bar.
        final ActionBar actionBar = getSupportActionBar();
//        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new LoadPuzzlePagerAdapter(
                getSupportFragmentManager(), this);

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.pager);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        // When swiping between different sections, select the corresponding
        // tab. We can also use ActionBar.Tab#select() to do this if we have
        // a reference to the Tab.
//        mViewPager
//                .setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
//                    @Override
//                    public void onPageSelected(int position) {
//                        actionBar.setSelectedNavigationItem(position);
//                    }
//                });

        // For each of the sections in the app, add a tab to the action bar.
//        for (int i = 0; i < mSectionsPagerAdapter.getCount(); i++) {
//            // Create a tab with text corresponding to the page title defined by
//            // the adapter. Also specify this Activity object, which implements
//            // the TabListener interface, as the callback (listener) for when
//            // this tab is selected.
//            actionBar.addTab(actionBar.newTab()
//                    .setText(mSectionsPagerAdapter.getPageTitle(i))
//                    .setTabListener(this));
//        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.load_puzzle, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_edit) {
            editPuzzle();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void loadPuzzleDone(Sudoku model) {
        MainGameFragment fragment = (MainGameFragment)mSectionsPagerAdapter.getItem(2);
        fragment.setModel(model);
        mViewPager.setCurrentItem(2);
    }

    private void editPuzzle() {
        MainGameFragment fragment = (MainGameFragment)mSectionsPagerAdapter.getItem(2);
        fragment.editModel();
        mViewPager.setCurrentItem(2);
    }
}
