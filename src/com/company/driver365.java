package com.company;

import org.jsoup.Jsoup;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;

import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

public class driver365 implements Callable<String> {
    private FirefoxDriver driver=new FirefoxDriver();   //create a new instance of the browser driver- and open a new window
    @Override
    public String call() throws Exception {
        driver.navigate().refresh();
        WebTesting.WaitSeconds(5);
        return Jsoup.parse(driver.getPageSource()).text();
    }

    public driver365(){
        System.setProperty("webdriver.gecko.driver","/home/markos/Desktop/selenium/geckodriver/geckodriver");


        driver.get("https://www.bet365.com/");  //get page
        driver.manage().timeouts().implicitlyWait(20, TimeUnit.SECONDS);//set delay to 20 seconds to check all the elements

        driver = new FirefoxDriver();
        driver.get("https://www.bet365.com/");  //get page
        driver.manage().timeouts().implicitlyWait(20, TimeUnit.SECONDS);//set delay to 20 seconds to check all the elements
        WebElement element; //Initialise element
        element = WebTesting.FindElementByLinkText(driver, "English"); //find element
        WebTesting.ClickOnElement(element);
        WebTesting.WaitSeconds(1);
        element = WebTesting.FindElementByLinkText(driver, "English");
        WebTesting.ClickOnElement(element);
        WebTesting.WaitSeconds(1);
        element = WebTesting.FindElementByXPath(driver, "//*[text()='Soccer']");
        WebTesting.ClickOnElement(element);
        WebTesting.WaitSeconds(1);
        element = WebTesting.FindElementByXPath(driver, "//*[text() = 'In-Play']");
        WebTesting.ClickOnElement(element);
        WebTesting.WaitSeconds(1);
    }
}
