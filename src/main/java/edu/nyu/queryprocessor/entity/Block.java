package edu.nyu.queryprocessor.entity;

import lombok.Data;

@Data
public class Block {
    private int length;
    private byte[] bytes;
    private int[] content;
}
