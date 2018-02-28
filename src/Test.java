import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Scanner;

/**
 * Created by : Marino Bjelopera
 * Created on : 02/10/2018
 * Database Access & Connectivity
 * RIT Croatia, 2018
 */
public class Test {

    /**
     * Main method
     * @param args Program arguments ( name of the file where information is stored )
     */
    public static void main(String[] args) {

        MySQLDatabase database = new MySQLDatabase();

        ArrayList<String> infoList = new ArrayList<>();
        String[] results;

        URL resource = MySQLDatabase.class.getResource(args[0]);
        Scanner scan = null;

        try { scan =  new Scanner(resource.openStream()); }
        catch(IOException ioe) { ioe.printStackTrace(); }

        //Read from file
        while (scan.hasNext()) {
            try {
                results = scan.nextLine().split(":");
                infoList.add(results[1]);
            }
            //if there is nothing provided after ':'
            catch (ArrayIndexOutOfBoundsException aioex){
                infoList.add("");
            }
        }//end of while()

        //close the scanner stream
        scan.close();

        //adding information to the database class
        database.setHost(infoList.get(0));
        database.setUser(infoList.get(1));
        database.setDb(infoList.get(2));
        database.setPort(infoList.get(3));
        database.setPw(infoList.get(4));

        //connecting to the database
        try { database.connect(); }
        catch (DLException kms) {
            database.log("Unable to connect to database.");
            System.exit(0);
        }

        try {
            database.printDBInfo();
            database.printTableInfo("equipment");
            database.printResultSetInfo("SELECT COUNT(*) FROM equipment");
        }
        catch(DLException kms) { database.log("Unable to print database information."); }

        //initializing Equipment object with set equipment ID
        Equipment eq = new Equipment(568, database);
        //testing fetch()
        eq.fetch(true); //fetching the data with the corresponding ID

        //initializing Equipment object with set data to be put into the Database
        Equipment equ = new Equipment(1234,"MySQL Driver", "Use with care", 1, database);
        //testing post()
        equ.post();
        equ.fetch();

        //testing put()
        equ.setEquipmentName("Oracle Driver");
        equ.setEquipmentDescription("Never used Oracle");
        equ.setEquipmentCapacity(5);
        equ.put();
        equ.fetch();

        //testing delete
        equ.delete();
        equ.fetch();

    }//end of main()

}//end of Test class
