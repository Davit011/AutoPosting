package com.example.autoposting.util;

import com.example.autoposting.model.Token;
import com.example.autoposting.model.User;
import com.example.autoposting.model.UserStatus;
import com.example.autoposting.model.UserType;
import com.example.autoposting.service.TokenService;
import com.example.autoposting.service.UserService;
import lombok.RequiredArgsConstructor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class ExplorerUtil {

    private final UserService userService;
    private final TokenService tokenService;
    private final CollectDisabledUsers collectDisabledUsers;

    @Value("${key.token}")
    private String token;

    public List<String> findToken(ChromeDriver driver) {
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
            if (substring1.contains("EAA")) {
                tokensStr.add(substring1);
                tokenService.save(Token.builder()
                        .token(substring1)
                        .build());
            }
        }
        return tokensStr;
    }


    public void saveUsers(ChromeDriver driver, List<String> tokensStr) {
        for (String singleToken : tokensStr) {
            try {
                driver.get("https://developers.facebook.com/tools/debug/accesstoken/");

                driver.findElement(By.name("access_token")).sendKeys(singleToken);
                List<WebElement> buttonForDebug = driver.findElements(By.tagName("button"));
                for (WebElement webElement : buttonForDebug) {
                    if (webElement.getText().equalsIgnoreCase("??????????????") || webElement.getText().equalsIgnoreCase("debug")) {
                        webElement.click();
                        break;
                    }
                }

                List<WebElement> debuggedButton = driver.findElements(By.tagName("button"));
                for (WebElement webElement : debuggedButton) {
                    if (webElement.getText().equalsIgnoreCase("???????????????? ???????????? ??????????????") || webElement.getText().equalsIgnoreCase("Extend Access Token")) {
                        webElement.click();
                        break;
                    }
                }

                new WebDriverWait(driver, Duration.ofMillis(500000)).until(ExpectedConditions.presenceOfElementLocated(By.tagName("code")));
                List<WebElement> longTimeToken = driver.findElements(By.tagName("code"));

                List<WebElement> trTagElements = driver.findElements(By.tagName("tr"));
                for (WebElement trTagElement : trTagElements) {
                    if (trTagElement.getText().contains("ID ????????????????") || trTagElement.getText().contains("Page ID")) {
                        List<WebElement> spanTags = trTagElement.findElements(By.tagName("span"));
                        for (WebElement spanTag : spanTags) {
                            if (!spanTag.getText().contains("ID ????????????????") && !spanTag.getText().contains("Page ID")) {
                                String[] info = spanTag.getText().split(":");
                                String id = info[0].trim();
                                String fullName = info[1].trim();
                                if (userService.findByProfileId(id)) {
                                    continue;
                                }
                                for (WebElement webElement : longTimeToken) {
                                    if (webElement.getText().contains("EAA")) {
                                        userService.save(User.builder()
                                                .profileId(id)
                                                .token(webElement.getText())
                                                .name(fullName)
                                                .profileType(UserType.FACEBOOK)
                                                .status(UserStatus.ACTIVE)
                                                .build());
                                    }
                                }

                            }
                        }
                    }
                }
            } catch (StaleElementReferenceException e) {
                continue;
            }
        }

    }

    public void saveFindTokensByProfileId(ArrayList<Integer> id) throws IOException {
        List<User> foundUsers = new ArrayList<>();
        for (int user : id) {
            Optional<User> all = userService.findById(user);
            all.ifPresent(foundUsers::add);
        }

        for (User user : foundUsers) {
            OkHttpClient client = new OkHttpClient().newBuilder()
                    .build();
            Request request1 = new Request.Builder()
                    .url("https://graph.facebook.com/" + user.getProfileId() + "?fields=access_token&access_token=" + token)
                    .build();
            System.out.println(request1);
            Response response1 = client.newCall(request1).execute();
            String response = response1.body().string();
            response1.close();
            String split = response.split(",")[0].split(":")[1];
            String substring = split.substring(1, split.length() - 1);
            user.setToken(substring);
            userService.save(user);
        }
    }

    public void findAllTokensByProfileId() throws IOException {
        List<User> all1 = userService.findAll();
        for (User user : all1) {
            OkHttpClient client = new OkHttpClient().newBuilder()
                    .build();
            Request request1 = new Request.Builder()
                    .url("https://graph.facebook.com/" + user.getProfileId() + "?fields=access_token&access_token=" + token)
                    .build();
            System.out.println(request1);
            Response response1 = client.newCall(request1).execute();
            String response = response1.body().string();
            response1.close();
            String split = response.split(",")[0].split(":")[1];
            String substring = split.substring(1, split.length() - 1);
            user.setToken(substring);
            userService.save(user);
        }
    }

    public void findListTokensByProfileId(int id) throws IOException {
        List<User> all1 = userService.findAll();
        for (User user : all1) {
            if (user.getId() > id) {
                OkHttpClient client = new OkHttpClient().newBuilder()
                        .build();
                Request request1 = new Request.Builder()
                        .url("https://graph.facebook.com/" + user.getProfileId() + "?fields=access_token&access_token=" + token)
                        .build();
                System.out.println(request1);
                Response response1 = client.newCall(request1).execute();
                String response = response1.body().string();
                response1.close();
                String split = response.split(",")[0].split(":")[1];
                String substring = split.substring(1, split.length() - 1);
                user.setToken(substring);
                userService.save(user);
            }
        }
    }

    public void saveUsersFromDb(ChromeDriver driver) {
        List<Token> all = tokenService.findAll();
        for (Token singleToken : all) {
            driver.get("https://developers.facebook.com/tools/debug/accesstoken/");
            new WebDriverWait(driver, Duration.ofMillis(500000)).until(ExpectedConditions.presenceOfElementLocated(By.name("access_token")));

            try {
                driver.findElement(By.name("access_token")).sendKeys(singleToken.getToken());
                List<WebElement> buttonForDebug = driver.findElements(By.tagName("button"));
                for (WebElement webElement : buttonForDebug) {
                    if (webElement.getText().equalsIgnoreCase("??????????????") || webElement.getText().equalsIgnoreCase("debug")) {
                        webElement.click();
                        break;
                    }
                }
                List<WebElement> debuggedButton = driver.findElements(By.tagName("button"));
                for (WebElement webElement : debuggedButton) {
                    if (webElement.getText().equalsIgnoreCase("???????????????? ???????????? ??????????????") || webElement.getText().equalsIgnoreCase("Extend Access Token")) {
                        webElement.click();
                        break;
                    }
                }

                new WebDriverWait(driver, Duration.ofMillis(500000)).until(ExpectedConditions.presenceOfElementLocated(By.tagName("code")));
                List<WebElement> longTimeToken = driver.findElements(By.tagName("code"));

                List<WebElement> trTagElements = driver.findElements(By.tagName("tr"));
                for (WebElement trTagElement : trTagElements) {
                    if (trTagElement.getText().contains("ID ????????????????") || trTagElement.getText().contains("Page ID")) {
                        List<WebElement> spanTags = trTagElement.findElements(By.tagName("span"));
                        for (WebElement spanTag : spanTags) {
                            if (!spanTag.getText().contains("ID ????????????????") && !spanTag.getText().contains("Page ID")) {
                                String[] info = spanTag.getText().split(":");
                                String id = info[0].trim();
                                String fullName = info[1].trim();
                                if (userService.findByProfileId(id)) {
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
            } catch (StaleElementReferenceException e) {
                continue;
            }
        }
        collectDisabledUsers.disableUsers();
    }

    public void findInstagramUsers(ChromeDriver driver) {
        List<User> users = userService.findAll();
        for (User user : users) {
            String profileId = user.getProfileId();
            driver.get("https://developers.facebook.com/tools/explorer");
            new WebDriverWait(driver, Duration.ofMillis(500000)).until(ExpectedConditions.presenceOfElementLocated(By.name("q")));
            WebElement query = driver.findElement(By.className("_58al"));
            query.click();
            for (int i = 0; i < 100; i++) {
                query.sendKeys(Keys.BACK_SPACE);
            }
            query.sendKeys(profileId + "?fields=instagram_business_account");
            List<WebElement> divElements = driver.findElements(By.tagName("div"));
            for (WebElement divElement : divElements) {
                if (divElement.getAttribute("className").equals("_43rl") && divElement.getText().equalsIgnoreCase("??????????????????") || divElement.getText().equalsIgnoreCase("submit")) {
                    divElement.click();
                    break;
                }
            }
            new WebDriverWait(driver, Duration.ofMillis(500000)).until(ExpectedConditions.presenceOfElementLocated(By.className("paneContent")));
            List<WebElement> driverElements = driver.findElements(By.tagName("div"));
            for (WebElement driverElement : driverElements) {
                try {
                    if (driverElement.getText().contains("\"instagram_business_account\"") && !driverElement.getText().contains("fields") && driverElement.getAttribute("className").equals("_68w8")) {
                        WebElement aTag = driverElement.findElement(By.tagName("a"));
                        user.setInstagramId(aTag.getText());
                        user.setProfileType(UserType.BOTH);
                        userService.save(user);
                    }
                } catch (StaleElementReferenceException e) {
                    continue;
                }
            }
            continue;
        }
    }

    public void findInstagramUsersFromDb(ChromeDriver driver) {
        new WebDriverWait(driver, Duration.ofMillis(500000)).until(ExpectedConditions.presenceOfElementLocated(By.name("q")));
        WebElement query = driver.findElement(By.className("_58al"));
        query.click();
        for (int i = 0; i < 100; i++) {
            query.sendKeys(Keys.BACK_SPACE);
        }
        List<User> users = userService.findAll();
        for (User user : users) {
            String profileId = user.getProfileId();
            driver.get("https://developers.facebook.com/tools/explorer");
            driver.manage().window().maximize();
            List<WebElement> aTagElements1 = driver.findElements(By.tagName("a"));
            for (WebElement aTagElement : aTagElements1) {
                if (aTagElement.getText().equalsIgnoreCase("log in")) {
                    aTagElement.click();
                    break;
                }
            }

            new WebDriverWait(driver, Duration.ofMillis(500000)).until(ExpectedConditions.presenceOfElementLocated(By.name("q")));
            WebElement query1 = driver.findElement(By.className("_58al"));
            query1.click();
            for (int i = 0; i < 100; i++) {
                query1.sendKeys(Keys.BACK_SPACE);
            }
            query1.sendKeys(profileId + "?fields=instagram_business_account");
            List<WebElement> divElements = driver.findElements(By.tagName("div"));
            for (WebElement divElement : divElements) {
                if (divElement.getAttribute("className").equals("_43rl") && divElement.getText().equalsIgnoreCase("??????????????????") || divElement.getText().equalsIgnoreCase("submit")) {
                    divElement.click();
                    break;
                }
            }
            new WebDriverWait(driver, Duration.ofMillis(500000)).until(ExpectedConditions.presenceOfElementLocated(By.className("paneContent")));
            List<WebElement> driverElements = driver.findElements(By.tagName("div"));
            for (WebElement driverElement : driverElements) {
                try {
                    if (driverElement.getText().contains("\"instagram_business_account\"") && !driverElement.getText().contains("fields") && driverElement.getAttribute("className").equals("_68w8")) {
                        WebElement aTag = driverElement.findElement(By.tagName("a"));
                        user.setInstagramId(aTag.getText());
                        user.setProfileType(UserType.BOTH);
                        userService.save(user);
                    }
                } catch (StaleElementReferenceException e) {
                    continue;
                }
            }
            continue;
        }
    }

    public void getLongTimeToken(ChromeDriver driver, List<User> user, int id) {
        new WebDriverWait(driver, Duration.ofMillis(500000)).until(ExpectedConditions.presenceOfElementLocated(By.id("js_k")));
        for (User user1 : user) {
            if (user1.getId() > id) {
                try {
                    driver.get("https://developers.facebook.com/tools/debug/accesstoken/");
                    new WebDriverWait(driver, Duration.ofMillis(500000)).until(ExpectedConditions.presenceOfElementLocated(By.name("access_token")));
                    driver.findElement(By.name("access_token")).sendKeys(user1.getToken());
                    List<WebElement> buttonForDebug = driver.findElements(By.tagName("button"));
                    for (WebElement webElement : buttonForDebug) {
                        if (webElement.getText().equalsIgnoreCase("??????????????") || webElement.getText().equalsIgnoreCase("debug")) {
                            webElement.click();
                            break;
                        }
                    }

                    List<WebElement> debuggedButton = driver.findElements(By.tagName("button"));
                    for (WebElement webElement : debuggedButton) {
                        if (webElement.getText().equalsIgnoreCase("???????????????? ???????????? ??????????????") || webElement.getText().equalsIgnoreCase("Extend Access Token")) {
                            webElement.click();
                            break;
                        }
                    }

                    new WebDriverWait(driver, Duration.ofMillis(500000)).until(ExpectedConditions.presenceOfElementLocated(By.tagName("code")));
                    List<WebElement> longTimeToken = driver.findElements(By.tagName("code"));

                    List<WebElement> trTagElements = driver.findElements(By.tagName("tr"));
                    for (WebElement trTagElement : trTagElements) {
                        if (trTagElement.getText().contains("ID ????????????????") || trTagElement.getText().contains("Page ID")) {
                            List<WebElement> spanTags = trTagElement.findElements(By.tagName("span"));
                            for (WebElement spanTag : spanTags) {
                                if (!spanTag.getText().contains("ID ????????????????") && !spanTag.getText().contains("Page ID")) {
                                    for (WebElement webElement : longTimeToken) {
                                        if (webElement.getText().contains("EAA")) {
                                            user1.setToken(webElement.getText());
                                            userService.save(user1);
                                        }
                                    }

                                }
                            }
                        }
                    }
                } catch (StaleElementReferenceException e) {
                    continue;
                }
            }

        }
    }

}
