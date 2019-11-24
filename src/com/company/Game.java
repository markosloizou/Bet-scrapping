package com.company;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Calendar;

public class Game {
    private FootballOdds startingOdds, currentOdds ,endOdds;
    private String homeTeam, awayTeam;
    private int homeGoals, awayGoals;
    private String result; //1X2  1 - home win, 2 - away win, X - draw, --- not yet known
    private ArrayList<FootballOdds> GameOdds = new ArrayList<FootballOdds>();
    private boolean active, recordingFromStart;
    private int  counts = 0;

    public Game(){
        FootballOdds odds  = new FootballOdds("null");
        startingOdds = odds;
        currentOdds = odds;
        endOdds = odds;
        result = "---";
        homeTeam = "";
        awayTeam = "";
        recordingFromStart = false;
        active = true;
    }

    public void setHomeTeamName(String homeTeam) {
        this.homeTeam = homeTeam;
    }

    public void setAwayTeamName(String awayTeam) {
        this.awayTeam = awayTeam;
    }

    public String getHomeTeamName() {
        return homeTeam;
    }

    public String getAwayTeamName() {
        return awayTeam;
    }

    public FootballOdds getCurrentOdds() {
        return currentOdds;
    }

    public FootballOdds getEndOdds() {
        return endOdds;
    }

    public FootballOdds getStartingOdds() {
        return startingOdds;
    }

    public int getAwayGoals() {
        return awayGoals;
    }

    public int getHomeGoals() {
        return homeGoals;
    }

    public String getResult() {
        return result;
    }

    public void setAwayGoals(int awayGoals) {
        this.awayGoals = awayGoals;
    }

    public void setCurrentOdds(FootballOdds currentOdds) {
        this.currentOdds = currentOdds;
    }

    public void setEndOdds(FootballOdds endOdds) {
        this.endOdds = endOdds;
    }

    public void setHomeGoals(int homeGoals) {
        this.homeGoals = homeGoals;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public void setStartingOdds(FootballOdds startingOdds) {
        this.startingOdds = startingOdds;
    }

    public void addOdds(FootballOdds odds){
        boolean existsFlag = false;
        int match = -1;
        this.currentOdds = odds;
        this.active = true;

        this.awayTeam = odds.getAwayTeamName();
        this.homeTeam = odds.getHomeTeamName();
        this.homeGoals = odds.getHomeTeamGoals();
        this.awayGoals = odds.getAwayTeamGoals();


        this.result = "x";
        if(this.homeGoals>this.awayGoals){
            this.result = "1";
        }
        if(this.awayGoals>this.homeGoals){
            this.result = "2";
        }


        //can only  test for the last or last two entries to the gameOdds, except for 90 mins -> think

        for (int i = 0; i <= GameOdds.size()-1; i++) {
            //check if no other entry for the same minute and goals then add
            if (GameOdds.get(i).getMinutes().matches(odds.getMinutes())) {
                if (GameOdds.get(i).getAwayTeamGoals() == odds.getAwayTeamGoals() && GameOdds.get(i).getHomeTeamGoals() == odds.getHomeTeamGoals()) {
                    existsFlag = true;
                    match = i;
                    break;
                }
            }
        }


        //if time = 00:00
        if(odds.getMinutes().matches("00") && recordingFromStart == false){
            if(odds.getSeconds().matches("00")){
                startingOdds = odds;
                recordingFromStart = true;
            }
            else if(odds.getAwayTeamGoals() == 0 && odds.getHomeTeamGoals() == 0){
                if(startingOdds == null){
                    startingOdds = odds;
                    recordingFromStart = true;
                }
            }
        }
        //if time >90:00 and same instance added again set result

        if((odds.getTime().matches("90:00") && match!= GameOdds.size()-1) || (odds.getTime().matches("120:00") && match!= GameOdds.size()-1)){
            endOdds = odds;
        }

        if (existsFlag) {
            //System.out.println("Match exists at i = " + match);
            return;
        }

        GameOdds.add(odds);


    }

    public void printGame(){
        System.out.println("\n\nHome team: " + homeTeam + "    Goals: " + homeGoals);
        System.out.println("Away team: " + awayTeam + "     Goals: " + awayGoals);
        System.out.println("Result: " + result);
        System.out.println("\nStarting Odds: \n");
        startingOdds.printFootballOdds();
        System.out.println("\nCurrent odds: \n");
        currentOdds.printFootballOdds();
        System.out.println("\nEnd odds: \n");
        endOdds.printFootballOdds();

        System.out.println("\nNumber of football odds added: " + GameOdds.size());

        for (FootballOdds odd:GameOdds) {
            odd.printFootballOdds();

        }

    }

    public boolean isActive(){
        return this.active;
    }

    public void setActive(boolean a){
        this.active = a;
    }

    public boolean RecordedFromStart(){
        return recordingFromStart;
    }

    public int getCounts() {
        return counts;
    }

    public void incrementCounts()
    {
        this.counts++;
    }

    public void saveToFile() {
        try {
            Calendar cal = Calendar.getInstance();
            String filename = "not_saved_" + cal.get(Calendar.DAY_OF_MONTH) + "_" + cal.get(Calendar.YEAR) + ".log";
            FileWriter fw = new FileWriter(filename, true);
            BufferedWriter bw = new BufferedWriter(fw);
            PrintWriter out = new PrintWriter(bw);
            String message = "\n\n\nNot all games were entered at " + cal.getTime() + "\n\n";
            out.println(message);
            out.println("Recording from beginning: " + recordingFromStart);

            out.println(homeTeam + " vs " + awayTeam + "\n\n");

            out.println("=====   Starting Odds   =====");
            out.flush();
            out.close();
            bw.close();
            fw.close();

            startingOdds.saveOdds(filename);

            fw = new FileWriter(filename, true);
            bw = new BufferedWriter(fw);
            out = new PrintWriter(bw);

            out.println("\n\n=====   In Game Odds   =====");

            out.flush();
            out.close();
            bw.close();
            fw.close();


            for (FootballOdds odd:GameOdds) {
                odd.saveOdds(filename);

            }

            fw = new FileWriter(filename, true);
            bw = new BufferedWriter(fw);
            out = new PrintWriter(bw);

            out.println("\n\n=====   End Odds   =====");

            out.flush();
            out.close();
            bw.close();
            fw.close();

            endOdds.saveOdds(filename);

        }catch(Exception e){
            e.printStackTrace();
        }
    }

    public FootballOdds getOdds(int n)
    {
        if(n < GameOdds.size()) {
            return GameOdds.get(n);
        }

        else{
            return endOdds;
        }
    }
}

