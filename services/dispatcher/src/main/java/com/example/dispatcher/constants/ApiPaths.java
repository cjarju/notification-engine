package com.example.dispatcher.constants;

public final class ApiPaths {

    private ApiPaths() {}

    public static final String SWAGGER_UI = "/swagger-ui.html";
    public static final String DISPATCH = "/api/" + ServiceInfo.VERSION + "/dispatch";
    public static final String USERS = "/api/" + ServiceInfo.VERSION + "/users";
    public static final String USER_PREFERENCES = USERS + "/{userId}/preferences";
}
