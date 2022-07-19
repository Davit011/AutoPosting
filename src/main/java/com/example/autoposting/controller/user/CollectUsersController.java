package com.example.autoposting.controller.user;

import com.example.autoposting.util.ExplorerUtil;
import lombok.RequiredArgsConstructor;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/users")
public class CollectUsersController {

    private final ExplorerUtil explorerUtil;

    @GetMapping("/collect")
    public String collectUsers() {


//        String ps = "MBS97facebook";
        String ps = "DAVO3032001";
        System.setProperty("webdriver.chrome.driver", "C:\\Users\\Kraken\\IdeaProjects\\AutoPosting\\src\\main\\resources\\static\\SeleniumWebDriver\\chromedriver.exe");
        ChromeDriver driver = new ChromeDriver();
        driver.get("https://developers.facebook.com/tools/explorer/?method=GET&path=me%2Faccounts&version=v14.0");
        driver.manage().window().maximize();
        List<WebElement> aTagElements = driver.findElements(By.tagName("a"));
        for (WebElement aTagElement : aTagElements) {
            if (aTagElement.getText().equalsIgnoreCase("log in")) {
                aTagElement.click();
                break;
            }
        }
        new WebDriverWait(driver, Duration.ofMillis(500000)).until(ExpectedConditions.presenceOfElementLocated(By.name("email")));
        WebElement email = driver.findElement(By.name("email"));
        WebElement pass = driver.findElement(By.name("pass"));
//        email.sendKeys("093094127");
        email.sendKeys("+37495136352");
        pass.sendKeys(ps);
        driver.findElement(By.name("login")).click();
        new WebDriverWait(driver, Duration.ofMillis(500000)).until(ExpectedConditions.presenceOfElementLocated(By.name("q")));
        WebElement query = driver.findElement(By.className("_58al"));
        query.click();
        for (int i = 0; i < 100; i++) {
            query.sendKeys(Keys.BACK_SPACE);
        }
//        query.sendKeys("me/accounts?fields=access_token&limit=1");
//        List<WebElement> divElements = driver.findElements(By.tagName("div"));
//        for (WebElement divElement : divElements) {
//            if (divElement.getText().equalsIgnoreCase("Отправить") || divElement.getText().equalsIgnoreCase("submit")) {
//                divElement.click();
//                break;
//            }
//        }
//        new WebDriverWait(driver, Duration.ofMillis(500000)).until(ExpectedConditions.presenceOfElementLocated(By.id("bizBlackBar")));
//        List<String> tokensStr = new ArrayList<>();
//        boolean hasNextPageFound;
//        do {
//            hasNextPageFound = false;
//            List<String> token = explorerUtil.findToken(driver);
//            tokensStr.addAll(token);
//            List<WebElement> driverElements = driver.findElements(By.tagName("a"));
//            for (WebElement driverElement : driverElements) {
//                try {
//                    if (driverElement.getText().contains("access_token") && driverElement.getText().contains("https://graph.facebook.com/") && driverElement.getText().contains("after")) {
//                        hasNextPageFound = true;
//                        driverElement.click();
//                        break;
//                    }
//                } catch (StaleElementReferenceException e) {
//                    continue;
//                }
//            }
//        } while (hasNextPageFound);
//        explorerUtil.saveUsers(driver, tokensStr);
        explorerUtil.findInstagramUsers(driver);
        return "redirect:/posts/users";
    }
}