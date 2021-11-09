package edu.nyu.queryprocessor.entity;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * @author jiale
 */
@Data
@AllArgsConstructor
public class Document {
    private long did;
    private Score score;
    private Page page;
}
