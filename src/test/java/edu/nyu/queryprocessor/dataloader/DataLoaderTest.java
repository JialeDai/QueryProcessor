package edu.nyu.queryprocessor.dataloader;

import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

class DataLoaderTest {
    @Test
    void loadLexicon() throws IOException {
        new DataLoader().loadLexicon();
    }

    @Test
    void loadPageTable() throws IOException {
        new DataLoader().loadPageTable();
    }

    @Test
    void loadIdfMapTest() throws IOException {
        new DataLoader().loadDocFreqMap();
    }
}