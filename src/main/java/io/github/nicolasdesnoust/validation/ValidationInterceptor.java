package io.github.nicolasdesnoust.validation;

import net.bytebuddy.implementation.bind.annotation.AllArguments;
import net.bytebuddy.implementation.bind.annotation.Origin;

import javax.validation.*;
import javax.validation.Path.Node;
import javax.validation.Validator;
import javax.validation.Path.ParameterNode;
import java.lang.reflect.Constructor;
import java.util.Set;
import java.util.stream.Collectors;

public class ValidationInterceptor {

    private static final Validator validator = Validation.buildDefaultValidatorFactory()
            .getValidator();

    public static <T> void validate(@Origin Constructor<T> constructor, @AllArguments Object[] args) {
        Set<ConstraintViolation<T>> violations = validator.forExecutables()
                .validateConstructorParameters(constructor, args);

        if (!violations.isEmpty()) {
            String message = violations.stream()
                    .sorted(ValidationInterceptor::compare)
                    .map(cv -> getParameterName(cv) + " - " + cv.getMessage())
                    .collect(Collectors.joining(System.lineSeparator()));

            throw new ConstraintViolationException("Invalid instantiation of record type " + constructor.getDeclaringClass().getSimpleName() + System.lineSeparator() + message, violations);
        }
    }

    private static int compare(ConstraintViolation<?> o1, ConstraintViolation<?> o2) {
        return Integer.compare(getParameterIndex(o1), getParameterIndex(o2));
    }

    private static String getParameterName(ConstraintViolation<?> cv) {
        for (Node node : cv.getPropertyPath()) {
            if (node.getKind() == ElementKind.PARAMETER) {
                return node.getName();
            }
        }

        return "";
    }

    private static int getParameterIndex(ConstraintViolation<?> cv) {
        for (Node node : cv.getPropertyPath()) {
            if (node.getKind() == ElementKind.PARAMETER) {
                return node.as(ParameterNode.class).getParameterIndex();
            }
        }

        return -1;
    }
}
