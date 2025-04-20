package com.github.quixotic95.brokerfirmchallenge.aspect;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface AuthorizeEndpoint {
    /**
     * Should this endpoint be accessible by a CUSTOMER?
     * If false, only ADMINs can access it.
     */
    boolean customerAccessible() default false;

    /**
     * Should the CUSTOMER be allowed only if the data belongs to them?
     * Expects Long-type method parameters like customerId or orderId.
     */
    boolean checkOwnership() default false;
}
