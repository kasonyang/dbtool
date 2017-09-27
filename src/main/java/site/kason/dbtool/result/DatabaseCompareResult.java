package site.kason.dbtool.result;

import site.kason.dbtool.Database;

/**
 *
 * @author Kason Yang
 */
public class DatabaseCompareResult {
    
    private TableCompareResult[] tableCompareResults;
    
    private String message;
    
    private boolean passed;
    
    private Database database1;
    
    private Database database2;

    public DatabaseCompareResult(boolean passed,String message,Database db1,Database db2,TableCompareResult[] tableComareResults) {
        this.passed = passed;
        this.message = message;
        this.tableCompareResults = tableComareResults;
        this.database1 = db1;
        this.database2 = db2;
    }

    public String getMessage() {
        return message;
    }

    public TableCompareResult[] getTableCompareResults() {
        return tableCompareResults;
    }

    public boolean isPassed() {
        return passed;
    }

    public Database getDatabase1() {
        return database1;
    }

    public Database getDatabase2() {
        return database2;
    }

}
