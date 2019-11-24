package com.company;

import javax.swing.plaf.nimbus.State;
import javax.xml.crypto.Data;
import java.io.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.Calendar;


public class DatabaseBackup {

    private Connection connection;

    public DatabaseBackup() {
        boolean error;

        try {
            // Class.forName("com.mysql.jdbc.Driver").newInstance(); //supposedly depreciated
            Class.forName("com.mysql.cj.jdbc.Driver").newInstance();
            //Class.forName("/home/markos/java_frameworks/com.mysql.cj.jdbc.Driver").newInstance();
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Unable to find and locate driver");
            System.exit(1);
        }

        error = connectToDB();

        if (error == false) {
            System.out.println("Could not connect to database, cannot backup files");
        }
    }

    private void displaySQLerror(SQLException e) {
        System.out.println("SQL Exception: " + e.getMessage());
        System.out.println("SQL State: " + e.getSQLState());
        System.out.println("Vendor Error : " + e.getErrorCode());
    }


    private boolean connectToDB() {
        try {
            connection = DriverManager.getConnection("jdbc:mysql://localhost/football_odds?user=javaBet&password=Hangar18&serverTimezone=Europe/Nicosia&useSSL=false");
            System.out.println("Connected to Database");
            return true;
        } catch (SQLException e) {
            displaySQLerror(e);
            return true;
        }
    }

    public void saveTableToFile() {
        Calendar cal;
        cal = Calendar.getInstance();

        String tableName;
        tableName = Integer.toString(cal.get(Calendar.MONTH) + 1) + "_" + Integer.toString(cal.get(Calendar.YEAR));
        String fileName = tableName + ".csv";

        String strStatement = "SELECT 'Home_Team_Name', 'Away_Team_Name', 'Result', 'Home_Goals', 'Away_Goals','Home_Team_Odds',"
                + "'Away_Team_Odds', 'Draw_Odds', 'Over_Odds', 'Under_Odds', 'Over_Number_Of_Goals', 'Under_Number_Of_Goals', 'ts'\n"
                + " UNION ALL "
                + " SELECT "
                + "Home_Team_Name, Away_Team_Name, Result, Home_Goals, Away_Goals,Home_Team_Odds, Away_Team_Odds, Draw_Odds, "
                + "Over_Odds, Under_Odds, Over_Number_Of_Goals, Under_Number_Of_Goals, ts "
                + "FROM " + tableName + " INTO OUTFILE  \'/home/markos/Desktop/sqlBackup/tmp.txt"
                + "\' FIELDS ENCLOSED BY \'\"\' TERMINATED BY \';\' ESCAPED BY \'\"\' LINES TERMINATED BY '\\r\\n' ;";

        try {
            connectToDB();
            Statement statement = connection.createStatement();
            statement.execute(strStatement);
            connection.close();
            statement.close();
        } catch (SQLException e) {
            displaySQLerror(e);
        }

        try {
            FileReader fileReader = new FileReader("/home/markos/Desktop/sqlBackup/tmp.txt");
            BufferedReader bufferedReader = new BufferedReader(fileReader);

            // Assume default encoding.
            FileWriter fileWriter = new FileWriter("/home/markos/Desktop/sqlBackup/" + fileName, false);

            // Always wrap FileWriter in BufferedWriter.
            BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);

            String line = null;
            while ((line = bufferedReader.readLine()) != null) {
                bufferedWriter.write(line);
                bufferedWriter.newLine();
            }
            File toDelete = new File("/home/markos/Desktop/sqlBackup/tmp.txt");
            toDelete.delete();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }



    public void saveTableToFile(int month, int year)
    {
        String tableName;
        tableName = Integer.toString(month+1) + "_" + Integer.toString(year);
        String fileName = tableName + ".csv";

        String strStatement = "SELECT 'Home_Team_Name', 'Away_Team_Name', 'Result', 'Home_Goals', 'Away_Goals','Home_Team_Odds',"
                + "'Away_Team_Odds', 'Draw_Odds', 'Over_Odds', 'Under_Odds', 'Over_Number_Of_Goals', 'Under_Number_Of_Goals', 'ts'\n"
                + " UNION ALL "
                + " SELECT "
                + "Home_Team_Name, Away_Team_Name, Result, Home_Goals, Away_Goals,Home_Team_Odds, Away_Team_Odds, Draw_Odds, "
                + "Over_Odds, Under_Odds, Over_Number_Of_Goals, Under_Number_Of_Goals, ts "
                + "FROM " + tableName + " INTO OUTFILE  \'/home/markos/Desktop/sqlBackup/tmp.txt"
                + "\' FIELDS ENCLOSED BY \'\"\' TERMINATED BY \';\' ESCAPED BY \'\"\' LINES TERMINATED BY '\\r\\n' ;";

        try {
            connectToDB();
            Statement statement = connection.createStatement();
            statement.execute(strStatement);
            connection.close();
            statement.close();
        }catch(SQLException e)
        {
            displaySQLerror(e);
        }

        try{
            FileReader fileReader = new FileReader("/home/markos/Desktop/sqlBackup/tmp.txt");
            BufferedReader bufferedReader = new BufferedReader(fileReader);

            // Assume default encoding.
            FileWriter fileWriter = new FileWriter("/home/markos/Desktop/sqlBackup/" + fileName, false);

            // Always wrap FileWriter in BufferedWriter.
            BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);

            String line = null;
            while((line = bufferedReader.readLine()) != null) {
                bufferedWriter.write(line);
                bufferedWriter.newLine();
            }
            File toDelete = new File("/home/markos/Desktop/sqlBackup/tmp.txt");
            toDelete.delete();
        }catch(Exception e)
        {
            e.printStackTrace();
        }
    }
}
