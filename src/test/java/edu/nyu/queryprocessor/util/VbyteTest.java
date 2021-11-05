package edu.nyu.queryprocessor.util;

import org.apache.commons.lang.ArrayUtils;
import org.junit.jupiter.api.Test;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class VbyteTest {
    @Test
    void test () throws IOException {
        List<Byte> byteList = new ArrayList<>();
        File file = new File("/Users/jialedai/Documents/JavaWorkSpace/IndexBuilder/textVbyte.txt");
        DocParserUtil.read(file);
        DataInputStream dataInputStream = new DataInputStream(new FileInputStream(file));
        while (dataInputStream.available() > 0) {
            long b = dataInputStream.read();
            System.out.println(b);
//            byteList.add(b);
        }
        byte[] bytes = ArrayUtils.toPrimitive(byteList.toArray(new Byte[byteList.size()]));
        List<Long> res = Vbyte.decode(bytes);
        System.out.println(res);
    }

    @Test
    void readTest() throws IOException {
        File file = new File("/Users/jialedai/Documents/JavaWorkSpace/IndexBuilder/textVbyte.txt");
        DocParserUtil.read(file);
    }

    @Test
    void decodeRandomFileReaderTest() throws IOException {
        RandomAccessFile randomAccessFile = new RandomAccessFile("/Users/jialedai/Documents/JavaWorkSpace/IndexBuilder/textVbyte.txt", "r");
        long offset = 0;
        long length = randomAccessFile.length();
        List<Long> res = Vbyte.decode(randomAccessFile, offset, length);
        System.out.println(res);
    }
}