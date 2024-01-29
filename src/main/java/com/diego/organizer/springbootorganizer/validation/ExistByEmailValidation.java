package com.diego.organizer.springbootorganizer.validation;

import org.springframework.beans.factory.annotation.Autowired;

import com.diego.organizer.springbootorganizer.services.UserService;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class ExistByEmailValidation implements ConstraintValidator<ExistByEmail, String>{

    @Autowired
    private UserService userService;

    @Override
    public boolean isValid(String email, ConstraintValidatorContext context) {
        if (userService == null) { // si no se inyecta el servicio, no se valida
            return true;
        }
        return !this.userService.existByEmail(email);    }

}
