package com.ericbarnhill.niftijio;

import com.ericbarnhill.niftijio.tools.IndexIterator;

import java.io.*;
import java.util.ArrayList;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

public class NiftiVolume
{

    private static boolean v;
    private NiftiHeader header;
    private NDimensionalArray data;

    public NDimensionalArray getData() {
        return data;
    }

    public Nifti2Header getHeader2() {
        return (Nifti2Header) header;
    }
    public Nifti1Header getHeader1() {
        return (Nifti1Header) header;
    }
    public char getVersion() {
        if (v) {
            return '1';
        }else{
            return '2';}
    }
//    public Nifti1Header getHeader() {
//        return (Nifti1Header) header;
//    }
    //    public NiftiVolume(int nx, int ny, int nz, int dim)
//    {
//        this(new int[]{nx, ny, nz, dim});
//    }

// creating Nifti volume can be a source of bugs when data is complex; so fist a header should be created then volume

//    public NiftiVolume(int[] dims)
//    {
//        dims = paddims(dims);
//        this.header = new NiftiHeader(dims);
//        this.data = new NDimensionalArray(dims);
//    }

    public NiftiVolume(Nifti2Header hdr)
    {
        this.header = hdr;
        int[] dims = new int[7];

        if (hdr.datatype == Nifti2Header.NIFTI_TYPE_COMPLEX64 || hdr.datatype == Nifti2Header.NIFTI_TYPE_COMPLEX128) {
            dims[0] = (int) (hdr.dim[1]*2);
        } else {
            dims[0] = (int) hdr.dim[1];
        }

        for (int i = 2; i < 8; i++) {
            dims[i-1] = (int) hdr.dim[i];
            if (dims[i-1] == 0)
                dims[i-1] = 1;
        }
        v = false;
        this.data = new NDimensionalArray(dims);

    }

    public NiftiVolume(Nifti1Header hdr)
    {
        this.header = hdr;
        int[] dims = new int[7];

        if (hdr.datatype == Nifti1Header.NIFTI_TYPE_COMPLEX64 || hdr.datatype == Nifti1Header.NIFTI_TYPE_COMPLEX128) {
            dims[0] = hdr.dim[1]*2;
        } else {
            dims[0] = hdr.dim[1];
        }

        for (int i = 2; i < 8; i++) {
            dims[i-1] = hdr.dim[i];
            if (dims[i-1] == 0)
                dims[i-1] = 1;
        }
        v = true;
        this.data = new NDimensionalArray(dims);

    }

    @Deprecated
    public NiftiVolume(double[][][][] data)
    {
        this.data = new FourDimensionalArray(data);
        final int nx = data.length;
        final int ny = data[0].length;
        final int nz = data[0][0].length;
        final int dim = data[0][0][0].length;
        this.header = new Nifti1Header(nx, ny, nz, dim);
    }

    public static NiftiVolume read(String filename) throws IOException {

        v = true;
        try {
            Nifti1Header hdr = Nifti1Header.read(filename);
            if (hdr.sizeof_hdr != 348) {
                v = false;
            } else if (hdr.sizeof_hdr == 348) {
                InputStream is = new FileInputStream(hdr.filename);
                if (hdr.filename.endsWith(".gz"))
                    is = new GZIPInputStream(is);
                try {
                    return read(new BufferedInputStream(is), hdr);
                } finally {
                    is.close();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (!v) {
            try {
                Nifti2Header hdr = Nifti2Header.read(filename);
                if (hdr.sizeof_hdr != 540) {
                    v = true;
                } else if (hdr.sizeof_hdr == 540) {
                    InputStream is = new FileInputStream(hdr.filename);
                    if (hdr.filename.endsWith(".gz"))
                        is = new GZIPInputStream(is);
                    try {
                        return read(new BufferedInputStream(is), hdr);
                    } finally {
                        is.close();
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

        }

        return null;
    }



    /** Read the NIFTI volume from a NIFTI input stream.
     *
     * @param is an input stream pointing to the beginning of the NIFTI file, uncompressed.
     * @return a NIFTI volume
     * @throws IOException
     */
    public static NiftiVolume read(InputStream is) throws IOException {
        return read(is, null);
    }

    /** Read the NIFTI volume from a NIFTI input stream.
     *
     * @param is an input stream pointing to the beginning of the NIFTI file, uncompressed. The operation will close the stream.
     * @param filename the name of the original file, can be null
     * @return a NIFTI volume
     * @throws IOException
     */
    public static NiftiVolume read(InputStream is, String filename) throws IOException {
        BufferedInputStream bis = new BufferedInputStream(is);
        try {
            bis.mark(2048);
            Nifti1Header hdr = Nifti1Header.read(bis, filename);
            bis.reset();
            return read(bis, hdr);
        } finally {
            bis.close();
        }
    }
    private static NiftiVolume read(BufferedInputStream is, Nifti2Header hdr) throws IOException {
        // skip header
        is.skip((long) hdr.vox_offset);
        int[] dims = new int[7];

        if (hdr.datatype == Nifti2Header.NIFTI_TYPE_COMPLEX64 || hdr.datatype == Nifti2Header.NIFTI_TYPE_COMPLEX128) {
            dims[0] = (int) (hdr.dim[1]*2);
        } else {
            dims[0] = (int) hdr.dim[1];
        }

        for (int i = 2; i < 8; i++) {
            dims[i-1] = (int) hdr.dim[i];
            if (dims[i-1] == 0)
                dims[i-1] = 1;
        }

        NiftiVolume out = new NiftiVolume(hdr);
        DataInput di = hdr.little_endian ? new LEDataInputStream(is) : new DataInputStream(is);
// the structure of code has a proble.may be better to have switch first then iterating over indecies
        double v;
        ArrayList<int[]> idcs = new IndexIterator().iterateReverse(dims);
        for (int[] idc:idcs)  {
            switch (hdr.datatype) {
                case Nifti2Header.NIFTI_TYPE_INT8:
                case Nifti2Header.NIFTI_TYPE_UINT8:
                    v = di.readByte();

                    if ((hdr.datatype == Nifti2Header.NIFTI_TYPE_UINT8) && v < 0)
                        v = v + 256d;
                    if (hdr.scl_slope != 0)
                        v = v * hdr.scl_slope + hdr.scl_inter;
                    break;
                case Nifti2Header.NIFTI_TYPE_INT16:
                case Nifti2Header.NIFTI_TYPE_UINT16:
                    v = (double) (di.readShort());

                    if ((hdr.datatype == Nifti2Header.NIFTI_TYPE_UINT16) && (v < 0))
                        v = Math.abs(v) + (double) (1 << 15);
                    if (hdr.scl_slope != 0)
                        v = v * hdr.scl_slope + hdr.scl_inter;
                    break;
                case Nifti2Header.NIFTI_TYPE_INT32:
                case Nifti2Header.NIFTI_TYPE_UINT32:
                    v = (double) (di.readInt());
                    if ((hdr.datatype == Nifti2Header.NIFTI_TYPE_UINT32) && (v < 0))
                        v = Math.abs(v) + (double) (1 << 31);
                    if (hdr.scl_slope != 0)
                        v = v * hdr.scl_slope + hdr.scl_inter;
                    break;
                case Nifti2Header.NIFTI_TYPE_INT64:
                case Nifti2Header.NIFTI_TYPE_UINT64:
                    v = (double) (di.readLong());
                    if ((hdr.datatype == Nifti2Header.NIFTI_TYPE_UINT64) && (v < 0))
                        v = Math.abs(v) + (double) (1 << 63);
                    if (hdr.scl_slope != 0)
                        v = v * hdr.scl_slope + hdr.scl_inter;
                    break;
                case Nifti2Header.NIFTI_TYPE_FLOAT32:
                    v = (double) (di.readFloat());
                    if (hdr.scl_slope != 0)
                        v = v * hdr.scl_slope + hdr.scl_inter;
                    break;
                case Nifti2Header.NIFTI_TYPE_FLOAT64:
                    v = (double) (di.readDouble());
                    if (hdr.scl_slope != 0)
                        v = v * hdr.scl_slope + hdr.scl_inter;
                    break;
                case Nifti2Header.NIFTI_TYPE_COMPLEX64:
                    v = (double) (di.readFloat());
                    if (hdr.scl_slope != 0)
                        v = v * hdr.scl_slope + hdr.scl_inter;
                    break;
                case Nifti2Header.DT_NONE:
                case Nifti2Header.DT_BINARY:
                case Nifti2Header.NIFTI_TYPE_FLOAT128:
                case Nifti2Header.NIFTI_TYPE_RGB24:
                case Nifti2Header.NIFTI_TYPE_COMPLEX128:
                    v = (double) (di.readDouble());
                    if (hdr.scl_slope != 0)
                        v = v * hdr.scl_slope + hdr.scl_inter;
                    break;
                case Nifti2Header.NIFTI_TYPE_COMPLEX256:
                case Nifti2Header.DT_ALL:
                default:
                    throw new IOException("Sorry, cannot yet read nifti-1 datatype " + Nifti2Header.decodeDatatype(hdr.datatype));
            }
            out.data.set(idc,v);
        }
        return out;
    }
    private static NiftiVolume read(BufferedInputStream is, Nifti1Header hdr) throws IOException {
        // skip header
        is.skip((long) hdr.vox_offset);
        int[] dims = new int[7];

        if (hdr.datatype == Nifti1Header.NIFTI_TYPE_COMPLEX64 || hdr.datatype == Nifti1Header.NIFTI_TYPE_COMPLEX128) {
            dims[0] = hdr.dim[1]*2;
        } else {
            dims[0] = hdr.dim[1];
        }

        for (int i = 2; i < 8; i++) {
            dims[i-1] = hdr.dim[i];
            if (dims[i-1] == 0)
                dims[i-1] = 1;
        }

        NiftiVolume out = new NiftiVolume(hdr);
        DataInput di = hdr.little_endian ? new LEDataInputStream(is) : new DataInputStream(is);

        double v;
        ArrayList<int[]> idcs = new IndexIterator().iterateReverse(dims);
            for (int[] idc:idcs)  {
                switch (hdr.datatype) {
                    case Nifti1Header.NIFTI_TYPE_INT8:
                    case Nifti1Header.NIFTI_TYPE_UINT8:
                        v = di.readByte();

                        if ((hdr.datatype == Nifti1Header.NIFTI_TYPE_UINT8) && v < 0)
                            v = v + 256d;
                        if (hdr.scl_slope != 0)
                            v = v * hdr.scl_slope + hdr.scl_inter;
                        break;
                    case Nifti1Header.NIFTI_TYPE_INT16:
                    case Nifti1Header.NIFTI_TYPE_UINT16:
                        v = (double) (di.readShort());

                        if ((hdr.datatype == Nifti1Header.NIFTI_TYPE_UINT16) && (v < 0))
                            v = Math.abs(v) + (double) (1 << 15);
                        if (hdr.scl_slope != 0)
                            v = v * hdr.scl_slope + hdr.scl_inter;
                        break;
                    case Nifti1Header.NIFTI_TYPE_INT32:
                    case Nifti1Header.NIFTI_TYPE_UINT32:
                        v = (double) (di.readInt());
                        if ((hdr.datatype == Nifti1Header.NIFTI_TYPE_UINT32) && (v < 0))
                            v = Math.abs(v) + (double) (1 << 31);
                        if (hdr.scl_slope != 0)
                            v = v * hdr.scl_slope + hdr.scl_inter;
                        break;
                    case Nifti1Header.NIFTI_TYPE_INT64:
                    case Nifti1Header.NIFTI_TYPE_UINT64:
                        v = (double) (di.readLong());
                        if ((hdr.datatype == Nifti1Header.NIFTI_TYPE_UINT64) && (v < 0))
                            v = Math.abs(v) + (double) (1 << 63);
                        if (hdr.scl_slope != 0)
                            v = v * hdr.scl_slope + hdr.scl_inter;
                        break;
                    case Nifti1Header.NIFTI_TYPE_FLOAT32:
                        v = (double) (di.readFloat());
                        if (hdr.scl_slope != 0)
                            v = v * hdr.scl_slope + hdr.scl_inter;
                        break;
                    case Nifti1Header.NIFTI_TYPE_FLOAT64:
                        v = (double) (di.readDouble());
                        if (hdr.scl_slope != 0)
                            v = v * hdr.scl_slope + hdr.scl_inter;
                        break;
                    case Nifti1Header.NIFTI_TYPE_COMPLEX64:
                        v = (double) (di.readFloat());
                        if (hdr.scl_slope != 0)
                            v = v * hdr.scl_slope + hdr.scl_inter;
                        break;
                    case Nifti1Header.DT_NONE:
                    case Nifti1Header.DT_BINARY:
                    case Nifti1Header.NIFTI_TYPE_FLOAT128:
                    case Nifti1Header.NIFTI_TYPE_RGB24:
                    case Nifti1Header.NIFTI_TYPE_COMPLEX128:
                        v = (double) (di.readDouble());
                        if (hdr.scl_slope != 0)
                            v = v * hdr.scl_slope + hdr.scl_inter;
                        break;
                    case Nifti1Header.NIFTI_TYPE_COMPLEX256:
                    case Nifti1Header.DT_ALL:
                    default:
                        throw new IOException("Sorry, cannot yet read nifti-1 datatype " + Nifti1Header.decodeDatatype(hdr.datatype));
                }
                out.data.set(idc,v);
            }
        return out;
    }

    public void write(String filename) throws IOException
    {
        if (v) {
            Nifti1Header hdr = (Nifti1Header) this.header;
            hdr.filename = filename;

            int[] dims = new int[7];

            if (hdr.datatype == Nifti1Header.NIFTI_TYPE_COMPLEX64 || hdr.datatype == Nifti1Header.NIFTI_TYPE_COMPLEX128) {
                dims[0] = hdr.dim[1]*2;
            } else {
                dims[0] = hdr.dim[1];
            }

            for (int i = 2; i < 8; i++) {
                dims[i-1] = hdr.dim[i];
                if (dims[i-1] == 0)
                    dims[i-1] = 1;
            }

            OutputStream os = new BufferedOutputStream(new FileOutputStream(hdr.filename));
            if (hdr.filename.endsWith(".gz"))
                os = new BufferedOutputStream(new GZIPOutputStream(os));

            DataOutput dout = (hdr.little_endian) ? new LEDataOutputStream(os) : new DataOutputStream(os);

            byte[] hbytes = hdr.encodeHeader();
            dout.write(hbytes);

            int nextra = (int) hdr.vox_offset - hbytes.length;
            byte[] extra = new byte[nextra];
            dout.write(extra);
            if (dims[0] !=0) {
                ArrayList<int[]> idcs = new IndexIterator().iterateReverse(dims);
                for (int[] idc:idcs)  {
                                double v = this.data.get(idc);
                                switch (hdr.datatype) {
                                    case Nifti1Header.NIFTI_TYPE_INT8:
                                    case Nifti1Header.NIFTI_TYPE_UINT8:
                                        if (hdr.scl_slope == 0)
                                            dout.writeByte((int) v);
                                        else
                                            dout.writeByte((int) ((v - hdr.scl_inter) / hdr.scl_slope));
                                        break;
                                    case Nifti1Header.NIFTI_TYPE_INT16:
                                    case Nifti1Header.NIFTI_TYPE_UINT16:
                                        if (hdr.scl_slope == 0)
                                            dout.writeShort((short) (v));
                                        else
                                            dout.writeShort((short) ((v - hdr.scl_inter) / hdr.scl_slope));
                                        break;
                                    case Nifti1Header.NIFTI_TYPE_INT32:
                                    case Nifti1Header.NIFTI_TYPE_UINT32:
                                        if (hdr.scl_slope == 0)
                                            dout.writeInt((int) (v));
                                        else
                                            dout.writeInt((int) ((v - hdr.scl_inter) / hdr.scl_slope));
                                        break;
                                    case Nifti1Header.NIFTI_TYPE_INT64:
                                    case Nifti1Header.NIFTI_TYPE_UINT64:
                                        if (hdr.scl_slope == 0)
                                            dout.writeLong((long) Math.rint(v));
                                        else
                                            dout.writeLong((long) Math.rint((v - hdr.scl_inter) / hdr.scl_slope));
                                        break;
                                    case Nifti1Header.NIFTI_TYPE_FLOAT32:
                                        if (hdr.scl_slope == 0)
                                            dout.writeFloat((float) (v));
                                        else
                                            dout.writeFloat((float) ((v - hdr.scl_inter) / hdr.scl_slope));
                                        break;
                                    case Nifti1Header.NIFTI_TYPE_FLOAT64:
                                        if (hdr.scl_slope == 0)
                                            dout.writeDouble(v);
                                        else
                                            dout.writeDouble((v - hdr.scl_inter) / hdr.scl_slope);
                                        break;
                                    case Nifti1Header.DT_NONE:
                                    case Nifti1Header.DT_BINARY:
                                    case Nifti1Header.NIFTI_TYPE_COMPLEX64:
                                        if (hdr.scl_slope == 0)
                                            dout.writeFloat((float) v);
                                        else
                                            dout.writeFloat((float) ((v - hdr.scl_inter) / hdr.scl_slope));
                                        break;
                                    case Nifti1Header.NIFTI_TYPE_FLOAT128:
                                    case Nifti1Header.NIFTI_TYPE_RGB24:
                                    case Nifti1Header.NIFTI_TYPE_COMPLEX128:
                                        if (hdr.scl_slope == 0)
                                            dout.writeDouble((double) v);
                                        else
                                            dout.writeDouble((double) ((v - hdr.scl_inter) / hdr.scl_slope));
                                        break;
                                    case Nifti1Header.NIFTI_TYPE_COMPLEX256:
                                    case Nifti1Header.DT_ALL:
                                    default:
                                        throw new IOException("Sorry, cannot yet write nifti-1 datatype " + Nifti1Header.decodeDatatype(hdr.datatype));

                                }
                            }
            }

            if (hdr.little_endian)
                ((LEDataOutputStream) dout).close();
            else
                ((DataOutputStream) dout).close();
        } else if (!v) {
            Nifti2Header hdr = (Nifti2Header) this.header;
            hdr.filename = filename;

            int[] dims = new int[7];

            if (hdr.datatype == Nifti2Header.NIFTI_TYPE_COMPLEX64 || hdr.datatype == Nifti2Header.NIFTI_TYPE_COMPLEX128) {
                dims[0] = (int) (hdr.dim[1]*2);
            } else {
                dims[0] = (int) hdr.dim[1];
            }

            for (int i = 2; i < 8; i++) {
                dims[i-1] = (int) hdr.dim[i];
                if (dims[i-1] == 0)
                    dims[i-1] = 1;
            }

            OutputStream os = new BufferedOutputStream(new FileOutputStream(hdr.filename));
            if (hdr.filename.endsWith(".gz"))
                os = new BufferedOutputStream(new GZIPOutputStream(os));

            DataOutput dout = (hdr.little_endian) ? new LEDataOutputStream(os) : new DataOutputStream(os);

            byte[] hbytes = hdr.encodeHeader();
            dout.write(hbytes);

            int nextra = (int) hdr.vox_offset - hbytes.length;
            byte[] extra = new byte[nextra];
            dout.write(extra);
            if (dims[0] !=0) {
                ArrayList<int[]> idcs = new IndexIterator().iterateReverse(dims);
                for (int[] idc : idcs) {
                    double v = this.data.get(idc);
                    switch (hdr.datatype) {
                        case Nifti2Header.NIFTI_TYPE_INT8:
                        case Nifti2Header.NIFTI_TYPE_UINT8:
                            if (hdr.scl_slope == 0)
                                dout.writeByte((int) v);
                            else
                                dout.writeByte((int) ((v - hdr.scl_inter) / hdr.scl_slope));
                            break;
                        case Nifti2Header.NIFTI_TYPE_INT16:
                        case Nifti2Header.NIFTI_TYPE_UINT16:
                            if (hdr.scl_slope == 0)
                                dout.writeShort((short) (v));
                            else
                                dout.writeShort((short) ((v - hdr.scl_inter) / hdr.scl_slope));
                            break;
                        case Nifti2Header.NIFTI_TYPE_INT32:
                        case Nifti2Header.NIFTI_TYPE_UINT32:
                            if (hdr.scl_slope == 0)
                                dout.writeInt((int) (v));
                            else
                                dout.writeInt((int) ((v - hdr.scl_inter) / hdr.scl_slope));
                            break;
                        case Nifti2Header.NIFTI_TYPE_INT64:
                        case Nifti2Header.NIFTI_TYPE_UINT64:
                            if (hdr.scl_slope == 0)
                                dout.writeLong((long) Math.rint(v));
                            else
                                dout.writeLong((long) Math.rint((v - hdr.scl_inter) / hdr.scl_slope));
                            break;
                        case Nifti2Header.NIFTI_TYPE_FLOAT32:
                            if (hdr.scl_slope == 0)
                                dout.writeFloat((float) (v));
                            else
                                dout.writeFloat((float) ((v - hdr.scl_inter) / hdr.scl_slope));
                            break;
                        case Nifti2Header.NIFTI_TYPE_FLOAT64:
                            if (hdr.scl_slope == 0)
                                dout.writeDouble(v);
                            else
                                dout.writeDouble((v - hdr.scl_inter) / hdr.scl_slope);
                            break;
                        case Nifti2Header.DT_NONE:
                        case Nifti2Header.DT_BINARY:
                        case Nifti2Header.NIFTI_TYPE_COMPLEX64:
                            if (hdr.scl_slope == 0)
                                dout.writeFloat((float) v);
                            else
                                dout.writeFloat((float) ((v - hdr.scl_inter) / hdr.scl_slope));
                            break;
                        case Nifti2Header.NIFTI_TYPE_FLOAT128:
                        case Nifti2Header.NIFTI_TYPE_RGB24:
                        case Nifti2Header.NIFTI_TYPE_COMPLEX128:
                            if (hdr.scl_slope == 0)
                                dout.writeDouble((double) v);
                            else
                                dout.writeDouble((double) ((v - hdr.scl_inter) / hdr.scl_slope));
                            break;
                        case Nifti2Header.NIFTI_TYPE_COMPLEX256:
                        case Nifti2Header.DT_ALL:
                        default:
                            throw new IOException("Sorry, cannot yet write nifti-1 datatype " + Nifti2Header.decodeDatatype(hdr.datatype));

                    }
                }
            }
            if (hdr.little_endian)
                ((LEDataOutputStream) dout).close();
            else
                ((DataOutputStream) dout).close();
        }


        return;
    }
}
