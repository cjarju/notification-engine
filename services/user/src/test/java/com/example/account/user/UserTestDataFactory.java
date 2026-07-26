package com.example.account.user;

import java.util.Arrays;
import java.util.List;
import java.util.stream.IntStream;

public final class UserTestDataFactory {

    private UserTestDataFactory() {
    }

    public static User activeUser(String username) {
        User user = new User();
        user.setUsername(username);
        user.setEmail(username + "@example.com");
        user.setPhoneNumber("+12025550100");
        return user;
    }

    public static List<User> activeUsers(String... usernames) {
        return Arrays.stream(usernames)
                .map(UserTestDataFactory::activeUser)
                .toList();
    }

    public static List<User> activeUsers(int count) {
        return IntStream.rangeClosed(1, count)
                .mapToObj(i -> activeUser("user" + i))
                .toList();
    }
}
