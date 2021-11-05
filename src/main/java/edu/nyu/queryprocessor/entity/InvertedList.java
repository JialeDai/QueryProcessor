package edu.nyu.queryprocessor.entity;

import com.alibaba.fastjson.JSON;
import com.mongodb.MongoClient;
import com.mongodb.client.MongoDatabase;
import edu.nyu.queryprocessor.util.ConfigUtil;
import edu.nyu.queryprocessor.util.MongoUtil;
import lombok.Data;
import org.bson.BsonDocument;
import org.bson.Document;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

/**
 * @author jiale
 */
@Data
public class InvertedList {
    private MetaData metaData;
    private List<Block> blocks;
    private String indexFileUrl;

    /**
     * open the inverted list for term t for reading
     * @param term
     */
    public InvertedList openList(Term term) throws IOException {
        indexFileUrl  = new ConfigUtil().getConfig("index_file_url");
        File indexFile = new File(indexFileUrl);
        Document filter = new Document().append("term",term.getContent());
        Lexicon lexicon = JSON.parseObject(new MongoUtil("admin","lexicon").findSingleDocWithFilter(filter),Lexicon.class);
        System.out.println(lexicon);
        Long fileOffset = lexicon.getFileOffset();
        Long metadataLength = lexicon.getMetadataLength();
        Long listLength = lexicon.getListLength();
        byte[] test = new MetaData().getMetaDataFromIndexFile(indexFile,fileOffset,metadataLength);
        for (int i = 0; i < test.length; i++) {
            System.out.println(test[i]);
        }
        return null;
    }

    /**
     * close the inverted list for reading
     * @param lp
     */
    public void closeList(InvertedList lp) {

    }

    /**
     * get the impact score of the current posting in list lp
     * @return
     */
    public Score getScore() {
        return new BM25();
    }

    /**
     * find the next posting in list lp with docID >= k and return its docID. Return null if none exists
     * @param lp
     * @param docID
     * @return
     */
    public Integer nextGEQ(InvertedList lp, Integer docID) {
        return null;
    }

    /**
     * get all frequencies
     * @param lp
     * @param did
     * @return
     */
    public List<Integer> getFreq(InvertedList lp, int did) {
        return null;
    }
}
