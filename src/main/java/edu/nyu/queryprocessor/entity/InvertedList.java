package edu.nyu.queryprocessor.entity;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Lists;
import edu.nyu.queryprocessor.singleton.LexiconCollection;
import edu.nyu.queryprocessor.util.ConfigUtil;
import edu.nyu.queryprocessor.util.Vbyte;
import lombok.Data;
import org.apache.commons.lang.ArrayUtils;
import org.bson.Document;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.List;

/**
 * @author jiale
 */
@Data
public class InvertedList {
    private Term term;
    private MetaData metaData;
    private List<Block> blocks;
    private String indexFileUrl;
    private RandomAccessFile randomAccessFile;
    private long fileOffset;
    private long metadataLength;
    private long listLength;
    private long freq;
    private long blockOffset;

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
        try {
            this.term = term;
            Document filter = new Document().append("term", term.getContent());
            Lexicon lexicon = JSON.parseObject(LexiconCollection.getInstance().findSingleDocWithFilter(filter), Lexicon.class);
            System.out.println(lexicon);
            fileOffset = lexicon.getFileOffset();
            metadataLength = lexicon.getMetadataLength();
            listLength = lexicon.getListLength();
            List<Long> metaData = Vbyte.decode(randomAccessFile, fileOffset, metadataLength);
            blockOffset = fileOffset + metadataLength;
            System.out.println(metaData);
            this.metaData = new MetaData(ArrayUtils.toPrimitive(metaData.toArray(new Long[metaData.size()])));
            return this;
        } catch (Exception e) {
            Result.addMissMatch(term);
            return null;
        }
    }

    /**
     * close the inverted list for reading
     */
    public void closeList() throws IOException {
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
    public long nextGEQ(InvertedList lp, Long docID) throws IOException {
        blockOffset = fileOffset + metadataLength;
        List<Long> decompressedBlock = null;
        MetaData metaData = lp.getMetaData();
        long[] metaDataValue = metaData.getValues();
        long curLastDid = -1;
//        long blockOffset = fileOffset;
        int i = 0;
        long initialDid = 0;
        while (i < metaDataValue.length) {
            curLastDid = metaDataValue[i];
            if (i >= 2) {
                initialDid = metaDataValue[i - 2];
            }
            if (docID <= curLastDid) {
                decompressedBlock = decompressBlock(randomAccessFile, blockOffset, metaDataValue[i + 1], initialDid);
                System.out.println("decompressedBlock:"+" "+ decompressedBlock.size() + " "+lp.getTerm()+ " " + blockOffset + " " + metaDataValue[i + 1] + decompressedBlock); // todo 查blockOffset
                break;
            }
            blockOffset += metaDataValue[i + 1];
            i += 2;
        }
        // did is larger than any did in the inverted
        if (decompressedBlock == null) {
            return 3213835+1; // todo config
        }
        List<List<Long>> partitions = Lists.partition(decompressedBlock,decompressedBlock.size()/2);
        List<Long> docIdBlock = partitions.get(0);
        List<Long> freqBlock = partitions.get(1);
        long nextGEQDId = binarySearch(docIdBlock, docID);
        System.out.println("search in block: key-" + docID+" return- "+ nextGEQDId + " block-" + docIdBlock);
        System.out.println("docIdBlock: "+docIdBlock.size()+docIdBlock);
        for (Long did: docIdBlock) {
            BM25 score = new BM25(1.2, 0.75);
            System.out.println("Calculating BM25 Score...");
            List<Term> termList = new ArrayList<>();
            termList.add(term);
            score.setVal(score.cal(termList, did));
            edu.nyu.queryprocessor.entity.Document document = new edu.nyu.queryprocessor.entity.Document(did,score,null, null);
            Result.addDoc(document);
        }
        System.out.println("freqBlock: "+freqBlock.size()+freqBlock);
        long idx = docIdBlock.indexOf(nextGEQDId);
        System.out.println(nextGEQDId+" "+idx);
        freq = freqBlock.get((int) idx);
        return nextGEQDId;
    }

    /**
     * get frequency based on the given document id
     *
     * @param lp
     * @param did
     * @return
     */
    public Long getFreq(InvertedList lp, long did) {
        return freq;
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
//        int left = 0;
//        int right = block.size() - 1;
//        int mid = -1;
//        while (left + 1 < right) {
//            mid = left + (right - left) / 2;
//            if (block.get(mid) == did) {
//                return block.get(mid);
//            } else if (block.get(mid) < did) {
//                right = mid;
//            } else if (block.get(mid) > did) {
//                left = mid;
//            }
//        }
//        if (block.get(left) >= did) {
//            return block.get(left);
//        }
//        if (block.get(right) >= did) {
//            return block.get(right);
//        }
//        return 3213835+1;
        for (int i = 0; i < block.size(); i++) {
            if (block.get(i) >= did) {
                return block.get(i);
            }
        }
        return 3213835+1;
    }

    private List<Long> decompressBlock(RandomAccessFile lp, long blockOffset, long blockLength, long initialDid) throws IOException {
        List<Long> block = Vbyte.decode(randomAccessFile, blockOffset, blockLength);

        for (int i = 0; i < block.size() / 2; i++) {
            if (i == 0) {
                block.set(i, block.get(i) + initialDid);
            }
            if (i != 0) {
                block.set(i, block.get(i - 1) + block.get(i));
            }
        }
        return block;
    }
}
