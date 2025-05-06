package vn.minhdat.jobhunter_be.util.annotation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import vn.minhdat.jobhunter_be.util.validation.RequireAddressIfRecruiterValidator;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Constraint(validatedBy = RequireAddressIfRecruiterValidator.class)
@Documented
public @interface RequireAddressIfRecruiter {
    String message() default "Recruiter must provide an address";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}

