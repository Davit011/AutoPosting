package com.example.autoposting.controller.post;

import com.example.autoposting.dto.SavePostRequest;
import com.example.autoposting.model.Post;
import com.example.autoposting.model.User;
import com.example.autoposting.service.PostService;
import com.example.autoposting.service.UserService;
import com.example.autoposting.util.SaveFbPostUtil;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.temporal.ChronoField;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

@Controller
@RequiredArgsConstructor
@RequestMapping("/posts")
public class PostController {

    private final PostService postService;
    private final UserService userService;
    private final SaveFbPostUtil saveFbPostUtil;
    private final ModelMapper modelMapper;

    @GetMapping
    public String findAll(ModelMap modelMap) {
        modelMap.addAttribute("posts", postService.findAll());
        return "post/all-posts";
    }

    @GetMapping("/{id}")
    public String findById(@PathVariable int id, ModelMap modelMap) {
        Optional<Post> byId = postService.findById(id);
        if (byId.isEmpty()) {
            return "redirect:/posts";
        }
        modelMap.addAttribute("post", byId.get());
        return "post/single-post";
    }

    @GetMapping("/users")
    public String posting(ModelMap modelMap) {
        List<User> users = userService.findAll();
        modelMap.addAttribute("users", users);
        return "user/choose-users";
    }

    @PostMapping("/save")
    public String savePost(@ModelAttribute SavePostRequest savePostRequest, ModelMap modelMap) throws IOException, ParseException {

        int[] profiles = savePostRequest.getProfiles();
        String[] postType = savePostRequest.getPostType();
        for (int i = 0; i < profiles.length; i++) {
            switch (postType[i]) {
                case "instagram":
                    //add instagram posting logic
                    System.out.println("Instagram");
                    break;
                case "facebook":
                    saveFbPostUtil.saveFbPost(savePostRequest,i);
                    break;
                case "both":
                    saveFbPostUtil.saveFbPost(savePostRequest,i);
                    System.out.println("instagram part in case both");
                    //do instagram posting
            }
        }
        return "redirect:/posts";
    }

    @GetMapping("/edit/{id}")
    public String singlePost(@PathVariable int id, ModelMap modelMap){

        Optional<Post> byId = postService.findById(id);
        if(byId.isPresent()){
            Post post = byId.get();
            modelMap.addAttribute("post",post);
            return "post/single-post";
        }
        return "redirect:/posts";
    }

    @PostMapping("/filter")
    public String filterByDate(@RequestParam LocalDateTime date) throws ParseException {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        Date parse = formatter.parse(formatter.format(date));
        System.out.println(parse);
        return null;
    }
}
