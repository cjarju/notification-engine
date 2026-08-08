package com.example.account.common.constants;

public final class ApiPaths {

    private ApiPaths() {}

    public static final String USERS = "/api/" + ServiceInfo.VERSION + "/users";
    public static final String USER = USERS + "/{id}";
    public static final String USER_PREFERENCES = USERS + "/{userId}/preferences";
    public static final String USER_PREFERENCE = USER_PREFERENCES + "/{preferenceId}";
    public static final String SWAGGER_UI = "/swagger-ui.html";
}
