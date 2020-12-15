package com.ericbarnhill.niftijio;

import java.io.IOException;

public class testNifti2Header {
    public static void main(String[] args) throws IOException {

        try {
            Nifti2Header hdr = Nifti2Header.read("D:\\svs_preprocessed.nii");
            System.out.println("finish");

        } catch (IOException e) {
            e.printStackTrace();
        } finally {

        }
    }
}
