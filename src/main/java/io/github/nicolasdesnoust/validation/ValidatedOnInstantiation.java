package io.github.nicolasdesnoust.validation;


import java.lang.annotation.*;

@Documented
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidatedOnInstantiation {
}
