package edu.nyu.queryprocessor.entity;

import lombok.Data;

import java.util.List;
import java.util.Locale;

@Data
public class CandidateSnippet {
    private String content;
    private Integer score;

    public CandidateSnippet(String content, List<Term> termList) {
        this.content = content;
        String[] termsInSnippet = content.toLowerCase(Locale.ROOT).split(" ");
        int score = 0;
        for (Term each : termList) {
            for (int i = 0; i < termsInSnippet.length; i++) {
                if (termsInSnippet[i].equals(each.getContent())) {
                    score ++;
                }
            }
        }
        this.score = score;
    }
}
