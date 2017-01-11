/**
 *
 */
package master.sudoku.shapes;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;

import master.sudoku.config.DeviceConfig;

/**
 * @author dannyzha
 *
 */
public class NumberCell extends ShapeBase {

    private int mNumber = 0;

    private String mText = "";

    private boolean mReadonly = false;

    private boolean mErrorFlag = false;

    private boolean mHighlighted = false;

    private static int NORMAL_COLOR = Color.BLACK;
    private static int NORMAL_BG_COLOR = Color.WHITE;
    private static int HIGHLIGHT_COLOR = Color.YELLOW;
    private static int HIGHLIGHT_BG_COLOR = Color.CYAN;
    private static int READONLY_COLOR = Color.WHITE;
    private static int READONLY_BG_COLOR = Color.GRAY;
    private static int ERROR_COLOR = Color.RED;

    private static Typeface NORMAL_FONT = Typeface.create("Helvetica", Typeface.BOLD);

    public int getNumber() {
        return mNumber;
    }

    public void setNumber(int mNumber) {
        this.mNumber = mNumber;
    }

    public boolean isReadonly() {
        return mReadonly;
    }

    public String getText() {
        return mText;
    }

    public void setText(String text) {
        mText = text;
    }

    public void setReadonly(boolean readonly) {
        this.mReadonly = readonly;
    }

    public void setErrorFlag(boolean errorFlag) {
        this.mErrorFlag = errorFlag;
    }

    public boolean isHightlighted() {
        return mHighlighted;
    }

    public void setHightlighted(boolean hightlighted) {
        this.mHighlighted = hightlighted;
    }

	/* (non-Javadoc)
	 * @see com.skyway.pandora.sudoku.shapes.ShapeBase#paint(javax.microedition.lcdui.Graphics)
	 *///			canvas.clipRect(mBound);

    public void paint(Canvas canvas) {
        if(mNumber >= 0 && mBound != null) {
            this.prepareColor();
            mPaint.setColor(mBackgroundColor);
            mPaint.setStyle(Paint.Style.FILL);
            canvas.drawRect(mBound, mPaint);
            if(mText != null && mText.length() > 0) {
                paintText(mText, canvas);
            } else if(mNumber > 0) {
                mText = String.valueOf(mNumber);
                paintText(String.valueOf(mNumber), canvas);
            }
        }
    }

    private void paintText(String text, Canvas canvas) {
        mPaint.setColor(mColor);
        mPaint.setTypeface(NORMAL_FONT);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setTextSize(DeviceConfig.mFontSize);

//		int strWidth = mPaint.measureText(mText);
        Rect textBounds = new Rect();
        mPaint.getTextBounds(text, 0, text.length(), textBounds);
        int strWidth = textBounds.width();
        int strHeight = textBounds.height();

        int x = mBound.left + (mBound.width() - strWidth) / 2;
        int y = mBound.top + (mBound.height() + strHeight) / 2;

        canvas.drawText(text, x, y, mPaint);
    }

    private void prepareColor() {
        mColor = NORMAL_COLOR;
        mBackgroundColor = NORMAL_BG_COLOR;

        // error color has higher priority
        if(mErrorFlag) {
            this.mColor = ERROR_COLOR;
        }
        else if(mReadonly) {
            this.mColor = READONLY_COLOR;
            this.mBackgroundColor = READONLY_BG_COLOR;
        }

        if(mHighlighted) {
            this.mColor = HIGHLIGHT_COLOR;
            this.mBackgroundColor = HIGHLIGHT_BG_COLOR;
        }
    }

}
