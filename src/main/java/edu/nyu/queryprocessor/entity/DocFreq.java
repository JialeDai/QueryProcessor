package edu.nyu.queryprocessor.entity;

import lombok.Data;

@Data
public class DocFreq {
    private String term;
    private Long docFreq;
}
