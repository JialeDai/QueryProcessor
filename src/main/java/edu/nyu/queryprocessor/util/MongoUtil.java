package edu.nyu.queryprocessor.util;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.MongoCredential;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import org.bson.BsonDocument;
import org.bson.Document;

import javax.print.Doc;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MongoUtil {
    private String connection_string = new ConfigUtil().getConfig("connection_string");
    private String username = new ConfigUtil().getConfig("username");
    private String pwd = new ConfigUtil().getConfig("pwd");
    private MongoClient mongoClient;
    private MongoCollection<Document> mongoCollection;

    public MongoUtil() throws IOException {
    }

    public MongoUtil(String databaseName, String collectionName) throws IOException {
        mongoClient = getClient(databaseName);
        mongoCollection = getConnection(mongoClient, collectionName);
    }

    public MongoClient getClient(String databaseName) {
        List<MongoCredential> credentials = new ArrayList<MongoCredential>();

        MongoClientURI connectionUrl = new MongoClientURI(connection_string + databaseName + "?maxIdleTimeMS=3600000&authSource="+databaseName);
        MongoClient mongoClient = new MongoClient(connectionUrl);
        return mongoClient;
    }

    public MongoCollection<Document> getConnection(MongoClient mongoClient, String collecionName) {
        return mongoClient.getDatabase(collecionName).getCollection(collecionName);
    }

    public boolean insertOne(MongoCollection<Document> collection, Document doc) {
        try {
            collection.insertOne(doc);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean insertMultiple(MongoCollection<Document> collection, List<Document> documents) {
        try {
            collection.insertMany(documents);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public Long getCount(MongoCollection<Document> collection) {
        return collection.count();
    }

    public String findFirst(MongoCollection<Document> collection) {
        return collection.find().first().toJson();
    }

    public String findSingleDocWithFilter(MongoCollection<Document> collection, BsonDocument filter) {
        return collection.find(filter).first().toJson();
    }

    public String findSingleDocWithFilter( Document filter) {
         String res = mongoCollection.find(filter).first().toJson();
         mongoClient.close();
        return res;
    }

    public MongoCursor<Document> find(MongoCollection<Document> collection, BsonDocument filter) {
        return collection.find(filter).iterator();
    }
}
