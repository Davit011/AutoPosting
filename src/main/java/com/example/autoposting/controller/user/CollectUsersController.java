package com.example.autoposting.controller.user;

import com.example.autoposting.model.User;
import com.example.autoposting.model.UserType;
import com.example.autoposting.service.UserService;
import com.example.autoposting.util.SaveFbPostUtil;
import lombok.RequiredArgsConstructor;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
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

    private final SaveFbPostUtil saveFbPostUtil;
    private final UserService userService;

    @GetMapping("/collect")
    public String collectUsers() {
        String ps = "DAVO3032001";
        System.setProperty("webdriver.chrome.driver", "C:\\Users\\Kraken\\IdeaProjects\\AutoPosting\\src\\main\\resources\\static\\SeleniumWebDriver\\chromedriver.exe");
        ChromeOptions options = new ChromeOptions();
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
        email.sendKeys("+37495136352");
        pass.sendKeys(ps);
        driver.findElement(By.name("login")).click();
        new WebDriverWait(driver, Duration.ofMillis(500000)).until(ExpectedConditions.presenceOfElementLocated(By.name("q")));
        WebElement query = driver.findElement(By.className("_58al"));
        String text = query.getText();
        query.click();
        for (int i = 0; i < 100; i++) {
            query.sendKeys(Keys.BACK_SPACE);
        }
        query.sendKeys("me/accounts");
        List<WebElement> divElements = driver.findElements(By.tagName("div"));
        for (WebElement divElement : divElements) {
            if (divElement.getText().equalsIgnoreCase("Отправить") || divElement.getText().equalsIgnoreCase("send")) {
                divElement.click();
                break;
            }
        }
        new WebDriverWait(driver, Duration.ofMillis(500000)).until(ExpectedConditions.presenceOfElementLocated(By.id("bizBlackBar")));
        List<String> tokens = new ArrayList<>();
        String accessToken = null;
        List<WebElement> driverElements = driver.findElements(By.className("_68w8"));
        List<WebElement> tokenParents = new ArrayList<>();
        try {
            List<WebElement> div = driverElements.get(0).findElements(By.tagName("div"));
            for (WebElement webElement : div) {
                String divText = webElement.getText();
                System.out.println(divText);
                if (divText.equalsIgnoreCase("access_token")) {
                    tokenParents.add(webElement);
//                    tokens.add(saveFbPostUtil.findLastElement("span", "EAAIrbJPX", webElement));
                }
            }
        } catch (StaleElementReferenceException e) {
            for (WebElement webElement : driverElements) {
                if (webElement.getText().contains("EAAIrbJPX")) {
                    tokens.add(saveFbPostUtil.findLastElement("span", "EAAIrbJPX", webElement));
                }
            }
        }


        driver.get("https://developers.facebook.com/tools/debug/accesstoken/");

        String substring1 = accessToken.substring(1, accessToken.length() - 1);
        driver.findElement(By.name("access_token")).sendKeys(substring1);
        List<WebElement> buttonForDebug = driver.findElements(By.tagName("button"));
        for (WebElement webElement : buttonForDebug) {
            if (webElement.getText().equalsIgnoreCase("Отладка") || webElement.getText().equalsIgnoreCase("debug")) {
                webElement.click();
                break;
            }
        }

        List<WebElement> debuggedButton = driver.findElements(By.tagName("button"));
        for (WebElement webElement : debuggedButton) {
            if (webElement.getText().equalsIgnoreCase("Продлить маркер доступа") || webElement.getText().equalsIgnoreCase("debug")) {
                webElement.click();
                break;
            }
        }

        new WebDriverWait(driver, Duration.ofMillis(500000)).until(ExpectedConditions.presenceOfElementLocated(By.tagName("code")));
        List<WebElement> longTimeToken = driver.findElements(By.tagName("code"));
        for (WebElement webElement : longTimeToken) {
            if (webElement.getText().contains("EAAIrbJPX")) {
                System.out.println("Token - " + webElement.getText());
            }
        }
        List<WebElement> trTagElements = driver.findElements(By.tagName("tr"));
        for (WebElement trTagElement : trTagElements) {
            if (trTagElement.getText().contains("ID Страницы")) {
                List<WebElement> spanTags = trTagElement.findElements(By.tagName("span"));
                for (WebElement spanTag : spanTags) {
                    if (!spanTag.getText().contains("ID Страницы")) {
                        System.out.println("Profile Id and Name - " + spanTag.getText());
                        String[] info = spanTag.getText().split(":");
                        String id = info[0].trim();
                        String fullName = info[1].trim();
                        userService.save(User.builder()
                                .profileId(id)
                                .token(substring1)
                                .name(fullName)
                                .profileType(UserType.FACEBOOK)
                                .surname("a")
                                .build());
                    }
                }

            }
        }
        return "redirect:/posts/users";
    }
}