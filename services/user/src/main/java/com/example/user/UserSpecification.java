package com.example.user;


import org.springframework.data.jpa.domain.Specification;

public final class UserSpecification {

    private UserSpecification() {}

    public static Specification<User> withCriteria(UserSearchCriteria criteria) {

        return Specification
                .where(usernameContains(criteria.username()))
                .and(emailContains(criteria.email()))
                .and(isActive(criteria.active()));
    }

    private static Specification<User> usernameContains(String username) {
        return (root, query, cb) -> {

            if (username == null || username.isBlank()) {
                return null;
            }

            return cb.like(
                    cb.lower(root.get("username")),
                    "%" + username.toLowerCase() + "%"
            );
        };
    }

    private static Specification<User> emailContains(String email) {
        return (root, query, cb) -> {

            if (email == null || email.isBlank()) {
                return null;
            }

            return cb.like(
                    cb.lower(root.get("email")),
                    "%" + email.toLowerCase() + "%"
            );
        };
    }

    private static Specification<User> isActive(Boolean active) {
        return (root, query, cb) -> {

            if (active == null) {
                return null;
            }

            return cb.equal(root.get("active"), active);
        };
    }
}
