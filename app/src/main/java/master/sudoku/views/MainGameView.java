/**
 *
 */
package master.sudoku.views;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.util.AttributeSet;

import master.sudoku.event.EventArgs;
import master.sudoku.event.EventListener;
import master.sudoku.logs.Logger;
import master.sudoku.model.Sudoku;
import master.sudoku.widgets.InputPanel;
import master.sudoku.widgets.MatrixGrid;

/**
 * @author dannyzha
 *
 */
public class MainGameView extends ViewBase implements EventListener {


    public final static int STYLE_PLAY = 0;
    public final static int STYLE_LOAD = 1;

    protected int mStyle = STYLE_PLAY;
    protected Sudoku mModel;
    protected MatrixGrid mGrid;
    protected InputPanel mInputPanel;

    /**
     * Need this constructor to fix the "Error inflating class" error
     * @param context
     * @param attrs
     */
    public MainGameView(Context context, AttributeSet attrs) {
        super(context, attrs);

        mGrid = new MatrixGrid(this);
        mInputPanel = new InputPanel(this);
        mInputPanel.addEventListener(mGrid);
    }

    public MainGameView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        mGrid = new MatrixGrid(this);
        mInputPanel = new InputPanel(this);
        mInputPanel.addEventListener(mGrid);
    }

    public void setModel(Sudoku model) {
        this.mModel = model;
        mGrid.setModel(this.mModel);
    }

    public void editModel(boolean editing) {
        mGrid.setIsEditing(editing);
    }

    public void setBound(Rect bound) {
        super.setBound(bound);

        int matrixW = bound.width();
        int matrixH = bound.height();
        if(matrixW > matrixH) {
            matrixW = matrixH;
        } else {
            matrixH = matrixW;
        }
        Rect matrixRect = new Rect(bound.left, bound.top, bound.left + matrixW, bound.top + matrixH);
        mGrid.setBound(matrixRect);

        if(Logger.ON) {
            Logger.getLogger().debug("bound.getHeight is:" + bound.height());
            Logger.getLogger().debug("matrixH is:" + matrixH);
            Logger.getLogger().debug("matrixW is:" + matrixW);
        }
        Rect inputRect = new Rect(matrixRect.left, matrixRect.bottom + 10,
                matrixRect.left + matrixW, matrixRect.bottom + 10 + bound.height() - matrixH - 10);
        mInputPanel.setBound(inputRect);
    }

    public void setStyle(int style) {
        mStyle = style;
        mInputPanel.setInputOnly(style == STYLE_LOAD);
    }

    public void paint(Canvas canvas) {
        if(Logger.ON) {
            Logger.getLogger().debug("paiting MainGameView");
        }
        mGrid.paint(canvas);
        mInputPanel.paint(canvas);
    }

    public void onTap(int x, int y) {
        boolean handled = false;
        handled |= mGrid.onTap(x, y);
        if(!handled) {
            mInputPanel.onTap(x, y);
        }
    }

    public void onPointerPressed(int x, int y) {
        boolean handled = false;
        handled |= mGrid.onPointerPressed(x, y);
        if(!handled) {
            mInputPanel.onPointerPressed(x, y);
        }
    }

    public void onPointerReleased(int x, int y) {
        boolean handled = false;
        handled |= mGrid.onPointerReleased(x, y);
        if(!handled) {
            mInputPanel.onPointerReleased(x, y);
        }
    }

    public void onKeyEvent(int key) {
        boolean handled = false;
        handled |= mGrid.onKeyEvent(key);
        if(!handled) {
            mInputPanel.onKeyEvent(key);
        }
    }

//	public void invalidate() {
//
//	}
//
//	public void invalidate(Rect rect) {
////		mCanvas.repaint(rect.left, rect.top, rect.width(), rect.height());
//	}

//	public void repaint() {
////		mCanvas.repaint();
//	}

    public boolean handleEvent(EventArgs args) {
        return false;
    }

}
