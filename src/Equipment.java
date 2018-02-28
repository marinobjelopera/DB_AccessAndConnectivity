import java.util.ArrayList;
import java.util.List;

/**
 * Created by : Marino Bjelopera
 * Created on : 02/10/2018
 * Database Access & Connectivity
 * RIT Croatia, 2018
 */
public class Equipment {

    private String equipmentName, equipmentDescription;
    private int equipID, equipmentCapacity;
    private MySQLDatabase database;

    /**
     * Default Constructor
     * @param db MySQLDatabase object
     */
    public Equipment(MySQLDatabase db){
        this.database = db;
        this.equipID = 0;
        this.equipmentName = "";
        this.equipmentCapacity = 0;
        this.equipmentDescription = "";
    }//end of Equipment()

    /**
     * Parameterized Constructor
     * @param id Equipment ID; used for setting the equipment ID in Equipment Table
     *           of the Travel Database
     * @param db MySQLDatabase object
     */
    public Equipment(int id, MySQLDatabase db) {
        this.database = db;
        this.equipID = id;
        this.equipmentName = "";
        this.equipmentCapacity = 0;
        this.equipmentDescription = "";
    }//end of Equipment()

    /**
     * Parameterized Constructor
     * @param id Equipment ID
     * @param name Equipment Name
     * @param desc Equipment Description
     * @param cap Equipment Capacity
     * @param db MySQLDatabase object
     */
    public Equipment(int id, String name, String desc, int cap, MySQLDatabase db) {
        this.database = db;
        this.equipID = id;
        this.equipmentName = name;
        this.equipmentDescription = desc;
        this.equipmentCapacity = cap;
    }//end of Equipment()

    /**
     * Method that retrieves a row from the Equipment table that matches the given equipment ID
     */
    public boolean fetch(){
        String query = "SELECT * FROM equipment WHERE equipID = " + this.getEquipID();
        ArrayList<ArrayList<String>> results = null;

        database.log("\n" + database.linebreak + "\nExecuting SELECT statement. Fetching results...\n" + database.linebreak);

        try {
            results = database.getData(query);
        }
        catch(DLException kms) { database.log("Unable to fetch results. Please try again later"); }

        String colName;
        database.log("");

        for(List<String> innerList : results){
            for(String res : innerList){
                colName = String.format("%-30s", res);
                database.log(colName);
            }
        }
        if(results.size() < 1) database.log("No results have been fetched. Check if the record exists in the database.");
        database.log("\n" + database.linebreak + "\n");
        results.clear(); // clearing previous results

        return false;
    }//end of fetch()

    /**
     * Retrieves results with column headers
     * @param include If true - include column headers; else run fetch without column headers
     * @return Boolean - depending on success of the fetch
     */
    public boolean fetch(boolean include) {

        if(include) {
            String query = "SELECT * FROM equipment WHERE equipID = " + this.getEquipID();
            ArrayList<ArrayList<String>> results = null;
            database.log(database.linebreak + "\nExecuting SELECT statement.\n" + database.linebreak +"\nFetching results... (with column names)\n");

            try {
                results = database.getData(query, true);
                for(List<String> innerList : results){
                    String line = "";
                    for(String res : innerList){
                        line += String.format("%-30s ", res);
                    }
                    database.log(line);
                }
                database.log(database.linebreak);
                results.clear();
            }
            catch(DLException kms) { database.log("Unable to fetch results."); }

            return true;
        }
        else { this.fetch(); }
        return false;
    }//end of fetch()

    /**
     * Method that updates a record in the Equipment table on the given table
     */
    public void put() {
        String query = String.format("UPDATE equipment SET equipmentName = \"%s\", equipmentDescription = \"%s\", equipmentCapacity = %d WHERE equipID = %d",
                this.getEquipmentName(), this.getEquipmentDescription(), this.getEquipmentCapacity(), this.getEquipID());
        boolean result = false;

        database.log(database.linebreak + "\nExecuting UPDATE statement.\n" + database.linebreak);

        try {
            result = database.setData(query);
        }
        catch(DLException kms){ database.log("Unable to execute UPDATE."); }

        if(result) database.log("Successfully executed UPDATE.");
        database.log(database.linebreak + "\n");
    }//end of put()

    /**
     * Method that inserts a new record into the Equipment table
     */
    public void post() {
        String query = String.format("INSERT INTO equipment VALUES(%d, \"%s\", \"%s\", %d)", this.getEquipID(), this.getEquipmentName(),
                this.getEquipmentDescription(), this.getEquipmentCapacity());
        boolean result = false;

        database.log("\n" + database.linebreak + "\nExecuting INSERT INTO statement.\n" + database.linebreak + "\n");

        try {
            result = database.setData(query);
        }
        catch (DLException kms) { database.log("Unable to execute INSERT INTO."); }

        if(result) database.log("Successfully executed INSERT.");

    }//end of post()

    /**
     * Method that deletes a record from the Equipment table which matches the given equipment ID
     */
    public void delete() {
        String query = String.format("DELETE FROM equipment WHERE equipID = %d", this.getEquipID());
        boolean result = false;

        database.log(database.linebreak + "\nExecuting DELETE statement.\n" + database.linebreak);

        try {
            result = database.setData(query);
        } catch (DLException kms) {
            database.log("\nUnable to execute DELETE.");
        }

        if(result) {
            database.log("\nSuccessfully deleted from the equipment table.");
        }
        database.log("\n" + database.linebreak);
    }//end of delete()

    //SETTERS AND GETTERS BELOW

    /**
     * Method which returns the equipmentName
     * @return String value of the equipment name
     */
    public String getEquipmentName() { return equipmentName; }

    /**
     * Method which returns the equipmentDescription
     * @return String value of the equipment name
     */
    public String getEquipmentDescription() { return equipmentDescription; }

    /**
     * Method which returns the equipID
     * @return Integer value of the equipment ID
     */
    public int getEquipID() { return equipID; }

    /**
     * Method which returns the equipmentCapacity
     * @return Integer value of the equipment capacity
     */
    public int getEquipmentCapacity() { return equipmentCapacity; }

    /**
     * Method that sets the value of equipmentName to given parameter
     * @param equipmentName Equipment name to be assigned
     */
    public void setEquipmentName(String equipmentName) { this.equipmentName = equipmentName; }

    /**
     * Method that sets the value of equipmentDescription to given parameter
     * @param equipmentDescription Equipment Description to be assigned
     */
    public void setEquipmentDescription(String equipmentDescription) { this.equipmentDescription = equipmentDescription; }

    /**
     * Method that sets the value of equipID to given parameter
     * @param equipID Equipment ID to be assigned
     */
    public void setEquipID(int equipID) { this.equipID = equipID; }

    /**
     * Method that sets the value of equipmentCapacity to given parameter
     * @param equipmentCapacity Equipment Capacity to be assigned
     */
    public void setEquipmentCapacity(int equipmentCapacity) { this.equipmentCapacity = equipmentCapacity; }

}//end of Equipment class
