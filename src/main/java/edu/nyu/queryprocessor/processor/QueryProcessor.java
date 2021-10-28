package edu.nyu.queryprocessor.processor;

import edu.nyu.queryprocessor.entity.InvertedList;
import edu.nyu.queryprocessor.entity.Term;

public abstract class QueryProcessor {
    /**
     * open the inverted list for term t for reading
     * @param term
     */
    abstract void openList(Term term);

    /**
     * close the inverted list for term t for reading
     * @param list
     */
    abstract void closeList(InvertedList list);

    /**
     *  find the next posting in list lp with docID >= k and
     * return its docID. Return value > MAXDID if none exists.
     * @param lp
     * @param k
     * @return
     */
    abstract Long nextGEQ(InvertedList lp, long k);

    /**
     * get the impact score of the current posting in list lp
     * @param lp
     * @return
     */
    abstract double getScore(InvertedList lp);

}
