package com.company;

import org.apache.commons.lang3.StringUtils;
import org.jsoup.helper.StringUtil;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;

public class GameDataManager365{

    private BinaryGame bGame = new BinaryGame();
    private FootballOdds currentOdds;
    private ArrayList<Game> games = new ArrayList<Game>();
    private ArrayList<Game> EndedGames = new ArrayList<Game>();
    private int previous_minute = 0;
    private int ended = 0;
    private int toSave = 0;
    private int max_missing_count = 2000;

    public GameDataManager365(){

    }

    public GameDataManager365(String[] data){
        processData(data);
    }

    public void UpdateData(String[] data){
        processData(data);
    }

    public boolean lateUpdate(){
        Calendar cal = Calendar.getInstance();
        if(Math.abs(previous_minute - cal.get(Calendar.MINUTE)) > 2){
            if(previous_minute != 58 && previous_minute != 59) {
                return true;
            }
        }

        return  false;
    }

    private void processData(String[] data){
        setGamesEnded(); //set all games as over

        int i = 0;
        while(i < data.length){
            if(data[i].matches("\\d{2,3}:\\d{2}")){
                try {
                    getMatchData(i, data);
                }catch(Exception ex){ex.printStackTrace();}
            }
            i++;
        }


        // Find inactive games and delete them from the games list
        try {
            Iterator<Game> gameIterator = games.iterator();
            int counter = 0;

            while (gameIterator.hasNext()) {
                Game g = gameIterator.next();
                counter++;
                boolean matchFlag = false;

                if (!g.isActive()) {
                    try{
                        if(Integer.parseInt(g.getStartingOdds().getMinutes()) == 0 && Integer.parseInt(g.getEndOdds().getMinutes()) >= 90){
                            matchFlag = true;
                            if(Math.abs(g.getStartingOdds().getHomeTeamOdds() + 1.0) < 0.0001)
                            {
                                g.setStartingOdds(g.getOdds(g.getCounts()));
                                g.incrementCounts();
                                continue;
                            }
                            EndedGames.add(g);

                            toSave++;
                            System.out.println("The following game will be saved: " + g.getHomeTeamName() + " vs " + g.getAwayTeamName() );
                            gameIterator.remove();
                            continue;
                        }
                    }catch(Exception e){
                        //e.printStackTrace();
                    }

                    if((g.getStartingOdds().getMinutes().matches("00") && g.getEndOdds().getMinutes().matches("90"))
                        || (g.getStartingOdds().getMinutes().matches("00") && g.getEndOdds().getMinutes().matches("120") )){

                        matchFlag = true;

                        if(Math.abs(g.getStartingOdds().getHomeTeamOdds() + 1) < 0.0001)
                        {
                            g.setStartingOdds(g.getOdds(g.getCounts()));
                            g.incrementCounts();
                            continue;
                        }


                        EndedGames.add(g);
                        toSave++;
                        System.out.println("The following game will be saved: " + g.getHomeTeamName() + " vs " + g.getAwayTeamName() );
                        gameIterator.remove();
                    }
                    else if(g.getStartingOdds().getMinutes().matches("00") && (!(g.getEndOdds().getMinutes().matches("90")) || (g.getEndOdds().getMinutes().matches("120")))) {
                        g.incrementCounts();

                        if (g.getCounts() >= max_missing_count) {
                            System.out.println("The following game won't be saved: " + g.getHomeTeamName() + " vs " + g.getAwayTeamName() +
                                    "\nStarting minute: " + g.getStartingOdds().getMinutes() + "\t End minute: " + g.getEndOdds().getMinutes());
                            ended++;
                            g.saveToFile();
                            gameIterator.remove();
                        }

                    }
                    else if(!g.getStartingOdds().getMinutes().matches("00") && ((g.getEndOdds().getMinutes().matches("90")) || g.getEndOdds().getMinutes().matches("120"))){
                        g.incrementCounts();

                        if(g.getCounts() >= max_missing_count){
                            System.out.println("The following game won't be saved: " + g.getHomeTeamName() + " vs " + g.getAwayTeamName() +
                                    "\nStarting minute: " + g.getStartingOdds().getMinutes() + "\t End minute: " + g.getEndOdds().getMinutes());
                            ended++;
                            g.saveToFile();
                            gameIterator.remove();
                        }

                    }
                    else{
                        g.incrementCounts();

                        if(g.getCounts() >= max_missing_count){
                            System.out.println("The following game won't be saved: " + g.getHomeTeamName() + " vs " + g.getAwayTeamName() +
                                    "\nStarting minute: " + g.getStartingOdds().getMinutes() + "\t End minute: " + g.getEndOdds().getMinutes());
                            ended++;
                            g.saveToFile();
                            gameIterator.remove();
                        }
                    }

                    if(matchFlag == false)
                    {
                        g.setEndOdds(g.getCurrentOdds());
                    }
                }
            }


            Calendar cal = Calendar.getInstance();
            int minute = cal.get(Calendar.MINUTE);
            if(minute != previous_minute) {
                previous_minute = minute;
                System.out.println(Integer.toString(counter) + " games of which " + Integer.toString(ended) + " ended and " + Integer.toString(toSave) + " will be saved  \t" + cal.getTime());
                ended = 0;
                toSave = 0;
            }
        }catch (Exception e){
            e.printStackTrace();
        }

        //Save the inactive games to free memory
        //wait to 10 for efficiency
        if(EndedGames.size() > 1) {
            try {
                System.out.println("Saving " + EndedGames.size() + " games");
                Runnable r = new DatabaseManager(EndedGames);
                new Thread(r).start();
                EndedGames = new ArrayList<>();
            }catch (Exception e){
                e.printStackTrace();
            }
        }

    }

    public void printData(){
        for(Game aGame: games){
            aGame.printGame();
        }

        System.out.println("Number of Games: " + games.size());
        for(Game aGame:games){
            System.out.println(aGame.getHomeTeamName() + " vs " + aGame.getAwayTeamName() + "\tRecorded from beginning: " + aGame.RecordedFromStart());
        }
    }

    public void limitedPrint(){
        System.out.println("Number of Games: " + games.size());
        for(Game aGame:games){
            System.out.println(aGame.getHomeTeamName() + " vs " + aGame.getAwayTeamName() + "\nRecorded from beginning: " + aGame.RecordedFromStart());
        }
    }
    private void getMatchData(int start,String[] data){
        int i = 1;
        bGame = new BinaryGame();
        currentOdds =  new FootballOdds();
        String name = "";
        boolean timeflag = false;

        while(!data[start + i].matches("GOAL")){
            //System.out.println(data[start+i]);
            timeflag = checkForTime(data[start + i]);
            if(timeflag == true){break;}
            else{timeflag = false;}
            if(data[start+i].matches("GOAL")){
                timeflag = false;
                break;
            }
            if(data[start+i].matches("ET")){
                i++;
                continue;
            }
            name += data[start + i];
            name += " ";
            i++;
            if(start+i >= data.length){
                return;
            }
        }
        if(timeflag == true)
        {
            /*System.out.println("\nNew time stamp found before data -> Invalid entry -> breaking");
            System.out.println("read: " + data[start+i]);
            System.out.println("start: " + start + "  i = " + i);
            System.out.println("data[start] = " + data[start]);
            System.out.println("data[start + i] = " + data[start+i]);
            System.out.println("Name so far: " + name + "\n");*/
            return;
        }
        i++;
        bGame.setHomeTeam(name);

        name = "";

        while(!data[start + i].matches("GOAL")){
            timeflag = checkForTime(data[start + i]);
            if(timeflag == true){break;}
            else{timeflag = false;}

            if(data[start+i].matches("GOAL")){
                timeflag = false;
                break;
            }
            if(data[start+i].matches("ET")){
                i++;
                continue;
            }
            name += data[start + i] + " ";
            i++;

            if(start+i >= data.length){
                return;
            }
        }

        if(timeflag == true)
        {
            System.out.println("New time stamp found before data -> Invalid entry -> breaking");
            return;
        }

        bGame.setAwayTeam(name);

        // Check If a game matches this in the array List if not add new game and add data to it
        // if there is a match, then add the new data to the game

        int gameFound = -1;

        for (int j = 0; j <= games.size()-1; j++) {
            if (games.get(j).getHomeTeamName().matches(bGame.getHomeTeamName()) && games.get(j).getAwayTeamName().matches(bGame.getAwayTeamName())) {
                gameFound = j;
                break;
            }
        }


        if(gameFound == -1){
            Game addGame = new Game();
            addGame.setAwayTeamName(bGame.getAwayTeamName());
            addGame.setHomeTeamName(bGame.getHomeTeamName());
            games.add(addGame);
            gameFound = games.indexOf(addGame);
        }
        games.get(gameFound).setHomeTeamName(bGame.getHomeTeamName());
        games.get(gameFound).setAwayTeamName(bGame.getAwayTeamName());

        games.get(gameFound).setActive(true);   //set game as running

        while(!data[start + i].matches("Draw")){
            timeflag = checkForTime(data[start + i]);
            if(timeflag == true){break;}
            else{timeflag = false;}
            i++;
            if(start+i >= data.length){
                return;
            }
        }
        if(timeflag == true)
        {
            System.out.println("New time stamp found before data -> Invalid entry -> breaking");
            return;
        }

        try {
            FootballOdds toAdd = complexData(start + i - 1, data, bGame);
            //check if all empty

            //if not add?
            games.get(gameFound).addOdds(toAdd);
        }catch(Exception ex){ex.printStackTrace();}


    }

    //Check to see if the string matches a regex for a time string mm:ss or mmm:ss ie, 45:00 or 115:47 for overtime
    private boolean checkForTime(String str){
        ///Check for time Regex, if foun set flag to true else return false

        if(str.matches("\\d{2,3}:\\d{2}")){
            //System.out.println("Match true for: " + str);
            return true;
        }

        return false;
    }

    private FootballOdds SimpleData(int start,String[] data) {
        int i = 0;
        String substr;

        //get home goals
        i++;
        //check if number first
        games.get(i).setHomeGoals(Integer.parseInt(data[start + i])); //not needed, implement within the games class when new odds are added
        currentOdds.setHomeTeamGoals(Integer.parseInt(data[start + i]));

        //get away team goals
        i++;
        //check if number
        games.get(i).setAwayGoals(Integer.parseInt(data[start+i]));//not needed
        currentOdds.setAwayTeamOdds(Integer.parseInt(data[start+i]));

        //get odds home team
        i++;
        currentOdds.setHomeTeamOdds(convertOdds(data[start+i]));

        //get away team odds
        i++;
        currentOdds.setAwayTeamOdds(convertOdds(data[start+i]));

        //get draw odds
        i++;
        currentOdds.setDrawOdds(convertOdds(data[start+i]));

        //get next goal home odds
        i++;
        currentOdds.setNextGoalHomeTeamOdds(convertOdds(data[start+i]));

        //get next goal away odds
        i++;
        currentOdds.setNextGoalAwayTeamOdds(convertOdds(data[start+i]));

        //get no next goal
        i++; //at string No
        i++;
        substr = data[start + i].substring(data[start+i].length()-3);
        currentOdds.setNoNextGoalOdds(convertOdds(substr));

        //get over goals
        i++; //AT string O
        i++;
        substr = data[start + i].substring(0,2);
        currentOdds.setOverNumberOfGoals(Float.parseFloat(substr));
        substr = data[start + i].substring(data[start+i].length() - 3);
        currentOdds.setOverOdds(convertOdds(substr));

        //get under goals
        i++; //AT string U
        i++;
        substr = data[start + i].substring(0,2);
        currentOdds.setUnderNumberOfGoals(Float.parseFloat(substr));
        substr = data[start + i].substring(data[start+i].length() - 3);
        currentOdds.setUnderOdds(convertOdds(substr));

        return currentOdds;
    }

    private float convertOdds(String odds){
        String[] parts = odds.split("[/]");
        float num,den;
        num = (float) Integer.parseInt(parts[0]);
        den = (float) Integer.parseInt(parts[1]);

        return (num/den);
    }

    private FootballOdds complexData(int start, String[] data, BinaryGame bGame){
        FootballOdds odds =  new FootballOdds();
        odds.setHomeTeamName(bGame.getHomeTeamName());
        odds.setAwayTeamName(bGame.getAwayTeamName());
        int i = 0;
        String Time;
        while (true){
            if(checkForTime(data[start + i])){
                Time = data[start+i];
                odds.setTime(Time);
                break;
            }
            i--;
            if(start + i < 0){
                System.out.println("Couldn't find time, went negative");
                break;
            }
        }
        i = 0;
        while(true){
            if(data[start+i].matches("Draw"))
            {
                //System.out.println("Matched Draw");
                if(StringUtils.isNumeric(data[start + i + 1]) && StringUtils.isNumeric(data[start + i +2])) {
                    odds.setHomeTeamGoals(Integer.parseInt(data[start + i + 1]));
                    odds.setAwayTeamGoals(Integer.parseInt(data[start + i + 2]));

                    //System.out.println("Home goals: " + odds.getHomeTeamGoals() + "\nAway team Goals: " + odds.getAwayTeamGoals());
                }

                if(data[start + i + 3].matches("\\d{1,2}/\\d{1,2}") && data[start + i + 4].matches("\\d{1,2}/\\d{1,2}") && data[start + i + 5].matches("\\d{1,2}/\\d{1,2}")){
                    odds.setHomeTeamOdds(convertOdds(data[start+i+3]));
                    odds.setAwayTeamOdds(convertOdds(data[start+i+4]));
                    odds.setDrawOdds(convertOdds(data[start+i+5]));
                }

                if(data[start+i+6].matches("\\d{1,2}/\\d{1,2}") && data[start+i+7].matches("\\d{1,2}/\\d{1,2}")){
                    odds.setNextGoalHomeTeamOdds(convertOdds(data[start+i+6]));
                    odds.setNextGoalAwayTeamOdds(convertOdds(data[start+i+7]));
                }
            }

            if(data[start+i].matches("No")){
                if(start+i+1 >= data.length){
                    break;
                }
                String[] str = data[start + i + 1].split("/");
                String substr1, substr2;
                int position;
                if(str.length < 2) //Array out of bounds exception occurred at the following if statement
                {
                    i++;
                    continue;
                }
                try {
                    if (str[0] == null || str[1] == null) {
                        i++;
                        continue;
                    }
                }catch (Exception e){
                    e.printStackTrace();
                    System.out.println("str = " + str);
                }
                substr2 = str[1];
                position = str[0].length()-1;
                substr1 = str[0].substring(position);
                while(StringUtils.isNumeric(substr1)){
                    position--;
                    substr1 = str[0].substring(position);
                }
                substr1 = str[0].substring(position+1);

                float noNextGoalOdds = (Float.parseFloat(substr1))/Float.parseFloat(substr2);
                odds.setNoNextGoalOdds(noNextGoalOdds);
            }

            if(data[start+i].matches("O")){
                String[] split = data[start+i+1].split("\\."); //Split 3.57/4 to 3 and 57/4
                //System.out.println("Matched O(ver)");
                //System.out.println("String to process: " + data[start+i+1]);
                //System.out.println("element 0: " + split[0]);
                //System.out.println("element 1: " + split[1]);
                String toConvert =  split[1].substring(1,split[1].length()); //reduce to 7/4
                odds.setOverOdds(convertOdds(toConvert));
                float over = Float.parseFloat(split[0]);
                over += 0.50;
                odds.setOverNumberOfGoals(over);
            }

            if(data[start+i].matches("U")){
                String[] split = data[start+i+1].split("\\."); //Split 3.57/4 to 3 and 57/4
                String toConvert =  split[1].substring(1,split[1].length()); //reduce to 7/4
                odds.setUnderOdds(convertOdds(toConvert));
                float under = Float.parseFloat(split[0]);
                under += 0.50;
                odds.setUnderNumberOfGoals(under);
            }

            if(checkForTime(data[start+i])){
                break;
            }

            i++;
            if(start + i >= data.length){
                break;
            }
        }
        return odds;
    }

    private void setGamesEnded(){
        for (Game g: games) {
            g.setActive(false);
        }
    }

}
