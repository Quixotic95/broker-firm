package com.github.quixotic95.brokerfirmchallenge.security;

import com.github.quixotic95.brokerfirmchallenge.enums.CustomerRole;
import com.github.quixotic95.brokerfirmchallenge.model.Customer;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public class SecurityUtil {

    public static Customer getCurrentCustomer() {
        Authentication auth = SecurityContextHolder.getContext()
                .getAuthentication();
        if (auth != null && auth.getPrincipal() instanceof CustomUserDetails userDetails) {
            return userDetails.getCustomer();
        }
        throw new IllegalStateException("No authenticated user found");
    }

    public static Long getCustomerId() {
        return getCurrentCustomer().getId();
    }

    public static boolean isAdmin() {
        return getCurrentCustomer().getRole() == CustomerRole.ADMIN;
    }
}
