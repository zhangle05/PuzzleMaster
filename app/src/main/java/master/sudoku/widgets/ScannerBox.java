package master.sudoku.widgets;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.RectF;
import android.graphics.Shader;
import android.util.AttributeSet;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhangle on 11/01/2017.
 */
public class ScannerBox extends View {

    private static int sStrokeSize = 3;
    private static int sCornerRadius = 0;
    private static int sLineLength = 20;
    private static int sDefaultBoxWidth = 200;
    private static int sInterval = 30;//动画每帧间隔时间,毫秒
    private static int sAnimationStep = 3;
    private static int sScanningColor = Color.BLUE;
    private static int sScanDoneColor = Color.argb(255, 6, 202, 232);
    private static int sScanErrorColor = Color.RED;
    private static boolean sIsScaled = false;

    private boolean mProgressGoingDown = true;
    private RectF mProgressRect;
    private Paint mProgressPaint;

    private RectF mBoundRect;
    private List<RectF> mCornerRectList;
    private Point mCenterPt;
    private Paint mPaint;
    private int mBoxWidth = sDefaultBoxWidth;
    private boolean mIsInAnimation = false;
    private int mColor = sScanningColor;

    private Runnable mThread = new Runnable()
    {
        public void run()
        {
            if (mProgressRect == null) {
                initProgress();
            }
            if (mPaint != null) {
                //set color to Focus-Done-Color while doing the animation
                mPaint.setColor(sScanningColor);
            }
            if(mProgressRect.height() >= mBoundRect.height()) {
                if(mProgressGoingDown) {
                    mProgressRect.top = mProgressRect.bottom;
                }
                else {
                    mProgressRect.bottom = mProgressRect.top;
                }
                mProgressGoingDown = !mProgressGoingDown;
            }
            if(mProgressGoingDown) {
                mProgressRect.bottom += sAnimationStep;
            }
            else {
                mProgressRect.top -= sAnimationStep;
            }
            invalidate((int)(mProgressRect.left-10), (int)(mProgressRect.top-10),
                    (int)(mProgressRect.right+10), (int)(mProgressRect.bottom+10));
            postDelayed(this, sInterval);//延迟mInterval后执行当前线程
        }
    };

    /**
     * Constructor
     * @param context
     */
    public ScannerBox(Context context) {
        super(context);
    }

    /**
     * Constructor
     * @param context
     * @param attrs
     */
    public ScannerBox(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    /**
     * set center point of the box
     * @param x
     * @param y
     */
    public void setCenterPoint(int x, int y) {
        if(mCenterPt == null) {
            mCenterPt = new Point();
        }
        mCenterPt.set(x, y);
        if(mBoundRect == null) {
            return;
        }
        float oldLeft = mBoundRect.left;
        float oldTop = mBoundRect.top;
        float oldRight = mBoundRect.right;
        float oldBottom = mBoundRect.bottom;
        relocateBound();
        invalidate((int)(oldLeft-10), (int)(oldTop-10),
                (int)(oldRight+10), (int)(oldBottom+10));
    }

    public void setScanResult(boolean success) {
        mColor = success ? sScanDoneColor : sScanErrorColor;
        if(mPaint != null) {
            mPaint.setColor(mColor);
        }
        if(mBoundRect != null) {
            invalidate((int)(mBoundRect.left-10), (int)(mBoundRect.top-10),
                    (int)(mBoundRect.right+10), (int)(mBoundRect.bottom+10));
        }
    }

    public void startAnimation() {
        if(!mIsInAnimation) {
            mIsInAnimation = true;
            post(mThread);
        }
    }

    private void setBoxWidth(int width) {
        mBoxWidth = width;
        relocateBound();
        invalidate((int)(mBoundRect.left-20), (int)(mBoundRect.top-20),
                (int)(mBoundRect.right+20), (int)(mBoundRect.bottom+20));
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if(mCenterPt == null) {
            mCenterPt = new Point(this.getWidth()/2, this.getHeight()/2);
        }
        if(mBoundRect == null) {
            float ratio = (float)this.getWidth() / sDefaultBoxWidth;
            scaleDrawSizes(ratio);
            relocateBound();
        }

        drawLines(canvas);
        drawProgressRect(canvas);
    }

    /**
     * Arc rectangle index starts on left-top corner of the out bound rectangle and goes clockwise from 0 to 3
     *             -------------
     *             | 0 |   | 1 |
     *             |---|---|---|
     *             |   |   |   |
     *             |---|---|---|
     *             | 3 |   | 2 |
     *             |---|---|---|
     */
    private void drawLines(Canvas c) {
        RectF r = mCornerRectList.get(0);
        c.drawLine(r.left + sCornerRadius, r.top, r.right, r.top, mPaint);
        c.drawLine(r.left, r.top + sCornerRadius, r.left, r.bottom, mPaint);

        r = mCornerRectList.get(1);
        c.drawLine(r.left, r.top, r.right - sCornerRadius, r.top, mPaint);
        c.drawLine(r.right, r.top + sCornerRadius, r.right, r.bottom, mPaint);

        r = mCornerRectList.get(2);
        c.drawLine(r.left, r.bottom, r.right - sCornerRadius, r.bottom, mPaint);
        c.drawLine(r.right, r.top, r.right, r.bottom - sCornerRadius, mPaint);

        r = mCornerRectList.get(3);
        c.drawLine(r.left + sCornerRadius, r.bottom, r.right, r.bottom, mPaint);
        c.drawLine(r.left, r.top, r.left, r.bottom - sCornerRadius, mPaint);
    }

    private void initPaint() {
        mPaint = new Paint();
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setAntiAlias(true);
        mPaint.setColor(mColor);
        mPaint.setStrokeWidth(sStrokeSize);

        mProgressPaint = new Paint();
        mProgressPaint.setStyle(Paint.Style.FILL);
    }

    /**
     * Arc rectangle index starts on left-top corner of the out bound rectangle and goes clockwise from 0 to 3
     *             -------------
     *             | 0 |   | 1 |
     *             |---|---|---|
     *             |   |   |   |
     *             |---|---|---|
     *             | 3 |   | 2 |
     *             |---|---|---|
     */
    private void initCornerRects() {
        mCornerRectList = new ArrayList<RectF>();
        for(int i=0; i<4; i++) {
            mCornerRectList.add(new RectF());
        }
    }

    private void scaleDrawSizes(float ratio) {
        if(!sIsScaled) {
            sLineLength = (int)(sLineLength * ratio);
            mBoxWidth = (int)(mBoxWidth * ratio);
            sStrokeSize = (int)(sStrokeSize * ratio);
            sCornerRadius = (int)(sCornerRadius * ratio);
            sDefaultBoxWidth = (int)(sDefaultBoxWidth * ratio);
            sAnimationStep = (int)(sAnimationStep * ratio);
            sIsScaled = true;
        }

        initPaint();
        initCornerRects();
    }

    private void relocateBound() {
        if(mBoundRect == null) {
            mBoundRect = new RectF();
        }
        mBoundRect.set(mCenterPt.x - mBoxWidth/2, mCenterPt.y - mBoxWidth/2,
                mCenterPt.x + mBoxWidth/2, mCenterPt.y + mBoxWidth/2);
        for(int i=0; i<mCornerRectList.size(); i++) {
            relocateCornerRect(i);
        }
    }

    private void initProgress() {
        if (mProgressRect == null) {
            mProgressRect = new RectF();
        }
        mProgressRect.set(mBoundRect.left, mBoundRect.top, mBoundRect.right, mBoundRect.top);
    }
    /**
     * Arc rectangle index starts on left-top corner of the out bound rectangle and goes clockwise from 0 to 3
     *             -------------
     *             | 0 |   | 1 |
     *             |---|---|---|
     *             |   |   |   |
     *             |---|---|---|
     *             | 3 |   | 2 |
     *             |---|---|---|
     */
    private void relocateCornerRect(int idx) {
        float left=0,top=0,right=0,bottom=0;
        int offset = sCornerRadius + sLineLength;
        switch(idx) {
            case 0:
                left = mBoundRect.left;
                top = mBoundRect.top;
                right = mBoundRect.left + offset;
                bottom = mBoundRect.top + offset;
                break;
            case 1:
                left = mBoundRect.right - offset;
                top = mBoundRect.top;
                right = mBoundRect.right;
                bottom = mBoundRect.top + offset;
                break;
            case 2:
                left = mBoundRect.right - offset;
                top = mBoundRect.bottom - offset;
                right = mBoundRect.right;
                bottom = mBoundRect.bottom;
                break;
            case 3:
                left = mBoundRect.left;
                top = mBoundRect.bottom - offset;
                right = mBoundRect.left + offset;
                bottom = mBoundRect.bottom;
                break;
        }
        mCornerRectList.get(idx).set(left, top, right, bottom);
    }

    private void drawProgressRect(Canvas canvas) {
        if (mProgressRect == null || mProgressPaint == null) {
            return;
        }
        if (mProgressGoingDown) {
            mProgressPaint.setShader(new LinearGradient(mProgressRect.left, mProgressRect.top,
                    mProgressRect.left, mProgressRect.bottom,
                    Color.TRANSPARENT, Color.CYAN, Shader.TileMode.MIRROR));
        }
        else {
            mProgressPaint.setShader(new LinearGradient(mProgressRect.left, mProgressRect.top,
                    mProgressRect.left, mProgressRect.bottom,
                    Color.CYAN, Color.TRANSPARENT, Shader.TileMode.MIRROR));
        }
        canvas.drawRect(mProgressRect, mProgressPaint);
    }

}
