/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.unibro.objtemplate;

import com.unibro.project.Project;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import org.apache.log4j.Logger;

/**
 *
 * @author Nguyen Duc Tho
 */
public final class OracleSqlObjectClass {

    private String host = "103.63.109.78";
    private String port = "1521";
    private String dbname = "HSTAY";
    private String serviceName = "HOST";
    String table;
    private String username = "CMS";
    private String password = "CMS123";
    Connection conn;
    private Project project;
    private ArrayList<String> listTable = null;
    private List<String> selectedTables;
    Logger logger = Logger.getLogger(this.getClass().getName());
    private List<String> listSchema = null;


    public OracleSqlObjectClass(Project project, String host, String port, String serviceName, String dbname, String username, String password, String pkg) {
        this.dbname = dbname;
        this.username = username;
        this.password = password;
        this.host = host;
        this.port = port;
        this.project = project;
        this.serviceName = serviceName;
        this.loadListTable();
    }

    public OracleSqlObjectClass() {

    }

    public void loadListSchema() {
        try {
            logger.info("Load list schema");
            Class.forName("oracle.jdbc.driver.OracleDriver");
            Connection con = DriverManager.getConnection("jdbc:oracle:thin:@//" + host + ":" + port + "/" + this.serviceName, username, password);
            DatabaseMetaData dbmd = con.getMetaData();
            logger.info(dbmd.getUserName());
            this.setListSchema((List<String>) new ArrayList());
            ResultSet ctlgs = dbmd.getSchemas();

            while (ctlgs.next()) {
//                logger.info(ctlgs.getString(1));
                this.getListSchema().add(ctlgs.getString(1));
            }
//            this.listTable = new ArrayList();
            this.selectedTables = new ArrayList();
        } catch (ClassNotFoundException ex) {
            logger.error(ex);
        } catch (SQLException ex) {
            logger.error(ex);
        }
    }

    public void loadListTable() {
        try {
            String url = "jdbc:oracle:thin:@//" + host + ":" + port + "/" + this.serviceName;
            System.out.println(url);
            Class.forName("oracle.jdbc.driver.OracleDriver").newInstance();
            conn = DriverManager.getConnection(url, username, password);
            this.listTable = new ArrayList();
            DatabaseMetaData metadata = conn.getMetaData();
            ResultSet rs = metadata.getTables(null, this.dbname, "%", new String[]{"TABLE"});
            listTable = new ArrayList();
            while (rs.next()) {
//                logger.info(rs.getString("TABLE_NAME"));
//                logger.info(rs.getString("TABLE_SCHEM"));
//                logger.info(rs.getString(3));
                listTable.add(rs.getString("TABLE_NAME"));
            }
//            this.selectedTables = new ArrayList();
            conn.close();
        } catch (ClassNotFoundException ex) {
            this.listTable = new ArrayList();
            logger.error(ex);
        } catch (IllegalAccessException ex) {
            this.listTable = new ArrayList();
            logger.error(ex);
        } catch (InstantiationException ex) {
            this.listTable = new ArrayList();
            logger.error(ex);
        } catch (SQLException ex) {
            this.listTable = new ArrayList();
            logger.error(ex);
        }
    }

    public String getObjectType(String type) {
        if (type.toLowerCase().equals("varchar2") || type.toLowerCase().equals("nvarchar2")
                || type.toLowerCase().equals("char") || type.toLowerCase().equals("nchar")) {
            return Varible.STRING_TYPE;
        }
        if (type.toLowerCase().equals("number")) {
            return Varible.BIGDECIMAL_TYPE;
        }
        if (type.toLowerCase().equals("integer")) {
            return Varible.INT_TYPE;
        }
        if (type.toLowerCase().equals("long")) {
            return Varible.LONG_TYPE;
        }
        if (type.toLowerCase().equals("float") || type.toLowerCase().equals("binary float")) {
            return Varible.FLOAT_TYPE;
        }
        if (type.toLowerCase().equals("double") || type.toLowerCase().equals("binary double")) {
            return Varible.DOUBLE_TYPE;
        }
        if (type.toLowerCase().equals("bit")) {
            return Varible.BOOLEAN_TYPE;
        }
        if (type.toLowerCase().equals("date")) {
            return Varible.DATE_TYPE;
        }
        if (type.toLowerCase().contains("timestamp")) {
            return Varible.DATE_TYPE;
        }
        return Varible.STRING_TYPE;
    }

    public List<Varible> initFieldClass(String table) {
        try {
            String url = "jdbc:oracle:thin:@//" + host + ":" + port + "/" + this.serviceName;
            conn = DriverManager.getConnection(url, getUsername(), getPassword());
            DatabaseMetaData metadata = conn.getMetaData();
            ResultSet resultSet = metadata.getColumns(null, this.dbname, table, null);
            List ret = new ArrayList();
            while (resultSet.next()) {
                String name = resultSet.getString(4);
                String type = resultSet.getString(6);
                logger.info(name + "-" + type);

                boolean primery = this.checkFieldPrimery(table, name, metadata);
                Varible v = new Varible();
                v.setName(name);
                v.setPrimeKey(primery);
                v.setProject(getProject());
                if (primery) {
                    v.setRequired(true);
                } else {
                    v.setRequired(!this.checkFieldNullable(table, name, conn));
                }
                v.setType(this.getObjectType(type));
                v.setValidVarible(1);
                v.initDefaultValue();
                v.initHelpMessage();
                v.initRequiredData();
                v.initValidateMessage();
                ret.add(v);
            }
            logger.info("Ret size:" + ret.size());
            conn.close();
            return ret;
        } catch (SQLException ex) {
            logger.error(ex);
            return null;
        }
    }

    private boolean checkFieldPrimery(String table, String name, DatabaseMetaData meta) {
        try {
            logger.info("table:" + table);
            ResultSet rs = meta.getPrimaryKeys(null, dbname, table);
            boolean ret = false;
            logger.info("Start check");
            while (rs.next() && !ret) {
                String columnName = rs.getString(2);
                logger.info("getPrimaryKeys(): columnName=" + columnName);

                if (columnName.equals(name)) {
                    ret = true;
                }
            }
            rs.close();
            return ret;
        } catch (SQLException ex) {
            logger.error("ExCheck:" + ex);
            return false;
        }
    }

    private boolean checkFieldNullable(String table, String name, Connection conn) {
        try {
            Statement statement = conn.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT " + name + " FROM " + this.dbname + "." + table);
            ResultSetMetaData metadata = resultSet.getMetaData();
            int nullability = metadata.isNullable(1);
            resultSet.close();
            return nullability == ResultSetMetaData.columnNullable;
        } catch (SQLException ex) {
            System.out.println("ExNull:" + ex);
            return false;
        }
    }

    /**
     * @return the host
     */
    public String getHost() {
        return host;
    }

    /**
     * @return the port
     */
    public String getPort() {
        return port;
    }

    /**
     * @param port the port to set
     */
    public void setPort(String port) {
        this.port = port;
    }

    /**
     * @return the dbname
     */
    public String getDbname() {
        return dbname;
    }

    /**
     * @param dbname the dbname to set
     */
    public void setDbname(String dbname) {
        this.dbname = dbname;
    }

    /**
     * @return the username
     */
    public String getUsername() {
        return username;
    }

    /**
     * @param username the username to set
     */
    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * @return the password
     */
    public String getPassword() {
        return password;
    }

    /**
     * @param password the password to set
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * @return the project
     */
    public Project getProject() {
        return project;
    }

    /**
     * @param project the project to set
     */
    public void setProject(Project project) {
        this.project = project;
    }

    /**
     * @return the listTable
     */
    public ArrayList<String> getListTable() {
        return listTable;
    }

    /**
     * @param listTable the listTable to set
     */
    public void setListTable(ArrayList<String> listTable) {
        this.listTable = listTable;
    }

    /**
     * @param host the host to set
     */
    public void setHost(String host) {
        this.host = host;
    }

    /**
     * @return the selectedTables
     */
    public List<String> getSelectedTables() {
        return selectedTables;
    }

    /**
     * @param selectedTables the selectedTables to set
     */
    public void setSelectedTables(List<String> selectedTables) {
        this.selectedTables = selectedTables;
    }

    /**
     * @return the listSchema
     */
    public List<String> getListSchema() {
        return listSchema;
    }

    /**
     * @param listSchema the listSchema to set
     */
    public void setListSchema(List<String> listSchema) {
        this.listSchema = listSchema;
    }

    /**
     * @return the serviceName
     */
    public String getServiceName() {
        return serviceName;
    }

    /**
     * @param serviceName the serviceName to set
     */
    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

}
