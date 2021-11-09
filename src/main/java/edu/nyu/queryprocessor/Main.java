package edu.nyu.queryprocessor;

import edu.nyu.queryprocessor.entity.Query;
import edu.nyu.queryprocessor.processor.DAATProcessor;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class Main {
    public static void main(String[] args) throws IOException {
        while (true) {
            System.out.println("Input Query:");
            BufferedReader bufferedReader= new BufferedReader(new InputStreamReader( System.in ) );
            String query = bufferedReader.readLine();
            System.out.println(new DAATProcessor().process(new Query(query)));
        }
    }
}
