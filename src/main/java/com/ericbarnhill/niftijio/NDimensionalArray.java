package com.ericbarnhill.niftijio;

import java.util.Arrays;

public class NDimensionalArray {

    private double[] data;
    private int[] dims;
    private int size;


    public NDimensionalArray(int[] dims) {
        this.dims = dims;
        size = 1;
        Arrays.stream(dims).forEach(i -> size *= i);
        this.data = new double[size];
    }

    public double get(int[] idc) {
        int idx = idc[idc.length-1];
        for (int i = idc.length-2; i >= 0 ; i--) {
            idx = ((dims[i] * idx) + idc[i]);
        }
        return data[idx];
    }

    public void set(int[] idc, double val) {
        int idx = idc[idc.length-1];
        for (int i = idc.length-2; i >= 0 ; i--) {
            idx = ((dims[i] * idx) + idc[i]);
        }
        data[idx] = val;
    }





//    public double[][][][] toArray() {
//        double[][][][] array = new double[nx][ny][nz][dim];
//        for (int d = 0; d < dim; d++)
//            for (int k = 0; k < nz; k++)
//                for (int j = 0; j < ny; j++)
//                    for (int i = 0; i < nx; i++) {
//                        array[i][j][k][d] = get(i,j,k,d);
//                    }
//        return array;
//    }

    public int sizeX() {return dims[0];}
    public int sizeY() {return dims[1];}
    public int sizeZ() {return dims[2];}
    public int dimension() {return dims[3];}

    public int[] getDims() {
        return dims;
    }

    public void setDims(int[] dims) {
        this.dims = dims;
    }
}
