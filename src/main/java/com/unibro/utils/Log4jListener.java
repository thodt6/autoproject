/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.unibro.utils;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import org.apache.log4j.PropertyConfigurator;

/**
 *
 * @author Only Love
 */
public class Log4jListener implements ServletContextListener {

    public void contextDestroyed(ServletContextEvent arg0) {
    }

    public void contextInitialized(ServletContextEvent sce) {
        ServletContext servletContext = sce.getServletContext();
        String log4jFile = servletContext.getRealPath("/") + "/" + servletContext.getInitParameter("log4jConfigLocation");
        PropertyConfigurator.configure(log4jFile);
    }
}
