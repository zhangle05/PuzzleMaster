/**
 *
 */
package master.sudoku.shapes;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Point;


/**
 * @author dannyzha
 *
 */
public class ThicknessLine extends ShapeBase{

    private int mThickness = 1;
    private Point mStart;
    private Point mEnd;
//	private float mSlope;
//	private int mSlopeType;
//
//	private static final int SLOPE_TYPE_HORIZONTAL = 0;
//	private static final int SLOPE_TYPE_VERTICAL = 1;
//	private static final int SLOPE_TYPE_DIAGONAL = 2;

    /**
     * Constructor
     * @param start
     * @param end
     */
    public ThicknessLine(Point start, Point end) {
        this.mStart = start;
        this.mEnd = end;

//		calculateSlope();
    }

    /**
     * Constructor with basic type arguments
     * @param startX
     * @param startY
     * @param endX
     * @param endY
     */
    public ThicknessLine(int startX, int startY, int endX, int endY) {
        this.mStart = new Point(startX, startY);
        this.mEnd = new Point(endX, endY);

//		calculateSlope();
    }

    public int getThickness() {
        return mThickness;
    }

    /**
     * set thickness of the line, default thickness is 1
     * Better to set it as odd number, like 3, 5, 7, etc.
     * @param mThickness
     */
    public void setThickness(int mThickness) {
        this.mThickness = mThickness;
    }

    public Point getStart() {
        return mStart;
    }

    public void setStart(Point mStart) {
        this.mStart = mStart;
    }

    public Point getEnd() {
        return mEnd;
    }

    public void setEnd(Point mEnd) {
        this.mEnd = mEnd;
    }

    public void paint(Canvas canvas) {
        mPaint.setColor(Color.BLACK);
        mPaint.setStrokeWidth(mThickness);

        canvas.drawLine(mStart.x, mStart.y, mEnd.x, mEnd.y, mPaint);
//		if(mThickness > 1) {
//			int xIncrement = 0;
//			int yIncrement = 0;
//			if(mSlopeType == SLOPE_TYPE_HORIZONTAL) {
//				yIncrement = 1;
//			}
//			else if(mSlopeType == SLOPE_TYPE_VERTICAL) {
//				xIncrement = 1;
//			}
//			else if(mSlopeType == SLOPE_TYPE_DIAGONAL) {
//				xIncrement = 1;
//				yIncrement = 1;
//			}
//			int startX = mStart.getX();
//			int startY = mStart.getY();
//			int endX = mEnd.getX();
//			int endY = mEnd.getY();
//			for(int i=0; i<mThickness/2; i++) {
//				startX += xIncrement;
//				startY += yIncrement;
//				endX += xIncrement;
//				endY += yIncrement;
//				g.drawLine(startX, startY, endX, endY);
//			}
//			startX = mStart.getX();
//			startY = mStart.getY();
//			endX = mEnd.getX();
//			endY = mEnd.getY();
//			for(int i=0; i<(mThickness-1)/2; i++) {
//				startX -= xIncrement;
//				startY -= yIncrement;
//				endX -= xIncrement;
//				endY -= yIncrement;
//				g.drawLine(startX, startY, endX, endY);
//			}
//		}
    }

//	private void calculateSlope() {
//		if(mStart.getX() == mEnd.getX()) {
//			mSlope = Float.MAX_VALUE;
//		} else {
//			mSlope = (mStart.getY() - mEnd.getY()) / (mStart.getX() - mEnd.getX());
//		}
//		if(mSlope <= 0.414) {
//			mSlopeType = SLOPE_TYPE_HORIZONTAL;
//		} else if(mSlope <= 2.414) {
//			mSlopeType = SLOPE_TYPE_DIAGONAL;
//		} else {
//			mSlopeType = SLOPE_TYPE_VERTICAL;
//		}
//	}
}
