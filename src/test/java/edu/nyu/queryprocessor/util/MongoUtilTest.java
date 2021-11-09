package edu.nyu.queryprocessor.util;

import com.alibaba.fastjson.JSON;
import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import edu.nyu.queryprocessor.entity.Lexicon;
import edu.nyu.queryprocessor.singleton.LexiconCollection;
import org.bson.Document;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

class MongoUtilTest {
//    @Test
//    void insertOneTest() throws IOException {
//        MongoClient mongoClient = null;
//        try {
//            mongoClient = new MongoUtil().getClient("admin");
//            MongoCollection<Document> collection = new MongoUtil().getConnection(mongoClient, "test");
//            Document doc = new Document("name", "mlgb")
//                    .append("type", "database")
//                    .append("count", 1)
//                    .append("info", new Document("x", 203).append("y", 102));
//            System.out.println(new MongoUtil().insertOne(collection, doc));
//        } catch (Exception e) {
//            e.printStackTrace();
//        }finally {
//            mongoClient.close();
//        }
//
//    }

    @Test
    void findFirstTest() throws IOException {
        LexiconCollection lexiconCollection = LexiconCollection.getInstance();
        System.out.println(lexiconCollection.findFirst());
    }

    @Test
    void findSingleDocWithFilterTest() throws IOException {
        Document filter = new Document().append("term", "dfasfa");
        String res = LexiconCollection.getInstance().findSingleDocWithFilter(filter);
        System.out.println(res);
        Lexicon lexicon = JSON.parseObject(res, Lexicon.class);
        System.out.println(lexicon);
    }
}