package master.sudoku.fragments;

import android.graphics.Rect;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import master.sudoku.R;
import master.sudoku.config.DeviceConfig;
import master.sudoku.model.Sudoku;
import master.sudoku.model.SudokuGame;
import master.sudoku.views.MainGameView;
import master.sudoku.views.SolveSudokuView;

/**
 * Created by zhangle on 03/01/2017.
 */
public class MainGameFragment extends Fragment {

    private int mStyle = MainGameView.STYLE_PLAY;

    private SolveSudokuView mGameView;

    public MainGameFragment() {
        this.mStyle = MainGameView.STYLE_PLAY;
    }

    public void setArguments(Bundle args) {
        super.setArguments(args);
        mStyle = args.getInt("style");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_sudoku_main, container,
                false);

        mGameView = (SolveSudokuView) rootView.findViewById(R.id.solve_sudoku_view);
        SudokuGame game = new SudokuGame();
        mGameView.setBound(new Rect(0,0, DeviceConfig.mWidth - 100, DeviceConfig.mHeight - 500));
        if (mStyle == MainGameView.STYLE_PLAY) {
            mGameView.setModel(game.getModel1());
        } else if (mStyle == MainGameView.STYLE_LOAD) {
            mGameView.setModel(new Sudoku());
        }
        mGameView.setStyle(mStyle);
        return rootView;
    }

    public void setModel(Sudoku model) {
        mGameView.setModel(model);
        mGameView.invalidate();
    }

    public void editModel() {
        mGameView.editModel(true);
    }
}
