/**
 *
 */
package master.sudoku.widgets;

import android.graphics.Canvas;
import android.graphics.Rect;

import java.util.Vector;

import master.sudoku.event.EventSource;
import master.sudoku.shapes.ShapeBase;
import master.sudoku.views.ViewBase;

/**
 * @author dannyzha
 *
 */
public abstract class WidgetBase extends EventSource {

    protected ViewBase mParentView;

    protected Vector<ShapeBase> mFixedShapes = new Vector<ShapeBase>();

    protected Rect mBound;

    /**
     * Constructor
     * @param parent
     */
    public WidgetBase(ViewBase parent) {
        this.mParentView = parent;
    }

    public void setBound(Rect rect) {
        this.mBound = rect;
    }

    public Rect getBound() {
        return mBound;
    }

    abstract public void paint(Canvas canvas);
    abstract public boolean onTap(int x, int y);
    abstract public boolean onPointerPressed(int x, int y);
    abstract public boolean onPointerReleased(int x, int y);
    abstract public boolean onKeyEvent(int key);

}
