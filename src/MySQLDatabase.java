import javax.swing.*;
import javax.xml.transform.Result;
import java.awt.*;
import java.util.ArrayList;
import java.sql.*;
import java.util.HashMap;

/**
 * Created by : Marino Bjelopera
 * Created on : 02/10/2018
 * Database Access & Connectivity
 * RIT Croatia, 2018
 */
public class MySQLDatabase {

    //Attributes
    private Connection connect;
    private ArrayList<ArrayList<String>> results;
    private String host, user, pw, db, port;
    public String linebreak = "==================================================================" +
            "===============================================================";


    /**
     * MySQLDatabase constructor
     */
    public MySQLDatabase() {
        results = new ArrayList<ArrayList<String>>();
    }//end of MySQLDatabase()

    /**
     * Method that tries to establish connection to database
     * @return Connection Object
     */
    public Connection connect() throws DLException{

        String mysql = String.format("jdbc:mysql://%s:%s/%s", this.host, this.port, this.db);

        try {
            connect = DriverManager.getConnection(mysql, user, pw);
        }
        catch(SQLException sql){ throw new DLException(sql, getErrorData(sql)); }
        catch(Exception e) { throw new DLException(e); }

        return connect;
    }//end of connect()

    /**
     * Method which closes connection to database
     * @return True or false; Depending if the closing succeeded
     */
    public boolean close() throws DLException {

        try {
            connect.close();
        }
        catch(SQLException sql) {
            log("Failed to close the connection to database.");
            throw new DLException(sql, getErrorData(sql));
        }
        catch(Exception e) {
            throw new DLException(e);
        }

        return true;
    }//end of close()

    /**
     * Method that retrieves data from the database
     * @param query Query the user wants to run
     * @return 2D ArrayList of results
     */
    public ArrayList<ArrayList<String>> getData(String query) throws DLException {

        String result;
        Statement stmnt;

        try {
            this.connect();
            stmnt = connect.createStatement();
            ResultSet rs = stmnt.executeQuery(query);
            int cols = rs.getMetaData().getColumnCount();

            while(rs.next()) {
                ArrayList<String> oneRow = new ArrayList<>();
                for(int i = 1; i < cols + 1; i++) {
                    result = rs.getString(i);
                    oneRow.add(result);
                }
                results.add(oneRow);
            }
        }
        catch(SQLException sql) { throw new DLException(sql, getErrorData(sql)); }
        catch(Exception e) { throw new DLException(e); }
        finally { this.close(); }

        return results;
    }//end of getData()

    /**
     * Retrieves data from the database with column headers
     * @param query String query
     * @param isIncluded Include headers or not
     * @return Results of the query
     * @throws DLException Custom exception
     */
    public ArrayList<ArrayList<String>> getData(String query, boolean isIncluded) throws DLException {

        Statement stmnt;
        String result;

        if(isIncluded) {
            try {
                this.connect();
                stmnt = connect.createStatement();
                ResultSet rs = stmnt.executeQuery(query);

                ResultSetMetaData metaData = rs.getMetaData();
                int numOfColumns = metaData.getColumnCount();
                ArrayList<String> columns = new ArrayList<>();
                for (int i = 1; i < numOfColumns + 1; i++) {
                    columns.add(metaData.getColumnName(i));
                }
                results.add(columns);
                while(rs.next()) {
                    ArrayList<String> oneRow = new ArrayList<>();
                    for(int i = 1; i < numOfColumns + 1; i++) {
                        result = rs.getString(i);
                        oneRow.add(result);
                    }
                    results.add(oneRow);
                }
            }
            catch (SQLException sql) { throw new DLException(sql, getErrorData(sql)); }
            catch (Exception e) { throw new DLException(e); }
            finally { this.close(); }
        }//end of if
        else return this.getData(query);

        return results;
    }//end of getData()

    /**
     * Method that runs INSERT, DELETE and UPDATE queries in database
     * @param query Query the user wants to run
     * @return True or false depending if the query succeeded
     */
    public boolean setData(String query) throws DLException{
        Statement stmnt = null;

        try {
            this.connect();
            stmnt = connect.createStatement();
            int result = stmnt.executeUpdate(query);

            if(result < 0) log("Update statement has not been executed.");
        }
        catch(SQLException sql) { throw new DLException(sql, getErrorData(sql)); }
        catch(Exception e) { throw new DLException(e); }
        finally { this.close(); }

        return true;
    }//end of setData()

    /**
     * Retrieves information about the error that occurred
     * @param sql SQLException Object
     * @return HashMap of error details
     */
    private HashMap<String, String> getErrorData(SQLException sql) {

        String errCode = "" + sql.getErrorCode();
        String sqlState = sql.getSQLState();
        String errMsg = sql.getMessage();

        HashMap<String, String> errList = new HashMap<>();

        errList.put("code", errCode);
        errList.put("sqlstate", sqlState);
        errList.put("message", errMsg);

        return errList;
    }//end of getErrorData()

    /**
     * Prints basic information about the database;
     * Product name and version, Driver name and version, All the tables suited in the database
     * Methods the database supports (outer joins, group by, statement pooling)
     * @throws DLException Custom exception
     */
    public void printDBInfo() throws DLException{
        String table[]={"TABLE"};
        try {
            this.connect();
            DatabaseMetaData dmd = connect.getMetaData();
            log(linebreak + "\nFetching Database Information:\n" + linebreak + "\nProduct Name: " + dmd.getDatabaseProductName());
            log("Product Version: " + dmd.getDatabaseProductVersion());
            log("Driver Name: " + dmd.getDriverName());
            log("Driver Version: " + dmd.getDriverVersion());
            ResultSet rs = dmd.getTables(null, null, null, table);
            log("Tables:");
            while(rs.next()){
                log(rs.getString(3) + "\t\t" + rs.getString("TABLE_TYPE"));
            }//end of while
            log("Supports Outer Joins:\t" + Boolean.toString(dmd.supportsOuterJoins()).toUpperCase());
            log("Supports Group By:\t" + Boolean.toString(dmd.supportsGroupBy()).toUpperCase());
            log("Supports Statement Pooling:\t" + Boolean.toString(dmd.supportsStatementPooling()).toUpperCase() + "\n" + linebreak + "\n");
        }//end of try
        catch(SQLException sql) { throw new DLException(sql, getErrorData(sql)); }
        catch(Exception e) { throw new DLException(e); }
    }//end of printDBInfo()

    /**
     * Prints the information for a specific table
     * Column count, columns names & their types, table's primary keys
     * @param tableName Name of the table to be inspected
     * @throws DLException Custom Exception
     */
    public void printTableInfo(String tableName) throws DLException{

        try {
            this.connect();
            DatabaseMetaData dmd = connect.getMetaData();
            ResultSetMetaData metaData = connect.createStatement().executeQuery(String.format("SELECT * FROM %s", tableName)).getMetaData();
            ResultSet columns = dmd.getColumns(null, null, tableName, null);
            ResultSet keys = dmd.getPrimaryKeys(null, null, tableName);

            log("\n" + linebreak + "\n" + tableName.toUpperCase() + " Table Information:");
            log(linebreak);
            log("Column count: " + metaData.getColumnCount() + " columns.");
            log("Column names & types:");
            while(columns.next()){
                log(columns.getString("COLUMN_NAME") + " " + columns.getString("TYPE_NAME"));
            }
            System.out.print("\nPrimary Key(s): ");
            while(keys.next()) {
                log(keys.getString("COLUMN_NAME") + ",\t");
            }
            log(linebreak + "\n");
        }//end of try
        catch(SQLException sql) { throw new DLException(sql, getErrorData(sql)); }
        catch(Exception e){ throw new DLException(e); }

    }//end of printTableInfo()

    /**
     * Retrieves information about ResultSet with given query
     * @param query String query
     * @throws DLException DLExceptio
     */
    public void printResultSetInfo(String query) throws DLException{

        try {
            this.connect();
            ResultSetMetaData metaData = connect.createStatement().executeQuery(query).getMetaData();

            log(linebreak);
            log("QUERY INFORMATION:" + "\n" + linebreak);
            log("Query: " + query);
            log("Column Count: " + Integer.toString(metaData.getColumnCount()));
            System.out.print("Column names: ");
            for(int i = 1; i < metaData.getColumnCount() + 1; i++) {
                log(metaData.getColumnName(i) + " " + metaData.getColumnTypeName(i));
            }
            log("\n" + linebreak + "\n");
        }
        catch(SQLException sql){
            log("Failed to print ResultSetInfo. Please try again later.");
            throw new DLException(sql, getErrorData(sql));
        }
        catch(Exception e) { throw new DLException(e); }
    }//end of printResultSetInfo()

    /**
     * Prevents DRY (less System.out.println copy paste)
     * @param message Message to  be printed to console
     */
    public void log(String message) {
        System.out.println(message);
    }//end of log()

    //SETTERS AND GETTERS BELOW

    /**
     * @param host New host name
     */
    public void setHost(String host) { this.host = host; }

    /**
     * @param user New user using the db
     */
    public void setUser(String user) { this.user = user; }

    /**
     * @param pw New password to be set
     */
    public void setPw(String pw) { this.pw = pw; }

    /**
     * @param db New database name
     */
    public void setDb(String db) { this.db = db; }

    /**
     * @param port Port to be set; port where MySQL server is running
     */
    public void setPort(String port) { this.port = port; }

    /**
     * @return Hostname
     */
    public String getHost() { return host; }

    /**
     * @return Username
     */
    public String getUser() { return user; }

    /**
     * @return User's password
     */
    public String getPw() { return pw; }

    /**
     * @return Database the user is browsing
     */
    public String getDb() { return db; }

    /**
     * @return Port the MySQL server is operating on
     */
    public String getPort() { return port; }

}//end of class MySQLDatabase
