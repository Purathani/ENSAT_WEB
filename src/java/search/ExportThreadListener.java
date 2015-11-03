/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package search;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import java.util.concurrent.ThreadPoolExecutor;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

/**
 *
 * @author astell
 */
public class ExportThreadListener implements ServletContextListener {

    private static final Logger logger = Logger.getLogger("rootLogger");    
    private static ExportThreadPoolExecutor etpe = null;    
    
    public static void setLogger(){
    }

    @Override
    public void contextInitialized(ServletContextEvent sce) {            
        ExportThreadListener.setLogger();
        logger.debug("ExportThreadPool started...");        
        etpe = new ExportThreadPoolExecutor();
        
        ServletContext context = sce.getServletContext();
        context.setAttribute("export_thread_pool", etpe);        
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {    
        ExportThreadListener.setLogger();
        logger.debug("ExportThreadPool destroyed...");
        etpe.shutDown();
    }
}
