package com.unibro.language;

import com.unibro.utils.Global;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.math.BigInteger;
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
import org.primefaces.model.UploadedFile;

/**
 *
 * @author Nguyen Duc Tho
 */
@ManagedBean
@ViewScoped
public class LanguageController implements Serializable, Converter {

    private List<Language> objects;
    private Language selectedObject = new Language();
    private Language[] selectedObjects;
    private Language newObject = new Language();
    private String selectedId;
    private Logger logger = Logger.getLogger(this.getClass().getName());

    public LanguageController() {
        this.loadObjects();
    }

    public void initSelectedObject() {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        if (!facesContext.isPostback() && !facesContext.isValidationFailed()) {
            LanguageDAO dao = new LanguageDAO();
            this.selectedObject = dao.getObjectByKey(this.getSelectedId());
        }
    }

    public final void loadObjects() {
        LanguageDAO dao = new LanguageDAO();
        this.objects = dao.load("");
    }

    public void setObjects(List<Language> objects) {
        this.objects = objects;
    }

    public List<Language> getObjects() {
        return objects;
    }

    public void setNewObject(Language newObject) {
        this.newObject = newObject;
    }

    public Language getNewObject() {
        return newObject;
    }

    public void setSelectedObject(Language selectedObject) {
        this.selectedObject = selectedObject;
    }

    public Language getSelectedObject() {
        return selectedObject;
    }

    public void setSelectedObjects(Language[] selectedObjects) {
        this.selectedObjects = selectedObjects;
    }

    public Language[] getSelectedObjects() {
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
            LanguageDAO dao = new LanguageDAO();
            if (dao.create(getNewObject())) {
                FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_INFO, Global.getResourceLanguage("general.operationSuccess"), "");
                FacesContext.getCurrentInstance().addMessage(null, msg);
            } else {
                FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, Global.getResourceLanguage("general.operationFail"), "");
                FacesContext.getCurrentInstance().addMessage(null, msg);
            }
        }
    }

    public void editSelected() {
        if (this.selectedObject != null) {
            LanguageDAO dao = new LanguageDAO();
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
            LanguageDAO dao = new LanguageDAO();
            dao.delete(selectedObject);
            this.loadObjects();
        }
    }

    public void deleteObjects() {
        if (this.selectedObjects != null) {
            LanguageDAO dao = new LanguageDAO();
            for (int i = 0; i < this.selectedObjects.length; i++) {
                dao.delete(selectedObjects[i]);
            }
            this.loadObjects();
        }
    }

    public void rowEdit(RowEditEvent event) {
        Language sf = (Language) event.getObject();
        if (sf != null) {
            this.selectedObject = sf;
            this.editSelected();
        }
    }

    public List<Language> completeObject(String query) {
        if (query.trim().equals("")) {
            LanguageDAO dao = new LanguageDAO();
            return dao.load("");
        }
        ArrayList condition = new ArrayList();
        LanguageDAO dao = new LanguageDAO();
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
        LanguageDAO dao = new LanguageDAO();
        Language ret = dao.getObjectByKey(id);
        if (ret != null) {
            return ret;
        } else {
            return new Language();
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
        return String.valueOf(((Language) value).getLanguageid());
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
