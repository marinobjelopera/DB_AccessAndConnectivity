import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.HashMap;

/**
 * Created by : Marino Bjelopera
 * Created on : 02/17/2018
 * Database Connectivity & Access
 * RIT Croatia 2018
 */
public class DLException extends Exception{

    private Exception exception;
    private File logFile;
    private FileWriter out;
    private HashMap<String, String> errorList = new HashMap<String, String>();
    private MySQLDatabase database;
    private String linebreak = "===============================================================================================================================================================";

    /**
     * Constructor
     * Handles thrown Exceptions
     * @param e Exception object
     */
    public DLException(Exception e) {
        super("Unable to complete operation.");
        exception = e;
        log();
    }//end of DLExpection()

    /**
     * Constructor
     * Handles thrown SQLExceptions
     * @param exception
     * @param errorList
     */
    public DLException(SQLException exception, HashMap<String, String> errorList) {
        super("Unable to complete request.");
        this.exception = exception;
        this.errorList = errorList;
        log();
    }//end of DLExpection()

    /**
     * Writes the occurred problems to the log txt file
     * Appends to file if exists, created new record if it doesn't
     */
    public void log(){

        try {
            logFile = new File("log.txt");

            if (logFile.exists()) out = new FileWriter(logFile, true);
            else out = new FileWriter(logFile);

            out.write(this.parseError());
            out.flush();
        }
        catch(IOException ioe){
            database.log("\nError occurred. Could not create the log file.\nPlease make sure you have the right privileges in the directory");
            System.exit(0);
        }

    }//end of log()

    /**
     * Formats the error report to
     * @return Formatted error report
     */
    public String parseError() {

        String date = LocalDate.now().toString() + " - " + LocalTime.now();

        if(errorList != null) {
            String errCode = errorList.get("code");
            String sqlState = errorList.get("sqlstate");
            String errMsg = errorList.get("message");


            return String.format("%s%nERROR DETAILS%n%nTime of error: %s%nERROR CODE: %s%nSQL STATE: %s%nMESSAGE: %s%n%s%n%n",linebreak, date, errCode, sqlState, errMsg, linebreak);

        }

        return date;
    }//end of parseError()

}//end of DLException class
