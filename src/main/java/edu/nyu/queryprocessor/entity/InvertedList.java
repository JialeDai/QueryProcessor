package edu.nyu.queryprocessor.entity;

import lombok.Data;

import java.util.List;

@Data
public class InvertedList {
    private MetaData metaData;
    private List<Block> blocks;
}
