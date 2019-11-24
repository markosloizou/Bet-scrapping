package com.company;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.interactions.Actions;

import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.TimeZone;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.*;

import org.jsoup.*;


public class Main {


    public static void main(String[] args){

/*
        System.setProperty("webdriver.gecko.driver","/home/markos/Desktop/selenium/geckodriver/geckodriver");
        // === Try if the next part of the provlem fails ===

        //Problem: probably value returned is the same for all time

        Calendar cal = Calendar.getInstance();
        GameDataManager365 gameManager365 = new GameDataManager365();
        int previous_minute = cal.get(Calendar.MINUTE);
        int current_minute = cal.get(Calendar.MINUTE);
        int last_restart = cal.get(Calendar.MINUTE);
        ExecutorService service = Executors.newFixedThreadPool(1);
        //driver365 d = new driver365();
        Future<String> futureResult = service.submit(new driver365());
        WebTesting.WaitSeconds(20);
        while (true){
            try {
                String result = null;

                try {
                    result = futureResult.get(20, TimeUnit.SECONDS);
                } catch (TimeoutException e) {
                    System.out.println("No response from driver after 10 seconds, restarting");
                    service.shutdown();
                    service = Executors.newFixedThreadPool(1);
                    //d = new driver365();
                    futureResult = service.submit(new driver365());
                    WebTesting.WaitSeconds(20);
                    futureResult.cancel(true);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (result == null) {
                    System.out.println("String is null");`
                    continue;
                }
                String[] data = result.split(" ");

                gameManager365.UpdateData(data);
            }catch (Exception e){
                e.printStackTrace();
                break;
            }

            WebTesting.WaitSeconds(5);

        }

*/

        //used to test connection to database
        /*
        try {
            Class.forName("com.mysql.jdbc.Driver");
        }catch (Exception e)
        {
            e.printStackTrace();
            System.out.println("\n\nConnector Not Found \n\n");
        }
        ArrayList<Game> games = new ArrayList<Game>();
        DatabaseManager dm = new DatabaseManager(games);
        dm.connectToDB();
        */
        TimeZone.setDefault(TimeZone.getTimeZone("Europe/Nicosia"));
        Calendar c;
        c = Calendar.getInstance();
        DatabaseBackup backup = new DatabaseBackup();
        backup.saveTableToFile();
        //backup.saveTableToFile(c.get(Calendar.MONTH),c.get(Calendar.YEAR));

        //   ---   For apple   ---
        //System.setProperty("webdriver.gecko.driver","/Users/apple/IdeaProjects/geckodriver"); //set the location of the browser driver

        //   ---   For Linux   --
        System.setProperty("webdriver.gecko.driver","/home/markos/java_frameworks/geckodriver");

        FirefoxDriver driver=new FirefoxDriver();   //create a new instance of the browser driver- and open a new window
        driver.get("https://www.bet365.com/");  //get page
        driver.manage().timeouts().implicitlyWait(20, TimeUnit.SECONDS);//set delay to 20 seconds to check all the elements

        // == If following line is useless then delete it ==
        // == check if running is fine now that it is commented ==
        //Actions action =  new Actions(driver);

        GameDataManager365 gameManager365 = new GameDataManager365();

        WebElement element; //Initialise element
        element = WebTesting.FindElementByLinkText(driver, "English"); //find element

        WebTesting.ClickOnElement(element);

        WebTesting.WaitSeconds(3);

        element = WebTesting.FindElementByLinkText(driver, "English");

        WebTesting.ClickOnElement(element);

   
        WebTesting.WaitSeconds(3);


        element = WebTesting.FindElementByXPath(driver, "//*[text()='Soccer']");
        if(element != null)
        {
            System.out.println("Found element with xpath link");
            System.out.println("Text on element:  " + element.getText());
        }

        WebTesting.ClickOnElement(element);
        WebTesting.WaitSeconds(2);

        element = WebTesting.FindElementByXPath(driver, "//*[text() = 'In-Play']");
        WebTesting.ClickOnElement(element);
        WebTesting.WaitSeconds(3);


        element = WebTesting.FindElementByXPath(driver, "//*[text()='Odds']");
        WebTesting.ClickOnElement(element);
        WebTesting.WaitSeconds(1);

        element = WebTesting.FindElementByXPath(driver, "//*[text() = 'Fractional']");
        WebTesting.ClickOnElement(element);
        WebTesting.WaitSeconds(1);

        try {
            FileWriter writer = new FileWriter("html-page.txt");
            writer.write(driver.getPageSource());
            writer.close();
        } catch(Exception ex){ex.printStackTrace();}
        try {
            FileWriter writer = new FileWriter("html-free-page.txt");
            writer.write(Jsoup.parse(driver.getPageSource()).text());
            writer.close();
        } catch(Exception ex){ex.printStackTrace();}

        try{
            FileWriter writer = new FileWriter("new-lines-page.txt");
            String page = Jsoup.parse(driver.getPageSource()).text();
            String[] data = page.split(" ");
            page = page.replaceAll(" ", "\n");
            writer.write(page);
            writer.close();

            gameManager365.UpdateData(data);
            gameManager365.printData();
        }catch(Exception ex){ex.printStackTrace();}

        Calendar cal = Calendar.getInstance();
        int previous_minute = cal.get(Calendar.MINUTE);
        int current_minute = cal.get(Calendar.MINUTE);
        int last_restart_hour = cal.get(Calendar.HOUR_OF_DAY);


        while(true){
            try{

                cal = Calendar.getInstance();
                String page = Jsoup.parse(driver.getPageSource()).text();

                String[] data = page.split(" ");

                gameManager365.UpdateData(data);

                //If something goes wrong with the browser/webpage/driver close the browser and reconnect
                if(data.length < 10 ||  gameManager365.lateUpdate() || ((last_restart_hour+1)%24-cal.get(Calendar.HOUR_OF_DAY))==0){
                    try{
                        last_restart_hour = cal.get(Calendar.HOUR_OF_DAY);
                        System.out.println("Redirecting");

                        // No need to

                        //just reload the page from the start

                        /*
                        driver.quit();
                        try {
                            driver.close();
                        }catch(Exception e){}
                        driver = new FirefoxDriver();
                        */

                        //Delete cookies - === NOT TESTED ====
                        driver.manage().deleteAllCookies();

                        driver.get("https://www.bet365.com.cy/en/");  //get page
                        driver.get("https://www.bet365.com.cy/?lng=1&cb=10326519438#/IP/");

                        // == If following line is useless then delete it ==
                        // == check if running is fine now that it is commented ==
                        //action =  new Actions(driver);
                        element = WebTesting.FindElementByLinkText(driver, "English"); //find element
                        WebTesting.ClickOnElement(element);
                        WebTesting.WaitSeconds(1);
                        element = WebTesting.FindElementByLinkText(driver, "English");
                        WebTesting.ClickOnElement(element);
                        WebTesting.WaitSeconds(1);

                        element = WebTesting.FindElementByLinkText(driver, "Ελληνικά");
                        WebTesting.ClickOnElement(element);
                        element = WebTesting.FindElementByLinkText(driver, "English");
                        WebTesting.ClickOnElement(element);
                        element = WebTesting.FindElementByXPath(driver, "//*[text()='Ελληνικά']");
                        WebTesting.ClickOnElement(element);
                        element = WebTesting.FindElementByLinkText(driver, "English");
                        WebTesting.ClickOnElement(element);

                        element = WebTesting.FindElementByXPath(driver, "//*[text()='Soccer']");
                        WebTesting.ClickOnElement(element);
                        WebTesting.WaitSeconds(1);
                        element = WebTesting.FindElementByXPath(driver, "//*[text() = 'In-Play']");
                        WebTesting.ClickOnElement(element);
                        WebTesting.WaitSeconds(1);

                        element = WebTesting.FindElementByXPath(driver, "//*[text()='Odds']");
                        WebTesting.ClickOnElement(element);
                        WebTesting.WaitSeconds(1);

                        element = WebTesting.FindElementByXPath(driver, "//*[text() = 'Fractional']");
                        WebTesting.ClickOnElement(element);
                        WebTesting.WaitSeconds(1);

                        continue;

                    }catch(Exception e){
                        e.printStackTrace();
                    }
                }

                current_minute = cal.get(Calendar.MINUTE);

                if((current_minute - ((previous_minute+30)%60)) == 0){
                    //gameManager365.limitedPrint();
                    System.out.println("Refreshing");
                    previous_minute = current_minute;
                    driver.navigate().refresh(); //refresh every 10 minutes
                    WebTesting.WaitSeconds(5);
                    element = WebTesting.FindElementByXPath(driver, "//*[text()='Soccer']");
                    if(element != null)
                    {
                        System.out.println("Found element with xpath link");
                        System.out.println("Text on element:  " + element.getText());
                    }
                    else {
                        System.out.println("Couldn't find 'Soccer' element");
                    }

                    WebTesting.ClickOnElement(element);
                    WebTesting.WaitSeconds(2);
                }

                if(cal.get(Calendar.HOUR_OF_DAY) == 3 && cal.get(Calendar.MINUTE) == 30)
                {
                    if(cal.get(Calendar.DAY_OF_MONTH) == 1)
                    {
                        backup.saveTableToFile(cal.get(Calendar.MONTH) - 1, cal.get(Calendar.YEAR));
                        System.out.println("Saved last month's table to file");
                    }
                    else{
                        backup.saveTableToFile();
                        System.out.println("Saved current month table to file");
                    }

                }

                WebTesting.WaitSeconds(5);
            }catch(Exception ex){
                ex.printStackTrace();
                break;
            }
        }
        gameManager365.printData();
        driver.close();

        System.out.println("Program stopped due to errors");

    }
}
