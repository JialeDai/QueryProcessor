package edu.nyu.queryprocessor.dataloader;

import edu.nyu.queryprocessor.dataloader.DataLoader;
import org.junit.jupiter.api.Test;

import java.io.IOException;

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