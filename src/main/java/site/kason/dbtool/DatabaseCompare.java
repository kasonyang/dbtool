package site.kason.dbtool;

import java.util.Arrays;
import java.util.Objects;

/**
 *
 * @author Kason Yang
 */
public class DatabaseCompare {
    
    public static String display(Field f){
        return DatabaseFormatter.format(f, "");
    }
    
    public static String display(Index idx){
        return DatabaseFormatter.format(idx, "");
    }
    
    public static boolean equals(Field f1,Field f2){
        return Objects.equals(f1.getType(), f2.getType())
                && Objects.equals(f1.getName(), f2.getName())
                && Objects.equals(f1.getSize(), f2.getSize())
                && Objects.equals(f1.getDefaultValue(), f2.getDefaultValue());
    }
    
    public static boolean equals(Index idx1,Index idx2){
        return Arrays.equals(idx1.getColumns(), idx2.getColumns());
    }

}
