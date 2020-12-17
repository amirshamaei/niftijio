package com.ericbarnhill.niftijio.test;

import com.ericbarnhill.niftijio.Nifti1Header;
import com.ericbarnhill.niftijio.NiftiVolume;
import com.ericbarnhill.niftijio.tools.IndexIterator;

import java.io.IOException;
import java.util.ArrayList;

public class testNDarray {
    public static void main(String[] args) throws IOException {
//        InputStream inputStream = new URL("https://nifti.nimh.nih.gov/nifti-1/data/filtered_func_data.nii.gz").openStream();
//        Files.copy(inputStream, Paths.get(System.getProperty("user.dir") + "/filtered_func_data.nii.gz"), StandardCopyOption.REPLACE_EXISTING);
        NiftiVolume niftiVolume = NiftiVolume.read(System.getProperty("user.dir") + "/filtered_func_data.nii.gz");

        ArrayList<int[]> indcs = new IndexIterator().iterateReverse(niftiVolume.getData().getDims());
//        niftiVolume.header.datatype = 64;
        NiftiVolume newNiftiVolume = new NiftiVolume((Nifti1Header) niftiVolume.getHeader1());
        for (int[] indc: indcs
             ) {
            newNiftiVolume.getData().set(indc, niftiVolume.getData().get(indc));
        }
        newNiftiVolume.write(System.getProperty("user.dir") + "/NewFiltered_func_data.nii.gz");
    }
}
