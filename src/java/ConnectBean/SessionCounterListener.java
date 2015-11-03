/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ConnectBean;

import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

/**
 *
 * @author astell
 */
public class SessionCounterListener implements HttpSessionListener {

    private static final Logger logger = Logger.getLogger("rootLogger");
        
    private static int totalActiveSessions;

    public static int getTotalActiveSession() {
        return totalActiveSessions;
    }
    
    public static void setLogger(String logFileName){        
        logger.setLevel(Level.DEBUG);
        PropertyConfigurator.configure(logFileName);        
    }

    @Override
    public void sessionCreated(HttpSessionEvent arg0) {        
        String logFileName = arg0.getSession().getServletContext().getInitParameter("log4j_property_file");        
        totalActiveSessions++;
        SessionCounterListener.setLogger(logFileName);
        logger.debug("New session created (total active sessions = " + totalActiveSessions + ")");        
    }

    @Override
    public void sessionDestroyed(HttpSessionEvent arg0) {
        totalActiveSessions--;
        //SessionCounterListener.setLogger();
        logger.debug("Session destroyed (total active sessions = " + totalActiveSessions + ")");        
    }
}
