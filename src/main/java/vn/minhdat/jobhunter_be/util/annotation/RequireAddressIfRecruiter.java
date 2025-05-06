package vn.minhdat.jobhunter_be.util.annotation;

import jakarta.validation.Constraint;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import jakarta.validation.Payload;
import vn.minhdat.jobhunter_be.entity.Recruiter;
import vn.minhdat.jobhunter_be.entity.User;

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

class RequireAddressIfRecruiterValidator implements ConstraintValidator<RequireAddressIfRecruiter, User> {
    @Override
    public boolean isValid(User o, ConstraintValidatorContext constraintValidatorContext) {
        if(o instanceof Recruiter recruiter){
            if (recruiter.getAddress() == null
                || recruiter.getAddress().getCity().isEmpty() || recruiter.getAddress().getCountry().isEmpty()
                || recruiter.getAddress().getDistrict().isEmpty() || recruiter.getAddress().getNumber().isEmpty()
                || recruiter.getAddress().getStreet().isEmpty() || recruiter.getAddress().getWard().isEmpty()
            ) {
                constraintValidatorContext.disableDefaultConstraintViolation();
                constraintValidatorContext
                        .buildConstraintViolationWithTemplate(
                                "Recruiter must provide an address or info about address is not empty")
                        .addPropertyNode("address")
                        .addConstraintViolation();
                return false;
            }
        }
        return true;
    }
}
