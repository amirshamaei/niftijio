<!-- Copy and paste the converted output. -->

<!-----
NEW: Check the "Suppress top comment" option to remove this info from the output.

Conversion time: 0.302 seconds.


Using this Markdown file:

1. Paste this output into your source file.
2. See the notes and action items below regarding this conversion run.
3. Check the rendered output (headings, lists, code blocks, tables) for proper
   formatting and use a linkchecker before you publish this page.

Conversion notes:

* Docs to Markdown version 1.0β29
* Wed Dec 09 2020 08:52:08 GMT-0800 (PST)
* Source doc: Untitled document
----->



# niftijio(7-dimension version)

I forked this repository from [niftijio](https://github.com/cabeen/niftijio), which is a Java library for reading and writing NIfTI-1 image volumes.


## new features:

### NOW niftijio can read NifTi-2 format

*    Support of 7-dimension data
*    Support of Complex 128
*    New example contais downloading, reading and rewriting a simple image time-series in gzipped single file nifti-1 form
*    Develop index iterator tool for iterating over shape of a matrix
*    allowing arbitarary dim shape


## To-do:



*    testing more data sets(both MRI and MRS(I); If you have datsets, specially image data with higher dimension(4), please contact me)
*    adding deafult datatype like nibabel
*    adding random access feature for large volume(ongoing)
*    Converting multi-dimension java array in once to data. (find a way for increasing the speed)
*    support for nifti-2


## License:

As the [niftijio](https://github.com/cabeen/niftijio), this is also released under the MIT license.  Feel free to create issues, report bug, and contact the developer via amirshamaei@isibrno.cz.

# This project was supported by:

European Union's Horizon 2020 research and innovation program under the Marie Sklodowska-Curie grant agreement No 813120 (INSPiRE-MED)
