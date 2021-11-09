package edu.nyu.queryprocessor.singleton;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.MongoCredential;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import edu.nyu.queryprocessor.util.ConfigUtil;
import lombok.Data;
import lombok.SneakyThrows;
import org.bson.BsonDocument;
import org.bson.Document;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Data
public class PageTableCollection {
    private MongoClient mongoClient;
    private MongoCollection<Document> mongoCollection;
    public static PageTableCollection pageTableCollection = null;

    public static PageTableCollection getInstance() throws IOException {
        if (pageTableCollection == null) {
            pageTableCollection =new PageTableCollection();
        }
        return pageTableCollection;
    }

    private PageTableCollection() throws IOException {

        mongoClient = getClient("queryprocessor");
        mongoCollection = getConnection(mongoClient, "pageTable");
    }

    public MongoClient getClient(String databaseName) throws IOException {
        String connection_string = new ConfigUtil().getConfig("connection_string");
        List<MongoCredential> credentials = new ArrayList<MongoCredential>();

        MongoClientURI connectionUrl = new MongoClientURI(connection_string + databaseName + "?maxIdleTimeMS=10000&authSource=" + databaseName);
        MongoClient mongoClient = new MongoClient(connectionUrl);
        return mongoClient;
    }

    public MongoCollection<Document> getConnection(MongoClient mongoClient, String collectionName) {
        return mongoClient.getDatabase(collectionName).getCollection(collectionName);
    }

    public String findFirst() {
        return mongoCollection.find().first().toJson();
    }


    public String findSingleDocWithFilter(Document filter) {
        String res = mongoCollection.find(filter).first().toJson();
        return res;
    }

    public void closeCollection() {
        mongoClient.close();
    }
}
