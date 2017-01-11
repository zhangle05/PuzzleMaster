/**
 *
 */
package master.sudoku.views;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;


/**
 * @author dannyzha
 *
 */
public abstract class ViewBase extends View {

    protected Canvas mCanvas;

    protected Rect mBound;

    private int mStartX;

    private int mStartY;

    /**
     * Need this constructor to fix the "Error inflating class" error
     * @param context
     * @param attrs
     */
    public ViewBase(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ViewBase(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public void setBound(Rect bound) {
        this.mBound = bound;
    }

    public Rect getBound() {
        return mBound;
    }

    public void setCanvas(Canvas canvas) {
        this.mCanvas = canvas;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch(event.getActionMasked()) {
            case MotionEvent.ACTION_DOWN:
                mStartX = (int)event.getX();
                mStartY = (int)event.getY();
                this.onPointerPressed(mStartX, mStartY);
                break;
            case MotionEvent.ACTION_UP:
                int endX = (int)event.getX();
                int endY = (int)event.getY();
                this.onPointerReleased(endX, endY);
                if(Math.abs(endX - mStartX) <= 3 && Math.abs(endY - mStartY) <= 3) {
                    this.onTap(mStartX, mStartY);
                }
                break;
        }
        return true;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        paint(canvas);
    }
    /**
     * Paint the view
     * @param canvas
     */
    abstract public void paint(Canvas canvas);
    //	abstract public void repaint();
//	abstract public void invalidate();
//	abstract public void invalidate(Rect rect);
    abstract public void onTap(int x, int y);
    abstract public void onPointerPressed(int x, int y);
    abstract public void onPointerReleased(int x, int y);
    abstract public void onKeyEvent(int key);

}
