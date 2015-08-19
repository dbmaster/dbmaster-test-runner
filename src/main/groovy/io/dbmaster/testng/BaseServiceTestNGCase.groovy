package io.dbmaster.testng;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;

import org.testng.annotations.AfterSuite;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.BeforeTest;

import com.branegy.service.core.ISecurityContext;

public abstract class BaseServiceTestNGCase extends BaseTestNGCase {
    public static final String USERNAME = "username";
    public static final String PASSWORD = "password";
    public static final String PROJECT  = "project";
    
    @BeforeClass
    public final void injectMembers(){
        getInjector().injectMembers(this);
    }
    
    @BeforeTest
    public final void beforeTest(){
        getInjector().getInstance(EntityManager.class).getTransaction().begin();
    }
    
    @AfterTest
    public final void afterTest(){
        EntityTransaction et = getInjector().getInstance(EntityManager.class).getTransaction();
        if (et.isActive()){
            if (et.getRollbackOnly()){
                et.rollback();
            } else {
                et.commit();
            }
        }
    }
    
    @BeforeSuite
    public final static void beforeSuite(){
        ISecurityContext context = getInjector().getInstance(ISecurityContext.class);
        if (!context.getCurrentUser().getUserId().equals(getTestProperty(USERNAME))){
            throw new IllegalArgumentException("User ${getTestProperty(USERNAME)} is required");
        }
        if (!context.getCurrentProject().getName().equals(getTestProperty(PROJECT))){
            throw new IllegalArgumentException("Project ${getTestProperty(PROJECT)} is required");
        }
        EntityTransaction et = getInjector().getInstance(EntityManager.class).getTransaction();
        if (et.isActive()){
            et.commit();
        }
    }
    
    @AfterSuite
    public final static void afterSuite(){
    }
    
}
