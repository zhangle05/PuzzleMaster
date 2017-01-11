/**
 *
 */
package master.sudoku.solve;

import java.util.Vector;

import master.sudoku.model.Index;

/**
 * @author dannyzha
 *
 */
class CellValue {
    private Index mIdx;
    private Vector<Integer> mValueList;

    /**
     * Constructor
     * @param i
     * @param j
     */
    public CellValue(int i, int j) {
        mIdx = new Index(i, j);
        mValueList = new Vector<Integer>();
    }

    /**
     * Constructor
     * @param idx
     */
    public CellValue(Index idx) {
        this.mIdx = idx;
        mValueList = new Vector<Integer>();
    }

    /**
     * get a copy of the value set
     * @return
     */
    public Vector<Integer> getValueList() {
        Vector<Integer> result =  new Vector<Integer>();
        for(int i=0; i<mValueList.size(); i++) {
            result.addElement(mValueList.elementAt(i));
        }

        return result;
    }

    public int getValueCount() {
        return mValueList.size();
    }

    public Index getIndex() {
        return mIdx;
    }

    public int getSingleValue() {
        return ((Integer)mValueList.elementAt(0)).intValue();
    }

    public boolean acceptValue(int value) {
        for(int i=0; i<mValueList.size(); i++) {
            if(((Integer)mValueList.elementAt(i)).intValue() == value) {
                return true;
            }
        }
        return false;
    }

    public boolean isMyIndex(int i, int j) {
        return mIdx.equals(i, j);
    }

    public void addValue(int value) {
        if(!acceptValue(value)) {
            mValueList.addElement(Integer.valueOf(value));
        }
    }

    public boolean equals(Object o) {
        if(!(o instanceof CellValue)) {
            return false;
        }
        CellValue other = (CellValue)o;
        if(this.getValueCount() != other.getValueCount()) {
            return false;
        }
        Vector<Integer> valueList = this.getValueList();
        for(int i=0; i<valueList.size(); i++) {
            if(!other.acceptValue(((Integer)valueList.elementAt(i)).intValue())) {
                return false;
            }
        }
        return true;
    }
}
