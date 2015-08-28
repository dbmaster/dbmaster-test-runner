import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import com.google.inject.Injector;

import com.branegy.util.InjectorUtil;

import org.slf4j.Logger;


def wildcards = [];
for (String line:p_tests.split("\n")){
    if (!line.trim().isEmpty()){
        wildcards.add(line.trim());
    }
}

if (wildcards.empty){
    return;
}

String firstLine;
if (p_config != null) {
    firstLine = p_config;
} else {
    firstLine = wildcards.get(0).replace('\\','/');
    if (firstLine.contains("/src/test/")) {
        firstLine = firstLine.substring(0,firstLine.indexOf("/src/test/"));
    } else if (firstLine.contains("/src/main/")) {
        firstLine = firstLine.substring(0,firstLine.indexOf("/src/main/"));
    } else {
        return;
    }
    firstLine = firstLine+"/src/test/resources/test.properties";
}

Map<String,String> properties = new HashMap<String, String>();
new File(firstLine).withInputStream { stream ->
      def props = new Properties()
      props.load(stream)
      properties.putAll(props);
}


ClassLoader parent = InjectorUtil.getInjector().getClass().getClassLoader();
GroovyClassLoader gcl = new GroovyClassLoader(parent);

gcl.addURL(getClass().getResource("extra/jcommander.jar"));
gcl.addURL(getClass().getResource("extra/bsh.jar"));
gcl.addURL(getClass().getResource("extra/testng.jar"));
Class baseTestCl =
gcl.parseClass(new GroovyCodeSource(getClass().getResource("io/dbmaster/testng/BaseTestNGCase.groovy")));
gcl.parseClass(new GroovyCodeSource(getClass().getResource("io/dbmaster/testng/OverridePropertyNames.groovy")));
gcl.parseClass(new GroovyCodeSource(getClass().getResource("io/dbmaster/testng/BaseServiceTestNGCase.groovy")));
gcl.parseClass(new GroovyCodeSource(getClass().getResource("io/dbmaster/testng/BaseToolTestNGCase.groovy")));

Field f;
f = baseTestCl.getDeclaredField("injector");
f.setAccessible(true);
f.set(null,InjectorUtil.getInjector());

f = baseTestCl.getDeclaredField("properties");
f.setAccessible(true);
f.set(null,properties);

def testFiles = [];
/*
WildcardFileFilter filter = new WildcardFileFilter(wildcards);
*/
for (String line:wildcards){
        testFiles.add(new File(line))
}
    
def testClasses = [];
for (File file:testFiles){
    testClasses.add(gcl.parseClass(file));
}

String css = getClass().getResource("testng.css").getText("utf-8");

Object runner = gcl.parseClass(new GroovyCodeSource(getClass().getResource("TestRunnerNG.groovy"))).newInstance();
println runner.run(css, logger, testClasses as Class[]);
