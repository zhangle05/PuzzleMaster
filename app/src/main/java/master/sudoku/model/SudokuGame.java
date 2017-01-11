package master.sudoku.model;

import master.sudoku.config.DeviceConfig;

public class SudokuGame {

    private Sudoku mModel1;
    private Sudoku mModel2;
    private Sudoku mModel3;
    private Sudoku mModel4;
    private Sudoku mModel5;
    private Sudoku mModelProfessional;
    private Sudoku mModelHardest;

    public SudokuGame() {
        if (DeviceConfig.mHeight <= 0) {
            // For Nokia C7-00
            DeviceConfig.mHeight = 520;
            DeviceConfig.mWidth = 360;
        }

        mModel1 = new Sudoku();
        mModel2 = new Sudoku();
        mModel3 = new Sudoku();
        mModel4 = new Sudoku();
        mModel5 = new Sudoku();
        mModelProfessional = new Sudoku();
        mModelHardest = new Sudoku();
        initModel();
    }

    public Sudoku getModel1() {
        return mModel1;
    }


    public Sudoku getModel2() {
        return mModel2;
    }


    public Sudoku getModel3() {
        return mModel3;
    }


    public Sudoku getModel4() {
        return mModel4;
    }


    public Sudoku getModel5() {
        return mModel5;
    }


    public Sudoku getModelProfessional() {
        return mModelProfessional;
    }


    public Sudoku getModelHardest() {
        return mModelHardest;
    }

    private void initModel() {
        int[] testValue1 = new int[] {
                6, 5, 3, 0, 0, 0, 7, 0, 0,
                8, 2, 0, 7, 3, 0, 5, 4, 0,
                0, 7, 0, 0, 0, 1, 8, 0, 3,
                7, 0, 0, 1, 4, 3, 0, 0, 8,
                3, 8, 0, 6, 7, 5, 0, 9, 0,
                1, 4, 0, 2, 0, 8, 0, 5, 0,
                2, 0, 0, 0, 0, 4, 0, 0, 6,
                0, 0, 0, 9, 0, 2, 1, 7, 4,
                4, 6, 9, 3, 1, 0, 0, 0, 5 };

        int[] testValue2 = new int[] {
                0, 7, 1, 0, 9, 0, 0, 0, 0,
                0, 2, 8, 3, 0, 7, 0, 6, 0,
                0, 0, 9, 0, 0, 0, 8, 1, 7,
                2, 0, 6, 0, 0, 0, 0, 0, 0,
                0, 0, 0, 9, 3, 6, 1, 0, 0,
                3, 0, 5, 7, 0, 2, 6, 0, 4,
                0, 0, 7, 0, 6, 3, 0, 2, 0,
                1, 0, 2, 8, 0, 0, 3, 4, 0,
                0, 0, 0, 0, 0, 4, 0, 7, 1 };

        int[] testValue3 = new int[] {
                0, 0, 0, 2, 0, 0, 8, 3, 7,
                7, 0, 0, 5, 1, 3, 0, 0, 0,
                9, 0, 3, 6, 7, 8, 1, 0, 0,
                0, 3, 8, 7, 0, 0, 0, 9, 1,
                0, 9, 6, 0, 5, 0, 0, 2, 0,
                5, 7, 1, 0, 0, 0, 0, 6, 0,
                0, 6, 9, 4, 2, 0, 0, 0, 5,
                0, 0, 7, 0, 0, 6, 0, 1, 0,
                0, 2, 0, 0, 3, 5, 9, 0, 0 };

        int[] testValue4 = new int[] {
                9, 0, 0, 0, 6, 0, 0, 0, 5,
                4, 0, 2, 3, 0, 5, 8, 6, 0,
                5, 3, 0, 0, 1, 0, 0, 7, 0,
                3, 0, 5, 0, 0, 0, 0, 9, 7,
                0, 0, 0, 5, 3, 0, 6, 2, 0,
                2, 1, 0, 0, 7, 0, 0, 0, 0,
                0, 0, 3, 0, 2, 0, 0, 8, 0,
                6, 4, 0, 0, 0, 0, 5, 1, 0,
                8, 0, 0, 0, 0, 7, 0, 4, 0 };

        int[] testValue5 = new int[] {
                0, 6, 0, 0, 0, 1, 9, 0, 7,
                1, 0, 0, 0, 0, 7, 2, 3, 0,
                0, 8, 0, 0, 0, 0, 4, 0, 6,
                0, 1, 8, 0, 0, 2, 0, 0, 4,
                0, 7, 0, 0, 4, 0, 0, 9, 0,
                9, 0, 0, 1, 0, 0, 7, 8, 0,
                6, 0, 7, 0, 0, 0, 0, 4, 0,
                0, 5, 1, 6, 0, 0, 0, 0, 9,
                8, 0, 9, 3, 0, 0, 0, 2, 0 };

        int[] professional = new int[] {
                6, 0, 0, 0, 0, 0, 0, 0, 9,
                0, 0, 7, 4, 0, 5, 0, 3, 0,
                4, 8, 9, 3, 0, 0, 5, 0, 0,
                0, 6, 3, 0, 0, 0, 9, 0, 2,
                0, 5, 0, 0, 0, 0, 0, 7, 0,
                9, 0, 4, 0, 0, 0, 3, 8, 0,
                0, 0, 6, 0, 0, 4, 2, 1, 5,
                0, 4, 0, 5, 0, 2, 7, 0, 0,
                5, 0, 0, 0, 0, 0, 0, 0, 3 };

        int[] hardest = new int[] {
                8, 0, 0, 0, 0, 0, 0, 0, 0,
                0, 0, 3, 6, 0, 0, 0, 0, 0,
                0, 7, 0, 0, 9, 0, 2, 0, 0,
                0, 5, 0, 0, 0, 7, 0, 0, 0,
                0, 0, 0, 0, 4, 5, 7, 0, 0,
                0, 0, 0, 1, 0, 0, 0, 3, 0,
                0, 0, 1, 0, 0, 0, 0, 6, 8,
                0, 0, 8, 5, 0, 0, 0, 1, 0,
                0, 9, 0, 0, 0, 0, 4, 0, 0 };

        mModel1 = buildModel(testValue1);
        mModel2 = buildModel(testValue2);
        mModel3 = buildModel(testValue3);
        mModel4 = buildModel(testValue4);
        mModel5 = buildModel(testValue5);
        mModelProfessional = buildModel(professional);
        mModelHardest = buildModel(hardest);
    }

    private Sudoku buildModel(int[] testValue) {
        Sudoku result = new Sudoku();
        for(int m=0; m<testValue.length; m++) {
            int i=m%9;
            int j=m/9;
            if(testValue[m] > 0) {
                result.setInitValue(i, j, testValue[m]);
            }
        }
        return result;
    }

}