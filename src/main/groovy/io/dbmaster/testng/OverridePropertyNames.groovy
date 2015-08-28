package io.dbmaster.testng;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Inherited
@interface OverridePropertyNames {
    String username() default 'username'; //BaseServiceTestNGCase.USERNAME;
    String password() default 'password'; //BaseServiceTestNGCase.PASSWORD;
    String project()  default 'project'; //BaseServiceTestNGCase.PROJECT;
}