niftijio-ndArray version
========

I forked this repository from niftijio, which is a Java library for reading and writing NIfTI-1 image volumes which is limited to .

new features:
+ Support of 7-dimension data
+ Support of Complex 128
+ New example contais downloading, reading and rewriting a simple image time-series in gzipped single file nifti-1 form
+ Develop index iterator tool for iterating over shape of a matrix
+ allowing arbitarary dim shape

To-do:
+ testing more data sets(both MRI and MRS(I); If you have datsets, specially image data with higher dimension(+4), please contact me)
+ adding deafult datatype like nibabel
+ adding random access feature for large volume(ongoing)
+ add multi-dimension java array in once to data.(find a way for increasing the spead)

License:
As the niftijio, this is also released under the MIT license.  feel free to create issues, report bug and contact the developer via amirshamaei@isibrno.cz.
