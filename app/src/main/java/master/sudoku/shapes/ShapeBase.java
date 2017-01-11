/**
 *
 */
package master.sudoku.shapes;


import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;

/**
 * @author dannyzha
 *
 */
public abstract class ShapeBase {

    protected int mColor = 0x000000FF;

    protected int mBackgroundColor = 0xFFFFFF;

    protected Rect mBound;

    protected Paint mPaint;

    /**
     * Constructor
     */
    public ShapeBase() {
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setColor(mColor);
        mPaint.setStyle(Paint.Style.STROKE);
    }

    public int getColor() {
        return mColor;
    }
    public void setColor(int mColor) {
        this.mColor = mColor;
    }

    public Rect getBound() {
        return mBound;
    }
    public void setBound(Rect mBound) {
        this.mBound = mBound;
    }

    abstract public void paint(Canvas canvas);
}
