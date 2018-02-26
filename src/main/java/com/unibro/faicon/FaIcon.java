/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.unibro.faicon;

import com.unibro.menuitem.Menuitem;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;

/**
 *
 * @author THOND
 */
public class FaIcon implements Converter {

    private String id;
    private String name;

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof FaIcon)) {
            return false;
        }
        FaIcon compareObj = (FaIcon) obj;
        return (compareObj.getId().equals(this.getId()));
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 13 * hash + (this.id != null ? this.id.hashCode() : 0);
        return hash;
    }

    public Object getAsObject(FacesContext context, UIComponent component, String value) {
        if (value.trim().equals("")) {
            return null;
        }
        String id;
        try {
            id = String.valueOf(value);
        } catch (Exception ex) {
            id = null;
        }
        FaIcon ret = new FaIcon();
        ret.setId(id);
        ret.setName("fa " + this.getId());
        return ret;
    }

    public String getAsString(FacesContext context, UIComponent component, Object value) {
        if (value == null) {
            return "";
        }
        if (value.equals("")) {
            return "";
        }
        return String.valueOf(((FaIcon) value).getId());
    }

    /**
     * @return the id
     */
    public String getId() {
        return id;
    }

    /**
     * @param id the id to set
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @param name the name to set
     */
    public void setName(String name) {
        this.name = name;
    }

}
