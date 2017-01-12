package master.sudoku.adapters;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import master.sudoku.activities.LoadPuzzleActivity;
import master.sudoku.fragments.CapturePuzzleFragment;
import master.sudoku.fragments.LoadPuzzleFragment;
import master.sudoku.fragments.MainGameFragment;
import master.sudoku.views.MainGameView;

/**
 * Created by zhangle on 03/01/2017.
 */
public class LoadPuzzlePagerAdapter extends FragmentPagerAdapter {

    private static final int sCount = 3;
    private LoadPuzzleActivity mActivity;
    private Fragment[] mFragments;

    public LoadPuzzlePagerAdapter(FragmentManager fm, LoadPuzzleActivity activity) {
        super(fm);
        if (mFragments == null) {
            mFragments = new Fragment[sCount];
        }
        mActivity = activity;
    }

    @Override
    public Fragment getItem(int position) {
        if (mFragments[position] != null) {
            return mFragments[position];
        }
        Fragment f = null;
        switch (position) {
            case 0:
                f = new CapturePuzzleFragment();
                ((CapturePuzzleFragment)f).setCallback(mActivity);
                break;
            case 1:
                f = new LoadPuzzleFragment();
                ((LoadPuzzleFragment)f).setCallback(mActivity);
                break;
            case 2:
                f = new MainGameFragment();
                Bundle bundle = new Bundle();
                bundle.putInt("style", MainGameView.STYLE_LOAD);
                f.setArguments(bundle);
                break;
        }
        if (f != null) {
            mFragments[position] = f;
        }
        return f;
    }

    @Override
    public int getCount() {
        // Show 3 total pages.
        return sCount;
    }
}