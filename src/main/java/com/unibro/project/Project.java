package com.unibro.project;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.unibro.language.Language;
import com.unibro.language.LanguageDAO;
import com.unibro.objtemplate.ObjTemplate;
import com.unibro.objtemplate.ObjTemplateDAO;
import com.unibro.utils.Global;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import jdbchelper.BeanCreator;
import jdbchelper.StatementMapper;
import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;
import org.zeroturnaround.zip.ZipUtil;

public class Project implements Serializable, Converter {

    public static final String ABSTRACT_ACCESS = "ABSTRACT";
    public static final String MYSQL_ACCESS = "MySQL";
    public static final String SQLSERVER_ACCESS = "SQL_SERVER";
    public static final String ORACLE_ACCESS = "ORACLE";
    public static final String MONGODB_ACCESS = "MONGO";
    public static final String API_ACCESS = "API";
    public static final String ELASTIC_ACCESS = "ELASTICSEARCH";

    private String projectid = Global.getRandomString();
    private String name = "";
    private java.util.Date createdtime = new java.util.Date();
    private String languageList = "";
    private String projectFolder = "";
    private String projectType = "CMS";
    private String dataAccessType = "ABSTRACT";

    private List<Language> language_list;

    static final Logger logger = Logger.getLogger(Project.class.getName());

    public void setProjectid(String projectid) {
        this.projectid = projectid;
    }

    public String getProjectid() {
        return this.projectid;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return this.name;
    }

    public void setCreatedtime(java.util.Date createdtime) {
        this.createdtime = createdtime;
    }

    public java.util.Date getCreatedtime() {
        return this.createdtime;
    }

    public void setLanguageList(String languageList) {
        this.languageList = languageList;
    }

    public String getLanguageList() {
        return this.languageList;
    }

    public void setProjectFolder(String projectFolder) {
        this.projectFolder = projectFolder;
    }

    public String getProjectFolder() {
        return this.projectFolder;
    }

    public static Project getInstant() {
        Project instant = new Project();
        instant.projectid = Global.getRandomString();
        instant.name = "";
        instant.createdtime = new java.util.Date();
        instant.languageList = "";
        instant.projectFolder = "";

        return instant;
    }

    public Project() {
    }

//    public Project(Hashtable hash) {
    //    this.projectid = (String)hash.get("projectid");
    //    this.name = (String)hash.get("name");
    //   this.createdtime = Global.convertSqlTimeStampToDate((java.sql.Timestamp)hash.get("createdtime"));
    //    this.srcCms = (String)hash.get("src_cms");
    //    this.srcApi = (String)hash.get("src_api");
    //    this.languageList = (String)hash.get("language_list");
    //    this.projectFolder = (String)hash.get("project_folder");
//    }
    public static Project getObjectFromJsonString(String jsonObj) {
        try {
            Gson gson = Global.getGsonObject();
            Project obj;
            obj = gson.fromJson(jsonObj, Project.class);
            return obj;
        } catch (JsonSyntaxException ex) {
            //this.getLogger.error("Error:": + ex);
            return null;
        }
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof Project)) {
            return false;
        }
        Project compareObj = (Project) obj;
        return (compareObj.getProjectid().equals(this.getProjectid()));
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 89 * hash + (this.projectid != null ? this.projectid.hashCode() : 0);
        return hash;
    }

    public String toJson() {
        Gson gson = Global.getGsonObject();
        return gson.toJson(this);
    }

    public static BeanCreator<Project> beanCreator = new BeanCreator<Project>() {
        @Override
        public Project createBean(ResultSet rs) throws SQLException {
            Project obj = new Project();
            obj.projectid = (String) rs.getObject("projectid");
            obj.name = (String) rs.getObject("name");
            obj.createdtime = (java.util.Date) rs.getObject("createdtime");
            obj.languageList = (String) rs.getObject("language_list");
            obj.projectFolder = (String) rs.getObject("project_folder");
            obj.projectType = (String) rs.getObject("project_type");
            obj.dataAccessType = (String) rs.getObject("dataAccess_type");
            obj.loadLanguageList();
            return obj;
        }
    };
    public static StatementMapper<Project> statementMapper = new StatementMapper<Project>() {
        @Override
        public void mapStatement(PreparedStatement stmt, Project obj) throws SQLException {
            obj.saveLanguageList();
            stmt.setObject(1, obj.projectid);
            stmt.setObject(2, obj.name);
            stmt.setObject(3, obj.createdtime);
            stmt.setObject(4, obj.languageList);
            stmt.setObject(5, obj.projectFolder);
            stmt.setObject(6, obj.projectType);
            stmt.setObject(7, obj.dataAccessType);
        }
    };

    public Object getAsObject(FacesContext fc, UIComponent uic, String submittedValue) {
        if (submittedValue.trim().equals("")) {
            return null;
        }
        String id;
        try {
            id = String.valueOf(submittedValue);
        } catch (Exception ex) {
            id = null;
        }
        ProjectDAO dao = new ProjectDAO();
        Project ret = dao.getObjectByKey(id);
        return ret;
    }

    public String getAsString(FacesContext facesContext, UIComponent component, Object value) {
        if (value == null) {
            return "";
        }
        if (value.equals("")) {
            return "";
        }
        return String.valueOf(((Project) value).getProjectid());
    }

    /**
     * @return the language_list
     */
    public List<Language> getLanguage_list() {
        return language_list;
    }

    /**
     * @param language_list the language_list to set
     */
    public void setLanguage_list(List<Language> language_list) {
        this.language_list = language_list;
    }

    public void loadLanguageList() {
        LanguageDAO dao = new LanguageDAO();
        this.language_list = dao.load("WHERE languageid IN(" + this.languageList + ")");
    }

    public void saveLanguageList() {
        if (this.language_list != null && !this.language_list.isEmpty()) {
            this.languageList = "";
            for (Language l : this.language_list) {
                this.languageList += ",'" + l.getLanguageid() + "'";
            }
            if (this.languageList.length() > 0) {
                this.languageList = this.languageList.substring(1);
            }
        } else {
            this.languageList = "";
        }
    }

    /**
     * @return the projectType
     */
    public String getProjectType() {
        return projectType;
    }

    /**
     * @param projectType the projectType to set
     */
    public void setProjectType(String projectType) {
        this.projectType = projectType;
    }

    /**
     * @return the dataAccessType
     */
    public String getDataAccessType() {
        return dataAccessType;
    }

    /**
     * @param dataAccessType the dataAccessType to set
     */
    public void setDataAccessType(String dataAccessType) {
        this.dataAccessType = dataAccessType;
    }

    public List<ObjTemplate> loadTemplateObject() {
        ObjTemplateDAO dao = new ObjTemplateDAO();
        return dao.load("WHERE projectid=? ORDER BY createdtime", this.getProjectid());
    }

    public List<ObjTemplate> getTemplateObjects() {
        ObjTemplateDAO dao = new ObjTemplateDAO();
        return dao.load("WHERE projectid=? ORDER BY createdtime", this.getProjectid());
    }

    public void deleteMe() {
        try {
            File f = new File(this.getProjectFolder());
            logger.info(f.getAbsolutePath());
            FileUtils.deleteDirectory(f);
            if (!f.exists()) {
                ObjTemplateDAO dao1 = new ObjTemplateDAO();
                dao1.excuteQuery("DELETE FROM obj_template WHERE projectid=?", this.getProjectid());
                ProjectDAO dao = new ProjectDAO();
                dao.delete(this);
            } else {
                logger.info("Can not delete");
            }
        } catch (IOException ex) {
            logger.error(ex);
        }
    }

    public StreamedContent getProjectFile() {
        try {
            logger.info("Start download");
            File f = new File(Global.getConfigValue("FILE_PROJECT_PATH") + this.getProjectid() + ".zip");
            if (f.exists()) {
                f.delete();
            }
            ZipUtil.pack(new File(this.getProjectFolder()), new File(Global.getConfigValue("FILE_PROJECT_PATH") + this.getProjectid() + ".zip"));
//            File f = new File(Global.getConfigValue("FILE_PROJECT_PATH") + this.getProjectid() + ".zip");
            logger.info(f);
            if (f.exists()) {
                InputStream stream = new FileInputStream(f);
                StreamedContent ret = new DefaultStreamedContent(stream, "application/zip", this.projectid + ".zip");
                return ret;
            }
            return null;
        } catch (FileNotFoundException ex) {
            return null;
        }
    }

}
