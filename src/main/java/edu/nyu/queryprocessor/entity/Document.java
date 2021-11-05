package edu.nyu.queryprocessor.entity;

import lombok.Data;

@Data
public class Document {
    private int did;
    private Score score;
}
