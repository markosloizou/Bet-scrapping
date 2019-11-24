package com.company;

import java.util.concurrent.TimeUnit;
import org.openqa.selenium.*;

public class WebTesting {

    public static void WaitSeconds(long seconds){
        try {
            TimeUnit.SECONDS.sleep(seconds);
        } catch (InterruptedException ex){}
    }

    public static WebElement FindElementByLinkText(WebDriver driver, String text){
        WebElement element = null;
        try {
            element = driver.findElement(By.linkText(text));
        } catch (Exception ex){
            ex.printStackTrace();
            System.out.println("Failed to find element with text: "  + text);
        }
        return element;
    }

    public static WebElement FindElementByPartialLinkText(WebDriver driver, String text){
        WebElement element = null;
        try {
            element = driver.findElement(By.partialLinkText(text));
        } catch (Exception ex){
            ex.printStackTrace();
            System.out.println("Failed to find element with partial text: "  + text);
        }
        return element;
    }

    public static WebElement FindElementByTagName(WebDriver driver, String text){
        WebElement element = null;
        try {
            element = driver.findElement(By.tagName(text));
        } catch (Exception ex){
            ex.printStackTrace();
            System.out.println("Failed to find element with tag name: "  + text);
        }
        return element;
    }

    public static WebElement FindElementByXPath(WebDriver driver, String text){
        WebElement element = null;
        try {
            element = driver.findElement(By.xpath(text));
        } catch (Exception ex){
            ex.printStackTrace();
            System.out.println("Failed to find element with tag name: "  + text);
        }
        return element;
    }

    public static WebElement FindElementByClassName(WebDriver driver, String text){
        WebElement element = null;
        try {
            element = driver.findElement(By.className(text));
        } catch (Exception ex){
            ex.printStackTrace();
            System.out.println("Failed to find element with class name: "  + text);
        }
        return element;
    }


    public static boolean ClickOnElement(WebElement element){
        if(element!= null) {
            try {
                element.click();
            } catch (Exception ex) {
                ex.printStackTrace();
                System.out.println("Failed to click on it");
                return false;
            }
        }
        else{
            System.out.println("Element not initialised");
            return false;
        }
        return true;
    }



}
