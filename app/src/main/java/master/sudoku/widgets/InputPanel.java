/**
 *
 */
package master.sudoku.widgets;

import android.graphics.Canvas;
import android.graphics.Rect;

import master.sudoku.event.EventArgs;
import master.sudoku.logs.Logger;
import master.sudoku.shapes.NumberGrid;
import master.sudoku.views.ViewBase;

/**
 * @author dannyzha
 *
 */
public class InputPanel extends WidgetBase {

    private NumberGrid mGrid;
    private boolean inputOnly = false;

    /**
     * Constructor
     * @param parent
     */
    public InputPanel(ViewBase parent) {
        super(parent);
        mGrid = new NumberGrid(5, 2);

        for(int i=0; i<5; i++) {
            for(int j=0; j<2; j++) {
                mGrid.setNumber(i, j, j*5+i+1);
                mGrid.setReadOnly(i, j, true);
            }
        }
        mGrid.setNumber(4, 1, 0);
        mGrid.setText(4, 1, "S");
    }

    public void setBound(Rect rect) {
        super.setBound(rect);
        mGrid.setBound(rect);
    }

    public void setInputOnly(boolean inputOnly) {
        if (inputOnly) {
            mGrid.setText(4, 1, "OK");
        } else {
            mGrid.setText(4, 1, "S");
        }
    }

    /* (non-Javadoc)
     * @see com.skyway.pandora.sudoku.widgets.WidgetBase#paint(javax.microedition.lcdui.Graphics)
     */
    public void paint(Canvas canvas) {
        mGrid.paint(canvas);
    }

    public boolean onTap(int x, int y) {
        return false;
    }

    public boolean onPointerPressed(int x, int y) {
        if(Logger.ON) {
            Logger.getLogger().debug("InputPanel.onPointerPressed, position is:[" + x + "," + y + "].");
        }
        if(mGrid.getBound().contains(x, y)) {
            if(Logger.ON) {
                Logger.getLogger().debug("InputPanel.onPointerPressed, mGrid.getBound().containsPoint(x, y)");
            }
            mGrid.selectByPixel(x, y);
            mParentView.invalidate(this.getBound());
            this.triggerEvent(EventArgs.INPUT_PANEL_SELECT, Integer.valueOf(mGrid.getSelectedCellNumber()));
            return true;
        }
        return false;
    }

    public boolean onPointerReleased(int x, int y) {
        if(Logger.ON) {
            Logger.getLogger().debug("InputPanel.onPointerReleased, position is:[" + x + "," + y + "].");
        }
        mGrid.clearSelection();
        mParentView.invalidate(this.getBound());
        return true;
    }

    public boolean onKeyEvent(int key) {
        // TODO Auto-generated method stub
        return false;
    }
}
