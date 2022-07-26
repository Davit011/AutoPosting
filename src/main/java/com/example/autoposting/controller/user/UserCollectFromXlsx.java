package com.example.autoposting.controller.user;

import com.example.autoposting.model.User;
import com.example.autoposting.model.UserCategory;
import com.example.autoposting.service.UserService;
import com.example.autoposting.util.CollectDisabledUsers;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.*;

@Controller
@RequiredArgsConstructor
public class UserCollectFromXlsx {
    private final UserService userService;
    private final CollectDisabledUsers collectDisabledUsers;

    @GetMapping("/xs")
    public String collect(ModelMap modelMap) throws IOException {
        checkUsers();
        collectCanadaUsers();
        collectLoanUsers();
        modelMap.addAttribute("users", userService.findCheckedUsers());
        return "user/active-users";
    }

    private void checkUsers() throws IOException {
        FileInputStream file = new FileInputStream(new File("C:\\Users\\Kraken\\IdeaProjects\\AutoPosting\\src\\main\\resources\\static\\XlsxFiles\\IG CLIENTS NEW SHEET gg.xlsx"));
        Workbook workbook = new XSSFWorkbook(file);
        Sheet sheet = workbook.getSheetAt(1);
        Map<Integer, List<String>> data = new HashMap<>();
        int i = 0;
        for (Row row : sheet) {

            List<User> all = userService.findAll();
            for (User user : all) {
                if(row.getCell(18) != null && row.getCell(18).getStringCellValue().contains(user.getProfileId())){
                    user.setChecked(true);
                    userService.save(user);
                }
            }
        }
        collectDisabledUsers.collectDisabledUsers();
    }

    private void collectCanadaUsers() throws IOException {
        FileInputStream file = new FileInputStream(new File("C:\\Users\\Kraken\\IdeaProjects\\AutoPosting\\src\\main\\resources\\static\\XlsxFiles\\IG CLIENTS NEW SHEET gg.xlsx"));
        Workbook workbook = new XSSFWorkbook(file);
        Sheet sheet = workbook.getSheetAt(3);
        Map<Integer, List<String>> data = new HashMap<>();
        int i = 0;
        for (Row row : sheet) {
            List<User> all = userService.findAll();
            for (User user : all) {
                if(row.getCell(3) != null && row.getCell(3).getStringCellValue().contains(user.getProfileId())){
                    user.setChecked(true);
                    user.setCategory(UserCategory.CANADA);
                    userService.save(user);
                }
            }
        }
    }

    private void collectLoanUsers() throws IOException {
        FileInputStream file = new FileInputStream(new File("C:\\Users\\Kraken\\IdeaProjects\\AutoPosting\\src\\main\\resources\\static\\XlsxFiles\\IG CLIENTS NEW SHEET gg.xlsx"));
        Workbook workbook = new XSSFWorkbook(file);
        Sheet sheet = workbook.getSheetAt(4);
        Map<Integer, List<String>> data = new HashMap<>();
        int i = 0;
        for (Row row : sheet) {
            List<User> all = userService.findAll();
            for (User user : all) {
                if(row.getCell(7) != null && row.getCell(7).getStringCellValue().contains(user.getProfileId())){
                    user.setChecked(true);
                    user.setCategory(UserCategory.LOAN);
                    userService.save(user);
                    //5
                }
            }
        }
    }
}
