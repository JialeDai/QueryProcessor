package edu.nyu.queryprocessor.entity;

import com.alibaba.fastjson.JSON;

import com.mongodb.BasicDBObject;
import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Accumulators;
import com.mongodb.client.model.Aggregates;
import com.mongodb.client.model.Filters;

import com.sun.tools.javac.util.StringUtils;
import edu.nyu.queryprocessor.cache.Cache;
import edu.nyu.queryprocessor.singleton.DocFreqCollection;
import edu.nyu.queryprocessor.singleton.PageTableCollection;
import edu.nyu.queryprocessor.util.ConfigUtil;
import edu.nyu.queryprocessor.util.DocParserUtil;
import org.bson.BSON;
import org.bson.Document;
import org.bson.conversions.Bson;

import javax.print.Doc;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.regex.Pattern;

import static org.apache.commons.lang.CharSetUtils.count;

public class BM25 extends Score {
    private double k = 1.2;
    private double b;

    public BM25() {
    }

    public BM25(double k, double b) {
        super();
        this.k = k;
        this.b = b;
//        super.setVal(cal());
    }

    public BM25(double b) {
        super();
        this.b = b;
    }

    public double cal(List<Term> termList, Long did) throws IOException {
        double sum = 0;
        double idf = 0;
        try {
            for (Term each : termList) {
                String keyword = each.getContent();
                if (Cache.cache.containsKey(keyword)) {
                    idf = Cache.cache.get(keyword);
                } else {
                    Document docFreqFilter = new Document().append("term", keyword);
                    DocFreq docFreq = JSON.parseObject(DocFreqCollection.getInstance().getMongoCollection().find(docFreqFilter).first().toJson(), DocFreq.class);
                    idf = Math.log(3213835 / docFreq.getDocFreq());
                    Cache.cache.put(keyword, idf);
                }
                Document filter = new Document().append("docId", did);
                MongoCollection<Document> pateTableCollection = PageTableCollection.getInstance().getMongoCollection();
                Page page = JSON.parseObject(pateTableCollection.find(filter).first().toJson(), Page.class);
                Double tf = (double) count(page.getDoc(), keyword) * keyword.length() / page.getDoc().length();
                double avgLength = Double.parseDouble(new ConfigUtil().getConfig("avg_length"));
                Double L = page.getDoc().length() / avgLength;
                sum += cal(idf, tf, L);
                return sum;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return new Random().nextDouble();
        }
        return new Random().nextDouble();
    }

    /**
     * @param idf
     * @param tf
     * @param L
     * @return
     */
    public double cal(double idf, double tf, double L) {
        double v = 0;
        v = (idf * (k + 1) * tf) / (k * (1.0 - b + b * L) + tf);
        return v;
    }

    public double cal(String keyword, Long did) throws IOException {
        if (did == null) {
            return 0.0d;
        }
        try {
            Document filter = new Document().append("docId", did);
            MongoCollection<Document> pateTableCollection = PageTableCollection.getInstance().getMongoCollection();
            Page page = JSON.parseObject(pateTableCollection.find(filter).first().toJson(), Page.class);
//            System.out.println(page);
            Double tf = (double) count(page.getDoc(), keyword) * keyword.length() / page.getDoc().length();
            System.out.println("tf: " + tf);
            Document docFreqFilter = new Document().append("term", keyword);
            DocFreq docFreq = JSON.parseObject(DocFreqCollection.getInstance().getMongoCollection().find(docFreqFilter).first().toJson(), DocFreq.class);
            double val = Math.log(3213835 / docFreq.getDocFreq());
            Double idf = val;
            System.out.println("idf: " + idf);
            double avgLength = Double.parseDouble(new ConfigUtil().getConfig("avg_length"));
            Double L = page.getDoc().length() / avgLength;
            return cal(idf, tf, L);
        } catch (Exception e) {
            e.printStackTrace();
            return 0.0d;
        }
    }

    public double calAvgLength() throws IOException {
        BufferedInputStream bis = null;
        try {
            Integer bufferSize = Integer.parseInt(new ConfigUtil().getConfig("insert_buffer_size"));
            Set<String> blackList = new HashSet<>();
            blackList.add("<DOC>");
            blackList.add("<TEXT>");
            blackList.add("</TEXT>");
            blackList.add("<DOCNO>");
            blackList.add("</DOCNO>");
            String url = null;
            StringTokenizer stringTokenizer = null;
            bis = new BufferedInputStream(new FileInputStream(new ConfigUtil().getConfig("input_url")));
            BufferedReader in = new BufferedReader(new InputStreamReader(bis, "utf-8"), 10 * 1024 * 1024);// 10M buffer
            StringBuilder rawDoc = new StringBuilder();
            Long sum = 0l;
            int docId = 0;
            while (in.ready()) {
                StringBuilder tmpStr = new StringBuilder();
                String line = in.readLine();
                if (blackList.contains(line) || DocParserUtil.check(line)) {
                    continue;
                }
                rawDoc.append(line);
                if (line.equals("</DOC>")) {
                    rawDoc.setLength(rawDoc.length() - 6);
                    int pageSize = rawDoc.toString().length();
                    try {
                        sum += pageSize;
                        System.out.println("parse document: " + docId + " sum: " + sum + " pageSize: " + pageSize);
                    } catch (Exception e) {
                        tmpStr.setLength(0);
                        continue;
                    }
                    docId++;
                    rawDoc.setLength(0);
                }
            }
            return sum / docId;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return 0;
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return 0;
        } catch (IOException e) {
            e.printStackTrace();
            return 0;
        } finally {
            bis.close();
        }
    }

//    /**
//     * @param keywords
//     * @param doc
//     * @param docs
//     * @param idfMap
//     * @return
//     */
//    public double cal(String keywords, String doc, List<String> docs, Map<String, Double> idfMap) {
//        Double idf = idfMap.get(keywords);
//        if (null == idf) {
//            idf = 1.0d;
//        }
//        Double tf = (double) count(doc, keywords) * keywords.length() / doc.length();
//        double avgLength = calAvgLength(docs);
//        Double L = (double) doc.length() / avgLength;
//        return cal(idf, tf, L);
//    }

    private double calAvgLength(List<String> docs) {
        if (null == docs || docs.size() <= 0) {
            throw new RuntimeException("docs is empty");
        }
        int s = 0;
        for (String d : docs) {
            s += d.length();
        }
        return (double) s / (double) docs.size();
    }

    @Override
    public int compareTo(Object o) {
        return ((Score) o).getVal().compareTo(this.getVal());
    }
}
