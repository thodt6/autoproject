package com.unibro.language;

import com.unibro.utils.Global;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import java.io.Serializable;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import jdbchelper.BeanCreator;
import jdbchelper.StatementMapper;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import org.apache.log4j.Logger;

public class Language implements Serializable, Converter {

    private String languageid = "";
    private String name = "";
    static final Logger logger = Logger.getLogger(Language.class.getName());

    public void setLanguageid(String languageid) {
        this.languageid = languageid;
    }

    public String getLanguageid() {
        return this.languageid;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return this.name;
    }

    public static Language getInstant() {
        Language instant = new Language();
        instant.languageid = Global.getRandomString();
        instant.name = "";

        return instant;
    }

    public Language() {
    }

//    public Language(Hashtable hash) {
    //    this.languageid = (String)hash.get("languageid");
    //    this.name = (String)hash.get("name");
//    }
    public static Language getObjectFromJsonString(String jsonObj) {
        try {
            Gson gson = Global.getGsonObject();
            Language obj;
            obj = gson.fromJson(jsonObj, Language.class);
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
        if (!(obj instanceof Language)) {
            return false;
        }
        Language compareObj = (Language) obj;
        return (compareObj.getLanguageid().equals(this.getLanguageid()));
    }

    public String toJson() {
        Gson gson = Global.getGsonObject();
        return gson.toJson(this);
    }

    public static BeanCreator<Language> beanCreator = new BeanCreator<Language>() {
        @Override
        public Language createBean(ResultSet rs) throws SQLException {
            Language obj = new Language();
            obj.languageid = (String) rs.getObject("languageid");
            obj.name = (String) rs.getObject("name");

            return obj;
        }
    };
    public static StatementMapper<Language> statementMapper = new StatementMapper<Language>() {
        @Override
        public void mapStatement(PreparedStatement stmt, Language obj) throws SQLException {
            stmt.setObject(1, obj.languageid);
            stmt.setObject(2, obj.name);

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
        LanguageDAO dao = new LanguageDAO();
        Language ret = dao.getObjectByKey(id);
        return ret;
    }

    public String getAsString(FacesContext facesContext, UIComponent component, Object value) {
        if (value == null) {
            return "";
        }
        if (value.equals("")) {
            return "";
        }
        return String.valueOf(((Language) value).getLanguageid());
    }
}
