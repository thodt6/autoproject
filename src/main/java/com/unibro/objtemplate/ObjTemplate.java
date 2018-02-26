package com.unibro.objtemplate;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import com.unibro.language.Language;
import com.unibro.project.Project;
import com.unibro.project.ProjectDAO;
import com.unibro.utils.Global;
import com.unibro.utils.SortedProperties;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.lang.reflect.Type;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import jdbchelper.BeanCreator;
import jdbchelper.StatementMapper;
import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;

@ManagedBean
@ViewScoped
public class ObjTemplate implements Serializable, Converter {

    private String id = "";
    private String projectid = "";
    private String name = "";
    private String objectFormat = "";
    private String helpFormat = "";
    private java.util.Date createdtime = new java.util.Date();
    static final Logger logger = Logger.getLogger(ObjTemplate.class.getName());

//    private List<Varible> list_varible;
    private List<LanguageData> list_help;
    private Project project;
    private VaribleController varibleController;

    public void setId(String id) {
        this.id = id;
    }

    public String getId() {
        return this.id;
    }

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

    public void setObjectFormat(String objectFormat) {
        this.objectFormat = objectFormat;
    }

    public String getObjectFormat() {
        return this.objectFormat;
    }

    public static ObjTemplate getInstant() {
        ObjTemplate instant = new ObjTemplate();
        instant.id = Global.getRandomString();
        instant.projectid = "";
        instant.name = "";
        instant.objectFormat = "";
        instant.helpFormat = "";
        return instant;
    }

    public ObjTemplate() {
    }

//    public ObjTemplate(Hashtable hash) {
    //    this.id = (String)hash.get("id");
    //    this.projectid = (String)hash.get("projectid");
    //    this.name = (String)hash.get("name");
    //    this.objectFormat = (String)hash.get("object_format");
    //    this.modelGenerator = (Boolean)hash.get("model_generator");
    //    this.serviceGenerator = (Boolean)hash.get("service_generator");
    //    this.apiGenerator = (Boolean)hash.get("api_generator");
    //    this.languageGenerator = (Boolean)hash.get("language_generator");
    //    this.viewGenerator = (Boolean)hash.get("view_generator");
//    }
    public static ObjTemplate getObjectFromJsonString(String jsonObj) {
        try {
            Gson gson = Global.getGsonObject();
            ObjTemplate obj;
            obj = gson.fromJson(jsonObj, ObjTemplate.class);
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
        if (!(obj instanceof ObjTemplate)) {
            return false;
        }
        ObjTemplate compareObj = (ObjTemplate) obj;
        return (compareObj.getId().equals(this.getId()));
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 31 * hash + (this.id != null ? this.id.hashCode() : 0);
        return hash;
    }

    public String toJson() {
        Gson gson = Global.getGsonObject();
        return gson.toJson(this);
    }

    public static BeanCreator<ObjTemplate> beanCreator = new BeanCreator<ObjTemplate>() {
        @Override
        public ObjTemplate createBean(ResultSet rs) throws SQLException {
            ObjTemplate obj = new ObjTemplate();
            obj.id = (String) rs.getObject("id");
            obj.projectid = (String) rs.getObject("projectid");
            obj.name = (String) rs.getObject("name");
            obj.objectFormat = (String) rs.getObject("object_format");
            obj.createdtime = (java.util.Date) rs.getObject("createdtime");
            obj.helpFormat = (String) rs.getObject("help_format");
            obj.varibleController = new VaribleController(obj.objectFormat);
//            obj.loadHelpFormat();
            return obj;
        }
    };
    public static StatementMapper<ObjTemplate> statementMapper = new StatementMapper<ObjTemplate>() {
        @Override
        public void mapStatement(PreparedStatement stmt, ObjTemplate obj) throws SQLException {
            logger.info("mapStatement");
            obj.getVaribleController().loadVaribleList();
            obj.getVaribleController().saveVaribleList();
            obj.objectFormat = obj.getVaribleController().getObjectFormat();
            obj.saveHelpList();
            stmt.setObject(1, obj.id);
            stmt.setObject(2, obj.projectid);
            stmt.setObject(3, obj.name);
            stmt.setObject(4, obj.objectFormat);
            stmt.setObject(5, obj.createdtime);
            stmt.setObject(6, obj.helpFormat);
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
        ObjTemplateDAO dao = new ObjTemplateDAO();
        ObjTemplate ret = dao.getObjectByKey(id);
        ret.loadProject();
        ret.setVaribleController(new VaribleController(ret.getObjectFormat()));
        ret.getVaribleController().setProject(ret.getProject());
        ret.getVaribleController().loadVaribleList();
        ret.loadHelpFormat();
        return ret;
    }

    public String getAsString(FacesContext facesContext, UIComponent component, Object value) {
        if (value == null) {
            return "";
        }
        if (value.equals("")) {
            return "";
        }
        return String.valueOf(((ObjTemplate) value).getId());
    }

    public void loadHelpFormat() {
        if (this.helpFormat == null || this.helpFormat.equals("") || this.helpFormat.equals("[]")) {
            this.setList_help((List<LanguageData>) new ArrayList());
            if (!this.project.getLanguage_list().isEmpty()) {
                for (Language lang : this.project.getLanguage_list()) {
                    LanguageData data = new LanguageData(lang.getLanguageid(), lang.getLanguageid() + " HELP");
                    this.getList_help().add(data);
                }
            }
        } else {
            try {
                Gson gson = Global.getGsonObject();
                Type listType = new TypeToken<List<LanguageData>>() {
                }.getType();
                this.list_help = gson.fromJson(this.helpFormat, listType);
            } catch (JsonSyntaxException ex) {
                logger.error(ex);
                this.setList_help((List<LanguageData>) new ArrayList());
            }
        }
    }

    public String getHelpMessge(Language lang) {
        for (LanguageData data : this.list_help) {
            if (data.getLanguageid().equals(lang.getLanguageid())) {
                return data.getContent();
            }
        }
        return "";
    }

    private void saveHelpList() {
        if (this.getList_help() == null || this.getList_help().isEmpty()) {
            this.helpFormat = "[]";
        } else {
            this.helpFormat = LanguageData.toJsonArrayString(this.list_help);
            logger.info("Object Format:" + this.objectFormat);
        }
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
     * @return the createdtime
     */
    public java.util.Date getCreatedtime() {
        return createdtime;
    }

    /**
     * @param createdtime the createdtime to set
     */
    public void setCreatedtime(java.util.Date createdtime) {
        this.createdtime = createdtime;
    }

    /**
     * @return the helpFormat
     */
    public String getHelpFormat() {
        return helpFormat;
    }

    /**
     * @param helpFormat the helpFormat to set
     */
    public void setHelpFormat(String helpFormat) {
        this.helpFormat = helpFormat;
    }

    /**
     * @return the list_help
     */
    public List<LanguageData> getList_help() {
        return list_help;
    }

    /**
     * @param list_help the list_help to set
     */
    public void setList_help(List<LanguageData> list_help) {
        this.list_help = list_help;
    }

    /**
     * @return the varibleController
     */
    public VaribleController getVaribleController() {
        return varibleController;
    }

    /**
     * @param varibleController the varibleController to set
     */
    public void setVaribleController(VaribleController varibleController) {
        this.varibleController = varibleController;
    }

    public boolean checkAutoIncrement() {
        for (Varible v : this.getVaribleController().getList_varible()) {
            if (v.getAutoIncrement()) {
                return true;
            }
        }
        return false;
    }

    public void editMe() {
        ObjTemplateDAO dao = new ObjTemplateDAO();
        this.varibleController.saveVaribleList();
        this.objectFormat = this.varibleController.getObjectFormat();
        if (dao.edit(this)) {
            FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "Update object " + this.name + " success", "");
            FacesContext.getCurrentInstance().addMessage(null, msg);
        } else {
            FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Update object " + this.name + " fail", "");
            FacesContext.getCurrentInstance().addMessage(null, msg);
        }
    }

    public String getClassName() {
        return this.getName().substring(0, 1).toUpperCase() + this.getName().substring(1);
    }

    private void loadProject() {
        ProjectDAO dao = new ProjectDAO();
        this.project = dao.getObjectByKey(this.getProjectid());
    }

    @Override
    public String toString() {
        return this.name;
    }

    //Abstract Java class
    private void writeAbstractCMSBaseClass() {
        try {
            File ObjClassTemFile = new File(Global.getConfigValue("FILE_PROJECT_TEMPLATE") + "/abstracobject/ObjClass/BaseObjClass.java");
            String content = FileUtils.readFileToString(ObjClassTemFile);
            content = content.replace("com.unibro.objclass", "com.unibro." + this.getName().toLowerCase());
            String varible_def = "";
            for (Varible v : this.getVaribleController().getList_varible()) {
                varible_def += v.getVaribleDefinition();
            }
            content = content.replace("[VaribleDefinition]", varible_def);

            String getter_setter_data = "";
            for (Varible v : this.getVaribleController().getList_varible()) {
                getter_setter_data += v.getSetter() + "\n";
                getter_setter_data += v.getGetter() + "\n";
            }

            content = content.replace("[SetterAndGetter]", getter_setter_data);

            for (Varible v : this.getVaribleController().getList_varible()) {
                if (v.getPrimeKey()) {
                    content = content.replace("[OBJECTKEY]", v.getName1());
                    break;
                }
            }

            content = content.replace("BaseObjClass", "Base" + this.getClassName());
            content = content.replace("ObjClass", this.getClassName());
            content = content.replace("objclass", this.getName());

            File file_dir = new File(this.project.getProjectFolder() + "/src/main/java/com/unibro/" + this.getClassName().toLowerCase());
            file_dir.mkdirs();
            FileUtils.writeStringToFile(new File(file_dir.getAbsolutePath() + "/Base" + this.getClassName() + ".java"), content);
            logger.info(content);
        } catch (IOException ex) {
            logger.error(ex);
        }
    }

    private void writeAbstractCMSObjClass() {
        try {
            File ObjClassTemFile = new File(Global.getConfigValue("FILE_PROJECT_TEMPLATE") + "/abstracobject/ObjClass/ObjClass.java");
            String content = FileUtils.readFileToString(ObjClassTemFile);
            logger.info("ObjClass Content:" + content);
            content = content.replace("com.unibro.objclass", "com.unibro." + this.getName().toLowerCase());
            content = content.replace("ObjClass", this.getClassName());
            content = content.replace("objclass", this.getName());

            File file_dir = new File(this.project.getProjectFolder() + "/src/main/java/com/unibro/" + this.getClassName().toLowerCase());
            file_dir.mkdirs();
            FileUtils.writeStringToFile(new File(file_dir.getAbsolutePath() + "/" + this.getClassName() + ".java"), content);
            logger.info(content);
        } catch (IOException ex) {
            logger.error(ex);
        }
    }

    private void writeAbstractCMSDAOClass() {
        try {
            File ObjClassTemFile = new File(Global.getConfigValue("FILE_PROJECT_TEMPLATE") + "/abstracobject/ObjClass/ObjClassDAO.java");
            String content = FileUtils.readFileToString(ObjClassTemFile);
            content = content.replace("com.unibro.objclass", "com.unibro." + this.getName().toLowerCase());
            content = content.replace("ObjClass", this.getClassName());
            content = content.replace("objclass", this.getName());

            File file_dir = new File(this.project.getProjectFolder() + "/src/main/java/com/unibro/" + this.getClassName().toLowerCase());
            file_dir.mkdirs();
            FileUtils.writeStringToFile(new File(file_dir.getAbsolutePath() + "/" + this.getClassName() + "DAO.java"), content);
            logger.info(content);
        } catch (IOException ex) {
            logger.error(ex);
        }
    }

    private void writeAbstractCMSObjClassLazyModel() {
        try {
            File ObjClassTemFile = new File(Global.getConfigValue("FILE_PROJECT_TEMPLATE") + "/abstracobject/ObjClass/ObjClassLazyModel.java");
            String content = FileUtils.readFileToString(ObjClassTemFile);
            content = content.replace("com.unibro.objclass", "com.unibro." + this.getName().toLowerCase());
            for (Varible v : this.getVaribleController().getList_varible()) {
                if (v.getPrimeKey()) {
                    content = content.replace("[OBJECTKEY]", v.getName1());
                    break;
                }
            }

            content = content.replace("objclass", this.getName());
            content = content.replace("ObjClass", this.getClassName());

            File file_dir = new File(this.project.getProjectFolder() + "/src/main/java/com/unibro/" + this.getClassName().toLowerCase());
            file_dir.mkdirs();
            FileUtils.writeStringToFile(new File(file_dir.getAbsolutePath() + "/" + this.getClassName() + "LazyModel.java"), content);
            logger.info(content);
        } catch (IOException ex) {
            logger.error(ex);
        }
    }

    private void writeAbstractCMSObjClassLazyService() {
        try {
            File ObjClassTemFile = new File(Global.getConfigValue("FILE_PROJECT_TEMPLATE") + "/abstracobject/ObjClass/ObjClassLazyService.java");
            String content = FileUtils.readFileToString(ObjClassTemFile);
            content = content.replace("com.unibro.objclass", "com.unibro." + this.getName().toLowerCase());
            content = content.replace("ObjClass", this.getClassName());
            content = content.replace("objclass", this.getName());

            File file_dir = new File(this.project.getProjectFolder() + "/src/main/java/com/unibro/" + this.getClassName().toLowerCase());
            file_dir.mkdirs();
            FileUtils.writeStringToFile(new File(file_dir.getAbsolutePath() + "/" + this.getClassName() + "LazyService.java"), content);
            logger.info(content);
        } catch (IOException ex) {
            logger.error(ex);
        }
    }

    private void writeAbstractObjClassService() {
        try {
            File ObjClassTemFile = new File(Global.getConfigValue("FILE_PROJECT_TEMPLATE") + "/abstracobject/ObjClass/ObjClassService.java");
            String content = FileUtils.readFileToString(ObjClassTemFile);
            content = content.replace("com.unibro.objclass", "com.unibro." + this.getName().toLowerCase());
            content = content.replace("ObjClass", this.getClassName());
            content = content.replace("objclass", this.getName());

            File file_dir = new File(this.project.getProjectFolder() + "/src/main/java/com/unibro/" + this.getClassName().toLowerCase());
            file_dir.mkdirs();
            FileUtils.writeStringToFile(new File(file_dir.getAbsolutePath() + "/" + this.getClassName() + "Service.java"), content);
            logger.info(content);
        } catch (IOException ex) {
            logger.error(ex);
        }
    }

    //CMS View Area
    private void writeLanguageFile() {
        SortedProperties prop = new SortedProperties();
        for (Language lang : this.project.getLanguage_list()) {
            String destination = this.project.getProjectFolder() + "/src/main/resources/config/";
            File dir = new File(destination);
            dir.mkdirs();
            try {
                prop.load(new FileInputStream(destination + "/language_" + this.getName() + "_" + lang.getLanguageid() + ".properties"));
                prop.clear();
            } catch (IOException ex) {
                logger.error(ex);
            }
            try {
//                prop.load(new FileInputStream(destination + "/language_" + this.getName() + "_" + lang.getLanguageid() + ".properties"));
                prop.setProperty("obj." + name + ".general", this.getClassName());
                prop.setProperty("obj." + name + ".general.help", this.getHelpMessge(lang));
                prop.setProperty("obj." + name + ".general.view", "View " + this.getClassName());
                prop.setProperty("obj." + name + ".general.list", this.getClassName() + " list");
                prop.setProperty("obj." + name + ".general.listempty", "No " + this.getClassName() + " found");

                prop.setProperty("obj." + name + ".general.create", "New " + this.getClassName());
                prop.setProperty("obj." + name + ".general.createSuccess", "Create " + this.getClassName() + " success");
                prop.setProperty("obj." + name + ".general.createFail", "Create " + this.getClassName() + " fail");

                prop.setProperty("obj." + name + ".general.delete", "Delete selected " + this.getClassName() + " ?");
                prop.setProperty("obj." + name + ".general.deleteSuccess", "Delete selected" + this.getClassName() + " success");
                prop.setProperty("obj." + name + ".general.deleteFail", "Delete selected " + this.getClassName() + " fail");

                prop.setProperty("obj." + name + ".general.deleteAll", "Delete all of selected " + this.getClassName() + " ?");
                prop.setProperty("obj." + name + ".general.deleteAllSuccess", "Delete all of selected " + this.getClassName() + " success");
                prop.setProperty("obj." + name + ".general.deleteAllFail", "Delete all of selected " + this.getClassName() + " fail");

                prop.setProperty("obj." + name + ".general.update", "Update selected " + this.getClassName() + " ?");
                prop.setProperty("obj." + name + ".general.updateSuccess", "Update selected " + this.getClassName() + " success");
                prop.setProperty("obj." + name + ".general.updateFail", "Update selected " + this.getClassName() + " fail");

                prop.setProperty("obj." + name + ".general.updateAll", "Update all of selected " + this.getClassName() + " ?");
                prop.setProperty("obj." + name + ".general.updateAllSuccess", "Update all of selected " + this.getClassName() + " success");
                prop.setProperty("obj." + name + ".general.updateAllFail", "Update all of selected " + this.getClassName() + " fail");

                prop.setProperty("obj." + name + ".general.edit", "Edit selected " + this.getClassName() + " ?");
                prop.setProperty("obj." + name + ".general.editSuccess", "Edit selected " + this.getClassName() + " success");
                prop.setProperty("obj." + name + ".general.editFail", "Edit selected " + this.getClassName() + " fail");

                prop.setProperty("obj." + name + ".general.editAll", "Edit all of selected " + this.getClassName() + "?");
                prop.setProperty("obj." + name + ".general.editAllSuccess", "Edit all of selected " + this.getClassName() + " success");
                prop.setProperty("obj." + name + ".general.editAllFail", "Edit all of selected " + this.getClassName() + " fail");
                for (int i = 0; i < this.varibleController.getList_varible().size(); i++) {
                    Varible v = this.varibleController.getList_varible().get(i);
                    prop.setProperty("obj." + this.name + ".detail." + v.getName(), v.getName1());
                    prop.setProperty("obj." + this.name + ".detail." + v.getName() + ".validateMsg", v.getValidateMessge(lang));
                    prop.setProperty("obj." + this.name + ".detail." + v.getName() + ".helpMsg", v.getHelpMessge(lang));
                }
                prop.store(new FileOutputStream(destination + "/lang_" + this.getName() + "_" + lang.getLanguageid() + ".properties", false), lang.getName() + " language properties file");
            } catch (IOException ex) {
                //System.out.println(ex); 
                logger.error(ex);
            }

        }
    }

    private void writeCMSIndexView() {
        try {
            //Folder or source file
            File folder = new File(this.project.getProjectFolder() + "/src/main/webapp/portal/" + this.getName());
            folder.mkdirs();
            //index.html
            File index_f = new File(Global.getConfigValue("FILE_PROJECT_TEMPLATE") + "/xhtml/crud/index.xhtml");
            String index_content = FileUtils.readFileToString(index_f);
            index_content = index_content.replace("ObjClass", this.getClassName());
            index_content = index_content.replace("objclass", this.getName());
            FileUtils.writeStringToFile(new File(this.project.getProjectFolder() + "/src/main/webapp/portal/" + this.getName() + "/index.xhtml"), index_content);
            //index_content.html
            File f = new File(Global.getConfigValue("FILE_PROJECT_TEMPLATE") + "/xhtml/crud/index_content.xhtml");
            String content = FileUtils.readFileToString(f);
//            content = content.replace("ObjClass", this.getClassName());
            String xhtml_content = "";
            String objkey = null;
            for (Varible v : this.varibleController.getList_varible()) {
                xhtml_content += v.getOutputColumn("obj", this.getName());
                if (v.getPrimeKey()) {
                    objkey = v.getName();
                }
            }
            content = content.replace("[TABLEROW]", xhtml_content);
            String viewName = this.getName().substring(0, 1).toLowerCase() + this.getName().substring(1);
            content = content.replace("objclassLazyService", viewName + "LazyService");
            content = content.replace("ObjClass", this.getClassName());
            content = content.replace("objclass", this.getName());
            if (objkey != null) {
                content = content.replace("[OBJECTKEY]", objkey);
            } else {
                content = content.replace("rowKey=\"#{obj.[OBJECTKEY]}\"", "");
            }

//            logger.info(xhtml_content);
            FileUtils.writeStringToFile(new File(this.project.getProjectFolder() + "/src/main/webapp/portal/" + this.getName() + "/index_content.xhtml"), content);
        } catch (IOException ex) {
            logger.error(ex);
        }
    }

    private void writeCMSCreateView() {
        try {
            //Folder or source file
            File folder = new File(this.project.getProjectFolder() + "/src/main/webapp/portal/" + this.getName());
            folder.mkdirs();
            String viewName = this.getName().substring(0, 1).toLowerCase() + this.getName().substring(1);
            //index.html
            File index_f = new File(Global.getConfigValue("FILE_PROJECT_TEMPLATE") + "/xhtml/crud/create.xhtml");
            String create_content = FileUtils.readFileToString(index_f);
            create_content = create_content.replace("objclassLazyService", viewName + "LazyService");
            create_content = create_content.replace("ObjClass", this.getClassName());
            create_content = create_content.replace("objclass", this.getName());

            FileUtils.writeStringToFile(new File(this.project.getProjectFolder() + "/src/main/webapp/portal/" + this.getName() + "/create.xhtml"), create_content);
            //index_content.html
            File f = new File(Global.getConfigValue("FILE_PROJECT_TEMPLATE") + "/xhtml/crud/create_content.xhtml");
            String content = FileUtils.readFileToString(f);
//            content = content.replace("ObjClass", this.getClassName());
            String xhtml_content = "";
            for (Varible v : this.varibleController.getList_varible()) {
                xhtml_content += v.getLabelName(this.getName());
                xhtml_content += v.getInputComponent(this.getName(), viewName + "LazyService.newObject");
            }
            content = content.replace("[CONTENT]", xhtml_content);
            content = content.replace("ObjClass", this.getClassName());
            content = content.replace("objclass", this.getName());
//            logger.info(xhtml_content);

            FileUtils.writeStringToFile(new File(this.project.getProjectFolder() + "/src/main/webapp/portal/" + this.getName() + "/create_content.xhtml"), content);
        } catch (IOException ex) {
            logger.error(ex);
        }
    }

    private void writeCMSDetailView() {
        try {
            //Folder or source file
            File folder = new File(this.project.getProjectFolder() + "/src/main/webapp/portal/" + this.getName());
            folder.mkdirs();
            String viewName = this.getName().substring(0, 1).toLowerCase() + this.getName().substring(1);
            //index.html
            File index_f = new File(Global.getConfigValue("FILE_PROJECT_TEMPLATE") + "/xhtml/crud/view.xhtml");
            String view_content = FileUtils.readFileToString(index_f);
            view_content = view_content.replace("objclassLazyService", viewName + "LazyService");
            view_content = view_content.replace("objclass", this.getName());

            for (Varible v : this.varibleController.getList_varible()) {
                if (v.getPrimeKey()) {
                    view_content = view_content.replace("objectkey", v.getName());
                    break;
                }
            }
            FileUtils.writeStringToFile(new File(this.project.getProjectFolder() + "/src/main/webapp/portal/" + this.getName() + "/view.xhtml"), view_content);
            //index_content.html
            File f = new File(Global.getConfigValue("FILE_PROJECT_TEMPLATE") + "/xhtml/crud/view_content.xhtml");
            String content = FileUtils.readFileToString(f);
//            content = content.replace("ObjClass", this.getClassName());
            String xhtml_content = "";
            for (Varible v : this.varibleController.getList_varible()) {
                xhtml_content += v.getLabelName(this.getName());
                xhtml_content += v.getOutputComponent(viewName + "LazyService.selectedObject");
                xhtml_content += "\n";
            }
            content = content.replace("[CONTENT]", xhtml_content);
            content = content.replace("ObjClass", this.getClassName());
            content = content.replace("objclass", this.getName());
            for (Varible v : this.varibleController.getList_varible()) {
                if (v.getPrimeKey()) {
                    content = content.replace("objectkey", v.getName());
                    break;
                }
            }

//            logger.info(xhtml_content);
            FileUtils.writeStringToFile(new File(this.project.getProjectFolder() + "/src/main/webapp/portal/" + this.getName() + "/view_content.xhtml"), content);
        } catch (IOException ex) {
            logger.error(ex);
        }
    }

    private void writeCMSEditView() {
        try {
            //Folder or source file
            File folder = new File(this.project.getProjectFolder() + "/src/main/webapp/portal/" + this.getName());
            folder.mkdirs();
            String viewName = this.getName().substring(0, 1).toLowerCase() + this.getName().substring(1);
            //index.html
            File index_f = new File(Global.getConfigValue("FILE_PROJECT_TEMPLATE") + "/xhtml/crud/edit.xhtml");
            String edit_content = FileUtils.readFileToString(index_f);
            edit_content = edit_content.replace("objclassLazyService", viewName + "LazyService");
            edit_content = edit_content.replace("ObjClass", this.getClassName());
            edit_content = edit_content.replace("objclass", this.getName());
            for (Varible v : this.varibleController.getList_varible()) {
                if (v.getPrimeKey()) {
                    edit_content = edit_content.replace("objectkey", v.getName());
                    break;
                }
            }
            FileUtils.writeStringToFile(new File(this.project.getProjectFolder() + "/src/main/webapp/portal/" + this.getName() + "/edit.xhtml"), edit_content);
            //index_content.html
            File f = new File(Global.getConfigValue("FILE_PROJECT_TEMPLATE") + "/xhtml/crud/edit_content.xhtml");
            String content = FileUtils.readFileToString(f);
//            content = content.replace("ObjClass", this.getClassName());
            String xhtml_content = "";
            for (Varible v : this.varibleController.getList_varible()) {
                xhtml_content += v.getLabelName(this.getName());
                xhtml_content += v.getInputComponent(this.getName(), viewName + "LazyService.selectedObject");
            }
            content = content.replace("[CONTENT]", xhtml_content);
            content = content.replace("ObjClass", this.getClassName());
            content = content.replace("objclass", this.getName());

//            logger.info(xhtml_content);
            FileUtils.writeStringToFile(new File(this.project.getProjectFolder() + "/src/main/webapp/portal/" + this.getName() + "/edit_content.xhtml"), content);
        } catch (IOException ex) {
            logger.error(ex);
        }
    }

    private void generateAbstractCMSCode() {
        this.writeAbstractCMSBaseClass();
        this.writeAbstractCMSObjClass();
        this.writeAbstractCMSObjClassLazyModel();
        this.writeAbstractCMSObjClassLazyService();
        this.writeAbstractObjClassService();
        this.writeAbstractCMSDAOClass();
        this.writeLanguageFile();
        this.writeCMSIndexView();
        this.writeCMSCreateView();
        this.writeCMSEditView();
        this.writeCMSDetailView();
    }

    private void generateApiCMSCode() {
        this.writeApiCMSBaseClass();
        this.writeApiCMSObjClass();
        this.writeApiCMSObjClassLazyModel();
        this.writeApiCMSObjClassLazyService();
        this.writeApiObjClassService();
        this.writeApiCMSDAOClass();
        this.writeLanguageFile();
        this.writeCMSIndexView();
        this.writeCMSCreateView();
        this.writeCMSEditView();
        this.writeCMSDetailView();
    }

    //MySQL API Area
    private void writeMySqlApiObjClass() {
        try {
            File ObjClassTemFile = new File(Global.getConfigValue("FILE_PROJECT_TEMPLATE") + "/mysqlobject/api/obj/BaseObjClass.java");
            String content = FileUtils.readFileToString(ObjClassTemFile);
            String varible_def = "";
            for (Varible v : this.getVaribleController().getList_varible()) {
                varible_def += v.getApiVaribleDefinition();
            }
            content = content.replace("[VaribleDefinition]", varible_def);
            String varible_default = "";
            for (Varible v : this.getVaribleController().getList_varible()) {
                if (!v.getDefaultValue().equals("") && v.getDefaultValue() != null) {
                    varible_default += "instant.set" + v.getName1() + "(" + v.getDefaultValue() + ");\n";
                }
            }
            content = content.replace("[DefaultValue]", varible_default);

            String getter_setter_data = "";
            for (Varible v : this.getVaribleController().getList_varible()) {
                getter_setter_data += v.getSetter() + "\n";
                getter_setter_data += v.getGetterJson() + "\n";
            }

            content = content.replace("[SetterAndGetter]", getter_setter_data);

            for (Varible v : this.getVaribleController().getList_varible()) {
                if (v.getPrimeKey()) {
                    content = content.replace("[OBJECTKEY]", v.getName1());
                    break;
                }
            }
            content = content.replace("com.unibro.objclass", "com.unibro." + this.getName().toLowerCase());
            content = content.replace("objclass", this.getName());
            content = content.replace("ObjClass", this.getClassName());

            File file_dir = new File(this.project.getProjectFolder() + "/src/main/java/com/unibro/" + this.getClassName().toLowerCase());
            file_dir.mkdirs();
            FileUtils.writeStringToFile(new File(file_dir.getAbsolutePath() + "/" + this.getClassName() + ".java"), content);
            logger.info(content);
        } catch (IOException ex) {
            logger.error(ex);
        }
    }

    private void writeMySqlApiObjClassArrayResult() {
        try {
            File ObjClassTemFile = new File(Global.getConfigValue("FILE_PROJECT_TEMPLATE") + "/mysqlobject/api/obj/ObjClassArrayResult.java");
            String content = FileUtils.readFileToString(ObjClassTemFile);
            content = content.replace("com.unibro.objclass", "com.unibro." + this.getName().toLowerCase());
            content = content.replace("objclass", this.getName());
            content = content.replace("ObjClass", this.getClassName());

            File file_dir = new File(this.project.getProjectFolder() + "/src/main/java/com/unibro/" + this.getClassName().toLowerCase());
            file_dir.mkdirs();
            FileUtils.writeStringToFile(new File(file_dir.getAbsolutePath() + "/" + this.getClassName() + "ArrayResult.java"), content);
            logger.info(content);
        } catch (IOException ex) {
            logger.error(ex);
        }
    }

    private void writeMySqlApiObjClassDAO() {
        try {
            File ObjClassTemFile = new File(Global.getConfigValue("FILE_PROJECT_TEMPLATE") + "/mysqlobject/api/obj/ObjClassDAO.java");
            String content = FileUtils.readFileToString(ObjClassTemFile);

            for (Varible v : this.getVaribleController().getList_varible()) {
                if (v.getPrimeKey()) {
                    content = content.replace("[KEYTYPE]", v.getType());
                    break;
                }
            }
            content = content.replace("com.unibro.objclass", "com.unibro." + this.getName().toLowerCase());
            content = content.replace("objclass", this.getName());
            content = content.replace("ObjClass", this.getClassName());

            File file_dir = new File(this.project.getProjectFolder() + "/src/main/java/com/unibro/" + this.getClassName().toLowerCase());
            file_dir.mkdirs();
            FileUtils.writeStringToFile(new File(file_dir.getAbsolutePath() + "/" + this.getClassName() + "DAO.java"), content);
            logger.info(content);
        } catch (IOException ex) {
            logger.error(ex);
        }
    }

    private void writeMySqlApiObjClassDAOImpl() {
        try {
            File ObjClassTemFile = new File(Global.getConfigValue("FILE_PROJECT_TEMPLATE") + "/mysqlobject/api/obj/ObjClassDAOImpl.java");
            String content = FileUtils.readFileToString(ObjClassTemFile);
            content = content.replace("com.unibro.objclass", "com.unibro." + this.getName().toLowerCase());
            for (Varible v : this.getVaribleController().getList_varible()) {
                if (v.getPrimeKey()) {
                    content = content.replace("[KEYTYPE]", v.getType());
                    break;
                }
            }

            for (Varible v : this.getVaribleController().getList_varible()) {
                if (v.getPrimeKey()) {
                    content = content.replace("[KEY]", v.getName());
                    content = content.replace("[OBJECTKEY]", v.getName1());
                    break;
                }
            }

            content = content.replace("[CREATE_PARAM]", this.getVaribleController().getInsertJdbcPrepareStatement());
            content = content.replace("[CREATE_MASK]", this.getVaribleController().getInsertJdbcPrepareStatementMask());
            content = content.replace("[CREATE_PARAM_STR]", this.getVaribleController().getInsertJdbcPrepareStatementStr());
            content = content.replace("[CREATE_SETOBJECT]", this.getVaribleController().getInsertJdbcPrepareStatementData());

            content = content.replace("[UPDATE_PARAM_MASK]", this.getVaribleController().getUpdateJdbcPrepareStatementMask());
            content = content.replace("[UPDATE_PARAM_STR]", this.getVaribleController().getUpdateJdbcPrepareStatementStr());
            content = content.replace("[UPDATE_SETOBJECT]", this.getVaribleController().getUpdateJdbcPrepareStatementData());

            content = content.replace("[objclass]", this.getName());
            content = content.replace("objclass", this.getName());
            content = content.replace("ObjClass", this.getClassName());

            File file_dir = new File(this.project.getProjectFolder() + "/src/main/java/com/unibro/" + this.getClassName().toLowerCase());
            file_dir.mkdirs();
            FileUtils.writeStringToFile(new File(file_dir.getAbsolutePath() + "/" + this.getClassName() + "DAOImpl.java"), content);
            logger.info(content);
        } catch (IOException ex) {
            logger.error(ex);
        }
    }

    private void writeMySqlApiObjClassDAOImplForAutoIncrement() {
        try {
            File ObjClassTemFile = new File(Global.getConfigValue("FILE_PROJECT_TEMPLATE") + "/mysqlobject/api/obj/ObjClassDAOImplForAutoIncrement.java");
            String content = FileUtils.readFileToString(ObjClassTemFile);
            content = content.replace("com.unibro.objclass", "com.unibro." + this.getName().toLowerCase());
            for (Varible v : this.getVaribleController().getList_varible()) {
                if (v.getPrimeKey()) {
                    content = content.replace("[KEYTYPE]", v.getType());
                    break;
                }
            }

            for (Varible v : this.getVaribleController().getList_varible()) {
                if (v.getPrimeKey()) {
                    content = content.replace("[KEY]", v.getName());
                    content = content.replace("[OBJECTKEY]", v.getName1());
                    break;
                }
            }

            content = content.replace("[CREATE_PARAM]", this.getVaribleController().getInsertJdbcPrepareStatement());
            content = content.replace("[CREATE_MASK]", this.getVaribleController().getInsertJdbcPrepareStatementMask());
            content = content.replace("[CREATE_PARAM_STR]", this.getVaribleController().getInsertJdbcPrepareStatementStr());
            content = content.replace("[CREATE_SETOBJECT]", this.getVaribleController().getInsertJdbcPrepareStatementData());

            content = content.replace("[UPDATE_PARAM_MASK]", this.getVaribleController().getUpdateJdbcPrepareStatementMask());
            content = content.replace("[UPDATE_PARAM_STR]", this.getVaribleController().getUpdateJdbcPrepareStatementStr());
            content = content.replace("[UPDATE_SETOBJECT]", this.getVaribleController().getUpdateJdbcPrepareStatementData());

            content = content.replace("[objclass]", this.getName());
            content = content.replace("objclass", this.getName());
            content = content.replace("ObjClass", this.getClassName());

            File file_dir = new File(this.project.getProjectFolder() + "/src/main/java/com/unibro/" + this.getClassName().toLowerCase());
            file_dir.mkdirs();
            FileUtils.writeStringToFile(new File(file_dir.getAbsolutePath() + "/" + this.getClassName() + "DAOImpl.java"), content);
            logger.info(content);
        } catch (IOException ex) {
            logger.error(ex);
        }
    }

    private void writeMySqlApiObjClassController() {
        try {
            File ObjClassTemFile = new File(Global.getConfigValue("FILE_PROJECT_TEMPLATE") + "/mysqlobject/api/obj/ObjClassController.java");
            String content = FileUtils.readFileToString(ObjClassTemFile);
            String authorization_data = "String[] params = {authorization, id};";

            for (Varible v : this.getVaribleController().getList_varible()) {
                if (v.getPrimeKey()) {
                    content = content.replace("[KEYTYPE]", v.getType());
                    if (!v.getType().equals(Varible.STRING_TYPE)) {
                        authorization_data = "String[] params = {authorization, String.valueOf(id)};";
                    }
                    break;
                }
            }
            content = content.replace("[authorization_data]", authorization_data);
            content = content.replace("[objclass]", this.getName());
            content = content.replace("objclass", this.getName());
            content = content.replace("ObjClass", this.getClassName());

            File file_dir = new File(this.project.getProjectFolder() + "/src/main/java/com/unibro/" + this.getClassName().toLowerCase());
            file_dir.mkdirs();
            FileUtils.writeStringToFile(new File(file_dir.getAbsolutePath() + "/" + this.getClassName() + "Controller.java"), content);
            logger.info(content);
        } catch (IOException ex) {
            logger.error(ex);
        }
    }

    private void writeMySqlApiObjClassException() {
        try {
            File ObjClassTemFile = new File(Global.getConfigValue("FILE_PROJECT_TEMPLATE") + "/mysqlobject/api/obj/ObjClassDAOException.java");
            String content = FileUtils.readFileToString(ObjClassTemFile);

            content = content.replace("com.unibro.objclass", "com.unibro." + this.getName().toLowerCase());
            content = content.replace("[objclass]", this.getName());
            content = content.replace("objclass", this.getName());
            content = content.replace("ObjClass", this.getClassName());

            File file_dir = new File(this.project.getProjectFolder() + "/src/main/java/com/unibro/" + this.getClassName().toLowerCase());
            file_dir.mkdirs();
            FileUtils.writeStringToFile(new File(file_dir.getAbsolutePath() + "/" + this.getClassName() + "DAOException.java"), content);
            logger.info(content);
        } catch (IOException ex) {
            logger.error(ex);
        }
    }

    private void writeMySqlApiObjClassMapper() {
        try {
            File ObjClassTemFile = new File(Global.getConfigValue("FILE_PROJECT_TEMPLATE") + "/mysqlobject/api/obj/ObjClassMapper.java");
            String content = FileUtils.readFileToString(ObjClassTemFile);
            content = content.replace("com.unibro.objclass", "com.unibro." + this.getName().toLowerCase());
            String mapper_data = "";
            for (Varible v : this.getVaribleController().getList_varible()) {
                mapper_data += v.getJdbcMapper();
            }

            content = content.replace("[MAPPER_LIST]", mapper_data);

            content = content.replace("[objclass]", this.getName());
            content = content.replace("objclass", this.getName());
            content = content.replace("ObjClass", this.getClassName());

            File file_dir = new File(this.project.getProjectFolder() + "/src/main/java/com/unibro/" + this.getClassName().toLowerCase());
            file_dir.mkdirs();
            FileUtils.writeStringToFile(new File(file_dir.getAbsolutePath() + "/" + this.getClassName() + "Mapper.java"), content);
            logger.info(content);
        } catch (IOException ex) {
            logger.error(ex);
        }
    }

    private void writeMySqlApiObjClassDataSource() {
        try {
            File ObjClassTemFile = new File(Global.getConfigValue("FILE_PROJECT_TEMPLATE") + "/mysqlobject/api/datasource/Spring-ObjClass.xml");
            String content = FileUtils.readFileToString(ObjClassTemFile);
            content = content.replace("com.unibro.objclass", "com.unibro." + this.getName().toLowerCase());
            content = content.replace("[objclass]", this.getName());
            content = content.replace("objclass", this.getName());
            content = content.replace("ObjClass", this.getClassName());

            File file_dir = new File(this.project.getProjectFolder() + "/src/main/resources/database/");
            file_dir.mkdirs();
            FileUtils.writeStringToFile(new File(file_dir.getAbsolutePath() + "/Spring-" + this.getClassName() + ".xml"), content);
            logger.info(content);
        } catch (IOException ex) {
            logger.error(ex);
        }
    }

    private void generateMySqlApiCode() {
        this.writeMySqlApiObjClass();
        this.writeMySqlApiObjClassArrayResult();
        this.writeMySqlApiObjClassDAO();
        if (!this.checkAutoIncrement()) {
            this.writeMySqlApiObjClassDAOImpl();
        } else {
            this.writeMySqlApiObjClassDAOImplForAutoIncrement();
        }
        this.writeMySqlApiObjClassController();
        this.writeMySqlApiObjClassException();
        this.writeMySqlApiObjClassMapper();
        this.writeMySqlApiObjClassDataSource();
    }

    //Oracle API
    private void writeOracleApiObjClass() {
        try {
            File ObjClassTemFile = new File(Global.getConfigValue("FILE_PROJECT_TEMPLATE") + "/oracleobject/api/obj/ObjClass.java");
            String content = FileUtils.readFileToString(ObjClassTemFile);
            content = content.replace("com.unibro.objclass", "com.unibro." + this.getName().toLowerCase());
            String varible_def = "";
            for (Varible v : this.getVaribleController().getList_varible()) {
                varible_def += v.getApiVaribleDefinition();
            }
            content = content.replace("[VaribleDefinition]", varible_def);
            String varible_default = "";
            for (Varible v : this.getVaribleController().getList_varible()) {
                if (!v.getDefaultValue().equals("") && v.getDefaultValue() != null) {
                    varible_default += "instant.set" + v.getName1() + "(" + v.getDefaultValue() + ");\n";
                }
            }
            content = content.replace("[DefaultValue]", varible_default);

            String getter_setter_data = "";
            for (Varible v : this.getVaribleController().getList_varible()) {
                getter_setter_data += v.getSetter() + "\n";
                getter_setter_data += v.getGetterJson() + "\n";
            }

            content = content.replace("[SetterAndGetter]", getter_setter_data);

            for (Varible v : this.getVaribleController().getList_varible()) {
                if (v.getPrimeKey()) {
                    content = content.replace("[OBJECTKEY]", v.getName1());
                    break;
                }
            }

            content = content.replace("objclass", this.getName());
            content = content.replace("ObjClass", this.getClassName());

            File file_dir = new File(this.project.getProjectFolder() + "/src/main/java/com/unibro/" + this.getClassName().toLowerCase());
            file_dir.mkdirs();
            FileUtils.writeStringToFile(new File(file_dir.getAbsolutePath() + "/" + this.getClassName() + ".java"), content);
            logger.info(content);
        } catch (IOException ex) {
            logger.error(ex);
        }
    }

    private void writeOracleApiObjClassArrayResult() {
        try {
            File ObjClassTemFile = new File(Global.getConfigValue("FILE_PROJECT_TEMPLATE") + "/oracleobject/api/obj/ObjClassArrayResult.java");
            String content = FileUtils.readFileToString(ObjClassTemFile);
            content = content.replace("com.unibro.objclass", "com.unibro." + this.getName().toLowerCase());
            content = content.replace("objclass", this.getName());
            content = content.replace("ObjClass", this.getClassName());

            File file_dir = new File(this.project.getProjectFolder() + "/src/main/java/com/unibro/" + this.getClassName().toLowerCase());
            file_dir.mkdirs();
            FileUtils.writeStringToFile(new File(file_dir.getAbsolutePath() + "/" + this.getClassName() + "ArrayResult.java"), content);
            logger.info(content);
        } catch (IOException ex) {
            logger.error(ex);
        }
    }

    private void writeOracleApiObjClassDAO() {
        try {
            File ObjClassTemFile = new File(Global.getConfigValue("FILE_PROJECT_TEMPLATE") + "/oracleobject/api/obj/ObjClassDAO.java");
            String content = FileUtils.readFileToString(ObjClassTemFile);
            content = content.replace("com.unibro.objclass", "com.unibro." + this.getName().toLowerCase());

            for (Varible v : this.getVaribleController().getList_varible()) {
                if (v.getPrimeKey()) {
                    content = content.replace("[KEYTYPE]", v.getType());
                    break;
                }
            }

            content = content.replace("objclass", this.getName());
            content = content.replace("ObjClass", this.getClassName());

            File file_dir = new File(this.project.getProjectFolder() + "/src/main/java/com/unibro/" + this.getClassName().toLowerCase());
            file_dir.mkdirs();
            FileUtils.writeStringToFile(new File(file_dir.getAbsolutePath() + "/" + this.getClassName() + "DAO.java"), content);
            logger.info(content);
        } catch (IOException ex) {
            logger.error(ex);
        }
    }

    private void writeOracleApiObjClassDAOImpl() {
        try {
            File ObjClassTemFile = new File(Global.getConfigValue("FILE_PROJECT_TEMPLATE") + "/oracleobject/api/obj/ObjClassDAOImpl.java");
            String content = FileUtils.readFileToString(ObjClassTemFile);
            content = content.replace("com.unibro.objclass", "com.unibro." + this.getName().toLowerCase());
            for (Varible v : this.getVaribleController().getList_varible()) {
                if (v.getPrimeKey()) {
                    content = content.replace("[KEYTYPE]", v.getType());
                    break;
                }
            }

            for (Varible v : this.getVaribleController().getList_varible()) {
                if (v.getPrimeKey()) {
                    content = content.replace("[KEY]", v.getName());
                    content = content.replace("[OBJECTKEY]", v.getName1());
                    break;
                }
            }

            content = content.replace("[CREATE_PARAM]", this.getVaribleController().getInsertJdbcPrepareStatement());
            content = content.replace("[CREATE_MASK]", this.getVaribleController().getInsertJdbcPrepareStatementMask());
            content = content.replace("[CREATE_PARAM_STR]", this.getVaribleController().getInsertJdbcPrepareStatementStr());
            content = content.replace("[CREATE_SETOBJECT]", this.getVaribleController().getInsertJdbcPrepareStatementData());

            content = content.replace("[UPDATE_PARAM_MASK]", this.getVaribleController().getUpdateJdbcPrepareStatementMask());
            content = content.replace("[UPDATE_PARAM_STR]", this.getVaribleController().getUpdateJdbcPrepareStatementStr());
            content = content.replace("[UPDATE_SETOBJECT]", this.getVaribleController().getUpdateJdbcPrepareStatementData());

            content = content.replace("[objclass]", this.getName());
            content = content.replace("objclass", this.getName());
            content = content.replace("ObjClass", this.getClassName());

            File file_dir = new File(this.project.getProjectFolder() + "/src/main/java/com/unibro/" + this.getClassName().toLowerCase());
            file_dir.mkdirs();
            FileUtils.writeStringToFile(new File(file_dir.getAbsolutePath() + "/" + this.getClassName() + "DAOImpl.java"), content);
            logger.info(content);
        } catch (IOException ex) {
            logger.error(ex);
        }
    }

    private void writeOracleApiObjClassDAOImplForAutoIncrement() {
        try {
            File ObjClassTemFile = new File(Global.getConfigValue("FILE_PROJECT_TEMPLATE") + "/oracleobject/api/obj/ObjClassDAOImplForAutoIncrement.java");
            String content = FileUtils.readFileToString(ObjClassTemFile);
            content = content.replace("com.unibro.objclass", "com.unibro." + this.getName().toLowerCase());
            for (Varible v : this.getVaribleController().getList_varible()) {
                if (v.getPrimeKey()) {
                    content = content.replace("[KEYTYPE]", v.getType());
                    break;
                }
            }

            for (Varible v : this.getVaribleController().getList_varible()) {
                if (v.getPrimeKey()) {
                    content = content.replace("[KEY]", v.getName());
                    content = content.replace("[OBJECTKEY]", v.getName1());
                    break;
                }
            }

            content = content.replace("[CREATE_PARAM]", this.getVaribleController().getInsertJdbcPrepareStatement());
            content = content.replace("[CREATE_MASK]", this.getVaribleController().getInsertJdbcPrepareStatementMask());
            content = content.replace("[CREATE_PARAM_STR]", this.getVaribleController().getInsertJdbcPrepareStatementStr());
            content = content.replace("[CREATE_SETOBJECT]", this.getVaribleController().getInsertJdbcPrepareStatementData());

            content = content.replace("[UPDATE_PARAM_MASK]", this.getVaribleController().getUpdateJdbcPrepareStatementMask());
            content = content.replace("[UPDATE_PARAM_STR]", this.getVaribleController().getUpdateJdbcPrepareStatementStr());
            content = content.replace("[UPDATE_SETOBJECT]", this.getVaribleController().getUpdateJdbcPrepareStatementData());

            content = content.replace("[objclass]", this.getName());
            content = content.replace("objclass", this.getName());
            content = content.replace("ObjClass", this.getClassName());

            File file_dir = new File(this.project.getProjectFolder() + "/src/main/java/com/unibro/" + this.getClassName().toLowerCase());
            file_dir.mkdirs();
            FileUtils.writeStringToFile(new File(file_dir.getAbsolutePath() + "/" + this.getClassName() + "DAOImpl.java"), content);
            logger.info(content);
        } catch (IOException ex) {
            logger.error(ex);
        }
    }

    private void writeOracleApiObjClassController() {
        try {
            File ObjClassTemFile = new File(Global.getConfigValue("FILE_PROJECT_TEMPLATE") + "/oracleobject/api/obj/ObjClassController.java");
            String content = FileUtils.readFileToString(ObjClassTemFile);
            content = content.replace("com.unibro.objclass", "com.unibro." + this.getName().toLowerCase());
            String authorization_data = "String[] params = {authorization, id};";

            for (Varible v : this.getVaribleController().getList_varible()) {
                if (v.getPrimeKey()) {
                    content = content.replace("[KEYTYPE]", v.getType());
                    if (!v.getType().equals(Varible.STRING_TYPE)) {
                        authorization_data = "String[] params = {authorization, String.valueOf(id)};";
                    }
                    break;
                }
            }
            content = content.replace("[authorization_data]", authorization_data);
            content = content.replace("[objclass]", this.getName());
            content = content.replace("objclass", this.getName());
            content = content.replace("ObjClass", this.getClassName());

            File file_dir = new File(this.project.getProjectFolder() + "/src/main/java/com/unibro/" + this.getClassName().toLowerCase());
            file_dir.mkdirs();
            FileUtils.writeStringToFile(new File(file_dir.getAbsolutePath() + "/" + this.getClassName() + "Controller.java"), content);
            logger.info(content);
        } catch (IOException ex) {
            logger.error(ex);
        }
    }

    private void writeOracleApiObjClassException() {
        try {
            File ObjClassTemFile = new File(Global.getConfigValue("FILE_PROJECT_TEMPLATE") + "/oracleobject/api/obj/ObjClassDAOException.java");
            String content = FileUtils.readFileToString(ObjClassTemFile);
            content = content.replace("com.unibro.objclass", "com.unibro." + this.getName().toLowerCase());

            content = content.replace("[objclass]", this.getName());
            content = content.replace("objclass", this.getName());
            content = content.replace("ObjClass", this.getClassName());

            File file_dir = new File(this.project.getProjectFolder() + "/src/main/java/com/unibro/" + this.getClassName().toLowerCase());
            file_dir.mkdirs();
            FileUtils.writeStringToFile(new File(file_dir.getAbsolutePath() + "/" + this.getClassName() + "DAOException.java"), content);
            logger.info(content);
        } catch (IOException ex) {
            logger.error(ex);
        }
    }

    private void writeOracleApiObjClassMapper() {
        try {
            File ObjClassTemFile = new File(Global.getConfigValue("FILE_PROJECT_TEMPLATE") + "/oracleobject/api/obj/ObjClassMapper.java");
            String content = FileUtils.readFileToString(ObjClassTemFile);
            content = content.replace("com.unibro.objclass", "com.unibro." + this.getName().toLowerCase());

            String mapper_data = "";
            for (Varible v : this.getVaribleController().getList_varible()) {
                mapper_data += v.getJdbcMapper();
            }

            content = content.replace("[MAPPER_LIST]", mapper_data);

            content = content.replace("[objclass]", this.getName());
            content = content.replace("objclass", this.getName());
            content = content.replace("ObjClass", this.getClassName());

            File file_dir = new File(this.project.getProjectFolder() + "/src/main/java/com/unibro/" + this.getClassName().toLowerCase());
            file_dir.mkdirs();
            FileUtils.writeStringToFile(new File(file_dir.getAbsolutePath() + "/" + this.getClassName() + "Mapper.java"), content);
            logger.info(content);
        } catch (IOException ex) {
            logger.error(ex);
        }
    }

    private void writeOracleApiObjClassDataSource() {
        try {
            File ObjClassTemFile = new File(Global.getConfigValue("FILE_PROJECT_TEMPLATE") + "/oracleobject/api/datasource/Spring-ObjClass.xml");
            String content = FileUtils.readFileToString(ObjClassTemFile);
            content = content.replace("com.unibro.objclass", "com.unibro." + this.getName().toLowerCase());
            content = content.replace("[objclass]", this.getName());
            content = content.replace("objclass", this.getName());
            content = content.replace("ObjClass", this.getClassName());

            File file_dir = new File(this.project.getProjectFolder() + "/src/main/resources/database/");
            file_dir.mkdirs();
            FileUtils.writeStringToFile(new File(file_dir.getAbsolutePath() + "/Spring-" + this.getClassName() + ".xml"), content);
            logger.info(content);
        } catch (IOException ex) {
            logger.error(ex);
        }
    }

    private void generateOracleApiCode() {
        this.writeOracleApiObjClass();
        this.writeOracleApiObjClassArrayResult();
        this.writeOracleApiObjClassDAO();
        this.writeOracleApiObjClassDAOImpl();
        this.writeOracleApiObjClassController();
        this.writeOracleApiObjClassException();
        this.writeOracleApiObjClassMapper();
        this.writeOracleApiObjClassDataSource();
    }

    //Abstract Java class
    private void writeApiCMSBaseClass() {
        try {
            File ObjClassTemFile = new File(Global.getConfigValue("FILE_PROJECT_TEMPLATE") + "/apiobject/ObjClass/BaseObjClass.java");
            String content = FileUtils.readFileToString(ObjClassTemFile);
            content = content.replace("com.unibro.objclass", "com.unibro." + this.getName().toLowerCase());
            String varible_def = "";
            for (Varible v : this.getVaribleController().getList_varible()) {
                varible_def += v.getVaribleDefinition();
            }
            content = content.replace("[VaribleDefinition]", varible_def);

            String getter_setter_data = "";
            for (Varible v : this.getVaribleController().getList_varible()) {
                getter_setter_data += v.getSetter() + "\n";
                getter_setter_data += v.getGetter() + "\n";
            }

            content = content.replace("[SetterAndGetter]", getter_setter_data);

            for (Varible v : this.getVaribleController().getList_varible()) {
                if (v.getPrimeKey()) {
                    content = content.replace("[OBJECTKEY]", v.getName1());
                    break;
                }
            }

            content = content.replace("BaseObjClass", "Base" + this.getClassName());
            content = content.replace("ObjClass", this.getClassName());
            content = content.replace("objclass", this.getName());

            File file_dir = new File(this.project.getProjectFolder() + "/src/main/java/com/unibro/" + this.getClassName().toLowerCase());
            file_dir.mkdirs();
            FileUtils.writeStringToFile(new File(file_dir.getAbsolutePath() + "/Base" + this.getClassName() + ".java"), content);
            logger.info(content);
        } catch (IOException ex) {
            logger.error(ex);
        }
    }

    private void writeApiCMSObjClass() {
        try {
            File ObjClassTemFile = new File(Global.getConfigValue("FILE_PROJECT_TEMPLATE") + "/apiobject/ObjClass/ObjClass.java");
            String content = FileUtils.readFileToString(ObjClassTemFile);
            content = content.replace("com.unibro.objclass", "com.unibro." + this.getName().toLowerCase());
            for (Varible v : this.getVaribleController().getList_varible()) {
                if (v.getPrimeKey()) {
                    content = content.replace("[OBJECTKEY]", v.getName1());
                    break;
                }
            }
            content = content.replace("ObjClass", this.getClassName());
            content = content.replace("objclass", this.getName());

            File file_dir = new File(this.project.getProjectFolder() + "/src/main/java/com/unibro/" + this.getClassName().toLowerCase());
            file_dir.mkdirs();
            FileUtils.writeStringToFile(new File(file_dir.getAbsolutePath() + "/" + this.getClassName() + ".java"), content);
            logger.info(content);
        } catch (IOException ex) {
            logger.error(ex);
        }
    }

    private void writeApiCMSDAOClass() {
        try {
            File ObjClassTemFile = new File(Global.getConfigValue("FILE_PROJECT_TEMPLATE") + "/apiobject/ObjClass/ObjClassDAO.java");
            String content = FileUtils.readFileToString(ObjClassTemFile);
            content = content.replace("com.unibro.objclass", "com.unibro." + this.getName().toLowerCase());
            content = content.replace("ObjClass", this.getClassName());
            content = content.replace("objclass", this.getName());
            for (Varible v : this.getVaribleController().getList_varible()) {
                if (v.getPrimeKey()) {
                    content = content.replace("[OBJECTKEY]", v.getName1());
                    break;
                }
            }
            File file_dir = new File(this.project.getProjectFolder() + "/src/main/java/com/unibro/" + this.getClassName().toLowerCase());
            file_dir.mkdirs();
            FileUtils.writeStringToFile(new File(file_dir.getAbsolutePath() + "/" + this.getClassName() + "DAO.java"), content);
            logger.info(content);
        } catch (IOException ex) {
            logger.error(ex);
        }
    }

    private void writeApiCMSObjClassLazyModel() {
        try {
            File ObjClassTemFile = new File(Global.getConfigValue("FILE_PROJECT_TEMPLATE") + "/apiobject/ObjClass/ObjClassLazyModel.java");
            String content = FileUtils.readFileToString(ObjClassTemFile);
            content = content.replace("com.unibro.objclass", "com.unibro." + this.getName().toLowerCase());
            for (Varible v : this.getVaribleController().getList_varible()) {
                if (v.getPrimeKey()) {
                    content = content.replace("[OBJECTKEY]", v.getName1());
                    break;
                }
            }
            content = content.replace("objclass", this.getName());
            content = content.replace("ObjClass", this.getClassName());

            File file_dir = new File(this.project.getProjectFolder() + "/src/main/java/com/unibro/" + this.getClassName().toLowerCase());
            file_dir.mkdirs();
            FileUtils.writeStringToFile(new File(file_dir.getAbsolutePath() + "/" + this.getClassName() + "LazyModel.java"), content);
            logger.info(content);
        } catch (IOException ex) {
            logger.error(ex);
        }
    }

    private void writeApiCMSObjClassLazyService() {
        try {
            File ObjClassTemFile = new File(Global.getConfigValue("FILE_PROJECT_TEMPLATE") + "/apiobject/ObjClass/ObjClassLazyService.java");
            String content = FileUtils.readFileToString(ObjClassTemFile);
            content = content.replace("com.unibro.objclass", "com.unibro." + this.getName().toLowerCase());
            for (Varible v : this.getVaribleController().getList_varible()) {
                if (v.getPrimeKey()) {
                    content = content.replace("[OBJECTKEY]", v.getName1());
                    break;
                }
            }
            content = content.replace("ObjClass", this.getClassName());
            content = content.replace("objclass", this.getName());
            File file_dir = new File(this.project.getProjectFolder() + "/src/main/java/com/unibro/" + this.getClassName().toLowerCase());
            file_dir.mkdirs();
            FileUtils.writeStringToFile(new File(file_dir.getAbsolutePath() + "/" + this.getClassName() + "LazyService.java"), content);
            logger.info(content);
        } catch (IOException ex) {
            logger.error(ex);
        }
    }

    private void writeApiObjClassService() {
        try {
            File ObjClassTemFile = new File(Global.getConfigValue("FILE_PROJECT_TEMPLATE") + "/apiobject/ObjClass/ObjClassService.java");
            String content = FileUtils.readFileToString(ObjClassTemFile);
            content = content.replace("com.unibro.objclass", "com.unibro." + this.getName().toLowerCase());
            for (Varible v : this.getVaribleController().getList_varible()) {
                if (v.getPrimeKey()) {
                    content = content.replace("[OBJECTKEY]", v.getName1());
                    break;
                }
            }
            content = content.replace("ObjClass", this.getClassName());
            content = content.replace("objclass", this.getName());
            File file_dir = new File(this.project.getProjectFolder() + "/src/main/java/com/unibro/" + this.getClassName().toLowerCase());
            file_dir.mkdirs();
            FileUtils.writeStringToFile(new File(file_dir.getAbsolutePath() + "/" + this.getClassName() + "Service.java"), content);
            logger.info(content);
        } catch (IOException ex) {
            logger.error(ex);
        }
    }

    //Elastic API Area
    private void writeElasticApiObjClass() {
        try {
            File ObjClassTemFile = new File(Global.getConfigValue("FILE_PROJECT_TEMPLATE") + "/elasticobject/ObjClass.java");
            String content = FileUtils.readFileToString(ObjClassTemFile);
            content = content.replace("com.unibro.objclass", "com.unibro." + this.getName().toLowerCase());
            String varible_def = "";
            for (Varible v : this.getVaribleController().getList_varible()) {
                varible_def += v.getApiVaribleDefinition();
            }
            content = content.replace("[VaribleDefinition]", varible_def);
            String varible_default = "";
            for (Varible v : this.getVaribleController().getList_varible()) {
                if (!v.getDefaultValue().equals("") && v.getDefaultValue() != null) {
                    varible_default += "instant.set" + v.getName1() + "(" + v.getDefaultValue() + ");\n";
                }
            }
            content = content.replace("[DefaultValue]", varible_default);

            String getter_setter_data = "";
            for (Varible v : this.getVaribleController().getList_varible()) {
                getter_setter_data += v.getSetter() + "\n";
                getter_setter_data += v.getGetterJson() + "\n";
            }
            content = content.replace("[SetterAndGetter]", getter_setter_data);

            String map_put_data = "";
            for (Varible v : this.getVaribleController().getList_varible()) {
                map_put_data += v.getMapPut();
            }

            content = content.replace("[MapPut]", map_put_data);

            for (Varible v : this.getVaribleController().getList_varible()) {
                if (v.getPrimeKey()) {
                    content = content.replace("[OBJECTKEY]", v.getName1());
                    break;
                }
            }

            content = content.replace("objclass", this.getName());
            content = content.replace("ObjClass", this.getClassName());

            File file_dir = new File(this.project.getProjectFolder() + "/src/main/java/com/unibro/" + this.getClassName().toLowerCase());
            file_dir.mkdirs();
            FileUtils.writeStringToFile(new File(file_dir.getAbsolutePath() + "/" + this.getClassName() + ".java"), content);
            logger.info(content);
        } catch (IOException ex) {
            logger.error(ex);
        }
    }

    private void writeElasticApiObjClassDAO() {
        try {
            File ObjClassTemFile = new File(Global.getConfigValue("FILE_PROJECT_TEMPLATE") + "/elasticobject/ObjClassDAO.java");
            String content = FileUtils.readFileToString(ObjClassTemFile);
            content = content.replace("com.unibro.objclass", "com.unibro." + this.getName().toLowerCase());
            for (Varible v : this.getVaribleController().getList_varible()) {
                if (v.getPrimeKey()) {
                    content = content.replace("[KEYTYPE]", v.getType());
                    break;
                }
            }

            content = content.replace("objclass", this.getName());
            content = content.replace("ObjClass", this.getClassName());

            File file_dir = new File(this.project.getProjectFolder() + "/src/main/java/com/unibro/" + this.getClassName().toLowerCase());
            file_dir.mkdirs();
            FileUtils.writeStringToFile(new File(file_dir.getAbsolutePath() + "/" + this.getClassName() + "DAO.java"), content);
            logger.info(content);
        } catch (IOException ex) {
            logger.error(ex);
        }
    }

    private void writeElasticApiObjClassDAOImpl() {
        try {
            File ObjClassTemFile = new File(Global.getConfigValue("FILE_PROJECT_TEMPLATE") + "/elasticobject/ObjClassDAOImpl.java");
            String content = FileUtils.readFileToString(ObjClassTemFile);
            content = content.replace("com.unibro.objclass", "com.unibro." + this.getName().toLowerCase());
            for (Varible v : this.getVaribleController().getList_varible()) {
                if (v.getPrimeKey()) {
                    content = content.replace("[KEYTYPE]", v.getType());
                    break;
                }
            }

            for (Varible v : this.getVaribleController().getList_varible()) {
                if (v.getPrimeKey()) {
                    content = content.replace("[KEY]", v.getName());
                    content = content.replace("[OBJECTKEY]", v.getName1());
                    break;
                }
            }

            content = content.replace("[objclass]", this.getName());
            content = content.replace("objclass", this.getName());
            content = content.replace("ObjClass", this.getClassName());

            File file_dir = new File(this.project.getProjectFolder() + "/src/main/java/com/unibro/" + this.getClassName().toLowerCase());
            file_dir.mkdirs();
            FileUtils.writeStringToFile(new File(file_dir.getAbsolutePath() + "/" + this.getClassName() + "DAOImpl.java"), content);
            logger.info(content);
        } catch (IOException ex) {
            logger.error(ex);
        }
    }

    private void writeElasticApiObjClassController() {
        try {
            File ObjClassTemFile = new File(Global.getConfigValue("FILE_PROJECT_TEMPLATE") + "/elasticobject/ObjClassController.java");
            String content = FileUtils.readFileToString(ObjClassTemFile);
            content = content.replace("com.unibro.objclass", "com.unibro." + this.getName().toLowerCase());
            String authorization_data = "String[] params = {authorization, id};";

            for (Varible v : this.getVaribleController().getList_varible()) {
                if (v.getPrimeKey()) {
                    content = content.replace("[KEYTYPE]", v.getType());
                    if (!v.getType().equals(Varible.STRING_TYPE)) {
                        authorization_data = "String[] params = {authorization, String.valueOf(id)};";
                    }
                    break;
                }
            }
            content = content.replace("[authorization_data]", authorization_data);
            content = content.replace("[objclass]", this.getName());
            content = content.replace("objclass", this.getName());
            content = content.replace("ObjClass", this.getClassName());

            File file_dir = new File(this.project.getProjectFolder() + "/src/main/java/com/unibro/" + this.getClassName().toLowerCase());
            file_dir.mkdirs();
            FileUtils.writeStringToFile(new File(file_dir.getAbsolutePath() + "/" + this.getClassName() + "Controller.java"), content);
            logger.info(content);
        } catch (IOException ex) {
            logger.error(ex);
        }
    }

    private void writeElasticApiObjClassException() {
        try {
            File ObjClassTemFile = new File(Global.getConfigValue("FILE_PROJECT_TEMPLATE") + "/elasticobject/ObjClassDAOException.java");
            String content = FileUtils.readFileToString(ObjClassTemFile);
            content = content.replace("com.unibro.objclass", "com.unibro." + this.getName().toLowerCase());
            content = content.replace("[objclass]", this.getName());
            content = content.replace("objclass", this.getName());
            content = content.replace("ObjClass", this.getClassName());

            File file_dir = new File(this.project.getProjectFolder() + "/src/main/java/com/unibro/" + this.getClassName().toLowerCase());
            file_dir.mkdirs();
            FileUtils.writeStringToFile(new File(file_dir.getAbsolutePath() + "/" + this.getClassName() + "DAOException.java"), content);
            logger.info(content);
        } catch (IOException ex) {
            logger.error(ex);
        }
    }

    private void generateElasticApiCode() {
        this.writeElasticApiObjClass();
        this.writeElasticApiObjClassController();
        this.writeElasticApiObjClassDAO();
        this.writeElasticApiObjClassDAOImpl();
        this.writeElasticApiObjClassException();
    }

    public void generateCode() {
        if (project.getProjectType().equals("CMS")) {
            if (this.getProject().getDataAccessType().equals(Project.ABSTRACT_ACCESS)) {
                this.generateAbstractCMSCode();
            }
            if (this.getProject().getDataAccessType().equals(Project.API_ACCESS)) {
                this.generateApiCMSCode();
            }

        }
        if (project.getProjectType().equals("API")) {
            if (this.getProject().getDataAccessType().equals(Project.MYSQL_ACCESS)) {
                this.generateMySqlApiCode();
            }
            if (this.getProject().getDataAccessType().equals(Project.ELASTIC_ACCESS)) {
                this.generateElasticApiCode();
            }
            if (this.getProject().getDataAccessType().equals(Project.ORACLE_ACCESS)) {
                this.generateOracleApiCode();
            }
        }
        FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "Operation is completed", "");
        FacesContext.getCurrentInstance().addMessage(null, msg);
    }

}
