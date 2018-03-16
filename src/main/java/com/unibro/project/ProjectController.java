package com.unibro.project;

import com.unibro.objtemplate.ObjTemplateDAO;
import com.unibro.utils.Global;
import java.io.File;
import java.io.IOException;
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
import org.primefaces.event.RowEditEvent;

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

    private static final Logger logger = Logger.getLogger(ProjectController.class.getName());

    public ProjectController() {
//        this.loadObjects();
    }
    
    public int getTotalProject(){
        return this.objects.size();
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

            ProjectDAO dao = new ProjectDAO();
            if (dao.create(getNewObject())) {
                this.copyProject(this.getNewObject());
                FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "Create project success", "");
                FacesContext.getCurrentInstance().addMessage(null, msg);
            } else {
                FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Create project fail", "");
                FacesContext.getCurrentInstance().addMessage(null, msg);
            }
        }
    }

    public void resetToDefaultProject(){
        if (this.getSelectedObject() != null) {
            File f = new File(Global.getConfigValue("FILE_PROJECT_PATH") + this.getSelectedObject().getName());
            logger.info("File path:" + f.getAbsolutePath());
            
            if (f.exists()) {
                boolean deleted=false;
                try {
                    FileUtils.deleteDirectory(f);
                    deleted=true;
                } catch (IOException ex) {
                    deleted=false;
                }
                if (deleted) {
                    this.copyProject(this.selectedObject);
                    FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "Reset done", "");
                    FacesContext.getCurrentInstance().addMessage(null, msg);
                } else {
                    FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Reset fail. Can not delete folder " + f.getAbsolutePath(), "");
                    FacesContext.getCurrentInstance().addMessage(null, msg);
                }
            } else {
                this.copyProject(this.selectedObject);
                FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "Reset done", "");
                FacesContext.getCurrentInstance().addMessage(null, msg);
            }
        }
    }

    private void copyProject(Project p) {
        if (p.getProjectType().equals("CMS")) {
            if (p.getDataAccessType().equals(Project.ABSTRACT_ACCESS)) {
                ProjectUtils.copyProjectCMSAbstract(p);
            }
            if (p.getDataAccessType().equals(Project.API_ACCESS)) {
                ProjectUtils.copyProjectCMSApi(p);
            }
        }
        if (p.getProjectType().equals("API")) {
            if (p.getDataAccessType().equals(Project.MYSQL_ACCESS)) {
                ProjectUtils.copyApiMySQLProject(p);
            }
            if (p.getDataAccessType().equals(Project.ELASTIC_ACCESS)) {
                ProjectUtils.copyApiElasticProject(p);
            }
            if (p.getDataAccessType().equals(Project.ORACLE_ACCESS)) {
                ProjectUtils.copyApiOracleProject(p);
            }
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

}
