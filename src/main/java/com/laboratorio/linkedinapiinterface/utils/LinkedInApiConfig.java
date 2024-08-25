package com.laboratorio.linkedinapiinterface.utils;

import java.io.FileReader;
import java.util.Properties;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 *
 * @author Rafael
 * @version 1.0
 * @created 16/08/2024
 * @updated 24/08/2024
 */
public class LinkedInApiConfig {
    private static final Logger log = LogManager.getLogger(LinkedInApiConfig.class);
    private static LinkedInApiConfig instance;
    private final Properties properties;

    private LinkedInApiConfig() {
        properties = new Properties();
        loadProperties();
    }

    private void loadProperties() {
        try {
            this.properties.load(new FileReader("config//linkedin_api.properties"));
        } catch (Exception e) {
            log.error("Ha ocurrido un error leyendo el fichero de configuración del API de LinkedIn. Finaliza la aplicación!");
            log.error(String.format("Error: %s", e.getMessage()));
            if (e.getCause() != null) {
                log.error(String.format("Causa: %s", e.getCause().getMessage()));
            }
            System.exit(-1);
        }
    }

    public static LinkedInApiConfig getInstance() {
        if (instance == null) {
            synchronized (LinkedInApiConfig.class) {
                if (instance == null) {
                    instance = new LinkedInApiConfig();
                }
            }
        }
        
        return instance;
    }

    public String getProperty(String key) {
        return properties.getProperty(key);
    }
}