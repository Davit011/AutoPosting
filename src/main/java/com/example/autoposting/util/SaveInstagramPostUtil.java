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
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class SaveInstagramPostUtil {

    private final UserService userService;
    private final PostService postService;
    private final StatusService statusService;

    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");


    public void saveInstaPost(SavePostRequest savePostRequest, int index) throws IOException {
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
                .build();
        try{
            MediaType mediaType = MediaType.parse("text/plain");
            RequestBody body = RequestBody.create(mediaType, "");
            Request request = new Request.Builder()
                    .url("https://graph.facebook.com/" + user.getInstagramId() + "/media?image_url=" + savePostRequest.getUrl() + "&caption=" + savePostRequest.getMessage() + "&access_token=" + user.getToken())
                    .method("POST", body)
                    .build();
            Response response = client.newCall(request).execute();
            String creationResponse = response.body().string();
            String creationId = creationResponse.split(":")[1].substring(1, creationResponse.split(":")[1].length() - 2);
            response.close();
            System.out.println(response);
            Request request1 = new Request.Builder()
                    .url("https://graph.facebook.com/" + user.getInstagramId() + "/media_publish?creation_id=" + creationId + "&access_token=" + user.getToken())
                    .method("POST", body)
                    .build();
            Response response1 = client.newCall(request1).execute();
            System.out.println(response1 + " Insta");
            String creationResponse1 = response1.body().string();
            String creationId1 = creationResponse1.split(":")[1].substring(1, creationResponse1.split(":")[1].length() - 2);
            response1.close();
            boolean successful = response1.isSuccessful();
            Post savedPost = postService.save(Post.builder()
                    .text("a")
                    .imgUrl(savePostRequest.getUrl())
                    .status(response1.code())
                    .createdDate(sdf.format(new Date()))
                    .user(user)
                    .creationId(creationId1)
                    .type("Instagram")
                    .build());
            if (successful) {
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
                        .text(response1.message())
                        .createdDate(LocalDateTime.now())
                        .status(response1.code())
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
