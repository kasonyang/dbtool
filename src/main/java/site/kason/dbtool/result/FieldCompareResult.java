package site.kason.dbtool.result;

import site.kason.dbtool.Field;

/**
 *
 * @author Kason Yang
 */
public class FieldCompareResult {
    
    private Field filed1;
    
    private Field field2;
    
    private String message;
    
    private boolean passed;

    public FieldCompareResult(boolean passed ,String message , Field filed1, Field field2) {
        this.filed1 = filed1;
        this.field2 = field2;
        this.message = message;
        this.passed = passed;
    }

    public Field getFiled1() {
        return filed1;
    }

    public Field getField2() {
        return field2;
    }

    public String getMessage() {
        return message;
    }

    public boolean isPassed() {
        return passed;
    }

}
