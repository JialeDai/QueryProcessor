package edu.nyu.queryprocessor.processor;

import edu.nyu.queryprocessor.entity.Query;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

class DAATProcessorTest {
    @Test
    void processTest() throws IOException {
        System.out.println(new DAATProcessor().process(new Query("aa aaa aaaa")));
    }
}