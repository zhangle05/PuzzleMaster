/**
 * 
 */
package master.sudoku.ocr.matrix;

/**
 * @author dannyzha
 *
 */
abstract public class MatrixBase
{
    protected int mDimensionX;
    protected int mDimensionY;

    public int getDimensionX()
    {
        return mDimensionX;
    }

    public int getDimensionY()
    {
        return mDimensionY;

    }

    /**
     * Constructor
     * @param dimensionX
     * @param dimensionY
     */
    public MatrixBase(int dimensionX, int dimensionY)
    {
        mDimensionX = dimensionX;
        mDimensionY = dimensionY;
    }

    public abstract int getValue(int x, int y);
    public abstract void setValue(int x, int y, int value);
    public abstract boolean isSimilar(MatrixBase another);
}
