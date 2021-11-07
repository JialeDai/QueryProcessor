package edu.nyu.queryprocessor.entity;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Comparator;

/**
 * @author jiale
 */
@Data
public abstract class Score implements Comparable {
    private Double val;
}
