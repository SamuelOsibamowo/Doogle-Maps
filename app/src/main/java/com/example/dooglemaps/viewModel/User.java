package com.example.dooglemaps.viewModel;

public class User {

    String name, username, email, password, token;

    public User() {}

    public User(String name, String username, String email, String password, String token) {
        this.name = name;
        this.username = username;
        this.email = email;
        this.password = password;
        this.token = token;
    }

    public String getName() {
        return name;
    }

    public String getUsername() {
        return username;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
