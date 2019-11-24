package com.company;

import org.jsoup.Jsoup;
import org.openqa.selenium.firefox.FirefoxDriver;

class getPage implements Runnable{
    public FirefoxDriver dr;
    public String pg;
    public getPage(FirefoxDriver d, String p){
        this.dr = d;
        this.pg = p;
    }
    @Override
    public void run() {
        pg = Jsoup.parse(dr.getPageSource()).text();
    }
}
