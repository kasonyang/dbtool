package site.kason.dbtool;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Kason Yang
 */
public class DatabaseInfo {

    public static Database[] getDatabases(Connection conn) throws SQLException {
        return _getDatabase(conn, null);
    }
    
    public static Database getDatabase(Connection conn,String name) throws SQLException{
        Database[] dbs = _getDatabase(conn, name);
        if(dbs.length>0){
            return dbs[0];
        }else{
            return null;
        }
    }
    
    private static Database[] _getDatabase(Connection conn,String name) throws SQLException{
        DatabaseMetaData meta = conn.getMetaData();
        ResultSet schemasResult = meta.getCatalogs();
        List<Database> database = new LinkedList();
        while (schemasResult.next()) {
            String dbname = schemasResult.getString("TABLE_CAT");
            if(name!=null && !name.isEmpty() && !name.equals(dbname)) continue;
            ResultSet tbRes = meta.getTables(dbname, null,"%", null);
            Database db = new Database(dbname);
            database.add(db);
            while (tbRes.next()) {
                String tbName = tbRes.getString("TABLE_NAME");
                String tbRemarks = tbRes.getString("REMARKS");
                //ResultSet colRes = meta.getColumns(dbname, null, tbName, "%");
                String sql = "show columns from " + dbname + "." + tbName;
                PreparedStatement colStmt = conn.prepareStatement(sql);
                ResultSet colRes;
                try{
                    colRes = colStmt.executeQuery();
                }catch(SQLException ex){
                    System.err.println("failed to execute sql : " + sql);
                    continue;
                }
                Table tb = db.createTable(tbName, tbRemarks);
                while (colRes.next()) {
                    String colName = colRes.getString("Field");
                    String colType = colRes.getString("Type");
                    boolean nullable = "YES".equals(colRes.getString("Null"));
                    String remarks = "";//colRes.getString("REMARKS");
                    int colSize = 0 ;// colRes.getInt("COLUMN_SIZE");
                    String colDefault = colRes.getString("Default");
                    tb.createField(colName, colType, colSize, nullable, colDefault, remarks);
                }
                ResultSet indexRes = meta.getIndexInfo(dbname, null , tbName ,false, false);
                Map<String,List<String>> indexes = new HashMap();
                Map<String,Boolean> isUniques = new HashMap();
                while(indexRes.next()){
                    String columnName = indexRes.getString("COLUMN_NAME");
                    String indexName  = indexRes.getString("INDEX_NAME");
                    boolean nonUnique = indexRes.getBoolean("NON_UNIQUE");
                    List<String> idx = indexes.get(indexName);
                    if(idx==null){
                        idx = new LinkedList();
                        indexes.put(indexName, idx);
                    }
                    idx.add(columnName);
                    isUniques.put(indexName, !nonUnique);
                }
                for(Map.Entry<String, List<String>> e:indexes.entrySet()){
                    String idxName = e.getKey();
                    String[] columns = e.getValue().toArray(new String[0]);
                    tb.createIndex(idxName, columns,isUniques.get(idxName));
                }
            }
        }
        return database.toArray(new Database[database.size()]);
    }

}
