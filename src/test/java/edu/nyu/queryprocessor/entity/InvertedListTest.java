package edu.nyu.queryprocessor.entity;

import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

class InvertedListTest {
    @Test
    void openListTest() throws IOException {
        InvertedList invertedList = new InvertedList().openList(new Term("aa"));
        System.out.println(invertedList);
    }

    @Test
    void nextGEQTest() throws IOException {
        InvertedList invertedList = new InvertedList().openList(new Term ("aa"));
        System.out.println(invertedList.nextGEQ(invertedList, 1));
    }
}