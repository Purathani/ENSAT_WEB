/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ConnectBean;

/**
 *
 * @author anthony
 */
import javax.servlet.ServletContext;
import java.sql.*;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import org.apache.commons.dbcp2.BasicDataSource;

public class ConnectBean {

    private static final Logger logger = Logger.getLogger(ConnectBean.class);
    private Connection conn, secConn, paramConn, ccConn;

    public ConnectBean() {        
    }
    
    public void setConnection(String dbName, String driverName, String serverName, String port, String username, String password){
        
        String connectionURL = "jdbc:mysql://" + serverName + ":" + port + "/" + dbName;
        try {
            Class.forName(driverName).newInstance();
            conn = DriverManager.getConnection(connectionURL,username,password);
        } catch (Exception e) {
            logger.debug("Database connection error: " + e.getMessage());
        }
    }

    public void setConnection(ServletContext context) {

        String versionParam = context.getInitParameter("version");
        String dbParamName = "";
        if (versionParam.equals("test")) {
            dbParamName = "db_name_test";
        } else {
            dbParamName = "db_name_prod";
        }
        String dbName = context.getInitParameter(dbParamName);
        String driverName = context.getInitParameter("driver_name");
        String serverName = context.getInitParameter("server_name");
        String port = context.getInitParameter("port");
        String username = context.getInitParameter("username");
        String password = context.getInitParameter("password");

        String connectionURL = "jdbc:mysql://" + serverName + ":" + port + "/" + dbName;
        try {
            Class.forName(driverName).newInstance();
            conn = DriverManager.getConnection(connectionURL,username,password);
        } catch (Exception e) {
            logger.debug("Database connection error: " + e.getMessage());
        }
    }
    
    public void setConnections(ServletContext context, Connection _conn, Connection _secConn, Connection _paramConn, Connection _ccConn) {

        String versionParam = context.getInitParameter("version");
        String dbNameStr = "";
        String dbSecNameStr = "";
        String dbParamNameStr = "";
        String dbCcNameStr = "";
        if (versionParam.equals("test")) {
            dbNameStr = "db_name_test";
            dbSecNameStr = "security_db_name_test";
            dbParamNameStr = "parameter_db_name";
            dbCcNameStr = "center_callout_db_name";
        } else {
            dbNameStr = "db_name_prod";
            dbSecNameStr = "security_db_name_prod";
            dbParamNameStr = "parameter_db_name";
            dbCcNameStr = "center_callout_db_name";
        }
        //Assign the database names
        String dbName = context.getInitParameter(dbNameStr);
        String dbSecName = context.getInitParameter(dbSecNameStr);
        String dbParamName = context.getInitParameter(dbParamNameStr);
        String dbCcName = context.getInitParameter(dbCcNameStr);
        
        //Assign the context names
        String driverName = context.getInitParameter("driver_name");
        String serverName = context.getInitParameter("server_name");
        String port = context.getInitParameter("port");
        String username = context.getInitParameter("username");
        String password = context.getInitParameter("password");

        //Set up the different connection strings
        String connectionURL = "jdbc:mysql://" + serverName + ":" + port + "/" + dbName;
        String connectionSecURL = "jdbc:mysql://" + serverName + ":" + port + "/" + dbSecName;
        String connectionParamURL = "jdbc:mysql://" + serverName + ":" + port + "/" + dbParamName;
        String connectionCcURL = "jdbc:mysql://" + serverName + ":" + port + "/" + dbCcName;        
        
        //Translate into connection objects
        try {
            /*Class.forName(driverName).newInstance();
            conn = DriverManager.getConnection(connectionURL,username,password);            
            secConn = DriverManager.getConnection(connectionSecURL,username,password);            
            paramConn = DriverManager.getConnection(connectionParamURL,username,password);            
            ccConn = DriverManager.getConnection(connectionCcURL,username,password);*/
            
            BasicDataSource ds = this.getDataSource(connectionURL, driverName, username, password);
            BasicDataSource secds = this.getDataSource(connectionSecURL, driverName, username, password);
            BasicDataSource paramds = this.getDataSource(connectionParamURL, driverName, username, password);
            BasicDataSource ccds = this.getDataSource(connectionCcURL, driverName, username, password);
            
            if(_conn == null){
                conn = ds.getConnection();
                logger.debug("Main database connection assigned from DataSource pool...");
            }else{
                conn = _conn;
            }
            
            if(_secConn == null){
                secConn = secds.getConnection();
                logger.debug("Security database connection assigned from DataSource pool...");
            }else{
                secConn = _secConn;
            }
            
            if(_paramConn == null){
                paramConn = paramds.getConnection();
                logger.debug("Parameter database connection assigned from DataSource pool...");
            }else{
                paramConn = _paramConn;
            }
            
            if(_ccConn == null){
                ccConn = ccds.getConnection();
                logger.debug("Center callout database connection assigned from DataSource pool...");
            }else{
                ccConn = _ccConn;
            }
            
            
            
        } catch (Exception e) {
            logger.debug("Database connection error: " + e.getMessage());
        }
    }
    
    private BasicDataSource getDataSource(String connectionURL, String driverName, String username, String password) throws Exception{
        
            BasicDataSource ds = new BasicDataSource();
            ds.setDriverClassName(driverName);
            ds.setUsername(username);
            ds.setPassword(password);
            ds.setUrl(connectionURL);
            return ds;
    }

    public Connection getConnection() {
        return conn;
    }
    
    public Connection getSecConnection() {
        return secConn;
    }
    
    public Connection getParamConnection() {
        return paramConn;
    }
    
    public Connection getCcConnection() {
        return ccConn;
    }
}
