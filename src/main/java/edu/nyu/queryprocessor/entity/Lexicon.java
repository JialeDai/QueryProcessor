package edu.nyu.queryprocessor.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Lexicon implements Serializable {
    private String term;
    private long fileOffset;
    private long listLength;
    private long metadataLength;
}
