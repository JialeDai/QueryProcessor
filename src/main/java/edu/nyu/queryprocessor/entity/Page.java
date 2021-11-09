package edu.nyu.queryprocessor.entity;

import lombok.Data;

@Data
public class Page {
    private long docId;
    private String url;
    private long pageSize;
    private String doc;
}
