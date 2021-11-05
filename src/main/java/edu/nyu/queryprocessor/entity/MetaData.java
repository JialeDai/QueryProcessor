package edu.nyu.queryprocessor.entity;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;

/**
 * @author jiale
 */
@Data
@AllArgsConstructor
public class MetaData {
    private byte[] bytes;
    private long[] values;

    public MetaData(long[] values) {
        this.values = values;
    }

    public byte[] getMetaDataFromIndexFile(File indexFile, long fileOffset, long metaDataLength) throws IOException {
        RandomAccessFile randomAccessFile = new RandomAccessFile(indexFile,"r");
        randomAccessFile.seek(fileOffset);
        bytes = new byte[(int) metaDataLength];
        int fileNameReadLength = 0;
        int hasReadLength = 0;
        while((fileNameReadLength=randomAccessFile.read(bytes,hasReadLength,(int) metaDataLength-hasReadLength))>0) {
            hasReadLength = hasReadLength + fileNameReadLength;
        }
        randomAccessFile.close();
        return bytes;
    }
}
