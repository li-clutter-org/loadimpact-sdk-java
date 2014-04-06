package com.loadimpact.eval;

/**
 * Defines all operators that can be used in failure criteria thresholds.
 *
 * @author jens
 */
public enum Operator {
    greaterThan(">", "&gt;"), lessThan("<", "&lt;");

    public final String symbol;
    public final String label;

    Operator(String symbol, String label) {
        this.symbol = symbol;
        this.label = label;
    }

    public String getId() {
        return name();
    }
    
    public String getLabel() {
        return label;
    }

}
