package com.finance.dashboard.dto;

import com.finance.dashboard.model.Role;
import lombok.Data;

@Data
public class UpdateUserRequest {
    private Role role;
    private Boolean active;
}
