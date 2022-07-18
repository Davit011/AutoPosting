package com.example.autoposting.util;

import com.example.autoposting.model.Token;
import com.example.autoposting.model.User;
import com.example.autoposting.model.UserType;
import com.example.autoposting.service.TokenService;
import com.example.autoposting.service.UserService;
import lombok.RequiredArgsConstructor;
import org.openqa.selenium.By;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class ExplorerUtil {

    private final UserService userService;
    private final TokenService tokenService;

    public List<String> findToken(ChromeDriver driver){
        new WebDriverWait(driver, Duration.ofMillis(100000)).until(ExpectedConditions.presenceOfElementLocated(By.tagName("br")));
        int iterIndex = 0;
        List<WebElement> driverElements = driver.findElements(By.tagName("div"));
        List<WebElement> tokenDivs = new ArrayList<>();
        for (WebElement driverElement : driverElements) {
            try {
                if (driverElement.getText().contains("access_token") && driverElement.getAttribute("className").contains("_68w8")) {
                    for (WebElement div : driverElement.findElements(By.tagName("div"))) {
                        if (div.getText().contains("EAA") || div.getText().equalsIgnoreCase("      \"access_token\" :")) {
                            if (iterIndex != 0 && iterIndex % 2 == 0) {
                                tokenDivs.add(div);
                            }
                            iterIndex++;
                        }
                    }
                }
            } catch (StaleElementReferenceException e) {
                continue;
            }
        }
        List<String> tokensStr = new ArrayList<>();
        for (WebElement tokenDiv : tokenDivs) {
                String token = tokenDiv.getText().split(":")[1];
                String substring1 = token.trim().substring(1, token.length() - 3);
            if(substring1.contains("EAA")){
                            tokensStr.add(substring1);
                            tokenService.save(Token.builder()
                                    .token(substring1)
                                    .build());
            }
        }
        return tokensStr;
    }

    public void saveUsers(ChromeDriver driver, List<String> tokensStr){
        for (String singleToken : tokensStr) {
            driver.get("https://developers.facebook.com/tools/debug/accesstoken/");
            try {

                driver.findElement(By.name("access_token")).sendKeys(singleToken);
                List<WebElement> buttonForDebug = driver.findElements(By.tagName("button"));
                for (WebElement webElement : buttonForDebug) {
                    if (webElement.getText().equalsIgnoreCase("Отладка") || webElement.getText().equalsIgnoreCase("debug")) {
                        webElement.click();
                        break;
                    }
                }

                List<WebElement> debuggedButton = driver.findElements(By.tagName("button"));
                for (WebElement webElement : debuggedButton) {
                    if (webElement.getText().equalsIgnoreCase("Продлить маркер доступа") || webElement.getText().equalsIgnoreCase("Extend Access Token")) {
                        webElement.click();
                        break;
                    }
                }

                new WebDriverWait(driver, Duration.ofMillis(500000)).until(ExpectedConditions.presenceOfElementLocated(By.tagName("code")));
                List<WebElement> longTimeToken = driver.findElements(By.tagName("code"));

                List<WebElement> trTagElements = driver.findElements(By.tagName("tr"));
                for (WebElement trTagElement : trTagElements) {
                    if (trTagElement.getText().contains("ID Страницы") || trTagElement.getText().contains("Page ID")) {
                        List<WebElement> spanTags = trTagElement.findElements(By.tagName("span"));
                        for (WebElement spanTag : spanTags) {
                            if (!spanTag.getText().contains("ID Страницы") && !spanTag.getText().contains("Page ID")) {
                                String[] info = spanTag.getText().split(":");
                                String id = info[0].trim();
                                String fullName = info[1].trim();
                                if(userService.findByProfileId(id)){
                                    continue;
                                }
                                for (WebElement webElement : longTimeToken) {
                                    if (webElement.getText().contains("EAA")) {
                                        userService.save(User.builder()
                                                .profileId(id)
                                                .token(webElement.getText())
                                                .name(fullName)
                                                .profileType(UserType.FACEBOOK)
                                                .build());
                                    }
                                }

                            }
                        }
                    }
                }
            }catch(StaleElementReferenceException e){
                continue;
            }
        }
    }

}