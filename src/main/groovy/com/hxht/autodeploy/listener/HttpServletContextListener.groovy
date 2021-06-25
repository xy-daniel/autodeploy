package com.hxht.autodeploy.listener

import org.apache.commons.logging.Log
import org.apache.commons.logging.LogFactory

import javax.servlet.ServletContextEvent
import javax.servlet.ServletContextListener
import java.sql.Driver
import java.sql.DriverManager
import java.sql.SQLException

/**
 * Created by Voyager on 2016/11/30.
 */
class HttpServletContextListener implements ServletContextListener{
    private static final Log log = LogFactory.getLog(HttpServletContextListener.class);

    @Override
    void contextInitialized(ServletContextEvent servletContextEvent) {

    }

    @Override
    void contextDestroyed(ServletContextEvent servletContextEvent) {
        // This manually deregisters JDBC driver, which prevents Tomcat 7 from complaining about memory leaks wrto this class
        Enumeration<Driver> drivers = DriverManager.getDrivers()
        while (drivers.hasMoreElements()) {
            Driver driver = drivers.nextElement()
            try {
                DriverManager.deregisterDriver(driver)
                log.info(String.format("deregistering jdbc driver: %s", driver))
            } catch (SQLException e) {
                log.error(String.format("Error deregistering driver %s", driver), e)
            }

        }
    }
}
