package site.kason.dbtool;

import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

/**
 *
 * @author Kason Yang
 */
public class Database {
    
    private String name;
    
    private final List<Table> tables = new LinkedList();

    public Database(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
    
    public Table createTable(String name,String remarks){
        Table tb = new Table(this, name, remarks);
        this.tables.add(tb);
        return tb;
    }

    public Table[] getTables() {
        return tables.toArray(new Table[tables.size()]);
    }

    @Override
    public String toString() {
        return name; 
    }

}
