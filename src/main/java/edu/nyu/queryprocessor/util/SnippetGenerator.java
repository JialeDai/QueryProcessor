package edu.nyu.queryprocessor.util;

import edu.nyu.queryprocessor.entity.CandidateSnippet;
import edu.nyu.queryprocessor.entity.Term;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.PriorityQueue;

public class SnippetGenerator {
    public static List<String> generate(String doc, List<Term> terms) throws IOException {
        int snippetLength = Integer.parseInt(new ConfigUtil().getConfig("snippet_length"));
        List<CandidateSnippet> candidates = getStrList(doc, snippetLength, terms);
        return topN(candidates, 2);
    }

    public static List<String> topN(List<CandidateSnippet> strList, int n) {
        try{
            PriorityQueue<CandidateSnippet> maxHeap = new PriorityQueue<>((o1, o2) -> o2.getScore().compareTo(o1.getScore()));
            for (CandidateSnippet each: strList) {
                maxHeap.offer(each);
            }
            List<String> res = new ArrayList<>();
            for (int i = 0; i < n; i++) {
                res.add(maxHeap.poll().getContent());
            }
            return res;
        } catch (Exception e) {
            return null;
        }
    }

    private static List<CandidateSnippet> getStrList(String inputString, int length, List<Term> terms) {
        int size = inputString.length() / length;
        if (inputString.length() % length != 0) {
            size += 1;
        }
        return getStrList(inputString, length, size, terms);
    }

    private static List<CandidateSnippet> getStrList(String inputString, int length, int size, List<Term> terms) {
        List<CandidateSnippet> list = new ArrayList<>();
        for (int index = 0; index < size; index++) {
            String childStr = subString(inputString, index * length, (index + 1) * length);
            list.add(new CandidateSnippet(childStr, terms));
        }
        return list;
    }

    private static String subString(String str, int f, int t) {
        if (f > str.length()) {
            return null;
        }
        if (t > str.length()) {
            return str.substring(f, str.length());
        } else {
            return str.substring(f, t);
        }
    }
}
