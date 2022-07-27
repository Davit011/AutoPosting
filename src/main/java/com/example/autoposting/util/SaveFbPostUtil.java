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
import java.net.SocketTimeoutException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

@Component
@RequiredArgsConstructor
public class SaveFbPostUtil {

    private final UserService userService;
    private final PostService postService;
    private final StatusService statusService;

    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

    public void saveFbPost(SavePostRequest savePostRequest, int index) throws IOException, ParseException {

        int[] profiles = savePostRequest.getProfiles();
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
            return;
        }

        User user = userById.get();
        OkHttpClient client = new OkHttpClient().newBuilder()
                .connectTimeout(60, TimeUnit.SECONDS)
                .readTimeout(60, TimeUnit.SECONDS)
                .writeTimeout(60, TimeUnit.SECONDS)
                .build();
        MediaType mediaType = MediaType.parse("text/plain");
        RequestBody body = RequestBody.create(mediaType, "");
//        System.out.println("https://graph.facebook.com/" + user.getProfileId() + "/photos?url=" + savePostRequest.getUrl() + "&message=" + savePostRequest.getMessage() + "&access_token=" + user.getToken());
        Request request = new Request.Builder()
                .url("https://graph.facebook.com/" + user.getProfileId() + "/photos?url=" + savePostRequest.getUrl() + "&message=" + savePostRequest.getMessage() + "&access_token=" + user.getToken())
                .method("POST", body)
                .build();
//        System.out.println(request);
        try{
            Response response = client.newCall(request).execute();

            String creationResponse = response.body().string();
            String creationId = creationResponse.split(",")[1].split(":")[1].substring(1, creationResponse.split(",")[1].split(":")[1].length() - 2);
            response.close();
            System.out.println(response + " fb");
            boolean successful = response.isSuccessful();
            Post savedPost = postService.save(Post.builder()
                    .text("a")
                    .imgUrl(savePostRequest.getUrl())
                    .status(response.code())
                    .createdDate(sdf.format(new Date()))
                    .user(user)
                    .creationId(creationId)
                    .type("Facebook")
                    .build());
            if (successful){
                statusService.save(Status.builder()
                        .text("ok")
                        .createdDate(LocalDateTime.now())
                        .profileId(user.getProfileId())
                        .status(200)
                        .token(user.getToken())
                        .post(savedPost)
                        .build());
            } else {
                statusService.save(Status.builder()
                        .text(response.message())
                        .createdDate(LocalDateTime.now())
                        .status(response.code())
                        .profileId(user.getProfileId())
                        .token(user.getToken())
                        .post(savedPost)
                        .build());
            }
        }catch(SocketTimeoutException e){
            System.out.println(e.getMessage());
            return;
        }
    }
}
