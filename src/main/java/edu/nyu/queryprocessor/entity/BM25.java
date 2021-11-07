package edu.nyu.queryprocessor.entity;

public class BM25 extends Score{
    private int k1;

    @Override
    public int compareTo(Object o) {
        return ((Score)o).getVal().compareTo(this.getVal());
    }
}
