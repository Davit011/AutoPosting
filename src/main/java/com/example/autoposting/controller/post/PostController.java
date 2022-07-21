package com.example.autoposting.controller.post;

import com.example.autoposting.dto.SavePostRequest;
import com.example.autoposting.model.Post;
import com.example.autoposting.model.User;
import com.example.autoposting.service.PostService;
import com.example.autoposting.service.StatusService;
import com.example.autoposting.service.UserService;
import com.example.autoposting.util.SaveFbPostUtil;
import com.example.autoposting.util.SaveInstagramPostUtil;
import lombok.RequiredArgsConstructor;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
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
    private final SaveInstagramPostUtil saveInstagramPostUtil;
    private final StatusService statusService;


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
        modelMap.addAttribute("users", userService.findAllUsersByStatus());
        modelMap.addAttribute("canada",userService.findAllCanadaCategoryAndStatus());
        modelMap.addAttribute("loan",userService.findAllByLoanCategoryAndStatus());
        return "user/choose-users";
    }

    @PostMapping("/save")
    public String savePost(@ModelAttribute SavePostRequest savePostRequest, ModelMap modelMap) throws IOException, ParseException {

        int[] profiles = savePostRequest.getProfiles();
        String[] postType = savePostRequest.getPostType();
        for (int i = 0; i < profiles.length; i++) {
            switch (postType[i]) {
                case "instagram":
                    saveInstagramPostUtil.saveInstaPost(savePostRequest,i);
                    break;
                case "facebook":
                    saveFbPostUtil.saveFbPost(savePostRequest,i);
                    break;
                case "both":
                    saveFbPostUtil.saveFbPost(savePostRequest,i);
                    saveInstagramPostUtil.saveInstaPost(savePostRequest,i);
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
    @GetMapping("/delete/{id}")
    public String deletePost(@PathVariable int id) throws IOException {
        OkHttpClient client = new OkHttpClient().newBuilder()
                .build();
        MediaType mediaType = MediaType.parse("text/plain");
        okhttp3.RequestBody body = RequestBody.create(mediaType, "");
        Optional<Post> postById = postService.findById(id);
        if(postById.isPresent()){
            Post post = postById.get();
            String creationId = post.getCreationId();
            Optional<User> userById = userService.findById(post.getUser().getId());
            if(creationId.contains("_")){
                if(userById.isPresent()){
                    statusService.deleteByPostId(post.getId());
                    Request request1 = new Request.Builder()
                            .url("https://graph.facebook.com/" + post.getCreationId() + "?access_token=" + userById.get().getToken())
                            .method("DELETE", body)
                            .build();
                    Response response1 = client.newCall(request1).execute();
                    response1.close();
                    postService.deleteById(id);
                }

            }
        }
        return "redirect:/posts";
    }

    @PostMapping("/delete")
    public String deleteAllByIds(@RequestParam("removedPost[]") String[] ids) throws IOException {
        for (String id : ids) {
            deletePost(Integer.parseInt(id));
        }
        return "redirect:/posts";
    }

    @PostMapping("/filter")
    public String filterByDate(@RequestParam String date,ModelMap modelMap) throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date parse1 = sdf.parse(date);
        System.out.println(parse1);
        String format = sdf.format(parse1);
//        LocalDateTime localDate = parse1.toInstant()
//                .atZone(ZoneId.systemDefault())
//                .toLocalDateTime();
        List<Post> posts = postService.filterByDate(format);
        modelMap.addAttribute("posts",posts);
        return "post/all-posts";
    }
}
