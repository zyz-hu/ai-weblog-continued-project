package com.zyz.weblog.auth.model;

import java.util.List;

public class UserInfoResponse {
    private String username;
    private List<String> roles;
    private List<String> permissions;

    public UserInfoResponse() {
    }

    public UserInfoResponse(String username, List<String> roles, List<String> permissions) {
        this.username = username;
        this.roles = roles;
        this.permissions = permissions;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public List<String> getRoles() {
        return roles;
    }

    public void setRoles(List<String> roles) {
        this.roles = roles;
    }

    public List<String> getPermissions() {
        return permissions;
    }

    public void setPermissions(List<String> permissions) {
        this.permissions = permissions;
    }
}
