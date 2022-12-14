package io.github.nicolasdesnoust.validation;

import net.bytebuddy.build.Plugin;
import net.bytebuddy.description.method.MethodDescription;
import net.bytebuddy.description.method.ParameterDescription;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.dynamic.ClassFileLocator;
import net.bytebuddy.dynamic.DynamicType.Builder;
import net.bytebuddy.implementation.MethodDelegation;
import net.bytebuddy.implementation.SuperMethodCall;

import javax.validation.Constraint;
import java.io.IOException;

import static net.bytebuddy.matcher.ElementMatchers.annotationType;
import static net.bytebuddy.matcher.ElementMatchers.hasAnnotation;

public class ValidationWeavingPlugin implements Plugin {

    @Override
    public boolean matches(TypeDescription target) {
        return target.getDeclaredAnnotations()
                .isAnnotationPresent(ValidatedOnInstantiation.class)
                && target.getDeclaredMethods()
                .stream()
                .anyMatch(m -> m.isConstructor() && isConstrained(m));
    }

    @Override
    public Builder<?> apply(Builder<?> builder, TypeDescription typeDescription, ClassFileLocator classFileLocator) {
        return builder.constructor(this::isConstrained)
                .intercept(SuperMethodCall.INSTANCE.andThen(
                        MethodDelegation.to(ValidationInterceptor.class)));
    }

    private boolean isConstrained(MethodDescription method) {
        return hasConstrainedReturnValue(method) || hasConstrainedParameter(method);
    }

    private boolean hasConstrainedReturnValue(MethodDescription method) {
        return !method.getDeclaredAnnotations()
                .asTypeList()
                .filter(hasAnnotation(annotationType(Constraint.class)))
                .isEmpty();
    }

    private boolean hasConstrainedParameter(MethodDescription method) {
        return method.getParameters()
                .asDefined()
                .stream()
                .anyMatch(this::isConstrained);
    }

    private boolean isConstrained(ParameterDescription.InDefinedShape parameter) {
        return !parameter.getDeclaredAnnotations()
                .asTypeList()
                .filter(hasAnnotation(annotationType(Constraint.class)))
                .isEmpty();
    }

    @Override
    public void close() throws IOException {
    }
}