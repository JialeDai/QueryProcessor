package edu.nyu.queryprocessor.processor;

import edu.nyu.queryprocessor.entity.InvertedList;
import edu.nyu.queryprocessor.entity.Query;
import edu.nyu.queryprocessor.entity.Result;
import edu.nyu.queryprocessor.entity.Term;

public abstract class QueryProcessor {
    /**
     * take the input query and return the result docs.
     * @param query
     * @return
     */
    abstract Result process(Query query);
}
