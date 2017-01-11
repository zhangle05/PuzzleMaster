/**
 * 
 */
package master.sudoku.ocr;

/**
 * @author dannyzha
 *
 */
public class Index {
    public static int INVALID_INDEX = -1;

    private int i = INVALID_INDEX;
    private int j = INVALID_INDEX;

    /**
     * Constructor
     * @param i
     * @param j
     */
    public Index(int i, int j) {
	    this.i = i;
	    this.j = j;
    }

    public boolean Equals(int i, int j) {
	    return this.i == i && this.j == j;
    }

    @Override
    public boolean equals(Object o) {
	    if(!(o instanceof Index)) {
		    return false;
	    }
	    Index other = (Index)o;
	    return other.Equals(this.i, this.j);
    }

    public int getI() {
	    return i;
    }

    public void setI(int i) {
	    this.i = i;
    }

    public int getJ() {
	    return j;
    }

    public void setJ(int j) {
	    this.j = j;
    }
}
