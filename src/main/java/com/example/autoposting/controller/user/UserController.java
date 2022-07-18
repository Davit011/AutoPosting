package com.example.autoposting.controller.user;

import com.example.autoposting.dto.SaveUserRequest;
import com.example.autoposting.model.User;
import com.example.autoposting.model.UserType;
import com.example.autoposting.service.UserService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@Controller
@RequiredArgsConstructor
@RequestMapping("/users")
public class UserController {
    private final UserService userService;
    private final ModelMapper mapper;

    @GetMapping
    public String allUsers(ModelMap modelMap) {
        List<User> all = userService.findAll();
        modelMap.addAttribute("users", all);
        return "user/all-users";

    }

    @GetMapping("/save")
    public String saveUser() {
        return "user/save-user";
    }

    @PostMapping("/save")
    public String saveAcc(@ModelAttribute SaveUserRequest userRequest, ModelMap modelMap) {

        if (userRequest.getName().trim().equals("") || userRequest.getName() == null) {
            modelMap.addAttribute("missedName", "Please input user's name");
            return "user/save-user";
        }

        if (userRequest.getToken().trim().equals("") || userRequest.getToken() == null) {
            modelMap.addAttribute("missedToken", "Please input user's token");
            return "user/save-user";
        }

        if (userRequest.getProfileId().trim().equals("") || userRequest.getProfileId() == null) {
            modelMap.addAttribute("missedId", "Please input user's profile id");
            return "user/save-user";
        }

        if (userRequest.getUserType() == null) {
            modelMap.addAttribute("missedProfileType", "Please select user's profile type");
            return "user/save-user";
        }

        User user = mapper.map(userRequest, User.class);

        if (userRequest.getId() != 0) {
            user.setId(userRequest.getId());
        }

        switch (userRequest.getUserType()) {
            case "INSTAGRAM":
                user.setProfileType(UserType.INSTAGRAM);
                break;
            case "FACEBOOK":
                user.setProfileType(UserType.FACEBOOK);
                break;
            case "BOTH":
                user.setProfileType(UserType.BOTH);
                break;
            default:
                modelMap.addAttribute("missedProfileType", "Select Profile Type");
        }

        userService.save(user);
        return "redirect:/users";
    }

    @GetMapping("/edit/{id}")
    public String editUser(@PathVariable int id, ModelMap modelMap) {
        Optional<User> userById = userService.findById(id);

        if (userById.isEmpty()) {
            return "redirect:/users";
        }

        User user = userById.get();
        modelMap.addAttribute("user", user);
        return "user/edit-user";

    }

    @GetMapping("/delete/{id}")
    public String deleteUser(@PathVariable int id) {

        Optional<User> userById = userService.findById(id);

        if (userById.isPresent()) {
            userService.deleteById(id);
        }

        return "redirect:/users";
    }
}

