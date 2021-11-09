package edu.nyu.queryprocessor.dataloader;

import com.alibaba.fastjson.JSON;
import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import edu.nyu.queryprocessor.entity.Page;
import edu.nyu.queryprocessor.util.ConfigUtil;
import edu.nyu.queryprocessor.util.DocParserUtil;
import edu.nyu.queryprocessor.util.MongoUtil;
import org.bson.Document;
import org.bson.conversions.Bson;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.regex.Pattern;

import static org.apache.commons.lang.CharSetUtils.count;

public class DataLoader {
    @SuppressWarnings("deprecation")

    public void loadDocFreqMap() throws IOException {
        BufferedReader inputStream = null;
        Map<String, Integer> docFreqMap = new HashMap<>();
        MongoClient queryProcessorMongoClient = null;
        Integer bufferSize = Integer.parseInt(new ConfigUtil().getConfig("insert_buffer_size"));
        try {
            queryProcessorMongoClient = new MongoUtil().getClient("admin");
            MongoCollection<Document> docFreqCollection = new MongoUtil().getConnection(queryProcessorMongoClient, "docFreq");
            String asciiIndexUrl = new ConfigUtil().getConfig("ascii_index_url");
            inputStream = new BufferedReader(new InputStreamReader(new FileInputStream(new File(asciiIndexUrl))), 10 * 1024 * 1024);
            List<Document> buffer = new ArrayList<>();
            while (inputStream.ready()) {
                if (buffer.size() >= bufferSize) {
                    docFreqCollection.insertMany(buffer);
                    System.out.println("insert into mongodb...");
                    buffer.clear();
                }
                String line = inputStream.readLine();
                Document doc = convertDocFreq(line);
                if (doc != null) {
                    buffer.add(doc);
                }
            }
            if (!buffer.isEmpty()) {
                docFreqCollection.insertMany(buffer);
                buffer = null;
            }
        } finally {
            inputStream.close();
            queryProcessorMongoClient.close();
        }
    }

    private Document convertDocFreq(String line) {
        String[] terms = line.split(":");
        if (terms.length == 1) {
            return null;
        }
        String term = terms[0];
        Integer docFreq = terms[1].split(",").length;
        System.out.println(term +":"+docFreq);
        return new Document().append("term", term).append("docFreq", docFreq);
    }

    public void loadLexicon() throws IOException {
        Integer bufferSize = Integer.parseInt(new ConfigUtil().getConfig("insert_buffer_size"));
        MongoClient queryProcessorMongoClient = new MongoUtil().getClient("admin");
        MongoCollection<Document> addLexiconCollection = new MongoUtil().getConnection(queryProcessorMongoClient, "lexicon");
        String lexiconUrl = new ConfigUtil().getConfig("lexicon_url");
        BufferedReader in = new BufferedReader(new InputStreamReader(new FileInputStream(new File(lexiconUrl))), 10 * 1024 * 1024);
        List<Document> buffer = new ArrayList<>();

        while (in.ready()) {
            if (buffer.size() >= bufferSize) {
                addLexiconCollection.insertMany(buffer);
                System.out.println("add docs:" + buffer.size());
                buffer.clear();
            }
            String line = in.readLine();
            Document doc = convertLexicon(line);
            buffer.add(doc);
        }
        if (!buffer.isEmpty()) {
            addLexiconCollection.insertMany(buffer);
            buffer = null;
        }
        queryProcessorMongoClient.close();
    }

    private Document convertLexicon(String line) {
        String term = line.split(":")[0];
        String rest = line.split(":")[1];
        String fileOffset = rest.split("\\|")[0];
        String listLength = rest.split("\\|")[1];
        String metaDataLength = rest.split("\\|")[2];
        Document document = new Document()
                .append("term", term)
                .append("fileOffset", fileOffset)
                .append("listLength", listLength)
                .append("metadataLength", metaDataLength);
        return document;
    }

    public void loadPageTable() throws IOException {
        Integer bufferSize = Integer.parseInt(new ConfigUtil().getConfig("insert_buffer_size"));
        MongoClient queryProcessorMongoClient = new MongoUtil().getClient("admin");
        MongoCollection<Document> addLPageTableCollection = new MongoUtil().getConnection(queryProcessorMongoClient, "pageTable");
        try {
            Set<String> blackList = new HashSet<>();
            blackList.add("<DOC>");
            blackList.add("<TEXT>");
            blackList.add("</TEXT>");
            blackList.add("<DOCNO>");
            blackList.add("</DOCNO>");
            String url = null;
            StringTokenizer stringTokenizer = null;
            BufferedInputStream bis = new BufferedInputStream(new FileInputStream(new ConfigUtil().getConfig("input_url")));
            BufferedReader in = new BufferedReader(new InputStreamReader(bis, "utf-8"), 10 * 1024 * 1024);// 10M buffer
            StringBuilder rawDoc = new StringBuilder();
            List<Document> pageTableBuffer = new ArrayList<>();
            int docId = 0;
            while (in.ready()) {
                StringBuilder tmpStr = new StringBuilder();
                String line = in.readLine();
                if (blackList.contains(line) || DocParserUtil.check(line)) {
                    if (line.equals("<TEXT>")) {
                        url = in.readLine();
                    }
                    continue;
                }
                rawDoc.append(line);
                if (line.equals("</DOC>")) {
                    int pageSize = rawDoc.toString().getBytes(StandardCharsets.UTF_8).length;
                    System.out.println("parse document:" + docId);
                    rawDoc.setLength(rawDoc.length() - 6);
                    try {
                        if (pageTableBuffer.size() >= bufferSize) {
                            addLPageTableCollection.insertMany(pageTableBuffer);
                            System.out.println("inserting..." + docId);
                            pageTableBuffer.clear();
                        }
                        Document doc = new Document()
                                .append("docId", docId)
                                .append("url", url)
                                .append("pageSize", pageSize)
                                .append("doc", rawDoc.toString());
                        pageTableBuffer.add(doc);
                    } catch (Exception e) {
                        tmpStr.setLength(0);
                        continue;
                    }
                    docId++;
                    rawDoc.setLength(0);
                }
            }
            if (!pageTableBuffer.isEmpty()) {
                addLPageTableCollection.insertMany(pageTableBuffer);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            queryProcessorMongoClient.close();
        }
    }
}
