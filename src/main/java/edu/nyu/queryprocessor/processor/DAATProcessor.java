package edu.nyu.queryprocessor.processor;

import edu.nyu.queryprocessor.entity.*;
import edu.nyu.queryprocessor.util.MongoUtil;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * @author jiale
 */
public class DAATProcessor extends QueryProcessor{

    @Override
    public  List<Document> process(Query query) throws IOException {
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
                did = lp.get(0).nextGEQ(lp.get(0),  did);
                // see if you find entries with same docID in other lists
                long d = Long.MIN_VALUE;
                int i = 1;
                while (i < lp.size()) {
                    d = lp.get(i).nextGEQ(lp.get(i), did); // TODO 读到底怎么处理
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
                    score.setVal(score.cal(termList, did));
                    Document interaction = new Document(did, score, null);
                    Result.addDoc(interaction);
                    System.out.println("find intersection: "+ interaction);
                    // and increase did to search for next post
                    did++;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            for (int i = 0; i < lp.size(); i++) {
                lp.get(i).closeList();
            }
            System.out.println("miss match term:" + Result.missMatchSet);
            return Result.getTopN(5);
        }
    }
}
