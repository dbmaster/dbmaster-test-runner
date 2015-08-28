package io.dbmaster.testng;

import java.io.File;

import io.dbmaster.testng.OverridePropertyNames;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;

import org.osgi.framework.Bundle;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.BeforeTest;

import com.branegy.service.base.api.ProjectService;
import com.branegy.service.core.ISecurityContext;
import com.branegy.service.standalone.api.IStandaloneAuthenticator;
import com.branegy.tools.osgi.OsgiToolBootstrapper;

@OverridePropertyNames
public abstract class BaseServiceTestNGCase extends BaseTestNGCase {
    public static final String USERNAME = "username";
    public static final String PASSWORD = "password";
    public static final String PROJECT  = "project";
    
    @BeforeClass
    public final void injectMembers(){
        OverridePropertyNames config = getClass().getAnnotation(OverridePropertyNames.class);
        
        ISecurityContext ctx = getInstance(ISecurityContext.class);

        IStandaloneAuthenticator auth = getInstance(IStandaloneAuthenticator.class);
        String usernameValue = getTestProperty(config.username());
        String projectValue = getTestProperty(config.project());

        if (!ctx.getCurrentUser().getUserName().equals(usernameValue)) {
           throw new IllegalArgumentException("User ${usernameValue} is required");
        }

        if (!ctx.getCurrentProject().getName().equals(projectValue)) {
             throw new IllegalArgumentException("Project ${projectValue} is required");
        }
        
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
    
    protected final Bundle getBundle() {
        return getInstance(OsgiToolBootstrapper.class).getBundleContext().getBundle(bundleId);
    }
    
    protected final File getBaseDir() {
        return new File(System.getProperty(ARTIFACT_BUNDLE_PATH)).getParentFile().getParentFile();
    }

    protected final File getResourcesDir() {
        return new File(getBaseDir(), "src/main/resources");
    }

    protected final File getTestResourcesDir() {
        return new File(getBaseDir(), "src/test/resources");
    }
}
