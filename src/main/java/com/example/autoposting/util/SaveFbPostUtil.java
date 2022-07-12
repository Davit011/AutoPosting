package com.example.autoposting.util;

import com.example.autoposting.dto.SavePostRequest;
import com.example.autoposting.model.Post;
import com.example.autoposting.model.Status;
import com.example.autoposting.model.User;
import com.example.autoposting.service.PostService;
import com.example.autoposting.service.StatusService;
import com.example.autoposting.service.UserService;
import lombok.RequiredArgsConstructor;
import okhttp3.*;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class SaveFbPostUtil {

    private final UserService userService;
    private final PostService postService;
    private final StatusService statusService;


    public void saveFbPost(SavePostRequest savePostRequest, int index) throws IOException {
        int[] profiles = savePostRequest.getProfiles();
        String[] postType = savePostRequest.getPostType();

        int profile = profiles[index];
        Optional<User> userById = userService.findById(profile);
        if (userById.isEmpty()) {
            statusService.save(Status.builder()
                    .status(404)
                    .profileId(String.valueOf(profile))
                    .createdDate(LocalDateTime.now())
                    .token(null)
                    .text("User With id " + profile + "not found")
                    .build());
        }

        User user = userById.get();
//        OkHttpClient client = new OkHttpClient().newBuilder()
//                .build();
//        MediaType mediaType = MediaType.parse("text/plain");
//        RequestBody body = RequestBody.create(mediaType, "");
//        Request request = new Request.Builder()
//                .url("https://graph.facebook.com/" + user.getProfileId() + "/photos?url=" + savePostRequest.getUrl() + "&message=" + savePostRequest.getMessage() + "&access_token=" + user.getToken())
//                .method("POST", body)
//                .build();
//        Response response = client.newCall(request).execute();
//        System.out.println(response);
//        boolean successful = response.isSuccessful();
//
//        Post savedPost = postService.save(Post.builder()
//                .text(savePostRequest.getMessage())
//                .imgUrl(savePostRequest.getUrl())
//                .status(response.code())
//                .build());
//        if (successful) {
//            statusService.save(Status.builder()
//                    .text("ok")
//                    .createdDate(LocalDateTime.now())
//                    .profileId(user.getProfileId())
//                    .status(200)
//                    .token(user.getToken())
//                    .post(savedPost)
//                    .build());
//        } else {
//            statusService.save(Status.builder()
//                    .text(response.message())
//                    .createdDate(LocalDateTime.now())
//                    .status(response.code())
//                    .profileId(user.getProfileId())
//                    .token(user.getToken())
//                    .post(savedPost)
//                    .build());
//        }
    }
}