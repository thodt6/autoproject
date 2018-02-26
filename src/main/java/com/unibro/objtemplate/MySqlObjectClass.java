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
public final class MySqlObjectClass {

    private String host = "localhost";
    private String port = "3306";
    private String dbname = "gtg_staffing";
    String table;
    private String username = "root";
    private String password = "123qwe!";
    Connection conn;
    private Project project;
    private ArrayList<String> listTable = null;
    private List<String> selectedTables;
    Logger logger = Logger.getLogger(this.getClass().getName());
    private List<String> listSchema = null;

    public MySqlObjectClass(Project project, String host, String port, String dbname, String username, String password, String pkg) {
        this.dbname = dbname;
        this.username = username;
        this.password = password;
        this.host = host;
        this.port = port;
        this.project = project;
        this.loadListTable();
    }

    public MySqlObjectClass() {

    }

    public void loadListSchema() {
        try {
            Class.forName("com.mysql.jdbc.Driver");
            Connection con = DriverManager.getConnection("jdbc:mysql://" + host + ":" + port, username, password);
            DatabaseMetaData dbmd = con.getMetaData();
            ResultSet ctlgs = dbmd.getCatalogs();
            this.setListSchema((List<String>) new ArrayList());
            while (ctlgs.next()) {
                this.getListSchema().add(ctlgs.getString(1));
            }
//            this.listTable = new ArrayList();
            this.selectedTables = new ArrayList();
        } catch (ClassNotFoundException ex) {

        } catch (SQLException ex) {

        }
    }

    public void loadListTable() {
        try {
            String url = "jdbc:mysql://" + host + ":" + port + "/" + dbname;
            System.out.println(url);
            Class.forName("com.mysql.jdbc.Driver").newInstance();
            conn = DriverManager.getConnection(url, username, password);
            this.listTable = new ArrayList();
            DatabaseMetaData metadata = conn.getMetaData();
            ResultSet rs = metadata.getTables(null, null, "%", null);
            listTable = new ArrayList();
            while (rs.next()) {
                listTable.add(rs.getString(3));
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
        if (type.toLowerCase().equals("varchar") || type.toLowerCase().equals("nvarchar")
                || type.toLowerCase().equals("text") || type.toLowerCase().equals("longtext")
                || type.toLowerCase().equals("ntext") || type.toLowerCase().equals("tinytext")
                || type.toLowerCase().equals("enum")) {
            return Varible.STRING_TYPE;
        }
        if (type.toLowerCase().equals("bigint") || type.toLowerCase().equals("bigint unsigned")) {
            return Varible.BIGINT_TYPE;
        }
        if (type.toLowerCase().equals("int") || type.toLowerCase().equals("int unsigned")) {
            return Varible.INT_TYPE;
        }
        if (type.toLowerCase().equals("int unsigned")) {
            return Varible.LONG_TYPE;
        }
        if (type.toLowerCase().equals("tinyint") || type.toLowerCase().equals("tinyint unsigned")) {
            return Varible.INT_TYPE;
        }
        if (type.toLowerCase().equals("smallint") || type.toLowerCase().equals("smallint unsigned")) {
            return Varible.INT_TYPE;
        }
        if (type.toLowerCase().equals("bit")) {
            return Varible.BOOLEAN_TYPE;
        }
        if (type.toLowerCase().equals("date")) {
            return Varible.DATE_TYPE;
        }
        if (type.toLowerCase().equals("datetime")) {
            return Varible.DATE_TYPE;
        }
        if (type.toLowerCase().equals("float")) {
            return Varible.FLOAT_TYPE;
        }
        if (type.toLowerCase().equals("double")) {
            return Varible.DOUBLE_TYPE;
        }
        if (type.toLowerCase().contains("decimal")) {
            return Varible.BIGDECIMAL_TYPE;
        }
        return Varible.STRING_TYPE;
    }

    public List<Varible> initFieldClass(String table) {
        try {
            String url = "jdbc:mysql://" + getHost() + ":" + getPort() + "/" + getDbname();
            conn = DriverManager.getConnection(url, getUsername(), getPassword());
            DatabaseMetaData metadata = conn.getMetaData();
            ResultSet resultSet = metadata.getColumns(null, null, table, null);
            List ret = new ArrayList();
            while (resultSet.next()) {
                String name = resultSet.getString("COLUMN_NAME");
                String type = resultSet.getString("TYPE_NAME");
                String size = resultSet.getString("COLUMN_SIZE");
                String comment = resultSet.getString("REMARKS");
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
            ResultSet rs = meta.getPrimaryKeys(null, null, table);
            boolean ret = false;
            while (rs.next() && !ret) {
                String columnName = rs.getString("COLUMN_NAME");
                //System.out.println("getPrimaryKeys(): columnName=" + columnName);

                if (columnName.equals(name)) {
                    ret = true;
                }
            }
            rs.close();
            return ret;
        } catch (SQLException ex) {
            System.out.println("Ex:" + ex);
            return false;
        }
    }

    private boolean checkFieldNullable(String table, String name, Connection conn) {
        try {
            Statement statement = conn.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT " + name + " FROM " + table);
            ResultSetMetaData metadata = resultSet.getMetaData();
            int nullability = metadata.isNullable(1);
            resultSet.close();
            return nullability == ResultSetMetaData.columnNullable;
        } catch (SQLException ex) {
            System.out.println("Ex:" + ex);
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

}
