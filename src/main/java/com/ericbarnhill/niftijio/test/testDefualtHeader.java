package com.ericbarnhill.niftijio.test;

import com.ericbarnhill.niftijio.NiftiHeader;
import com.ericbarnhill.niftijio.NiftiVolume;

import java.io.IOException;

public class testDefualtHeader {
    public static void main(String[] args) throws IOException {
        NiftiHeader niftiHeader = new NiftiHeader();
        NiftiVolume niftiVolume = new NiftiVolume(niftiHeader);
        niftiVolume.write("default.nii");
    }
}
