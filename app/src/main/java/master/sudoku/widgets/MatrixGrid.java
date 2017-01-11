/**
 *
 */
package master.sudoku.widgets;

import android.graphics.Canvas;
import android.graphics.Rect;
import android.view.KeyEvent;

import java.util.Vector;

import master.sudoku.config.DeviceConfig;
import master.sudoku.event.EventArgs;
import master.sudoku.event.EventListener;
import master.sudoku.exception.SolutionException;
import master.sudoku.logs.Logger;
import master.sudoku.model.Index;
import master.sudoku.model.Sudoku;
import master.sudoku.shapes.SudokuGrid;
import master.sudoku.views.ViewBase;

/**
 * @author dannyzha
 *
 */
public class MatrixGrid extends WidgetBase implements EventListener {

    private Sudoku mModel;
    private SudokuGrid mGrid;
    private boolean mIsEditing = false;

    /**
     * Constructor
     */
    public MatrixGrid(ViewBase parent) {
        super(parent);
        mGrid = new SudokuGrid(Sudoku.SUDOKU_SIZE, Sudoku.SUDOKU_SIZE);
    }

    public void setModel(Sudoku model) {
        this.mModel = model;
        SudokuGrid oldGrid = mGrid;
        mGrid = new SudokuGrid(Sudoku.SUDOKU_SIZE, Sudoku.SUDOKU_SIZE);
        mGrid.setBound(oldGrid.getBound());
        for(int i=0; i<Sudoku.SUDOKU_SIZE; i++) {
            for(int j=0; j<Sudoku.SUDOKU_SIZE; j++) {
                int value = mModel.getInitValue(i, j);
                if(value > 0) {
                    mGrid.setNumber(i, j, value);
                    mGrid.setReadOnly(i, j, true);
                }
            }
        }
    }

    public void setIsEditing(boolean isEditing) {
        mIsEditing = isEditing;
    }

    public void setBound(Rect rect) {
        super.setBound(rect);
        mGrid.setBound(rect);
    }

    public void paint(Canvas canvas) {
//		for(int i=0; i<mFixedShapes.size(); i++) {
//			ShapeBase shape = (ShapeBase)mFixedShapes.elementAt(i);
//			shape.paint(g);
//		}
//		if(Logger.ON) {
//			Logger.getLogger().debug("paiting MatrixGrid");
//		}
        for(int i=0; i<Sudoku.SUDOKU_SIZE; i++) {
            for(int j=0; j<Sudoku.SUDOKU_SIZE; j++) {
                int value = 0;
                if(null != mModel) {
                    value = mModel.getValue(i, j);
                }
                if(value > 0) {
                    mGrid.setNumber(i, j, value);
                }
            }
        }
        mGrid.paint(canvas);
    }

    public boolean onTap(int x, int y) {
        if(Logger.ON) {
            Logger.getLogger().debug("MatrixGrid.onTap, position is:[" + x + "," + y + "].");
        }
        if(mGrid.getBound().contains(x, y)) {
            if(Logger.ON) {
                Logger.getLogger().debug("MatrixGrid.onTap, mGrid.getBound().containsPoint(x, y)");
            }
            mGrid.selectByPixel(x, y);
            mParentView.invalidate(this.getBound());
            return true;
        }
        return false;
    }

    public boolean onPointerPressed(int x, int y) {
        // TODO Auto-generated method stub
        return false;
    }

    public boolean onPointerReleased(int x, int y) {
        // TODO Auto-generated method stub
        return false;
    }

    public boolean onKeyEvent(int key) {
        if(Logger.ON) {
            Logger.getLogger().debug("MatrixGrid.onKeyEvent, key is:" + key);
        }
        int i = mGrid.getSelectedI();
        int j = mGrid.getSelectedJ();

        switch(key) {
            case KeyEvent.KEYCODE_DPAD_LEFT:
                i -= 1;
                if(i<0) {
                    i += Sudoku.SUDOKU_SIZE;
                }
                mGrid.selectByIndex(i, j);
                break;
            case KeyEvent.KEYCODE_DPAD_RIGHT:
                i += 1;
                if(i >= Sudoku.SUDOKU_SIZE) {
                    i -= Sudoku.SUDOKU_SIZE;
                }
                mGrid.selectByIndex(i, j);
                break;
            case KeyEvent.KEYCODE_DPAD_UP:
                j -= 1;
                if(j<0) {
                    j += Sudoku.SUDOKU_SIZE;
                }
                mGrid.selectByIndex(i, j);
                break;
            case KeyEvent.KEYCODE_DPAD_DOWN:
                j += 1;
                if(j >= Sudoku.SUDOKU_SIZE) {
                    j -= Sudoku.SUDOKU_SIZE;
                }
                mGrid.selectByIndex(i, j);
                mParentView.invalidate(this.getBound());
                return true;
        }
        return false;
    }

    public boolean handleEvent(EventArgs args) {
        switch(args.getEventType()) {
            case EventArgs.INPUT_PANEL_SELECT:
                int i = mGrid.getSelectedI();
                int j = mGrid.getSelectedJ();
                Integer value = (Integer)args.getEventData();
                try {
                    if(value.intValue() <= 0) {
                        return false;
                    }
                    if(DeviceConfig.mErrorHintLevel==3 && mGrid.hasError()) {
                        //TODO: pop up error message
                        return true;
                    }
                    if(DeviceConfig.mErrorHintLevel==1) {
                        mGrid.clearErrorFlag();
                    }
                    mGrid.clearErrorFlagForCell(i, j);

                    if(mModel.acceptValue(i, j, value.intValue())) {
                        mModel.setResultValue(i, j, value.intValue());
                    }
                    else {
                        Vector<Index> conflicts = mModel.getConflicts(i, j, value.intValue());

                        if(DeviceConfig.mErrorHintLevel > 0) {
                            mGrid.setErrorFlag(i, j, true);
                            for(int k=0; k<conflicts.size(); k++) {
                                Index idx = (Index)conflicts.elementAt(k);
                                mGrid.setErrorFlag(idx.getI(), idx.getJ(), true);
                            }
                        }

                        mModel.setResultValueWithoutChecking(i, j, value.intValue());
                    }
                    if(mIsEditing) {
                        mGrid.setReadOnly(i, j, true);
                    }
                } catch (SolutionException e) {
                    e.printStackTrace();
                }
                mParentView.invalidate();
                return true;
        }
        return false;
    }

}
