package com.company;

import javax.swing.plaf.nimbus.State;
import javax.xml.crypto.Data;
import java.io.FileWriter;
import java.io.BufferedWriter;
import java.io.PrintWriter;
import java.sql.*;
import java.util.ArrayList;
import java.util.Calendar;

public class DatabaseManager implements Runnable{

    private Connection connection;
    private ArrayList<Game> games;

    public DatabaseManager(ArrayList<Game> g){
        try{
            Class.forName("com.mysql.jdbc.Driver").newInstance();
        } catch (Exception e){
            e.printStackTrace();
            System.err.println("Unable to find and locate driver");
            System.exit(1);
        }
        this.games = g;
        System.out.println("Games to be saved: " + Integer.toString(games.size()));
    }



    private void displaySQLerror(SQLException e){
        System.out.println("SQL Exception: " + e.getMessage());
        System.out.println("SQL State: " + e.getSQLState());
        System.out.println("Vendor Error : " + e.getErrorCode());
    }


    public void connectToDB(){
        try{
            connection = DriverManager.getConnection("jdbc:mysql://localhost/football_odds?user=javaBet&password=Hangar18&serverTimezone=Europe/Nicosia&useSSL=false");
            System.out.println("Connected to Database");
        }catch (SQLException e){
            displaySQLerror(e);
        }
    }

    private int createMonthTable(){
        connectToDB();
        Calendar cal = Calendar.getInstance();
        String tableName = Integer.toString(cal.get(Calendar.MONTH)+1) + "_" + Integer.toString(cal.get(Calendar.YEAR));
        String StrStatement = "CREATE TABLE " + tableName + "(id MEDIUMINT UNSIGNED NOT NULL AUTO_INCREMENT, " +
                            "Home_Team_Name VARCHAR(64), Away_Team_Name VARCHAR(64), "
                            + "Result CHAR(1), Home_Goals TINYINT UNSIGNED, Away_Goals TINYINT UNSIGNED, " +
                            "Home_Team_Odds FLOAT(8,6), Away_Team_Odds FLOAT(8,6) "
                            + ", Draw_Odds FLOAT(8,6), Over_Odds FLOAT(8,6), Under_Odds FLOAT(8,6), " +
                            "Over_Number_Of_Goals FLOAT(6,3), Under_Number_Of_Goals FLOAT(6,3), ts TIMESTAMP"
                            + ", PRIMARY KEY(id))";

        try {
            Statement statement = connection.createStatement();
            ResultSet rs = statement.executeQuery("SHOW TABLES");

            while(rs.next()){
                if(rs.getString(1).matches(tableName))
                {
                    return 1;
                }
            }

            rs.close();

        } catch (SQLException e){
            System.out.println("Exception at show tables");
            displaySQLerror(e);
        }

        int i = 0;

        try {
            Statement statement = connection.createStatement();
            i = statement.executeUpdate(StrStatement);

            statement.close();
        }catch (SQLException e){
            System.out.println("Exception at create table");
            displaySQLerror(e);

            try {
                connection.close();
            }catch(Exception ex){
                ex.printStackTrace();
            }
            return -1;
        }

        try{
            Statement statement = connection.createStatement();
            i = statement.executeUpdate("INSERT INTO table_names (Name, Year) VALUES (\"" + tableName + "\", now() )");

            connection.close();
            statement.close();

        }catch (SQLException e){
            System.out.println("Interrupt in adding table to table_of_tables");
            displaySQLerror(e);
        }

        return i;
    }

    public int saveFootballOdds(){

        connectToDB();
        Calendar cal = Calendar.getInstance();
        String tableName = Integer.toString(cal.get(Calendar.MONTH)+1) + "_" + Integer.toString(cal.get(Calendar.YEAR));
        boolean flag = false;

        try {
            if(connection == null){
                connectToDB();
            }
            Statement statement = connection.createStatement();
            ResultSet rs = statement.executeQuery("SHOW TABLES");

            while(rs.next()){
                if(rs.getString(1).matches(tableName))
                {
                    flag = true;
                }
            }

            rs.close();

        } catch (SQLException e){
            displaySQLerror(e);
        }

        if(flag == false){
            createMonthTable();
        }


        char result;
        int sum = 0;
        String strStatement = null;
        try {

            Statement statement = connection.createStatement();
            for (Game g : games) {
                if(g.getHomeGoals() > g.getAwayGoals()){
                    result = '1';
                }
                else if(g.getHomeGoals() < g.getAwayGoals())
                {
                    result = '2';
                }
                else{
                    result = 'x';
                }
                strStatement = "INSERT INTO " + tableName + "(Home_Team_Name, Away_Team_Name, Result, Home_Goals, Away_Goals," +
                        "Home_Team_Odds, Away_Team_Odds, Draw_Odds, Over_Odds, Under_Odds, Over_Number_Of_Goals, Under_Number_Of_Goals, ts) " +
                        "VALUES( \"" + g.getHomeTeamName() + "\", \"" + g.getAwayTeamName() + "\", \'" + result + "\', " + Integer.toString(g.getHomeGoals()) +
                        ", " + Integer.toString(g.getAwayGoals()) + ", " + Float.toString(g.getStartingOdds().getHomeTeamOdds()) + ", " + Float.toString(g.getStartingOdds().getAwayTeamOdds()) +
                        ", " + Float.toString(g.getStartingOdds().getDrawOdds()) + ", " + Float.toString(g.getStartingOdds().getOverOdds()) + ", " + Float.toString(g.getStartingOdds().getUnderOdds()) + ", " +
                        Float.toString(g.getStartingOdds().getOverNumberOfGoals()) + ", " + Float.toString(g.getStartingOdds().getUnderNumberOfGoals()) + ", now() )";

                statement.addBatch(strStatement);

            }

            int[] results = statement.executeBatch();

            String filename = "log" + cal.get(Calendar.DAY_OF_MONTH) + "-" + cal.get(Calendar.MONTH) + "-"+cal.get(Calendar.YEAR) + ".log";
            FileWriter fw = new FileWriter(filename,true);
            BufferedWriter bw = new BufferedWriter(fw);
            PrintWriter out = new PrintWriter(bw);

            for (int i: results) {
                if(i == 0){
                    System.out.println("Error - insertion failed?");
                }
                sum += i;
            }

            String message = "Added " + Integer.toString(sum) + " new games at " + cal.getTime();
            out.println(message);

            out.flush();
            out.close();
            bw.close();
            fw.close();
        }catch(SQLException e) {
            System.out.println("Exception in insert odds to table");
            System.out.println("Statement: \n" + strStatement );
            displaySQLerror(e);
            return -1;
        }catch (Exception e){
            //do nothing
        }
        return 1;
    }


    public void run(){
        int check = 0;
        check = saveFootballOdds();
        Calendar cal = Calendar.getInstance();
        if(check != 1){
            try{
                String filename = "log" + cal.get(Calendar.DAY_OF_MONTH) + "-" + cal.get(Calendar.MONTH) + "-"+cal.get(Calendar.YEAR) + ".log";
                FileWriter fw = new FileWriter(filename,true);
                BufferedWriter bw = new BufferedWriter(fw);
                PrintWriter out = new PrintWriter(bw);
                String message = "Not all games were entered at " + cal.getTime();
                out.println(message);
                out.flush();
                out.close();
                bw.close();
                fw.close();
            }catch(Exception e){
                e.printStackTrace();
                System.out.println("Can't open log");
            }
        }
    }
}
