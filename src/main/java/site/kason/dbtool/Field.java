package site.kason.dbtool;
/**
 *
 * @author Kason Yang
 */
public class Field {
    
    private Table table;
    
    private String name,type,defaultValue,remarks;
    
    private boolean nullable;
    
    private int size;

    protected Field(Table table,String name, String type, int size, boolean nullable,String defaultValue,String remarks) {
        this.name = name;
        this.type = type;
        this.nullable = nullable;
        this.size = size;
        this.defaultValue = defaultValue;
        this.remarks = remarks;
        this.table = table;
    }

    public String getName() {
        return name;
    }

    public String getType() {
        return type;
    }

    public boolean isNullable() {
        return nullable;
    }

    public int getSize() {
        return size;
    }

    public String getRemarks() {
        return remarks;
    }

    @Override
    public String toString() {
        return name;
    }
    
    public String getDefaultValue() {
        return defaultValue;
    }

    public Table getTable() {
        return table;
    }

    public String toVerboseString() {
        return "Field{" + "table=" + table + ", name=" + name + ", type=" + type + ", defaultValue=" + defaultValue + ", remarks=" + remarks + ", nullable=" + nullable + ", size=" + size + '}';
    }
}
