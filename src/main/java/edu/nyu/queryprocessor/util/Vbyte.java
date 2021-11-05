package edu.nyu.queryprocessor.util;

import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.List;

public class Vbyte {
    public static List<Long> decode(byte[] encodedBytes) throws EOFException {
        List<Long> res = new ArrayList<>();
        long out = 0;
        int shift = 0;
        long readbyte = encodedBytes[0];
        if (readbyte == -1) throw new EOFException();
        int count = 0;
        int i = 0;
        while (true) {
            while ((readbyte & 0x80) == 0) {
                if (shift >= 50) {
                    throw new IllegalArgumentException();
                }
                out |= (readbyte & 127) << shift;
                i++;
                readbyte = encodedBytes[i];
                if (readbyte == -1) throw new EOFException();
                shift += 7;
            }
            out |= (readbyte & 127) << shift;
            res.add(out);
            if (i >= encodedBytes.length) {
                break;
            }
            while (i < encodedBytes.length && (readbyte = encodedBytes[i++]) == -1) {
            }
            shift = 0;
            out = 0;
        }
        return res;
    }
    public static List<Long> decode(RandomAccessFile in, long fileOffset, long length) throws IOException {
        List<Long> list = new ArrayList<>();
        in.seek(fileOffset);
        long out = 0;
        int shift = 0;
        long readbyte = in.read();
        if (readbyte == -1) throw new EOFException();

        while (true) {
            while ((readbyte & 0x80) == 0) {
                // We read more bytes than required to load the max long
                if (shift >= 50) {
                    throw new IllegalArgumentException();
                }
//                System.out.println(readbyte);
                out |= (readbyte & 127) << shift;

                readbyte = in.read();
                if (readbyte == -1) {
                    throw new EOFException();
                }

                shift += 7;
            }
            out |= (readbyte & 127) << shift;
//            System.out.println(out);
            list.add(out);
            if (in.getFilePointer() - fileOffset >= length) {
                break;
            }
            while (in.getFilePointer() - fileOffset < length && (readbyte = in.read()) == -1) {
            }
            shift = 0;
            out = 0;
        }
        return list;
    }
}
