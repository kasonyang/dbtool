package site.kason.dbtool.result;

import site.kason.dbtool.Table;

/**
 *
 * @author Kason Yang
 */
public class TableCompareResult {
    
    private FieldCompareResult[] fieldCompareResults;
    
    private IndexCompareResult[] indexCompareResults;
    
    private String message;
    
    private boolean passed;
    
    private Table table1;
    private Table table2;

    public TableCompareResult(boolean passed,String message,Table table1,Table table2,FieldCompareResult[] fieldCompareResults,IndexCompareResult[] indexCompareResults) {
        this.passed = passed;
        this.fieldCompareResults = fieldCompareResults;
        this.indexCompareResults = indexCompareResults;
        this.message = message;
        this.table1 = table1;
        this.table2 = table2;
    }

    public FieldCompareResult[] getFieldCompareResults() {
        return fieldCompareResults;
    }

    public IndexCompareResult[] getIndexCompareResults() {
        return indexCompareResults;
    }

    public boolean isPassed() {
        return passed;
    }

    public String getMessage() {
        return message;
    }

    public Table getTable1() {
        return table1;
    }

    public Table getTable2() {
        return table2;
    }

}
