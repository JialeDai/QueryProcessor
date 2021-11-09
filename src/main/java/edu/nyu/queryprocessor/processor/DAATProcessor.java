package edu.nyu.queryprocessor.processor;

import com.alibaba.fastjson.JSON;
import com.mongodb.client.MongoCollection;
import edu.nyu.queryprocessor.entity.*;
import edu.nyu.queryprocessor.singleton.DocFreqCollection;
import edu.nyu.queryprocessor.singleton.LexiconCollection;
import edu.nyu.queryprocessor.singleton.PageTableCollection;
import edu.nyu.queryprocessor.util.SnippetGenerator;

import javax.print.Doc;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * @author jiale
 */
public class DAATProcessor extends QueryProcessor {

    @Override
    public List<Document> process(Query query) throws IOException {
        boolean findIntersection = false;
        List<InvertedList> lp = new ArrayList<>();
        List<Term> termList = query.getTermList();
        List<Long> freqList = new ArrayList<>();
        try {
            for (int i = 0; i < termList.size(); i++) {
                InvertedList openList = new InvertedList().openList(termList.get(i));
                if (openList != null) {
                    lp.add(openList);
                }
            }
            long did = 0;
            while (did <= 3213835) { //TODO config
                // get next post from the shortest list
                did = lp.get(0).nextGEQ(lp.get(0), did);
                // see if you find entries with same docID in other lists
                long d = Long.MIN_VALUE;
                int i = 1;
                while (i < lp.size()) {
                    d = lp.get(i).nextGEQ(lp.get(i), did);
                    if (d == did) {
                        i++;
                    } else {
                        break;
                    }
                }
                // not in intersection
                if (d > did) {
                    did = d;
                } else {
                    // docId is in intersection; now get all frequencies
                    for (i = 0; i < lp.size(); i++) {
                        freqList.add(lp.get(i).getFreq(lp.get(i), did));
                    }
                    // TODO compute BM25 score from frequencies and other data
                    BM25 score = new BM25(1.2, 0.75);
                    if (did > 3213835) {
                        break;
                    }
                    System.out.println("Calculating BM25 Score...");
                    score.setVal(score.cal(termList, did));
                    System.out.println("Score: " + score);
//                    BM25 score = new BM25();
//                    score.setVal(new Random().nextDouble());
                    Document interaction = new Document(did, score, null, null);
                    Result.addDoc(interaction);
                    findIntersection = true;
                    System.out.println("find intersection: " + interaction);
                    // and increase did to search for next post
                    did++;
                }
            }
            if (!findIntersection) {
                System.out.println("not finding intersection");
            }
            System.out.println("miss match term:" + Result.missMatchSet);
            PageTableCollection pageTableCollection = PageTableCollection.getInstance();
            List<Document> res = Result.getTopN(2);
            for (Document document : res) {
                org.bson.Document filter = new org.bson.Document().append("docId", document.getDid());
                System.out.println(filter);
                MongoCollection<org.bson.Document> pateTableCollection = PageTableCollection.getInstance().getMongoCollection();
                Page page = JSON.parseObject(pateTableCollection.find(filter).first().toJson(), Page.class);
                System.out.println(page);
                document.setSnippet(new SnippetGenerator().generate(page.getDoc(), termList).toString());
            }
            return res;
        } catch (Exception e) {
            LexiconCollection.getInstance().closeCollection();
            PageTableCollection.getInstance().closeCollection();
            DocFreqCollection.getInstance().closeCollection();
//            e.printStackTrace();
            return null;
        } finally {
            for (int i = 0; i < lp.size(); i++) {
                lp.get(i).closeList();
            }
        }
    }
}
