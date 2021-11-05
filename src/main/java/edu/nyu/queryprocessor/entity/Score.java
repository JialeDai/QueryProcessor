package edu.nyu.queryprocessor.entity;

import lombok.Data;

import java.util.Comparator;

@Data
public abstract class Score implements Comparable {
    private Double val;
}
