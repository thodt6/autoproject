/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.unibro.faicon;

import com.unibro.utils.Global;
import java.util.ArrayList;
import java.util.List;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

/**
 *
 * @author THOND
 */
@ManagedBean
@ViewScoped
public class FaIconService {

    List<FaIcon> list_icon;

    public List<FaIcon> getList_icon() {
        if (list_icon == null) {
            list_icon = new ArrayList();
        }
        String[] icons = Global.getConfigValue("app.fa-icon.id").split(",");
        for (String icon : icons) {
            FaIcon ic = new FaIcon();
            ic.setId(icon);
            ic.setName("fa " + icon);
            this.list_icon.add(ic);
        }
        return this.list_icon;
    }

}
