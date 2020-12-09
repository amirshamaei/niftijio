package com.ericbarnhill.niftijio;

import java.util.Arrays;

public class NDimensionalArray {

    private double[] data;
    private int[] dims = new int[7];
    private int size;
    private int nx, ny, nz, dim;

    public NDimensionalArray(int[] dims) {
        this.nx = dims[0];
        this.ny = dims[1];
        this.nz = dims[2];
        this.dim = dims[3];
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

    public double get(int x, int y, int z, int d) {
        int idx = d * (nx * ny * nz) + z * (nx * ny) + y * nx + x;
        return data[idx];
    }

    public void set(int x, int y, int z, int d, double val) {
        int idx = d * (nx * ny * nz) + z * (nx * ny) + y * nx + x;
        data[idx] = val;
    }

    public double[][][][] toArray() {
        double[][][][] array = new double[nx][ny][nz][dim];
        for (int d = 0; d < dim; d++)
            for (int k = 0; k < nz; k++)
                for (int j = 0; j < ny; j++)
                    for (int i = 0; i < nx; i++) {
                        array[i][j][k][d] = get(i,j,k,d);
                    }
        return array;
    }

    public int sizeX() {return nx;}
    public int sizeY() {return ny;}
    public int sizeZ() {return nz;}
    public int dimension() {return dim;}

    public int[] getDims() {
        return dims;
    }

    public void setDims(int[] dims) {
        this.dims = dims;
    }
}
