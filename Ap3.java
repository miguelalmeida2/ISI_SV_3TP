
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
        newBetHouse,
        newPlayerAtBetHouse,
        newPlayerBet,
        suspendPlayer,
        totalPlayersInBetHouse,
        insertBetResolution,
        showPlayersBets,
        Exit
    }
    private static App __instance = null;
    private String __connectionString;
    private HashMap<Option,DbWorker> __dbMethods;

    private App()
    {
        __dbMethods = new HashMap<Option,DbWorker>();
        __dbMethods.put(Option.newBetHouse, new DbWorker() {public void doWork() {Model.newBetHouse();}});
        __dbMethods.put(Option.newPlayerAtBetHouse, new DbWorker() {public void doWork() {Model.newPlayerAtBetHouse();}});
        __dbMethods.put(Option.newPlayerBet, new DbWorker() {public void doWork() {Model.newPlayerBet();}});
        __dbMethods.put(Option.suspendPlayer, new DbWorker() {public void doWork() {Model.suspendPlayer();}});
        __dbMethods.put(Option.totalPlayersInBetHouse, new DbWorker() {public void doWork() {Model.totalPlayersInBetHouse();}});
        __dbMethods.put(Option.insertBetResolution, new DbWorker() {public void doWork() {Model.insertBetResolution();}});
        __dbMethods.put(Option.showPlayersBets, new DbWorker() {public void doWork() {Model.showPlayersBets();}});
        __dbMethods.put(Option.Exit , new DbWorker(){public void doWork() {Model.exit();}});
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
            System.out.println("\t\tBetHouse management");
            System.out.println();
            System.out.println("1.\tNew BetHouse");
            System.out.println("2.\tInsert new Player in BetHouse");
            System.out.println("3.\tInsert new Bet from Player in BetHouse");
            System.out.println("4.\tSuspend Player from Playing or Making Transactions");
            System.out.println("5.\tShow the Total of Players inside BetHouse");
            System.out.println("6.\tInsert Bet Resolution");
            System.out.println("7.\tShow Manuel Fernandes's bets");
            System.out.println("8.\tExit");
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
}

public class Ap3
{
    public static void main(String[] args) throws SQLException,Exception
    {
        String url =  "jdbc:postgresql://10.62.73.22:5432/?user=l3n4&password=isigods";
        App.getInstance().setConnectionString(url);
        App.getInstance().Run();
    }
}
