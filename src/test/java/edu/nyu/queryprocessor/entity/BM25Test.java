package edu.nyu.queryprocessor.entity;

import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

class BM25Test {
    @Test
    void calAvgLength() throws IOException {
        System.out.println(new BM25().calAvgLength());
    }

    @Test
    void calTest() throws IOException {
        BM25 score = new BM25(1.2, 0.75);
        System.out.println(score.cal("aaa", 37l));
    }
}