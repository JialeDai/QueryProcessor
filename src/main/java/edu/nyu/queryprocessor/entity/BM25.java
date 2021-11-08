package edu.nyu.queryprocessor.entity;

public class BM25 extends Score {
    private double k = 1.2;
    private double b;

    public BM25() {
    }

    public BM25(double k, double b) {
        super();
        this.k = k;
        this.b = b;
    }

    public BM25(double b) {
        super();
        this.b = b;
    }

    public double cal(double idf, double tf, double L) {
        double v = 0;
        v = (idf * (k + 1) * tf) / (k * (1.0 - b + b * L) + tf);
        return v;
    }



    @Override
    public int compareTo(Object o) {
        return ((Score) o).getVal().compareTo(this.getVal());
    }
}
