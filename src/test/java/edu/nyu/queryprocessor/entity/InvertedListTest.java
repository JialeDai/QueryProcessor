package edu.nyu.queryprocessor.entity;

import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

class InvertedListTest {
    @Test
    void openList() throws IOException {
        new InvertedList().openList(new Term("aa"));
    }
}