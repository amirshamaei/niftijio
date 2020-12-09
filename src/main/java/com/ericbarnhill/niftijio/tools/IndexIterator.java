package com.ericbarnhill.niftijio.tools;

import java.util.ArrayList;
import java.util.Arrays;

public class IndexIterator {
    public ArrayList<int[]> iterate(int[] shape) {
        return iterate(shape, 0, shape.length );
    }

    public ArrayList<int[]> iterateReverse(int[] shape) {
        return iterateReverse(shape, 0, shape.length );
    }

    public ArrayList<int[]> iterate(int[] shape, int from, int to ) {
        int[] realshape = Arrays.copyOfRange(shape, from, to);
        int[] indx = new int[realshape.length];
        boolean endFlag = true;
        int temp = 0;
        ArrayList<int[]> mat = new ArrayList<int[]>();
        int l = 0;
        boolean flag = true;
        mat.add(indx.clone());

        int j = realshape.length - 1;
        for (int i = 0; i < product(realshape) - 1; i++) {
            if ((j == realshape.length - 1)) {
                while ((indx[j] == realshape[j] - 1)) {
                    if (j > 0) {
                        indx[j] = 0;
                        j -= 1;
                    } else {
                        j = realshape.length - 1;
                    }
                }
            } else {
                if ((indx[j] > realshape[j] - 1)) {
                    if (j > 0) {
                        indx[j] = 0;
                        j -= 1;
                    } else {
                        j = realshape.length - 1;
                    }
                } else {
                    j = realshape.length - 1;
                }
            }
            indx[j] += 1;
            j = realshape.length - 1;
            mat.add(indx.clone());
        }
        return mat;
    }

    public ArrayList<int[]> iterateReverse(int[] shape, int from, int to ) {
        int[] realshape = Arrays.copyOfRange(shape, from, to);
        int[] indx = new int[realshape.length];
        boolean endFlag = true;
        int temp = 0;
        ArrayList<int[]> mat = new ArrayList<int[]>();
        int l = 0;
        boolean flag = true;
        mat.add(indx.clone());

        int j = 0;
        for (int i = 0; i < product(realshape) - 1; i++) {
            if ((j == 0)) {
                while ((indx[j] == realshape[j] - 1)) {
                    if (j < realshape.length - 1) {
                        indx[j] = 0;
                        j += 1;
                    } else {
                        j = 0;
                    }
                }
            } else {
                if ((indx[j] > realshape[j] - 1)) {
                    if (j < realshape.length - 1) {
                        indx[j] = 0;
                        j += 1;
                    } else {
                        j = 0;
                    }
                } else {
                    j = 0;
                }
            }
            indx[j] += 1;
            j = 0;
            mat.add(indx.clone());
        }
        return mat;
    }

    int product(int ar[]) {
        int result = 1;
        for (int i = 0; i < ar.length; i++)
            result = result * ar[i];
        return result;
    }
}
