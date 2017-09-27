package site.kason.dbtool;

import site.kason.dbtool.result.DatabaseCompareResult;
import site.kason.dbtool.result.FieldCompareResult;
import site.kason.dbtool.result.IndexCompareResult;
import site.kason.dbtool.result.TableCompareResult;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import kamons.text.table.TextTableConfig;
import kamons.text.table.TextTableWriter;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

/**
 *
 * @author Kason Yang 
 */
public class App {

  private final static String SYNTAX = "dbtool [show|compare]";

  private final static Options OPTIONS;

  private final static TextTableWriter tableWriter = new TextTableWriter(System.out, new TextTableConfig(false, new int[]{30, 30, 20}));

  static {
    OPTIONS = new Options();
    OPTIONS.addOption("h", false, "show this help message")
            .addOption("verbose", false, "show verbose")
            .addOption("s", true, "server")
            .addOption("u", true, "user")
            .addOption("p", true, "password")
            .addOption("s1", true, "server1")
            .addOption("s2", true, "server2")
            .addOption("u1", true, "user1")
            .addOption("u2", true, "user2")
            .addOption("p1", true, "password1")
            .addOption("p2", true, "password2");
  }

  public static void printUsage() {
    HelpFormatter formatter = new HelpFormatter();
    formatter.printHelp(SYNTAX, OPTIONS);
  }

  public static void main(String[] cliArgs) throws ParseException, IOException {
    DefaultParser parser = new DefaultParser();
    CommandLine cli = parser.parse(OPTIONS, cliArgs);
    if (cli.hasOption("h")) {
      printUsage();
      return;
    }
    String[] args = cli.getArgs();
    if (args.length == 0) {
      printUsage();
      return;
    }
    boolean showVerbose = cli.hasOption("verbose");
    String action = args[0];
    try {
      if ("show".equals(action)) {
        String db = args.length > 1 ? args[1] : null;
        String url = String.format("%s:%s://%s", "jdbc", "mysql", cli.getOptionValue("s", "localhost"));
        String user = cli.getOptionValue("u", "");
        String password = cli.getOptionValue("p", "");
        try (Connection conn = DriverManager.getConnection(url, user, password)) {
          Database[] databases = DatabaseInfo.getDatabases(conn);
          for (Database d : databases) {
            if (db == null || db.equals(d.getName())) {
              System.out.println(DatabaseFormatter.format(d));
            }
          }
        }
      } else if ("compare".equals(action)) {
        if (args.length <= 1) {
          printUsage();
          return;
        }
        String db1 = args[1];
        String db2 = args.length > 2 ? args[2] : db1;
        String server = cli.getOptionValue("s", "localhost");
        String user = cli.getOptionValue("u", "");
        String password = cli.getOptionValue("p", "");
        String server1 = cli.getOptionValue("s1", server);
        String user1 = cli.getOptionValue("u1", user);
        String password1 = cli.getOptionValue("p1", password);
        String server2 = cli.getOptionValue("s2", server);
        String user2 = cli.getOptionValue("u2", user);
        String password2 = cli.getOptionValue("p2", password);
        String url1 = String.format("%s:%s://%s/%s", "jdbc", "mysql", server1, db1);
        String url2 = String.format("%s:%s://%s/%s", "jdbc", "mysql", server2, db2);
        Connection conn1 = DriverManager.getConnection(url1, user1, password1);
        Connection conn2 = DriverManager.getConnection(url2, user2, password2);
        Database d1 = DatabaseInfo.getDatabase(conn1, db1);
        Database d2 = DatabaseInfo.getDatabase(conn2, db2);
        if (d1 == null) {
          error("failed to open database:" + db1);
          return;
        }
        if (d2 == null) {
          error("failed to open database:" + db2);
          return;
        }
        DatabaseCompareResult result = compare(d1, d2);
        handleCompareResult(result, showVerbose);
      } else {
        printUsage();
      }
    } catch (SQLException ex) {
      Logger.getLogger(App.class.getName()).log(Level.SEVERE, null, ex);
    }
  }

  private static void handleCompareResult(DatabaseCompareResult r, boolean verbose) throws IOException {
    TableCompareResult[] tbResults = r.getTableCompareResults();
    int tableCount = tbResults.length;
    int fieldCount = 0;
    for (TableCompareResult tr : tbResults) {
      fieldCount += tr.getFieldCompareResults().length;
      tableWriter.printRow(
              getDisplayNameOfTable(tr.getTable1()), getDisplayNameOfTable(tr.getTable2()), tr.getMessage()
      );
      if (!tr.isPassed()) {
        //System.out.println("Compare fail:");
        for (FieldCompareResult fr : tr.getFieldCompareResults()) {
          if (!fr.isPassed()) {
            System.out.println("");
            System.out.format(
              "%s      <=!=>      %s\n"
              , getDisplayNameOfField(fr.getFiled1())
              , getDisplayNameOfField(fr.getField2())
            );
            if(verbose){
              System.out.format("%s\n%s\n", displayVerboseField(fr.getFiled1()),displayVerboseField(fr.getField2()));
            }
            System.out.println("");
          }
        }
        for (IndexCompareResult ic : tr.getIndexCompareResults()) {
          if (!ic.isPassed()) {
            System.out.println("");
            System.out.println(
                    String.format("%s", getDisplayNameOfIndex(ic.getIndex1()))
            );
            System.out.println(
                    String.format("%s", getDisplayNameOfIndex(ic.getIndex2()))
            );
          }
        }
        System.out.println("");
      }
    }
    System.out.println(String.format("Compare %s (total tables:%s , total fields:%s).", r.isPassed() ? "PASSED" : "FAIL",
            tableCount,
            fieldCount
    ));
    if (!r.isPassed()) {
      System.exit(1);
    }
  }

  private static void error(String msg) {
    System.out.println(msg);
  }

  private static String getDisplayNameOfDatabase(Database db) {
    return db == null ? "Not Found" : db.getName();
  }

  private static String getDisplayNameOfTable(Table tb) {
    if (tb == null) {
      return "Not Found";
    }
    String dbName = getDisplayNameOfDatabase(tb.getDatabase());
    String tbName = tb.getName();
    return String.format("%s.%s", dbName, tbName);
  }

  private static String displayVerboseField(Field field) {
    if (field == null) {
      return "";
    }
    return field.toVerboseString();
  }

  private static String getDisplayNameOfField(Field field) {
    if (field == null) {
      return "Not Found";
    }
    return DatabaseCompare.display(field);
//        if(field==null) return "Not Found";
//        String tbName = getDisplayNameOfTable(field.getTable());
//        String fieldName = field.getName();
//        return String.format("%s.%s",tbName,fieldName);
  }

  private static String getDisplayNameOfIndex(Index idx) {
    if (idx == null) {
      return "Not Found";
    }
    return DatabaseCompare.display(idx);
  }

  private static DatabaseCompareResult compare(Database db1, Database db2) throws IOException {
    List<TableCompareResult> tableResults = new LinkedList();
    boolean passed = true;
    Table[] tbs1 = db1.getTables();
    Map<String, Table> tbs2Map = new HashMap();
    for (Table t : db2.getTables()) {
      tbs2Map.put(t.getName(), t);
    }
    for (Table t : tbs1) {
      Table t2 = tbs2Map.get(t.getName());
      if (t2 == null) {
        passed = false;
        tableResults.add(new TableCompareResult(false, "FAIL", t, t2, new FieldCompareResult[0], new IndexCompareResult[0]));
      } else {
        TableCompareResult tbResult = compare(t, t2);
        if (!tbResult.isPassed()) {
          passed = false;
        }
        tableResults.add(tbResult);
        tbs2Map.remove(t.getName());
      }
    }
    for (Map.Entry<String, Table> e : tbs2Map.entrySet()) {
      passed = false;
      tableResults.add(new TableCompareResult(false, "FAIL", null, e.getValue(), new FieldCompareResult[0], new IndexCompareResult[0]));
    }
    return new DatabaseCompareResult(
            passed,
            passed ? "PASS" : "FAIL",
            db1, db2,
            tableResults.toArray(new TableCompareResult[tableResults.size()])
    );
  }

  private static TableCompareResult compare(Table tb1, Table tb2) throws IOException {
    //System.out.println("comparing  " +tb1.getName() + "  " + tb2.getName());
    List<FieldCompareResult> fieldResults = new LinkedList();
    List<IndexCompareResult> indexResults = new LinkedList();
    boolean passed = true;
    Field[] fds1 = tb1.getFields();
    Map<String, Field> fds2Map = new HashMap();
    Index[] idx1 = tb1.getIndexes();
    Map<String, Index> idx2Map = new HashMap();
    for (Field f : tb2.getFields()) {
      fds2Map.put(f.getName(), f);
    }
    for (Index idx : tb2.getIndexes()) {
      idx2Map.put(idx.getName(), idx);
    }
    for (Field f : fds1) {
      Field f2 = fds2Map.get(f.getName());
      if (f2 == null) {
        passed = false;
        fieldResults.add(new FieldCompareResult(false, "FAIL", f, null));
      } else {
        if (!DatabaseCompare.equals(f, f2)) {
          passed = false;
          fieldResults.add(new FieldCompareResult(false, "FAIL", f, f2));
        } else {
          fieldResults.add(new FieldCompareResult(true, "PASS", f, f2));
        }
        fds2Map.remove(f.getName());
      }
    }
    for (Map.Entry<String, Field> e : fds2Map.entrySet()) {
      passed = false;
      fieldResults.add(new FieldCompareResult(false, "FAIL", null, e.getValue()));
    }
    for (Index idx : idx1) {
      Index idx2 = idx2Map.get(idx.getName());
      if (idx2 == null) {
        passed = false;
        indexResults.add(new IndexCompareResult(false, "FAIL", idx, null));
      } else {
        if (!DatabaseCompare.equals(idx, idx2)) {
          passed = false;
          indexResults.add(new IndexCompareResult(false, "FAIL", idx, idx2));
        } else {
          indexResults.add(new IndexCompareResult(true, "PASS", idx, idx2));
        }
        idx2Map.remove(idx.getName());
      }
    }
    for (Map.Entry<String, Index> e : idx2Map.entrySet()) {
      passed = false;
      indexResults.add(new IndexCompareResult(false, "FAIL", null, e.getValue()));
    }
    return new TableCompareResult(
            passed, passed ? "PASS" : "FAIL", tb1, tb2, fieldResults.toArray(new FieldCompareResult[fieldResults.size()]), indexResults.toArray(new IndexCompareResult[indexResults.size()])
    );
  }
}
