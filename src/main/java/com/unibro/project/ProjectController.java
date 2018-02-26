package com.unibro.project;

import com.google.common.io.Files;
import com.unibro.objtemplate.ObjTemplateDAO;
import com.unibro.utils.Global;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.event.RowEditEvent;
import org.primefaces.model.UploadedFile;

/**
 *
 * @author Nguyen Duc Tho
 */
@ManagedBean
@ViewScoped
public class ProjectController implements Serializable, Converter {

    private List<Project> objects;
    private Project selectedObject = new Project();
    private Project[] selectedObjects;
    private Project newObject = new Project();
    private String selectedId;
    private Logger logger = Logger.getLogger(this.getClass().getName());

    public ProjectController() {
//        this.loadObjects();
    }

    public void initSelectedObject() {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        if (!facesContext.isPostback() && !facesContext.isValidationFailed()) {
            ProjectDAO dao = new ProjectDAO();
            this.selectedObject = dao.getObjectByKey(this.getSelectedId());
        }
    }

    public final void loadObjects() {
        ProjectDAO dao = new ProjectDAO();
        this.objects = dao.load("ORDER BY createdtime DESC");
    }

    public List<Project> getAllProject() {
        ProjectDAO dao = new ProjectDAO();
        return dao.load("ORDER BY createdtime DESC");
    }

    public void setObjects(List<Project> objects) {
        this.objects = objects;
    }

    public List<Project> getObjects() {
        return objects;
    }

    public void setNewObject(Project newObject) {
        this.newObject = newObject;
    }

    public Project getNewObject() {
        return newObject;
    }

    public void setSelectedObject(Project selectedObject) {
        this.selectedObject = selectedObject;
    }

    public Project getSelectedObject() {
        return selectedObject;
    }

    public void setSelectedObjects(Project[] selectedObjects) {
        this.selectedObjects = selectedObjects;
    }

    public Project[] getSelectedObjects() {
        return selectedObjects;
    }

    public void setSelectedId(String selectedId) {
        this.selectedId = selectedId;
    }

    public String getSelectedId() {
        return selectedId;
    }

    public void createObject() {
        if (this.getNewObject() != null) {
            this.getNewObject().setProjectid(this.getNewObject().getName().toLowerCase());
            this.getNewObject().setProjectFolder(Global.getConfigValue("FILE_PROJECT_PATH") + this.getNewObject().getName());

//            File f = new File(this.getNewObject().getProjectFolder());
//            f.mkdirs();
            ProjectDAO dao = new ProjectDAO();
            if (dao.create(getNewObject())) {
                this.copyProject();
                FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "Create project success", "");
                FacesContext.getCurrentInstance().addMessage(null, msg);
            } else {
                FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Create project fail", "");
                FacesContext.getCurrentInstance().addMessage(null, msg);
            }
        }
    }

    private void copyProject() {
        if (this.getNewObject().getProjectType().equals("CMS")) {
            if (this.getNewObject().getDataAccessType().equals(Project.ABSTRACT_ACCESS)) {
                this.copyProjectCMSAbstract();
            }
            if (this.getNewObject().getDataAccessType().equals(Project.API_ACCESS)) {
                this.copyProjectCMSApi();
            }
        }
        if (this.getNewObject().getProjectType().equals("API")) {
            if (this.getNewObject().getDataAccessType().equals(Project.MYSQL_ACCESS)) {
                this.copyApiMySQLProject();
            }
            if (this.getNewObject().getDataAccessType().equals(Project.ELASTIC_ACCESS)) {
                this.copyApiElasticProject();
            }
            if (this.getNewObject().getDataAccessType().equals(Project.ORACLE_ACCESS)) {
                this.copyApiOracleProject();
            }
        }
    }

    private void copyApiMySQLProject() {
        try {
            File src = new File(Global.getConfigValue("FILE_PROJECT_TEMPLATE") + "autoproject_api_template");
            logger.info(src.getAbsolutePath());
            File dst = new File(Global.getConfigValue("FILE_PROJECT_PATH"));
            logger.info(dst.getAbsolutePath());
            FileUtils.copyDirectoryToDirectory(src, dst);
            File current_dst = new File(Global.getConfigValue("FILE_PROJECT_PATH") + "autoproject_api_template");
            Files.move(current_dst, new File(Global.getConfigValue("FILE_PROJECT_PATH") + this.getNewObject().getName()));
            //Replace project name in log4j
            File log4j = new File(Global.getConfigValue("FILE_PROJECT_PATH") + this.getNewObject().getName() + "/src/main/resources/log4j.properties");
            File pomFile = new File(Global.getConfigValue("FILE_PROJECT_PATH") + this.getNewObject().getName() + "/pom.xml");
            Global.replaceString(log4j, "projectname", this.getNewObject().getName());
            Global.replaceString(pomFile, "autoprojectapitemplate", this.getNewObject().getProjectid());
        } catch (IOException ex) {
            logger.error(ex);
        }
    }
    
    private void copyApiOracleProject() {
        try {
            File src = new File(Global.getConfigValue("FILE_PROJECT_TEMPLATE") + "autoproject_api_oracle_template");
            logger.info(src.getAbsolutePath());
            File dst = new File(Global.getConfigValue("FILE_PROJECT_PATH"));
            logger.info(dst.getAbsolutePath());
            FileUtils.copyDirectoryToDirectory(src, dst);
            File current_dst = new File(Global.getConfigValue("FILE_PROJECT_PATH") + "autoproject_api_oracle_template");
            Files.move(current_dst, new File(Global.getConfigValue("FILE_PROJECT_PATH") + this.getNewObject().getName()));
            //Replace project name in log4j
            File log4j = new File(Global.getConfigValue("FILE_PROJECT_PATH") + this.getNewObject().getName() + "/src/main/resources/log4j.properties");
            File pomFile = new File(Global.getConfigValue("FILE_PROJECT_PATH") + this.getNewObject().getName() + "/pom.xml");
            Global.replaceString(log4j, "projectname", this.getNewObject().getName());
            Global.replaceString(pomFile, "autoprojectapitemplate", this.getNewObject().getProjectid());
        } catch (IOException ex) {
            logger.error(ex);
        }
    }

    private void copyApiElasticProject() {
        try {
            File src = new File(Global.getConfigValue("FILE_PROJECT_TEMPLATE") + "autoproject_api_elastic_template");
            logger.info(src.getAbsolutePath());
            File dst = new File(Global.getConfigValue("FILE_PROJECT_PATH"));
            logger.info(dst.getAbsolutePath());
            FileUtils.copyDirectoryToDirectory(src, dst);
            File current_dst = new File(Global.getConfigValue("FILE_PROJECT_PATH") + "autoproject_api_elastic_template");
            Files.move(current_dst, new File(Global.getConfigValue("FILE_PROJECT_PATH") + this.getNewObject().getName()));
            //Replace project name in log4j
            File log4j = new File(Global.getConfigValue("FILE_PROJECT_PATH") + this.getNewObject().getName() + "/src/main/resources/log4j.properties");
            File pomFile = new File(Global.getConfigValue("FILE_PROJECT_PATH") + this.getNewObject().getName() + "/pom.xml");
            Global.replaceString(log4j, "projectname", this.getNewObject().getName());
            Global.replaceString(pomFile, "autoprojectapitemplate", this.getNewObject().getProjectid());
        } catch (IOException ex) {
            logger.error(ex);
        }
    }

    private void copyProjectCMSAbstract() {
        try {
            File src = new File(Global.getConfigValue("FILE_PROJECT_TEMPLATE") + "autoproject_cms_abstract_template");
            File dst = new File(Global.getConfigValue("FILE_PROJECT_PATH"));
            FileUtils.copyDirectoryToDirectory(src, dst);
            File current_dst = new File(Global.getConfigValue("FILE_PROJECT_PATH") + "autoproject_cms_abstract_template");
            Files.move(current_dst, new File(Global.getConfigValue("FILE_PROJECT_PATH") + this.getNewObject().getName()));
            //Replace project name in log4j
            File log4j = new File(Global.getConfigValue("FILE_PROJECT_PATH") + this.getNewObject().getName() + "/src/main/resources/config/log4j.properties");
            Global.replaceString(log4j, "projectname", this.getNewObject().getName());
            File config4j = new File(Global.getConfigValue("FILE_PROJECT_PATH") + this.getNewObject().getName() + "/src/main/resources/config/config.properties");
            Global.replaceString(config4j, "projectname", this.getNewObject().getName());
            File meta_inf = new File(Global.getConfigValue("FILE_PROJECT_PATH") + this.getNewObject().getName() + "/src/main/webapp/META-INF/context.xml");
            Global.replaceString(meta_inf, "projectname", this.getNewObject().getName());
            File template = new File(Global.getConfigValue("FILE_PROJECT_PATH") + this.getNewObject().getName() + "/src/main/webapp/WEB-INF/template.xhtml");
            Global.replaceString(template, "projectname", this.getNewObject().getName());
            File footer = new File(Global.getConfigValue("FILE_PROJECT_PATH") + this.getNewObject().getName() + "/src/main/webapp/WEB-INF/footer.xhtml");
            Global.replaceString(footer, "projectname", this.getNewObject().getName());
            File pomFile = new File(Global.getConfigValue("FILE_PROJECT_PATH") + this.getNewObject().getName() + "/pom.xml");
            Global.replaceString(pomFile, "autoprojecttemplate", this.getNewObject().getProjectid());
        } catch (IOException ex) {
            logger.error(ex);
        }
    }

    private void copyProjectCMSApi() {
        try {
            logger.info("Start copy api cms project");
            File src = new File(Global.getConfigValue("FILE_PROJECT_TEMPLATE") + "autoproject_cms_api_template");
            File dst = new File(Global.getConfigValue("FILE_PROJECT_PATH"));
            FileUtils.copyDirectoryToDirectory(src, dst);
            File current_dst = new File(Global.getConfigValue("FILE_PROJECT_PATH") + "autoproject_cms_api_template");
            Files.move(current_dst, new File(Global.getConfigValue("FILE_PROJECT_PATH") + this.getNewObject().getName()));
            //Replace project name in log4j
            File log4j = new File(Global.getConfigValue("FILE_PROJECT_PATH") + this.getNewObject().getName() + "/src/main/resources/config/log4j.properties");
            Global.replaceString(log4j, "projectname", this.getNewObject().getName());
            File config4j = new File(Global.getConfigValue("FILE_PROJECT_PATH") + this.getNewObject().getName() + "/src/main/resources/config/config.properties");
            Global.replaceString(config4j, "projectname", this.getNewObject().getName());
            File meta_inf = new File(Global.getConfigValue("FILE_PROJECT_PATH") + this.getNewObject().getName() + "/src/main/webapp/META-INF/context.xml");
            Global.replaceString(meta_inf, "projectname", this.getNewObject().getName());
            File template = new File(Global.getConfigValue("FILE_PROJECT_PATH") + this.getNewObject().getName() + "/src/main/webapp/WEB-INF/template.xhtml");
            Global.replaceString(template, "projectname", this.getNewObject().getName());
            File footer = new File(Global.getConfigValue("FILE_PROJECT_PATH") + this.getNewObject().getName() + "/src/main/webapp/WEB-INF/footer.xhtml");
            Global.replaceString(footer, "projectname", this.getNewObject().getName());
            File pomFile = new File(Global.getConfigValue("FILE_PROJECT_PATH") + this.getNewObject().getName() + "/pom.xml");
            Global.replaceString(pomFile, "autoprojecttemplate", this.getNewObject().getProjectid());
        } catch (IOException ex) {
            logger.error(ex);
        }
    }

    public void editSelected() {
        if (this.selectedObject != null) {
            ProjectDAO dao = new ProjectDAO();
            if (dao.edit(this.selectedObject)) {
                FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_INFO, Global.getResourceLanguage("general.operationSuccess"), "");
                FacesContext.getCurrentInstance().addMessage(null, msg);
            } else {
                FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, Global.getResourceLanguage("general.operationFail"), "");
                FacesContext.getCurrentInstance().addMessage(null, msg);
            }
        }
    }

    public void deleteObject() {
        if (this.selectedObject != null) {
            File f = new File(Global.getConfigValue("FILE_PROJECT_PATH") + this.getSelectedObject().getProjectid());
            if (f.delete()) {
                ObjTemplateDAO dao1 = new ObjTemplateDAO();
                dao1.excuteQuery("DELETE FROM obj_template WHERE projectid=?", selectedObject.getProjectid());
                ProjectDAO dao = new ProjectDAO();
                dao.delete(selectedObject);
                this.loadObjects();
            }
        }
    }

    public void deleteObjects() {
        if (this.selectedObjects != null) {
            ProjectDAO dao = new ProjectDAO();
            for (Project selectedObject1 : this.selectedObjects) {
                dao.delete(selectedObject1);
            }
            this.loadObjects();
        }
    }

    public void rowEdit(RowEditEvent event) {
        Project sf = (Project) event.getObject();
        if (sf != null) {
            this.selectedObject = sf;
            this.editSelected();
        }
    }

    public List<Project> completeObject(String query) {
        if (query.trim().equals("")) {
            ProjectDAO dao = new ProjectDAO();
            return dao.load("");
        }
        ArrayList condition = new ArrayList();
        ProjectDAO dao = new ProjectDAO();
        condition.add("%" + query.toLowerCase().trim().replaceAll("(\\s+){2,}", " ") + "%");
        condition.add("%" + Global.convertToUnsigned(query.toLowerCase().trim().replaceAll("(\\s+){2,}", " ")) + "%");
        return dao.load("WHERE LOWER(CAST(Name AS CHAR)) LIKE ?", condition.toArray());
    }

    @Override
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
        if (ret != null) {
            return ret;
        } else {
            return new Project();
        }
    }

    @Override
    public String getAsString(FacesContext facesContext, UIComponent component, Object value) {
        if (value == null) {
            return "";
        }
        if (value.equals("")) {
            return "";
        }
        return String.valueOf(((Project) value).getProjectid());
    }

    public void handleFileUpload(FileUploadEvent event) {
        UploadedFile f = event.getFile();
        String filename = f.getFileName();
        File saveFile = new File("DIR" + "/" + filename);
        if (saveFile.exists()) {
            filename = System.currentTimeMillis() + filename;
            saveFile = new File("DIR" + "/" + filename);
        }
        try {
            final int BUFFER_SIZE = 1024;
            FileOutputStream fileOutputStream = new FileOutputStream(saveFile);
            byte[] buffer = new byte[BUFFER_SIZE];
            int bulk;
            InputStream inputStream = f.getInputstream();
            while (true) {
                bulk = inputStream.read(buffer);
                if (bulk < 0) {
                    break;
                }
                fileOutputStream.write(buffer, 0, bulk);
                fileOutputStream.flush();
            }
            fileOutputStream.close();
            inputStream.close();
        } catch (IOException ex) {
            logger.error("Error:" + ex);
        }
    }

}
