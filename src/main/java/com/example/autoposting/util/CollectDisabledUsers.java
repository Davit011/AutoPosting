package com.example.autoposting.util;

import com.example.autoposting.model.DisabledUser;
import com.example.autoposting.model.User;
import com.example.autoposting.model.UserStatus;
import com.example.autoposting.service.DisabledUserService;
import com.example.autoposting.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class CollectDisabledUsers {

    private final UserService userService;
    private final DisabledUserService disabledUserService;

    public void collectDisabledUsers() {

        List<User> allByStatus = userService.findAllByStatus(UserStatus.DISABLED);
        for (User byStatus : allByStatus) {
            Optional<DisabledUser> byprofileId = disabledUserService.findByprofileId(byStatus.getProfileId());
            if (byprofileId.isEmpty()) {
                disabledUserService.save(DisabledUser.builder()
                        .name(byStatus.getName())
                        .profileId(byStatus.getProfileId())
                        .build());
            }
        }
    }

    public void disableUsers() {
        List<DisabledUser> all = disabledUserService.findAll();
        for (DisabledUser disabledUser : all) {
            User userByProfileId = userService.findUserByProfileId(disabledUser.getProfileId());
            userByProfileId.setStatus(UserStatus.DISABLED);
            userService.save(userByProfileId);

        }
    }
}
