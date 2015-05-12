package com.dubszy.webdriver_cookieclicker;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.util.ArrayList;
import java.util.List;

public class CookieElement {
    public static void main(String[] args) {
        String url = "http://orteil.dashnet.org/cookieclicker/";

        WebDriver driver = new FirefoxDriver();
        driver.get(url);

        // Set up a wait for page loading with a 30 second timeout
        WebDriverWait wait = new WebDriverWait(driver, 30);
        boolean exThrown = false;
        WebElement bigCookie;
        WebElement product0;

        try {
            // Wait until the element with the id 'bigCookie' is found
            bigCookie = wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector("#bigCookie")));
            System.out.println("Success: Found #bigCookie");
            try {
                // Wait until the element with the id 'product0' is found
                product0 = wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector("#product0")));
                System.out.println("Success: Found #product0");
            }
            catch (org.openqa.selenium.TimeoutException e) {
                // Throw an exception if the page timed out, throw a timeout exception
                exThrown = true;
                throw e;
            }
        } catch (org.openqa.selenium.TimeoutException e) {
            // Attempt to find #bigCookie timed out, log the error and throw a timeout exception
            if (driver.getCurrentUrl().equals(url)) {
                System.out.println("Error: URL is correct, but #bigCookie was not found.");
            }
            else {
                System.out.println("Error: URL is incorrect: " + driver.getCurrentUrl());
            }
            exThrown = true;
            throw e;
        }
        finally {
            if (exThrown) {
                // If an exception was thrown, quit WebDriver
                driver.quit();
            }
        }

        // Find all elements in the page that have the class 'product'
        List<WebElement> productsList = driver.findElements(By.cssSelector(".product"));
        for (WebElement product : productsList) {
            System.out.println("Found " + product.getAttribute("id"));
        }

        /*
            Infinite loop:
            1. Click the bigCookie
            2. Check if any products are available to click.
            3. If any products are available to click, click the product that costs the least amount of cookies.
         */

        WebElement cheapestClickableProduct;
        long cheapestPrice = 0, price = 0;
        boolean nan = false, hasClickableProduct = false;

        while (true) {
            // Click the big cookie
            bigCookie.click();

            // Check if any products are available to click and setup the cheapest one for clicking.
            for (WebElement product : productsList) {
                // Check if any products are available to click
                if (product.getAttribute("class").equals("product unlocked enabled")) {
                    // Example xpath: //*[@id="product0"]/*/[@class="price"]
                    String priceString = driver.findElement(By.xpath("//*[@id=\"" + product.getAttribute("id") + "\"]/*/[@class=\"price\"]")).getText();
                    // Attempt to parse the string for a Long (the price of the product).
                    try {
                        price = Long.parseLong(priceString);
                    }
                    catch (NumberFormatException e) {
                        // The text of the price element was not a number
                        nan = true;
                        System.out.println("Warning: Attempted to parse '"+priceString+"' for type Long. Number not formatted correctly.");
                    }
                    finally {
                        // If the text of the price element was a number, and the current element's price is less than the cheapest price,
                        // *OR* the cheapest price is 0:
                        if (!nan && price < cheapestPrice || cheapestPrice == 0) {
                            cheapestPrice = price;  // Set the new cheapest price
                            cheapestClickableProduct = product; // Set the cheapest product, this is the element that will be clicked
                            hasClickableProduct = true; // We have a product available to click
                            nan = false; // Reset not-a-number boolean
                        }
                    }
                }
            }

            if (hasClickableProduct)    // Do we have a clickable product?
                cheapestClickableProduct.click(); // Yes, click it.
            hasClickableProduct = false; // Reset has clickable product boolean for next iteration
        }
    }
}