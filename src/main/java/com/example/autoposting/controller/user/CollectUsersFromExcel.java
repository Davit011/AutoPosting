package com.example.autoposting.controller.user;

import com.example.autoposting.model.*;
import com.example.autoposting.service.FailedUserService;
import com.example.autoposting.service.TokenService;
import com.example.autoposting.service.UserService;
import com.example.autoposting.util.ExplorerUtil;
import lombok.RequiredArgsConstructor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.openqa.selenium.chrome.ChromeDriver;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Controller
@RequiredArgsConstructor
@RequestMapping("/users")
public class CollectUsersFromExcel {
    private final UserService userService;

    private final FailedUserService failedUserService;
    private final ExplorerUtil explorerUtil;

    private final CollectUsersController collectUsersController;

    @Value("${key.excelPath}")
    private String excelPath;

    @Value("${key.driver}")
    private String driverPath;
    @Value("${key.token}")
    private String token;

    @GetMapping("/excel/fb")
    public String collectFbUsersFromExcel() throws IOException {
        FileInputStream file = new FileInputStream(excelPath);
        Workbook workbook = new XSSFWorkbook(file);

        int j = 0;
        int cellNumber = 0;

        for (int i = 1; i <= 4; i++) {
            switch (i) {
                case 1:
                    cellNumber = 18;
                    break;
                case 2:
                    cellNumber = 5;
                    break;
                case 3:
                    cellNumber = 3;
                    break;
                case 4:
                    cellNumber = 6;
                    break;
            }
            Sheet sheet = workbook.getSheetAt(i);

            for (Row row : sheet) {
                UserType userType = UserType.FACEBOOK;
                UserCategory userCategory = null;
                String name = "";
                switch (i) {
                    case 1:
                        userType = UserType.FACEBOOK;
                        name = row.getCell(0).getStringCellValue();
                        break;

                    case 2:
                        if (row.getCell(0) != null) {
                            name += row.getCell(0).getStringCellValue() + " ";
                        }
                        if (row.getCell(1) != null) {
                            name += row.getCell(1).getStringCellValue();
                        }
                        break;
                    case 3:
                        if (row.getCell(0) != null){
                            name = row.getCell(0).getStringCellValue();
                            userCategory = UserCategory.CANADA;
                        }
                        break;
                    case 4:
                        userCategory = UserCategory.LOAN;
                        if (row.getCell(0) != null) {
                            name += row.getCell(0).getStringCellValue() + " ";
                        }
                        if (row.getCell(1) != null) {
                            name += row.getCell(1).getStringCellValue();
                        }
                        break;

                }
                j++;
                if (row.getCell(cellNumber) != null && !row.getCell(cellNumber).getStringCellValue().equals("")) {
                    String stringCellValue = row.getCell(cellNumber).getStringCellValue();
                    String profileId = stringCellValue.split("asset_id=")[1].split("&")[0];
                    System.out.println(j);
                    if (userService.findUserByProfileId(profileId) == null) {
                        OkHttpClient client = new OkHttpClient().newBuilder()
                                .build();
                        Request request = new Request.Builder()
                                .url("https://graph.facebook.com/" + profileId + "?fields=access_token&access_token=" + token)
                                .build();
                        Response response = client.newCall(request).execute();
                        String tokenStr = response.body().string().split(",")[0].split(":")[1];
                        String token = tokenStr.substring(1, tokenStr.length() - 1);
                        User user = null;
                        if (!token.contains("message")) {
                            user = userService.findUserByProfileId(profileId);
                        } else {
                            System.err.println(i + "  " + row.getRowNum());
                            failedUserService.save(FailedUser.builder()
                                    .profileId(profileId)
                                    .build());
                        }
                        if (user == null && !token.contains("message")) {
                            System.out.println(i + "  " + row.getRowNum());
                            System.out.println(row.getRowNum());
                            userService.save(User.builder()
                                    .name(name)
                                    .isChecked(true)
                                    .token(token)
                                    .profileId(profileId)
                                    .profileType(userType)
                                    .category(userCategory)
                                    .status(UserStatus.ACTIVE)
                                    .build());
                            response.close();
                        }
                    }
                }
            }
        }
        System.setProperty("webdriver.chrome.driver", driverPath);
        ChromeDriver driver = new ChromeDriver();
        collectUsersController.login(driver);
        explorerUtil.saveUsersFromDb(driver);
        return "redirect:/users";
    }

    @GetMapping("/category")
    private String usersCategory() throws IOException {
        FileInputStream file = new FileInputStream(excelPath);
        Workbook workbook = new XSSFWorkbook(file);
        Sheet sheet = workbook.getSheetAt(3);
        for (Row row : sheet) {
            if (row.getCell(4) != null && !row.getCell(4).getStringCellValue().equals("")) {
                String stringCellValue = row.getCell(4).getStringCellValue();
                String profileId = stringCellValue.split("asset_id=")[1].split("&")[0];
                User userByProfileId = userService.findUserByProfileId(profileId);
                userByProfileId.setCategory(UserCategory.CANADA);
            }
        }
        Sheet sheet1 = workbook.getSheetAt(4);
        for (Row row : sheet1) {
            if (row.getCell(7) != null && !row.getCell(7).getStringCellValue().equals("")) {
                String stringCellValue = row.getCell(7).getStringCellValue();
                String profileId = stringCellValue.split("asset_id=")[1].split("&")[0];
                User userByProfileId = userService.findUserByProfileId(profileId);
                userByProfileId.setCategory(UserCategory.CANADA);
            }
        }
        return null;
    }

    @GetMapping("/remove/repeats")
    public String deleteRepeatUsers(){
        List<FailedUser> all = failedUserService.findAll();
        Set<String> id = new HashSet<>();
        for (FailedUser failedUser : all) {
            id.add(failedUser.getProfileId());
            failedUserService.deleteAllRepeats(failedUser.getProfileId());
        }

        for (String s : id) {
            failedUserService.save(FailedUser.builder()
                    .profileId(s)
                    .build());
        }
        return "redirect:/users";
    }

    @GetMapping("/excel/ig")
    public String findIgUsers() throws IOException {
        List<User> uncheckedUsers = userService.findUncheckedUsers();
        for (User uncheckedUser : uncheckedUsers) {
            OkHttpClient client = new OkHttpClient().newBuilder()
                    .build();
            Request request = new Request.Builder()
                    .url("https://graph.facebook.com/" + uncheckedUser.getProfileId() + "?fields=instagram_business_account&access_token=" + uncheckedUser.getToken())
                    .build();
            Response response = client.newCall(request).execute();
            String responseBody = response.body().string();
            if(responseBody.contains("instagram_business_account")){
                String split = responseBody.split(",")[0].split(":")[2];
                String substring = split.substring(1, split.length() - 2);
                uncheckedUser.setProfileType(UserType.BOTH);
                uncheckedUser.setInstagramId(substring);
            }
            uncheckedUser.setChecked(true);
            userService.save(uncheckedUser);
        }
        return "redirect:/users";
    }
}
