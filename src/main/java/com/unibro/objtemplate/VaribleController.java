/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.unibro.objtemplate;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import static com.unibro.objtemplate.ObjTemplate.logger;
import com.unibro.project.Project;
import com.unibro.utils.Global;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author THOND
 */
public class VaribleController {

    private List<Varible> list_varible;
    private Varible selectedVarible;
    private Project project;
    private String objectFormat;

    public VaribleController() {
        logger.info("VaribleController");
        this.loadVaribleList();
    }
    
    public VaribleController(List<Varible> list_varible) {
        this.list_varible = list_varible;
//        this.saveVaribleList();
    }

    public VaribleController(String objectFormat) {
        this.objectFormat = objectFormat;
    }

    public final void loadVaribleList() {
//        logger.info("Load varible list");
        if (this.getObjectFormat() == null || this.getObjectFormat().equals("")) {
            this.setList_varible((List<Varible>) new ArrayList());
        } else {
            try {
                Gson gson = Global.getGsonObject();
                Type listType = new TypeToken<List<Varible>>() {
                }.getType();
                this.list_varible = gson.fromJson(this.getObjectFormat(), listType);
            } catch (JsonSyntaxException ex) {
                logger.error(ex);
                this.setList_varible((List<Varible>) new ArrayList());
            }
        }
    }

    public final void saveVaribleList() {
        if (this.getList_varible() == null || this.getList_varible().isEmpty()) {
            this.setObjectFormat("[]");
        } else {
            List<Varible> saveObjectList = new ArrayList();
            for (Varible v : this.getList_varible()) {
                if (v.getValidVarible() == 1) {
                    saveObjectList.add(v);
                }
            }
            this.setObjectFormat(Varible.toJsonArrayString(saveObjectList));
            logger.info("Object Format:" + this.getObjectFormat());
        }
    }

    public void addVarible() {
        logger.info(this.getList_varible().size());
        Varible v = new Varible();
        v.setProject(this.getProject());
        this.getList_varible().add(v);
        logger.info(this.getList_varible().size());
    }

    public void initSelectedVarible() {
        logger.info("init selected varible " + this.getSelectedVarible().getName());
        if (this.getSelectedVarible() != null) {
            if (this.getSelectedVarible().getName().matches("[a-zA-Z0-9_]+")) {
                int checkDuplicate = 0;
                for (Varible v : this.getList_varible()) {
                    if (v.getValidVarible() == 1 && (v != this.getSelectedVarible())) {
                        if (v.getName().toLowerCase().equals(this.getSelectedVarible().getName().toLowerCase())) {
                            checkDuplicate++;
                        }
                    }
                }
                logger.info(checkDuplicate);
                if (checkDuplicate >= 1) {
                    this.getSelectedVarible().setValidVarible(0);
                } else {
                    this.getSelectedVarible().setValidVarible(1);
                    this.getSelectedVarible().initRequiredData();
                }
            } else {
                this.getSelectedVarible().setValidVarible(0);
            }
        }
    }
//    FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "Create project success", "");
//                FacesContext.getCurrentInstance().addMessage(null, msg);

    public void deleteSelected() {
        if (this.getSelectedVarible() != null) {
            this.getList_varible().remove(this.getSelectedVarible());
        }
    }

    /**
     * @return the list_varible
     */
    public List<Varible> getList_varible() {
        return list_varible;
    }

    /**
     * @param list_varible the list_varible to set
     */
    public void setList_varible(List<Varible> list_varible) {
        this.list_varible = list_varible;
    }

    /**
     * @return the selectedVarible
     */
    public Varible getSelectedVarible() {
        return selectedVarible;
    }

    /**
     * @param selectedVarible the selectedVarible to set
     */
    public void setSelectedVarible(Varible selectedVarible) {
        this.selectedVarible = selectedVarible;
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
     * @return the objectFormat
     */
    public String getObjectFormat() {
        return objectFormat;
    }

    /**
     * @param objectFormat the objectFormat to set
     */
    public void setObjectFormat(String objectFormat) {
        this.objectFormat = objectFormat;
    }

    //Return ?,?,? for insert
    public String getInsertJdbcPrepareStatementMask() {
        String ret = "";
        for (int i = 0; i < this.list_varible.size(); i++) {
            if (!list_varible.get(i).getAutoIncrement()) {
                ret += ",?";
            }
        }
        if (ret.length() > 0) {
            ret = ret.substring(1);
        }
        return ret;
    }

    //Return "param1","param2" for insert
    public String getInsertJdbcPrepareStatement() {
        String ret = "";
        for (int i = 0; i < this.list_varible.size(); i++) {
            if (!list_varible.get(i).getAutoIncrement()) {
                ret += "," + list_varible.get(i).getName();
            }
        }
        if (ret.length() > 0) {
            ret = ret.substring(1);
        }
        return ret;
    }

    //Return "param1","param2" for insert
    public String getInsertJdbcPrepareStatementStr() {
        String ret = "";
        for (int i = 0; i < this.list_varible.size(); i++) {
            if (!list_varible.get(i).getAutoIncrement()) {
                ret += ",\"" + list_varible.get(i).getName() + "\"";
            }
        }
        if (ret.length() > 0) {
            ret = ret.substring(1);
        }
        return ret;
    }

    //Return prepared statement data
    public String getInsertJdbcPrepareStatementData() {
        String ret = "";
        int index=0;
        for (int i = 0; i < this.list_varible.size(); i++) {
            if (!list_varible.get(i).getAutoIncrement()) {
                ret += "    ps.setObject(" + (index + 1) + ", object.get" + list_varible.get(i).getName1() + "());\n";
                index++;
            }
        }
        if (ret.length() > 0) {
            ret = ret.substring(1);
        }
        return ret;
    }

    //Return SET name=?,,? for insert
    public String getUpdateJdbcPrepareStatementMask() {
        String ret = "";
        for (int i = 0; i < this.list_varible.size(); i++) {
            if (!list_varible.get(i).getAutoIncrement()) {
                ret += "," + list_varible.get(i).getName() + " = ?";
            }
        }
        if (ret.length() > 0) {
            ret = ret.substring(1);
        }
        return ret;
    }

    //Return "param1","param2" for insert
    public String getUpdateJdbcPrepareStatementStr() {
        String ret = "";
        for (int i = 0; i < this.list_varible.size(); i++) {
            if (!list_varible.get(i).getAutoIncrement()) {
                ret += ",\"" + list_varible.get(i).getName() + "\"";
            }
        }
        for (int i = 0; i < this.list_varible.size(); i++) {
            if (list_varible.get(i).getPrimeKey()) {
                ret += ",\"" + list_varible.get(i).getName() + "\"";
                break;
            }
        }
        if (ret.length() > 0) {
            ret = ret.substring(1);
        }
        return ret;
    }

    //Return prepared statement data
    public String getUpdateJdbcPrepareStatementData() {
        String ret = "";
        int index = 0;
        for (int i = 0; i < this.list_varible.size(); i++) {
            if (!list_varible.get(i).getAutoIncrement()) {
                ret += "    ps.setObject(" + (index + 1) + ", object.get" + list_varible.get(i).getName1() + "());\n";
                index++;
            }
        }
        for (int i = 0; i < this.list_varible.size(); i++) {
            if (list_varible.get(i).getPrimeKey()) {
                ret += "    ps.setObject(" + (index + 1) + ", object.get" + list_varible.get(i).getName1() + "());\n";
                break;
            }
        }
        if (ret.length() > 0) {
            ret = ret.substring(1);
        }
        return ret;
    }

}
