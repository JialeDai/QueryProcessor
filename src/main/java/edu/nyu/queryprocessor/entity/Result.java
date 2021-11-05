package edu.nyu.queryprocessor.entity;

import java.util.Comparator;
import java.util.PriorityQueue;

public class Result {
    private static PriorityQueue<Document> res = new PriorityQueue<>(new Comparator<Document>() {
        @Override
        public int compare(Document o1, Document o2) {
            return o1.getScore().compareTo(o2.getScore());
        }
    });

    public static void addDoc(Document document) {
        res.offer(document);
    }
}
