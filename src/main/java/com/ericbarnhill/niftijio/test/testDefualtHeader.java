package com.ericbarnhill.niftijio.test;

import com.ericbarnhill.niftijio.Nifti1Header;
import com.ericbarnhill.niftijio.NiftiVolume;

import java.io.IOException;

public class testDefualtHeader {
    public static void main(String[] args) throws IOException {
        Nifti1Header nifti1Header = new Nifti1Header();
        NiftiVolume niftiVolume = new NiftiVolume(nifti1Header);
        niftiVolume.write("default.nii");
    }
}
