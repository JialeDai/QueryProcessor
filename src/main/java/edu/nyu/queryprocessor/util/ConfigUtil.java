package edu.nyu.queryprocessor.util;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class ConfigUtil {
    public String getConfig(String key) throws IOException {
        Properties props = new Properties();
        props.load(new FileInputStream("src/main/resources/config.properties"));
        return props.getProperty(key);
    }
}
