/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ConnectBean;

import java.sql.DriverManager;
import java.sql.Connection;
import javax.servlet.ServletContext;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;


/**
 *
 * @author astell
 */
public class ConnectionAuxiliary {
    
    private static final Logger logger = Logger.getLogger(ConnectBean.class);

    public ConnectionAuxiliary() {
        //Set up logger        
        logger.setLevel(Level.DEBUG);
        PropertyConfigurator.configure("/root/logs/log4j_ensat.properties");        
    }

    public Connection getAuxiliaryConnection(ServletContext context, String dbType) {

        String versionParam = "";
        String dbParamName = "";
        String dbName = "";
        String driverName = "";
        String serverName = "";
        String port = "";
        String username = "";
        String password = "";

        if (context != null) {

            versionParam = context.getInitParameter("version");
            if (dbType.equals("main")) {
                if (versionParam.equals("test")) {
                    dbParamName = "db_name_test";
                } else {
                    dbParamName = "db_name_prod";
                }
            } else if (dbType.equals("security")) {
                if (versionParam.equals("test")) {
                    dbParamName = "security_db_name_test";
                } else {
                    dbParamName = "security_db_name_prod";
                }
            } else if (dbType.equals("parameters")) {
                dbParamName = "parameter_db_name";
            } else if (dbType.equals("center_callout")) {
                dbParamName = "center_callout_db_name";
            }

            dbName = context.getInitParameter(dbParamName);
            driverName = context.getInitParameter("driver_name");
            serverName = context.getInitParameter("server_name");
            port = context.getInitParameter("port");
            username = context.getInitParameter("username");
            password = context.getInitParameter("password");

        }

        String connectionURL = "jdbc:mysql://" + serverName + ":" + port + "/" + dbName;
        Connection conn = null;
        try {
            Class.forName(driverName).newInstance();
            conn = DriverManager.getConnection(connectionURL, username, password);
        } catch (Exception e) {
            logger.debug("Database connection error: " + e.getMessage());
        }
        return conn;
    }
    
    public Connection getAuxiliaryConnection(String dbName, String driverName, String serverName, String port, String username, String password) {

        String connectionURL = "jdbc:mysql://" + serverName + ":" + port + "/" + dbName;
        Connection conn = null;
        try {
            Class.forName(driverName).newInstance();
            conn = DriverManager.getConnection(connectionURL, username, password);
        } catch (Exception e) {
            logger.debug("Database connection error: " + e.getMessage());
        }
        return conn;
    }
}
