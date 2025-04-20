package com.github.quixotic95.brokerfirmchallenge.aspect;

import com.github.quixotic95.brokerfirmchallenge.exception.InsufficientException;
import com.github.quixotic95.brokerfirmchallenge.exception.error.ErrorCode;
import com.github.quixotic95.brokerfirmchallenge.model.Customer;
import com.github.quixotic95.brokerfirmchallenge.security.SecurityUtil;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import java.lang.reflect.Parameter;

@Aspect
@Component
@Slf4j
public class AuthorizationAspect {

    @Before("@annotation(authorize)")
    public void authorizeAccess(JoinPoint joinPoint, AuthorizeEndpoint authorize) {
        Customer currentUser = SecurityUtil.getCurrentCustomer();
        Long currentCustomerId = currentUser.getId();
        boolean isAdmin = SecurityUtil.isAdmin();

        if (!authorize.customerAccessible() && !isAdmin) {
            throw new InsufficientException(ErrorCode.UNAUTHORIZED_ACCESS);
        }

        if (authorize.checkOwnership() && !isAdmin) {
            Object[] args = joinPoint.getArgs();
            Parameter[] parameters = ((MethodSignature) joinPoint.getSignature()).getMethod()
                    .getParameters();

            for (int i = 0; i < args.length; i++) {
                if (args[i] instanceof Long id) {
                    String paramName = parameters[i].getName()
                            .toLowerCase();
                    if (paramName.contains("customer") || paramName.contains("order")) {
                        if (id.equals(currentCustomerId)) {
                            return;
                        }
                    }
                }
            }

            throw new InsufficientException(ErrorCode.UNAUTHORIZED_ACCESS);
        }
    }
}
