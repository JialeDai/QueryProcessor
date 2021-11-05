package edu.nyu.queryprocessor.entity;

import com.alibaba.fastjson.JSON;
import edu.nyu.queryprocessor.util.ConfigUtil;
import edu.nyu.queryprocessor.util.MongoUtil;
import edu.nyu.queryprocessor.util.Vbyte;
import lombok.Data;
import org.apache.commons.lang.ArrayUtils;
import org.bson.Document;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.List;

/**
 * @author jiale
 */
@Data
public class InvertedList {
    private MetaData metaData;
    private List<Block> blocks;
    private String indexFileUrl;
    private RandomAccessFile randomAccessFile;
    private long fileOffset;
    private long metadataLength;
    private long listLength;

    public InvertedList() throws IOException {
        indexFileUrl = new ConfigUtil().getConfig("index_file_url");
        File indexFile = new File(indexFileUrl);
        randomAccessFile = new RandomAccessFile(indexFile, "r");
    }

    /**
     * open the inverted list for term t for reading
     *
     * @param term
     */
    public InvertedList openList(Term term) throws IOException {
        Document filter = new Document().append("term", term.getContent());
        Lexicon lexicon = JSON.parseObject(new MongoUtil("admin", "lexicon").findSingleDocWithFilter(filter), Lexicon.class);
//        System.out.println(lexicon);
        fileOffset = lexicon.getFileOffset();
        metadataLength = lexicon.getMetadataLength();
        listLength = lexicon.getListLength();
        List<Long> metaData = Vbyte.decode(randomAccessFile, fileOffset, metadataLength);
//        System.out.println(metaData);
        this.metaData = new MetaData(ArrayUtils.toPrimitive(metaData.toArray(new Long[metaData.size()])));
        return this;
    }

    /**
     * close the inverted list for reading
     *
     * @param lp
     */
    public void closeList(InvertedList lp) throws IOException {
        this.randomAccessFile.close();
    }

    /**
     * get the impact score of the current posting in list lp
     *
     * @return
     */
    public Score getScore() {
        return new BM25();
    }

    /**
     * find the next posting in list lp with docID >= k and return its docID. Return null if none exists
     *
     * @param lp
     * @param docID
     * @return
     */
    public long nextGEQ(InvertedList lp, Integer docID) throws IOException {
        List<Long> decompressedBlock = null;
        MetaData metaData = lp.getMetaData();
        long[] metaDataValue = metaData.getValues();
        long curLastDid = -1;
        long blockOffset = fileOffset;
        int i = 0;
        while (i < metaDataValue.length) {
            curLastDid = metaDataValue[i];
            if (docID <= curLastDid) {
                decompressedBlock = Vbyte.decode(randomAccessFile, blockOffset, metaDataValue[i + 1]);
            }
            blockOffset += metaDataValue[i + 1];
            i += 2;
        }
        List<Long> docIdBlock = decompressedBlock.subList(0, decompressedBlock.size()/2);
        List<Long> freqBlock = decompressedBlock.subList(decompressedBlock.size()/2, decompressedBlock.size());
        return binarySearch(docIdBlock, docID);
    }

    /**
     * get all frequencies
     *
     * @param lp
     * @param did
     * @return
     */
    public List<Integer> getFreq(InvertedList lp, int did) {
        return null;
    }

    /**
     * for a sorted block, return the first element greater or equal to the key
     * 1,2,3,4,5,6,7,8,9,10
     *
     * @param block
     * @param did
     * @return
     */
    private long binarySearch(List<Long> block, long did) {
        int left = 0;
        int right = block.size() - 1;
        int mid = -1;
        while (left+1 < right) {
            mid = left + (right - left) / 2;
            if  (block.get(mid) == did) {
                return block.get(mid);
            } else if (block.get(mid) < did) {
                right = mid;
            } else if (block.get(mid) > did){
                left = mid;
            }
        }
        if (block.get(left) >= did) {
            return block.get(left);
        }
        if (block.get(right) >= did) {
            return block.get(right);
        }
        return -1;
    }
}
