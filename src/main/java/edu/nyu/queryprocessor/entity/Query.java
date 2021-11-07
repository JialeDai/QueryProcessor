package edu.nyu.queryprocessor.entity;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

/**
 * @author jiale
 */
@Data
public class Query {
    private String queryStr;
    private List<Term> termList;

    public Query(String queryStr) {
        termList = new ArrayList<>();
        StringTokenizer stringTokenizer = new StringTokenizer(queryStr);
        while (stringTokenizer.hasMoreTokens()) {
            this.termList.add(new Term(stringTokenizer.nextToken()));
        }
    }
}
