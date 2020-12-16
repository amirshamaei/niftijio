package com.ericbarnhill.niftijio.test;

import com.ericbarnhill.niftijio.NiftiVolume;

import java.io.IOException;

public class testNifti2Header {
    public static void main(String[] args) throws IOException {

        try {
//            Nifti2Header hdr = Nifti2Header.read("D:\\svs_preprocessed.nii");
            NiftiVolume niftiVolume = NiftiVolume.read("D:\\svs_preprocessed.nii");
//            double[] data = new double[4096];
//            double[] x = new double[4096];
//            for (int i = 0; i < 4096; i++) {
//                data[i] = niftiVolume.data.get(new int[] {0,0,0,i});
//                x[i]  = ((double) i);
//            }
//            Plot plot = Plot.plot(null).
//                            series(null, Plot.data().xy(x,data)
//                                    , null);
//            plot.save("sample_minimal", "png");
//            System.out.println("finish");

        } catch (IOException e) {
            e.printStackTrace();
        } finally {

        }
    }
}
