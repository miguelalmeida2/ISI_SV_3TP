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
        newBetHouse,
        newPlayerAtBetHouse,
        newPlayerBet,
        suspendPlayer,
        totalPlayersInBetHouse,
        insertBetResolution,
        manuelFernandesBets
    }
    private static App __instance = null;
    private String __connectionString;
    private HashMap<Option,DbWorker> __dbMethods;

    private App()
    {
        __dbMethods = new HashMap<Option,DbWorker>();
        __dbMethods.put(Option.newBetHouse, new DbWorker() {public void doWork() {App.this.newBetHouse();}});
        __dbMethods.put(Option.newPlayerAtBetHouse, new DbWorker() {public void doWork() {App.this.newPlayerAtBetHouse();}});
        __dbMethods.put(Option.newPlayerBet, new DbWorker() {public void doWork() {App.this.newPlayerBet();}});
        __dbMethods.put(Option.suspendPlayer, new DbWorker() {public void doWork() {App.this.suspendPlayer();}});
        __dbMethods.put(Option.totalPlayersInBetHouse, new DbWorker() {public void doWork() {App.this.totalPlayersInBetHouse();}});
        __dbMethods.put(Option.insertBetResolution, new DbWorker() {public void doWork() {App.this.insertBetResolution();}});
        __dbMethods.put(Option.manuelFernandesBets, new DbWorker() {public void doWork() {App.this.manuelFernandesBets();}});
        __dbMethods.put(Option.Exit, new DbWorker() {public void doWork() {App.this.Exit();}});

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
            System.out.println("0.\tNew BetHouse");
            System.out.println("1.\tInsert new Player in BetHouse");
            System.out.println("2.\tInsert new Bet from Player in BetHouse");
            System.out.println("3.\tSuspend Player from Playing or Making Transactions");
            System.out.println("4.\tShow the Total of Players inside BetHouse");
            System.out.println("5.\tInsert Bet Resolution");
            System.out.println("6.\tShow Manuel Fernandes's bets");
            System.out.println("7.\tExit");
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
        String url =  "jdbc:postgresql://10.62.73.22:5432/?user=l3n4&password=isigods&ssl=false";
        App.getInstance().setConnectionString(url);
        App.getInstance().Run();
    }
}
