package site.kason.dbtool.result;

import site.kason.dbtool.Index;
import site.kason.dbtool.Index;

/**
 *
 * @author Kason Yang
 */
public class IndexCompareResult {
    
    private Index index1;
    
    private Index index2;
    
    private String message;
    
    private boolean passed;

    public IndexCompareResult(boolean passed ,String message , Index index1, Index index2) {
        this.index1 = index1;
        this.index2 = index2;
        this.message = message;
        this.passed = passed;
    }

    public Index getIndex1() {
        return index1;
    }

    public Index getIndex2() {
        return index2;
    }

    public String getMessage() {
        return message;
    }

    public boolean isPassed() {
        return passed;
    }

}
