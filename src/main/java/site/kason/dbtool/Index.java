package site.kason.dbtool;
/**
 *
 * @author Kason Yang
 */
public class Index {
    
    private String name;
    
    private String[] columns;
    
    private boolean unique;

    public Index(String name, String[] columns,boolean isUnique) {
        this.name = name;
        this.columns = columns;
        this.unique = isUnique;
    }

    public String getName() {
        return name;
    }

    public String[] getColumns() {
        return columns;
    }

    public boolean isUnique() {
        return unique;
    }

}
