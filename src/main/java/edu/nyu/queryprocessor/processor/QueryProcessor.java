package edu.nyu.queryprocessor.processor;

import edu.nyu.queryprocessor.entity.*;

import java.io.IOException;
import java.util.List;

public abstract class QueryProcessor {
    /**
     * take the input query and return the result docs.
     * @param query
     * @return
     */
    abstract List<Document> process(Query query) throws IOException;
}
