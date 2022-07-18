package com.example.autoposting.controller.user;

import com.example.autoposting.model.User;
import com.example.autoposting.service.UserService;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.*;

@Controller
@RequiredArgsConstructor
public class UserCollectFromXlsx {
    private final UserService userService;

    @GetMapping("/xs")
    public String collecter() throws IOException {

        FileInputStream file = new FileInputStream(new File("C:\\Users\\Kraken\\IdeaProjects\\AutoPosting\\src\\main\\resources\\static\\XlsxFiles\\test users.xlsx"));
        Workbook workbook = new XSSFWorkbook(file);

        Sheet sheet = workbook.getSheetAt(0);

        Map<Integer, List<String>> data = new HashMap<>();
        int i = 0;
        for (Row row : sheet) {
            data.put(i, new ArrayList<String>());
            Optional<User> byName = null;
            if(row.getCell(1) != null){
                byName = userService.findByName(row.getCell(0).getStringCellValue() + " " + row.getCell(1).getStringCellValue());
            }else{
               byName = userService.findByName(row.getCell(0).getStringCellValue());

            }

            byName.ifPresent(user -> System.out.println(user.getName()));
        }
        return null;
    }

}
