package site.kason.dbtool;

import java.util.Arrays;

/**
 *
 * @author Kason Yang
 */
public class DatabaseFormatter {
    
    public static String format(Database db){
        StringBuilder sb = new StringBuilder();
        Table[] tables = db.getTables();
        sb.append(db.getName()).append("[\n");
        for(int i=0;i<tables.length;i++){
            sb.append(format(tables[i],"    "));
            sb.append("\n");
        }
        sb.append("\n]");
        return sb.toString();
    }
    
    public static String format(Table tb,String align){
        StringBuilder sb = new StringBuilder();
        sb.append(align).append(tb.getName());
        sb.append("(\n");
        Field[] fields = tb.getFields();
        for(int i=0;i<fields.length;i++){
            sb.append(format(fields[i],align + "    "));
            sb.append(",");
            sb.append("\n");
        }
        Index[] indexes = tb.getIndexes();
        for(Index idx:indexes){
            sb.append(format(idx,align + "    ")).append(",").append("\n");
        }
        sb.delete(sb.length()-2, sb.length()-1);
        sb.append(align).append(")");
        return sb.toString();
    }
    
    public static String format(Field field, String align) {
        String defaultValue = field.getDefaultValue();
        return align + String.format("%s %s %s %s", field.getName(), field.getType(), field.isNullable() ? "NULL" : "NOT NULL", defaultValue != null ? ("DEFAULT " + defaultValue) : "");
    }
    
    public static String format(Index idx,String align){
        return align + String.format("%sINDEX %s(%s)"
                , idx.isUnique() ? "UNIQUE " : ""
                , idx.getName()
                , String.join(",", Arrays.asList(idx.getColumns()))
        );
    }

}
