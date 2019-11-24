package com.company;

import java.io.*;

public class FootballOdds implements Serializable{
    private String time, homeTeamName, awayTeamName;
    private int homeTeamGoals, awayTeamGoals;
    private float homeTeamOdds, awayTeamOdds, drawOdds, nextGoalHomeTeamOdds, nextGoalAwayTeamOdds;

    private float overNumberOfGoals, underNumberOfGoals;
    private float overOdds, underOdds;
    private float noNextGoalOdds;

    private String minutes,seconds;

    public FootballOdds(){
        setNull();
    }

    public FootballOdds(String str){
        if(str.matches("null")) {
            setNull();
        }
    }

    private void setNull()
    {
        this.time = "--:--";
        this.homeTeamName = "-";
        this.awayTeamName = "-";
        this.homeTeamGoals = -1;
        this.awayTeamGoals = -1;
        this.homeTeamOdds = -1;
        this.awayTeamOdds = -1;
        this.drawOdds = -1;
        this.nextGoalAwayTeamOdds = -1;
        this.nextGoalAwayTeamOdds = -1;
        this.overNumberOfGoals = -1;
        this.underNumberOfGoals = -1;
        this.overOdds = -1;
        this.underOdds = -1;
        this.noNextGoalOdds = -1;
        this.minutes = "--";
        this.seconds = "--";
    }

    public void setTime(String Time){
        time = Time;
        String[] parts = Time.split("[:]");
        minutes = parts[0];
        seconds = parts[1];
    }

    public void setHomeTeamName(String homeName){
        this.homeTeamName = homeName;
    }

    public void setAwayTeamName(String awayName){
        this.awayTeamName = awayName;
    }

    public void setHomeTeamGoals(int homeGoals){
        this.homeTeamGoals = homeGoals;
    }

    public void setAwayTeamGoals(int awayGoals){
        this.awayTeamGoals = awayGoals;
    }

    public void setHomeTeamOdds(float homeOdds){
        this.homeTeamOdds = homeOdds;
    }

    public void setAwayTeamOdds(float awayOdds){
        this.awayTeamOdds = awayOdds;
    }

    public void setDrawOdds(float drawOdds1){
        this.drawOdds = drawOdds1;
    }

    public void setNextGoalHomeTeamOdds(float nextGoalHomeOdds){
        this.nextGoalHomeTeamOdds = nextGoalHomeOdds;
    }

    public void setNextGoalAwayTeamOdds(float nextGoalAwayOdds){
        this.nextGoalAwayTeamOdds = nextGoalAwayOdds;
    }

    public float getNoNextGoalOdds() {
        return noNextGoalOdds;
    }

    public void setNoNextGoalOdds(float noNextGoal) {
        this.noNextGoalOdds = noNextGoal;
    }

    public void setOverNumberOfGoals(float overNumber){
        this.overNumberOfGoals = overNumber;
    }

    public void setUnderNumberOfGoals(float underNumber){
        this.underNumberOfGoals = underNumber;
    }

    public void setOverOdds(float overOdds1){
        this.overOdds = overOdds1;
    }

    public void setUnderOdds(float underOdds1){
        this.underOdds = underOdds1;
    }

    public String getTime() {
        return time;
    }

    public String getHomeTeamName() {
        return homeTeamName;
    }

    public String getAwayTeamName(){
        return awayTeamName;
    }

    public float getHomeTeamOdds() {
        return homeTeamOdds;
    }

    public float getAwayTeamOdds() {
        return awayTeamOdds;
    }

    public float getDrawOdds() {
        return drawOdds;
    }

    public float getNextGoalHomeTeamOdds() {
        return nextGoalHomeTeamOdds;
    }

    public float getNextGoalAwayTeamOdds() {
        return nextGoalAwayTeamOdds;
    }

    public int getHomeTeamGoals() {
        return homeTeamGoals;
    }


    public float getOverNumberOfGoals() {
        return overNumberOfGoals;
    }

    public int getAwayTeamGoals() {
        return awayTeamGoals;
    }

    public float getUnderNumberOfGoals() {
        return underNumberOfGoals;
    }

    public float getOverOdds() {
        return overOdds;
    }

    public float getUnderOdds() {
        return underOdds;
    }

    public String getMinutes() {
        return minutes;
    }

    public String getSeconds() {
        return seconds;
    }

    public void printFootballOdds(){
        System.out.println("\nTime: " + time);
        System.out.println("Minutes: " + minutes + "\t Seconds: " + seconds);
        System.out.println("Home team: " +homeTeamName);
        System.out.println("Away team: " + awayTeamName);
        System.out.println("Home team Goals: " + homeTeamGoals);
        System.out.println("Away team Goals: " + awayTeamGoals);
        System.out.println("Home team odds: " + homeTeamOdds);
        System.out.println("Away team odds: " + awayTeamOdds);
        System.out.println("Draw odds: " + drawOdds);
        System.out.println("Next Goal Home Team odds: " + nextGoalHomeTeamOdds);
        System.out.println("Next Goal away Team odds: " + nextGoalAwayTeamOdds);
        System.out.println("No next Goal Odds: " + noNextGoalOdds);
        System.out.println("Over " + overNumberOfGoals + " goals odds: " + overOdds);
        System.out.println("Under " + underNumberOfGoals + " goals odds: " + underOdds + "\n");
    }

    public boolean isNull()
    {
        //finish the function
        if(time.matches("--.--") && homeTeamName.matches("-") && awayTeamName.matches("-") && homeTeamGoals == -1 && awayTeamGoals == -1
                && homeTeamOdds == -1 && awayTeamOdds == -1 && drawOdds == -1 && noNextGoalOdds == -1 && nextGoalAwayTeamOdds == -1 && nextGoalHomeTeamOdds == -1
                && overOdds == -1 && underOdds == -1) {
            return true;}
        return false;
    }

    public void saveOdds(String filename){
        try {
            FileWriter fw = new FileWriter(filename, true);
            BufferedWriter bw = new BufferedWriter(fw);
            PrintWriter out = new PrintWriter(bw);

            out.println("\nTime: " + time);
            out.println("Minutes: " + minutes + "\t Seconds: " + seconds);
            out.println("Home team: " +homeTeamName);
            out.println("Away team: " + awayTeamName);
            out.println("Home team Goals: " + homeTeamGoals);
            out.println("Away team Goals: " + awayTeamGoals);
            out.println("Home team odds: " + homeTeamOdds);
            out.println("Away team odds: " + awayTeamOdds);
            out.println("Draw odds: " + drawOdds);
            out.println("Next Goal Home Team odds: " + nextGoalHomeTeamOdds);
            out.println("Next Goal away Team odds: " + nextGoalAwayTeamOdds);
            out.println("No next Goal Odds: " + noNextGoalOdds);
            out.println("Over " + overNumberOfGoals + " goals odds: " + overOdds);
            out.println("Under " + underNumberOfGoals + " goals odds: " + underOdds + "\n");

            out.flush();
            out.close();
            bw.close();
            fw.close();
        }catch(Exception ex){
            ex.printStackTrace();
        }
    }
}
