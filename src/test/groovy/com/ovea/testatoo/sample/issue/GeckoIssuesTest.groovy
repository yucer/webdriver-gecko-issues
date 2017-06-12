package com.ovea.testatoo.sample.issue

import org.eclipse.jetty.server.Server
import org.eclipse.jetty.server.handler.DefaultHandler
import org.eclipse.jetty.server.handler.HandlerList
import org.eclipse.jetty.server.handler.ResourceHandler
import org.junit.AfterClass
import org.junit.BeforeClass
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import org.openqa.selenium.By
import org.openqa.selenium.Keys
import org.openqa.selenium.WebDriver
import org.openqa.selenium.WebElement
import org.openqa.selenium.chrome.ChromeDriver
import org.openqa.selenium.firefox.FirefoxDriver
import org.openqa.selenium.interactions.Action
import org.openqa.selenium.interactions.Actions
import org.openqa.selenium.remote.DesiredCapabilities

/**
 * @author David Avenante (d.avenante@gmail.com)
 */
@RunWith(JUnit4)
class GeckoIssuesTest {
    static Server  server
    static WebDriver webDriver

    @BeforeClass
    static void before() {
        startJetty()

        System.setProperty('webdriver.gecko.driver', '/usr/bin/geckodriver')
        DesiredCapabilities capabilities = DesiredCapabilities.firefox()
        capabilities.setCapability('marionette', true)
        webDriver = new FirefoxDriver(capabilities)

//        System.setProperty('webdriver.chrome.driver', '/usr/bin/chromedriver')
//        webDriver = new ChromeDriver()

        webDriver.get('http://localhost:8080/index.html')
    }

    @AfterClass
    static void after() {
        webDriver.quit()
        server.stop()
    }

    @Test
    void cannot_handle_key_modifier() {
        WebElement input = webDriver.findElement(By.id('textfield'))
        input.click()

        Actions actions = new Actions(webDriver)
        Action action = actions
        .keyDown(input, Keys.SHIFT)
        .sendKeys(input, 'testatoo')
        .keyUp(input, Keys.SHIFT)
        .build()

        action.perform()

        assert input.getAttribute('value') == 'TESTATOO'
    }

    private static void startJetty() {
        server = new Server(8080)
        ResourceHandler resource_handler = new ResourceHandler()

        resource_handler.directoriesListed = true
        resource_handler.welcomeFiles = ['index.html']
        resource_handler.resourceBase = 'src/test/webapp'

        HandlerList handlers = new HandlerList()
        handlers.handlers = [resource_handler, new DefaultHandler()]
        server.handler = handlers

        server.start()
    }
}
