package com.unibro.menuitem;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import com.unibro.faicon.FaIcon;
import com.unibro.language.Language;
import com.unibro.objtemplate.LanguageData;
import com.unibro.project.Project;
import com.unibro.utils.Global;
import java.io.Serializable;
import java.lang.reflect.Type;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import jdbchelper.BeanCreator;
import jdbchelper.StatementMapper;
import org.apache.log4j.Logger;

public class Menuitem implements Serializable, Converter {

    private String id = "";
    private String name = "";
    private String outcome = "";
    private Boolean submenu = false;
    private String parentId = "";
    private java.util.Date createdtime = new java.util.Date();
    private String projectid = "";
    private int orderindex = 0;
    private String icon = "fa-angle-right";
    private FaIcon fa_icon;
    static final Logger logger = Logger.getLogger(Menuitem.class.getName());

    private Project project;

    private List<LanguageData> list_name;

    public Menuitem() {
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getId() {
        return this.id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return this.name;
    }

    public void setOutcome(String outcome) {
        this.outcome = outcome;
    }

    public String getOutcome() {
        return this.outcome;
    }

    public void setSubmenu(Boolean submenu) {
        this.submenu = submenu;
    }

    public Boolean getSubmenu() {
        return this.submenu;
    }

    public void setParentId(String parentId) {
        this.parentId = parentId;
    }

    public String getParentId() {
        return this.parentId;
    }

    public void setCreatedtime(java.util.Date createdtime) {
        this.createdtime = createdtime;
    }

    public java.util.Date getCreatedtime() {
        return this.createdtime;
    }

    public void setProjectid(String projectid) {
        this.projectid = projectid;
    }

    public String getProjectid() {
        return this.projectid;
    }

    public static Menuitem getInstant() {
        Menuitem instant = new Menuitem();
        instant.id = Global.getRandomString();
        instant.name = "";
        instant.outcome = "";
        instant.submenu = false;
        instant.parentId = "";
        instant.createdtime = new java.util.Date();
        instant.projectid = "";
        instant.orderindex = 0;
        instant.icon = "fa-angle-right";
        instant.fa_icon = new FaIcon();
        instant.fa_icon.setId(instant.icon);
        instant.fa_icon.setName("fa " + instant.icon);
        return instant;
    }

//    public Menuitem(Hashtable hash) {
    //    this.id = (String)hash.get("id");
    //    this.name = (String)hash.get("name");
    //    this.outcome = (String)hash.get("outcome");
    //    this.submenu = (Boolean)hash.get("submenu");
    //    this.parentId = (String)hash.get("parent_id");
    //   this.createdtime = Global.convertSqlTimeStampToDate((java.sql.Timestamp)hash.get("createdtime"));
    //    this.projectid = (String)hash.get("projectid");
//    }
    public static Menuitem getObjectFromJsonString(String jsonObj) {
        try {
            Gson gson = Global.getGsonObject();
            Menuitem obj;
            obj = gson.fromJson(jsonObj, Menuitem.class);
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
        if (!(obj instanceof Menuitem)) {
            return false;
        }
        Menuitem compareObj = (Menuitem) obj;
        return (compareObj.getId().equals(this.getId()));
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 11 * hash + (this.id != null ? this.id.hashCode() : 0);
        return hash;
    }

    public String toJson() {
        Gson gson = Global.getGsonObject();
        return gson.toJson(this);
    }

    public static BeanCreator<Menuitem> beanCreator = new BeanCreator<Menuitem>() {
        @Override
        public Menuitem createBean(ResultSet rs) throws SQLException {
            Menuitem obj = new Menuitem();
            obj.id = (String) rs.getObject("id");
            obj.name = (String) rs.getObject("name");
            obj.outcome = (String) rs.getObject("outcome");
            obj.submenu = (Boolean) rs.getObject("submenu");
            obj.parentId = (String) rs.getObject("parent_id");
            obj.createdtime = (java.util.Date) rs.getObject("createdtime");
            obj.projectid = (String) rs.getObject("projectid");
            obj.orderindex = (Integer) rs.getObject("orderindex");
            obj.icon = (String) rs.getObject("icon");
            obj.fa_icon = new FaIcon();
            obj.fa_icon.setId(obj.icon);
            obj.fa_icon.setName("fa " + obj.icon);
            return obj;
        }
    };
    public static StatementMapper<Menuitem> statementMapper = new StatementMapper<Menuitem>() {
        @Override
        public void mapStatement(PreparedStatement stmt, Menuitem obj) throws SQLException {
            obj.saveNameData();
            obj.icon = obj.fa_icon.getId();
            stmt.setObject(1, obj.id);
            stmt.setObject(2, obj.name);
            stmt.setObject(3, obj.outcome);
            stmt.setObject(4, obj.submenu);
            stmt.setObject(5, obj.parentId);
            stmt.setObject(6, obj.createdtime);
            stmt.setObject(7, obj.projectid);
            stmt.setObject(8, obj.orderindex);
            stmt.setObject(9, obj.icon);

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
        MenuitemDAO dao = new MenuitemDAO();
        Menuitem ret = dao.getObjectByKey(id);
        return ret;
    }

    public String getAsString(FacesContext facesContext, UIComponent component, Object value) {
        if (value == null) {
            return "";
        }
        if (value.equals("")) {
            return "";
        }
        return String.valueOf(((Menuitem) value).getId());
    }

    /**
     * @return the list_name
     */
    public List<LanguageData> getList_name() {
        return list_name;
    }

    /**
     * @param list_name the list_name to set
     */
    public void setList_name(List<LanguageData> list_name) {
        this.list_name = list_name;
    }

    /**
     * @return the orderindex
     */
    public int getOrderindex() {
        return orderindex;
    }

    /**
     * @param orderindex the orderindex to set
     */
    public void setOrderindex(int orderindex) {
        this.orderindex = orderindex;
    }

    public void loadNameData() {
        if (this.name == null || this.name.equals("") || this.name.equals("[]")) {
            this.setList_name(new ArrayList());
            if (!this.project.getLanguage_list().isEmpty()) {
                for (Language lang : this.getProject().getLanguage_list()) {
                    LanguageData data = new LanguageData(lang.getLanguageid(), "");
                    this.list_name.add(data);
                }
            }
        } else {
            try {
                Gson gson = Global.getGsonObject();
                Type listType = new TypeToken<List<LanguageData>>() {
                }.getType();
                this.list_name = gson.fromJson(this.name, listType);
            } catch (JsonSyntaxException ex) {
                logger.error(ex);
                this.list_name = new ArrayList();
            }
        }
    }

    private void saveNameData() {
        if (this.getList_name() == null || this.getList_name().isEmpty()) {
            this.name = "[]";
        } else {
            this.name = LanguageData.toJsonArrayString(this.list_name);
        }
    }

    public List<Menuitem> getBrotherList() {
        MenuitemDAO dao = new MenuitemDAO();
        return dao.load("WHERE parent_id=? ORDER BY orderindex", this.getParentId());
    }

    public List<Menuitem> getChildList() {
        MenuitemDAO dao = new MenuitemDAO();
        return dao.load("WHERE parent_id=? ORDER BY orderindex", this.getId());
    }

    public int getLastOrderIndex() {
        MenuitemDAO dao = new MenuitemDAO();
        Menuitem last_item = dao.queryForObject("WHERE parent_id=? ORDER BY orderindex DESC LIMIT 1", this.getParentId());
        if (last_item != null) {
            return last_item.getOrderindex();
        }
        return -1;
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

    @Override
    public String toString() {
        return this.getId();
    }

    /**
     * @return the icon
     */
    public String getIcon() {
        return icon;
    }

    /**
     * @param icon the icon to set
     */
    public void setIcon(String icon) {
        this.icon = icon;
    }

    public Menuitem getParentMenu() {
        if (this.parentId == null || this.parentId.equals("")) {
            return null;
        }
        MenuitemDAO dao = new MenuitemDAO();
        return dao.getObjectByKey(this.getParentId());
    }

    /**
     * @return the fa_icon
     */
    public FaIcon getFa_icon() {
        return fa_icon;
    }

    /**
     * @param fa_icon the fa_icon to set
     */
    public void setFa_icon(FaIcon fa_icon) {
        this.fa_icon = fa_icon;
    }

    public String getLanguageValue(String languageid) {
        for (LanguageData data : this.getList_name()) {
            if (data.getLanguageid().equals(languageid)) {
                return data.getContent();
            }
        }
        return null;
    }

    public String toXHTMLItem() {
        String ret = "<p:menuitem id=\"menu_[id]\" value=\"#{msg['menu.[id]']}\" icon=\"fa fa-fw [icon]\" outcome=\"[link]\"/>";
        ret = ret.replace("[id]", this.getId());
        ret = ret.replace("[icon]", this.getIcon());
        if (this.getOutcome().equals("")) {
            ret = ret.replace("outcome=\"[link]\"", "");
        } else {
            ret = ret.replace("[link]", this.getOutcome());
        }
        ret = ret + "\n";
        return ret;
    }

    public String toXHTMLOpenQuote() {
        String ret = "<p:submenu id=\"menu_[id]\" label=\"#{msg['menu.[id]']}\" icon=\"fa fa-fw [icon]\">\n";
        ret = ret.replace("[id]", this.getId());
        ret = ret.replace("[icon]", this.getIcon());
        return ret;
    }

    public String toXHTMLCloseQuote() {
        String ret = "</p:submenu>\n";
        return ret;
    }

}
