package edu.nyu.queryprocessor.entity;

import com.alibaba.fastjson.JSON;
import com.mongodb.MongoClient;
import com.mongodb.client.MongoDatabase;
import edu.nyu.queryprocessor.util.MongoUtil;
import lombok.Data;
import org.bson.BsonDocument;
import org.bson.Document;

import java.io.IOException;
import java.util.List;

@Data
public class InvertedList {
    private MetaData metaData;
    private List<Block> blocks;

    /**
     * open the inverted list for term t for reading
     * @param term
     */
    public InvertedList openList(Term term) throws IOException {
        Document fliter = new Document().append("term",term.getContent());
        Lexicon lexicon = JSON.parseObject(new MongoUtil("admin","lexicon").findSingleDocWithFilter(fliter),Lexicon.class);
        System.out.println(lexicon);
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
