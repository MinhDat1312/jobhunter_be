package vn.minhdat.jobhunter_be.util.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import vn.minhdat.jobhunter_be.entity.Recruiter;
import vn.minhdat.jobhunter_be.util.annotation.RequireAddressIfRecruiter;

public class RequireAddressIfRecruiterValidator implements ConstraintValidator<RequireAddressIfRecruiter, Object> {

    public RequireAddressIfRecruiterValidator() {
    }

    @Override
    public boolean isValid(Object o, ConstraintValidatorContext constraintValidatorContext) {
        if(o instanceof Recruiter recruiter){
            if (recruiter.getAddress() == null || recruiter.getAddress().isEmpty()) {
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

