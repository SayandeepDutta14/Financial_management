package com.finance.dashboard.dto;

import com.finance.dashboard.model.Role;
import com.finance.dashboard.model.User;
import lombok.Data;
import java.time.LocalDateTime;

@Data
public class UserResponse {
    private Long id;
    private String username;
    private String email;
    private Role role;
    private boolean active;
    private LocalDateTime createdAt;

    public static UserResponse from(User user) {
        UserResponse r = new UserResponse();
        r.id = user.getId();
        r.username = user.getUsername();
        r.email = user.getEmail();
        r.role = user.getRole();
        r.active = user.isActive();
        r.createdAt = user.getCreatedAt();
        return r;
    }
}
