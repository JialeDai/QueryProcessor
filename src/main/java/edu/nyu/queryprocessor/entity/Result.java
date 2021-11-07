package edu.nyu.queryprocessor.entity;

import lombok.Data;

import java.util.*;

/**
 * @author jiale
 */
public class Result {
    public static Set<Term> missMatchSet = new HashSet<>();
    private static PriorityQueue<Document> rankList = new PriorityQueue<>(new Comparator<Document>() {
        @Override
        public int compare(Document o1, Document o2) {
            return o1.getScore().compareTo(o2.getScore());
        }
    });

    public static void addDoc(Document document) {
        rankList.offer(document);
    }

    public static List<Document> getTopN(int n) {
        List<Document> documents = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            documents.add(rankList.poll());
        }
        return documents;
    }

    public static void addMissMatch(Term term) {
        missMatchSet.add(term);
    }

}
