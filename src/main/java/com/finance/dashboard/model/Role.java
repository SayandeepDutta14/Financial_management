package com.finance.dashboard.model;

public enum Role {
    VIEWER,    // read-only dashboard access
    ANALYST,   // read + summary/insights access
    ADMIN      // full CRUD + user management
}
