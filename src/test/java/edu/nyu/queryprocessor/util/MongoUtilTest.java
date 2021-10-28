package edu.nyu.queryprocessor.util;

import com.mongodb.client.MongoCollection;
import org.bson.Document;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

class MongoUtilTest {
    @Test
    void insertOneTest() throws IOException {
        MongoCollection<Document> collection = new MongoUtil().getConnection("admin", "test");
        Document doc = new Document("name", "MongoDB")
                .append("type", "database")
                .append("count", 1)
                .append("info", new Document("x", 203).append("y", 102));
        System.out.println(new MongoUtil().insertOne(collection, doc));
    }

    @Test
    void findFirstTest() throws IOException {
        MongoCollection<Document> collection = new MongoUtil().getConnection("admin", "test");
        System.out.println(new MongoUtil().findFirst(collection));
    }
}