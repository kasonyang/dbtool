package site.kason.dbtool;

import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

/**
 *
 * @author Kason Yang
 */
public class Table {
    
    private String name,remarks;
    
    private final List<Field> fields = new LinkedList();
    
    private final List<Index> indexes = new LinkedList();
    
    private Database database;

    protected Table(Database database,String name , String remarks) {
        this.name = name;
        this.remarks = remarks;
        this.database = database;
    }

    public String getName() {
        return name;
    }

    public String getRemarks() {
        return remarks;
    }

    public Field[] getFields() {
        return fields.toArray(new Field[fields.size()]);
    }
    
    public Index[] getIndexes(){
        return indexes.toArray(new Index[indexes.size()]);
    }

    @Override
    public String toString() {
        return name;
    }

    public Database getDatabase() {
        return database;
    }
    
    public Field createField(String name,String type,int size,boolean nullable,String defaultValue,String remarks){
        Field f = new Field(this, name, type, size, nullable, defaultValue, remarks);
        this.fields.add(f);
        return f;
    }
    
    public Index createIndex(String name,String[] columns,boolean isUnique){
        Index idx = new Index(name, columns,isUnique);
        this.indexes.add(idx);
        return idx;
    }

}
