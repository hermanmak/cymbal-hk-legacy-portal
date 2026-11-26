package com.cymbalhk.portal;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;

/**
 * Cymbal HK Legacy Merchant Portal (2016).
 * Packaged as a WAR for Apache Tomcat 8.5 on CentOS 7.
 */
@SpringBootApplication
public class CymbalPortalApplication extends SpringBootServletInitializer {

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        return application.sources(CymbalPortalApplication.class);
    }

    public static void main(String[] args) {
        SpringApplication.run(CymbalPortalApplication.class, args);
    }
}
