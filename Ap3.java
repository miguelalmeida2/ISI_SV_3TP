/**
 ISEL - DEETC
 Introdução a Sistemas de Informação
 MP,ND, 2014-2022
 */

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.util.HashMap;
import java.util.Scanner;


interface DbWorker
{
	void doWork();
}
class App
{
	private enum Option
	{
		Unknown,
		Exit,
		RegisterBookMaker,
		ListCourse,
		RegisterStudent,
		EnrolStudent
	}
	private static App __instance = null;
	private String __connectionString;
	private HashMap<Option,DbWorker> __dbMethods;
	private static final String SELECT_CMD = "select name,dateBirth,sex from dbo.Student";

	private App()
	{
		__dbMethods = new HashMap<Option,DbWorker>();
		__dbMethods.put(Option.RegisterBookMaker, new DbWorker() {public void doWork() {App.this.RegisterBookMaker();}});
		//__dbMethods.put(Option.ListStudent, new DbWorker() {public void doWork() {App.this.ListStudent();}});
		//__dbMethods.put(Option.ListCourse, new DbWorker() {public void doWork() {App.this.ListCourse();}});
		//__dbMethods.put(Option.RegisterStudent, new DbWorker() {public void doWork() {App.this.RegisterStudent();}});
		//__dbMethods.put(Option.EnrolStudent, new DbWorker() {public void doWork() {App.this.EnrolStudent();}});

	}
	public static App getInstance()
	{
		if(__instance == null)
		{
			__instance = new App();
		}
		return __instance;
	}

	private Option DisplayMenu()
	{
		Option option=Option.Unknown;
		try
		{
			System.out.println("Course management");
			System.out.println();
			System.out.println("1. Exit");
			System.out.println("2. List students");
			System.out.println("3. List courses");
			System.out.println("4. Register student");
			System.out.println("5. Enrol student");
			System.out.print(">");
			Scanner s = new Scanner(System.in);
			int result = s.nextInt();
			option = Option.values()[result];
		}
		catch(RuntimeException ex)
		{
			//nothing to do.
		}

		return option;

	}
	private final static void clearConsole() throws Exception
	{
		for (int y = 0; y < 25; y++) //console is 80 columns and 25 lines
			System.out.println("\n");

	}
	private void Login() throws java.sql.SQLException
	{

		Connection con = DriverManager.getConnection(getConnectionString());
		if(con != null)
			con.close();

	}
	public void Run() throws Exception
	{
		Login ();
		Option userInput = Option.Unknown;
		do
		{
			clearConsole();
			userInput = DisplayMenu();
			clearConsole();
			try
			{
				__dbMethods.get(userInput).doWork();
				System.in.read();

			}
			catch(NullPointerException ex)
			{
				//Nothing to do. The option was not a valid one. Read another.
			}

		}while(userInput!=Option.Exit);
	}

	public String getConnectionString()
	{
		return __connectionString;
	}
	public void setConnectionString(String s)
	{
		__connectionString = s;
	}

	/**
	 To implement from this point forward. Do not need to change the code above.
	 -------------------------------------------------------------------------------
	 IMPORTANT:
	 --- DO NOT MOVE IN THE CODE ABOVE. JUST HAVE TO IMPLEMENT THE METHODS BELOW ---
	 -------------------------------------------------------------------------------

	 */

	private void printResults(ResultSet dr)
	{
		//TODO
	}
	private void RegisterBookMaker()
	{
		//TODO: Implement
		System.out.println("ListStudent()");
	}
	private void ListCourse()
	{
		//TODO: Implement
		System.out.println("ListCourse()");
	}
	private void RegisterStudent()
	{
		//TODO: Implement
		System.out.println("RegisterStudent()");
	}
	private void EnrolStudent()
	{
		//TODO: Implement
		System.out.println("EnrolStudent()");
	}

}

public class Ap3
{
	public static void main(String[] args) throws SQLException,Exception
	{
		String url =  "jdbc:postgresql://10.62.73.22:5432/?user=l3n4&password=isigods&ssl=false";
		App.getInstance().setConnectionString(url);
		App.getInstance().Run();
	}
}

/* -------------------------------------------------------------------------------- 
private class Connect {
	private java.sql.Connection con = null;
    private final String url = "jdbc:sqlserver://";
    private final String serverName = "localhost";
    private final String portNumber = "1433";
    private final String databaseName = "aula03";
    private final String userName = "matildepato";
    private final String password = "xxxxxxx";

    // Constructor
    public Connect() {
    }

    private java.sql.Connection getConnection() {
        try {
            con = java.sql.DriverManager.getConnection(url, user, pwd);
            if (con != null) {
                System.out.println("Connection Successful!");
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Error Trace in getConnection() : " + e.getMessage());
        }
        return con;
    }

    private void closeConnection() {
        try {
            if (con != null) {
                con.close();
            }
            con = null;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
 --------------------------------------------------------------------------------
 */