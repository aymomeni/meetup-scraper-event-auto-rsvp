package com.momeni.meetup.rsvp.helper;

import static org.openqa.selenium.support.ui.ExpectedConditions.visibilityOf;
import static org.openqa.selenium.support.ui.ExpectedConditions.visibilityOfElementLocated;

import com.momeni.meetup.rsvp.config.Hook;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class VisibilityHelper {

    @Autowired
    private Hook hooks;

    /**
     * Waits until the given element is visible.
     * The element must be present on the DOM before the waiting starts
     *
     * @param element Element to check
     */
    public void waitForVisibilityOf(WebElement element) {
        hooks.getWait().until(visibilityOf(element));
    }

    /**
     * Waits for presence and visibility of the element matched by given selector.
     * The element can be present in the DOM or not before the waiting starts
     *
     * @param by Selector of the element
     */
    public void waitForPresenceOf(By by) {
        System.out.println("got to waitForPresenceOf");
        hooks.getWait().until(visibilityOfElementLocated(by));
        System.out.println(by.toString() + "found");
    }
}
