package com.unibro.objtemplate;

import com.unibro.project.Project;
import com.unibro.project.ProjectDAO;
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
import org.apache.log4j.Logger;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.event.RowEditEvent;
import org.primefaces.event.TabChangeEvent;
import org.primefaces.model.UploadedFile;

/**
 *
 * @author Nguyen Duc Tho
 */
@ManagedBean
@ViewScoped
public class ObjTemplateController implements Serializable, Converter {

    private List<ObjTemplate> objects;
    private ObjTemplate selectedObject;
    private ObjTemplate[] selectedObjects;
    private ObjTemplate newObject;
    private String selectedId;
    private Logger logger = Logger.getLogger(this.getClass().getName());

    private String projectid = "";
    private Project project;

    private Project copyProject;
    private ObjTemplate copyObjTemplate;

    private MySqlObjectClass mysql_object = new MySqlObjectClass();

    private OracleSqlObjectClass oracle_object = new OracleSqlObjectClass();

    public ObjTemplateController() {

    }

    public void initObject() {
        logger.info("Init project");
        FacesContext facesContext = FacesContext.getCurrentInstance();
        if (!facesContext.isPostback() && !facesContext.isValidationFailed()) {
            logger.info("Init project insign");
            ProjectDAO dao = new ProjectDAO();
            this.project = dao.getObjectByKey(this.getProjectid());
            this.loadObjects();
            this.newObject = ObjTemplate.getInstant();
            this.newObject.setProject(this.getProject());
            this.newObject.loadHelpFormat();
            this.newObject.setVaribleController(new VaribleController());
            this.newObject.getVaribleController().setProject(this.project);
            if (this.mysql_object == null) {
                this.mysql_object = new MySqlObjectClass();
            }
            this.mysql_object.setProject(this.getProject());
            if (this.oracle_object == null) {
                this.oracle_object = new OracleSqlObjectClass();
            }
            this.oracle_object.setProject(this.getProject());
        }
    }

    public void initSelectedObject() {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        if (!facesContext.isPostback() && !facesContext.isValidationFailed()) {
            ObjTemplateDAO dao = new ObjTemplateDAO();
            this.selectedObject = dao.getObjectByKey(this.getSelectedId());
        }
    }

    public final void loadObjects() {
        ObjTemplateDAO dao = new ObjTemplateDAO();
        if (this.project != null) {
            this.objects = dao.load("WHERE projectid=? ORDER BY createdtime DESC", this.getProject().getProjectid());
            for (ObjTemplate obj : objects) {
                obj.setProject(this.getProject());
                obj.loadHelpFormat();
                logger.info("loadObjects " + obj.getName());
                obj.getVaribleController().setProject(this.getProject());
                obj.getVaribleController().loadVaribleList();
            }
            if (!this.objects.isEmpty()) {
                this.selectedObject = this.objects.get(0);
            }
        } else {
            this.objects = new ArrayList();
        }
//        this.newObject=new ObjTemplate();
//        this.objects.add(this.newObject);
    }

    public void setObjects(List<ObjTemplate> objects) {
        this.objects = objects;
    }

    public List<ObjTemplate> getObjects() {
        return objects;
    }

    public void setNewObject(ObjTemplate newObject) {
        this.newObject = newObject;
    }

    public ObjTemplate getNewObject() {
        return newObject;
    }

    public void setSelectedObject(ObjTemplate selectedObject) {
        this.selectedObject = selectedObject;
    }

    public ObjTemplate getSelectedObject() {
        return selectedObject;
    }

    public void setSelectedObjects(ObjTemplate[] selectedObjects) {
        this.selectedObjects = selectedObjects;
    }

    public ObjTemplate[] getSelectedObjects() {
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
            this.getNewObject().setId(this.getProject().getProjectid() + "-" + this.getNewObject().getName().toLowerCase());
            this.getNewObject().setProjectid(this.getProject().getProjectid());
            ObjTemplateDAO dao = new ObjTemplateDAO();
            if (dao.create(getNewObject())) {
                FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "Create Object success", "");
                FacesContext.getCurrentInstance().addMessage(null, msg);
                this.selectedObject = this.newObject;
                this.loadObjects();
                this.newObject = new ObjTemplate();
                this.newObject.setProject(this.getProject());
            } else {
                FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Create Object fail", "");
                FacesContext.getCurrentInstance().addMessage(null, msg);
            }
        }
    }

    public void editSelected() {
        if (this.selectedObject != null) {
            ObjTemplateDAO dao = new ObjTemplateDAO();
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
            ObjTemplateDAO dao = new ObjTemplateDAO();
            dao.delete(selectedObject);
            this.loadObjects();
        }
    }

    public void deleteObjects() {
        if (this.selectedObjects != null) {
            ObjTemplateDAO dao = new ObjTemplateDAO();
            for (ObjTemplate selectedObject1 : this.selectedObjects) {
                dao.delete(selectedObject1);
            }
            this.loadObjects();
        }
    }

    public void rowEdit(RowEditEvent event) {
        ObjTemplate sf = (ObjTemplate) event.getObject();
        if (sf != null) {
            this.selectedObject = sf;
            this.editSelected();
        }
    }

    public List<ObjTemplate> completeObject(String query) {
        if (query.trim().equals("")) {
            ObjTemplateDAO dao = new ObjTemplateDAO();
            return dao.load("");
        }
        ArrayList condition = new ArrayList();
        ObjTemplateDAO dao = new ObjTemplateDAO();
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
        ObjTemplateDAO dao = new ObjTemplateDAO();
        ObjTemplate ret = dao.getObjectByKey(id);
        if (ret != null) {
            return ret;
        } else {
            return new ObjTemplate();
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
        return String.valueOf(((ObjTemplate) value).getId());
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

    /**
     * @return the projectid
     */
    public String getProjectid() {
        return projectid;
    }

    /**
     * @param projectid the projectid to set
     */
    public void setProjectid(String projectid) {
        this.projectid = projectid;
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

    public void onTabChange(TabChangeEvent event) {
        this.selectedObject = (ObjTemplate) event.getData();
    }

    /**
     * @return the copyProject
     */
    public Project getCopyProject() {
        return copyProject;
    }

    /**
     * @param copyProject the copyProjectid to set
     */
    public void setCopyProject(Project copyProject) {
        this.copyProject = copyProject;
    }

    public void doCopyObject() {
        if (this.copyProject != null && this.project != null && this.selectedObject != null && this.copyObjTemplate != null) {
            ObjTemplateDAO dao = new ObjTemplateDAO();
            this.selectedObject.getVaribleController().setList_varible(this.copyObjTemplate.getVaribleController().getList_varible());
            this.selectedObject.editMe();
        }
    }

    /**
     * @return the copyObjTemplate
     */
    public ObjTemplate getCopyObjTemplate() {
        return copyObjTemplate;
    }

    /**
     * @param copyObjTemplate the copyObjTemplate to set
     */
    public void setCopyObjTemplate(ObjTemplate copyObjTemplate) {
        this.copyObjTemplate = copyObjTemplate;
    }

    /**
     * @return the mysql_object
     */
    public MySqlObjectClass getMysql_object() {
        return mysql_object;
    }

    /**
     * @param mysql_object the mysql_object to set
     */
    public void setMysql_object(MySqlObjectClass mysql_object) {
        this.mysql_object = mysql_object;
    }

    public void doImportFromMySql() {
        if (this.mysql_object.getSelectedTables() != null && !this.mysql_object.getSelectedTables().isEmpty()) {
            ObjTemplateDAO dao = new ObjTemplateDAO();
            for (String table : this.mysql_object.getSelectedTables()) {
                ObjTemplate obj = ObjTemplate.getInstant();
                obj.setId(this.getProject().getProjectid() + "-" + table);
                obj.setName(table);
                obj.setProject(this.getProject());
                obj.setProjectid(this.getProject().getProjectid());
                obj.loadHelpFormat();
                obj.setVaribleController(new VaribleController(this.mysql_object.initFieldClass(table)));
                obj.getVaribleController().setProject(this.project);
                obj.getVaribleController().saveVaribleList();
                obj.setObjectFormat(obj.getVaribleController().getObjectFormat());
//                obj.getVaribleController().initSelectedVarible();
                if (dao.getObjectByKey(obj.getId()) == null) {
                    dao.create(obj);
                } else {
                    dao.edit(obj);
                }
                this.loadObjects();
            }
//            if (!this.objects.isEmpty()) {
//                this.selectedObject = this.objects.get(this.objects.size() - 1);
//            }
        }

    }

    public void doImportFromOracle() {
        if (this.oracle_object.getSelectedTables() != null && !this.oracle_object.getSelectedTables().isEmpty()) {
            ObjTemplateDAO dao = new ObjTemplateDAO();
            for (String table : this.oracle_object.getSelectedTables()) {
                ObjTemplate obj = ObjTemplate.getInstant();
                obj.setId(this.getProject().getProjectid() + "-" + table);
                obj.setName(table);
                obj.setProject(this.getProject());
                obj.setProjectid(this.getProject().getProjectid());
                obj.loadHelpFormat();
                obj.setVaribleController(new VaribleController(this.oracle_object.initFieldClass(table)));
                obj.getVaribleController().setProject(this.project);
                obj.getVaribleController().saveVaribleList();
                obj.setObjectFormat(obj.getVaribleController().getObjectFormat());
//                obj.getVaribleController().initSelectedVarible();
                if (dao.getObjectByKey(obj.getId()) == null) {
                    dao.create(obj);
                } else {
                    dao.edit(obj);
                }
                this.loadObjects();
            }
            if (!this.objects.isEmpty()) {
                this.selectedObject = this.objects.get(this.objects.size() - 1);
            }
        }

    }

    /**
     * @return the oracle_object
     */
    public OracleSqlObjectClass getOracle_object() {
        return oracle_object;
    }

    /**
     * @param oracle_object the oracle_object to set
     */
    public void setOracle_object(OracleSqlObjectClass oracle_object) {
        this.oracle_object = oracle_object;
    }
}
