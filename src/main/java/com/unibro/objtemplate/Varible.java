/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.unibro.objtemplate;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.unibro.language.Language;
import com.unibro.project.Project;
import com.unibro.utils.Global;
import java.util.ArrayList;
import java.util.List;
import org.apache.log4j.Logger;

/**
 *
 * @author THOND
 */
public class Varible {

    public static final String STRING_TYPE = "String";
    public static final String DOUBLE_TYPE = "Double";
    public static final String FLOAT_TYPE = "Float";
    public static final String INT_TYPE = "Integer";
    public static final String BOOLEAN_TYPE = "Boolean";
    public static final String DATE_TYPE = "java.util.Date";
    public static final String LONG_TYPE = "Long";
    public static final String BIGINT_TYPE = "BigInteger";
    public static final String BIGDECIMAL_TYPE = "BigDecimal";

    private String name = "";
    private Boolean primeKey = false;
    private String type = STRING_TYPE;
    private Boolean required = false;
    private String defaultValue = "";
    private List<LanguageData> validation_msg = new ArrayList();
    private List<LanguageData> help_msg = new ArrayList();
    private Project project;
    private Integer validVarible = -1;
    private Boolean autoIncrement = false;

    static final Logger logger = Logger.getLogger(Varible.class.getName());

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

    /**
     * @return the type
     */
    public String getType() {
        return type;
    }
    

    /**
     * @param type the type to set
     */
    public void setType(String type) {
        this.type = type;
//        this.initDefaultValue();
    }

    /**
     * @return the required
     */
    public Boolean getRequired() {
        return required;
    }

    /**
     * @param required the required to set
     */
    public void setRequired(Boolean required) {
        this.required = required;

    }

    public void initRequiredData() {
        logger.info("Init required value");
        logger.info("Required:" + required);
        logger.info("Name:" + name);
        logger.info("PrimeKey:" + this.primeKey);
        logger.info("defaultValue:" + defaultValue);
        logger.info("autoincrement:" + this.autoIncrement);
        if (required && (this.defaultValue.equals("") || this.defaultValue == null)) {
            this.initDefaultValue();
        }
        if (this.name != null && !this.name.equals("")) {
            this.initValidateMessage();
        }
    }

    /**
     * @return the defaultValue
     */
    public String getDefaultValue() {
        return defaultValue;
    }

    /**
     * @param defaultValue the defaultValue to set
     */
    public void setDefaultValue(String defaultValue) {
        this.defaultValue = defaultValue;
    }

    /**
     * @return the validation_msg
     */
    public List<LanguageData> getValidation_msg() {
        return validation_msg;
    }

    /**
     * @param validation_msg the validation_msg to set
     */
    public void setValidation_msg(List<LanguageData> validation_msg) {
        this.validation_msg = validation_msg;
    }

    public JsonObject toJson() {
        Gson gson = Global.getGsonObject();
        return gson.toJsonTree(this).getAsJsonObject();
    }

    public static Varible getObjectFromJson(String content) {
        Gson gson = Global.getGsonObject();
        return gson.fromJson(content, Varible.class);
    }

    public static String toJsonArrayString(List<Varible> list) {
        Gson gson = Global.getGsonObject();
        return gson.toJson(list);
    }

    public static JsonArray toJsonArray(List<Varible> list) {
        Gson gson = Global.getGsonObject();
        JsonElement element = gson.toJsonTree(list, new TypeToken<List<Varible>>() {
        }.getType());
        JsonArray jsonArray = element.getAsJsonArray();
        return jsonArray;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof Varible)) {
            return false;
        }
        Varible compareObj = (Varible) obj;
        return (compareObj.getName().equals(this.getName()));
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 41 * hash + (this.name != null ? this.name.hashCode() : 0);
        return hash;
    }

    public String getVaribleDefinition() {
        if (this.defaultValue.equals("") || this.defaultValue == null) {
            return "    private " + this.type + " " + this.name + ";\n";
        } else {
            return "    private " + this.type + " " + this.name + " = " + this.defaultValue + ";\n";
        }
    }

    public String getApiVaribleDefinition() {
        if (this.defaultValue.equals("") || this.defaultValue == null) {
            if (!this.type.equals(DATE_TYPE)) {
                return "    private " + this.type + " " + this.name + ";\n";
            } else {
                return "    " + "@JsonFormat(pattern = \"yyyy-MM-dd'T'HH:mm:ss.SSSZ\")\n"
                        + "    private " + this.type + " " + this.name + ";\n";
            }
        } else {
            if (!this.type.equals(DATE_TYPE)) {
                return "    private " + this.type + " " + this.name + " = " + this.defaultValue + ";\n";
            } else {
                return "    " + "@JsonFormat(pattern = \"yyyy-MM-dd'T'HH:mm:ss.SSSZ\")\n"
                        + "    private " + this.type + " " + this.name + " = " + this.defaultValue + ";\n";
            }

        }
    }

    public String getJdbcMapper() {
        return "object.set" + this.getName1() + "((" + this.getType() + ") rs.getObject(\"" + this.getName() + "\"));\n";
    }

    public String getSetter() {
        String ret = "public void set[_VaribleField]([VaribleType] [VaribleField]) {\n"
                + "        this.[VaribleField] = [VaribleField] ;\n"
                + "    }";
        ret = ret.replace("[_VaribleField]", this.getName1());
        ret = ret.replace("[VaribleType]", this.type);
        ret = ret.replace("[VaribleField]", this.name);
        return ret;
    }

    public String getGetterJson() {
        String ret = " @JsonProperty(\"[VaribleField]\")\n"
                + "public [VaribleType] get[_VaribleField]() {\n"
                + "        return this.[VaribleField];\n"
                + "    }";
        ret = ret.replace("[_VaribleField]", this.getName1());
        ret = ret.replace("[VaribleType]", this.type);
        ret = ret.replace("[VaribleField]", this.name);
        return ret;
    }

    public String getGetter() {
        String ret = "public [VaribleType] get[_VaribleField]() {\n"
                + "        return this.[VaribleField];\n"
                + "    }";
        ret = ret.replace("[_VaribleField]", this.getName1());
        ret = ret.replace("[VaribleType]", this.type);
        ret = ret.replace("[VaribleField]", this.name);
        return ret;
    }

    public String getName1() {
        return this.name.substring(0, 1).toUpperCase() + this.name.substring(1);
    }

    public void initDefaultValue() {
        logger.info("Init default value");
        if (this.type.equals(Varible.STRING_TYPE)) {
            if (this.primeKey) {
                this.defaultValue = "Global.getRandomString()";
            } else {
                this.defaultValue = "\"\"";
            }
        }
        if (this.type.equals(Varible.BIGDECIMAL_TYPE)) {
            this.defaultValue = "java.math.BigDecimal.valueOf(0)";
        }
        if (this.type.equals(Varible.BIGINT_TYPE)) {
            this.defaultValue = "java.math.BigInteger.valueOf(System.currentTimeMillis())";
        }
        if (this.type.equals(Varible.BOOLEAN_TYPE)) {
            this.defaultValue = "Boolean.FALSE";
        }
        if (this.type.equals(Varible.DATE_TYPE)) {
            this.defaultValue = "new java.util.Date()";
        }
        if (this.type.equals(Varible.DOUBLE_TYPE)) {
            this.defaultValue = "Double.valueOf(0.00)";
        }
        if (this.type.equals(Varible.FLOAT_TYPE)) {
            this.defaultValue = "Float.valueOf(0)";
        }
        if (this.type.equals(Varible.LONG_TYPE)) {
            this.defaultValue = "Long.valueOf(0)";
        }
        if (this.type.equals(Varible.INT_TYPE)) {
            this.defaultValue = "0";
        }
    }
//    if (type.equals("Integer")) {
//            return "  private " + this.type + " " + this.name + " = 0;\n";
//        }
//        if (type.equals("Float")) {
//            return "  private " + this.type + " " + this.name + " = Float.valueOf(0);\n";
//        }
//        if (type.equals("Double")) {
//            return "  private " + this.type + " " + this.name + " = Double.valueOf(0.00);\n";
//        }
//        if (type.equals("java.util.Date")) {
//            return "  private " + this.type + " " + this.name + " = new java.util.Date();\n";
//        }
//        if (type.equals("java.math.BigInteger")) {
//            return "  private " + this.type + " " + this.name + " = java.math.BigInteger.valueOf(System.currentTimeMillis());\n";
//        }
//        if (type.equals("java.math.BigDecimal")) {
//            return "  private " + this.type  " " + this.name + " = java.math.BigDecimal.valueOf(0);\n";
//        }
//        if (type.equals("Long")) {
//            return "  private " + this.type + " " + this.name + " = Long.valueOf(0);\n";
//        }
//        if (type.equals("Boolean")) {
//            return "  private " + this.type + " " + this.name + " = false;\n";
//        }

    public void initValidateMessage() {
        logger.info("Project:" + this.getProject().getName());
        List<Language> list = this.getProject().getLanguage_list();
        this.validation_msg = new ArrayList();
        for (Language l : list) {
            LanguageData lang = new LanguageData(l.getLanguageid(), this.getName1() + " required");
            this.validation_msg.add(lang);
//            logger.info(lang.toJson());
        }
    }

    public void initHelpMessage() {
        List<Language> list = this.getProject().getLanguage_list();
        this.help_msg = new ArrayList();
        for (Language l : list) {
            LanguageData lang = new LanguageData(l.getLanguageid(), this.getName1() + " required");
            this.help_msg.add(lang);
//            logger.info(lang.toJson());
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
     * @return the primeKey
     */
    public Boolean getPrimeKey() {
        return primeKey;
    }

    /**
     * @param primeKey the primeKey to set
     */
    public void setPrimeKey(Boolean primeKey) {
        this.primeKey = primeKey;
    }

    /**
     * @return the validVarible
     */
    public Integer getValidVarible() {
        return validVarible;
    }

    /**
     * @param validVarible the validVarible to set
     */
    public void setValidVarible(Integer validVarible) {
        this.validVarible = validVarible;
    }

    /**
     * @return the help_msg
     */
    public List<LanguageData> getHelp_msg() {
        return help_msg;
    }

    /**
     * @param help_msg the help_msg to set
     */
    public void setHelp_msg(List<LanguageData> help_msg) {
        this.help_msg = help_msg;
    }

    public String getValidateMessge(Language lang) {
        for (LanguageData data : this.validation_msg) {
            if (data.getLanguageid().equals(lang.getLanguageid())) {
                return data.getContent();
            }
        }
        return "";
    }

    public String getHelpMessge(Language lang) {
        for (LanguageData data : this.help_msg) {
            if (data.getLanguageid().equals(lang.getLanguageid())) {
                return data.getContent();
            }
        }
        return "";
    }

    public String getLabelName(String ObjClass) {
        String ret;
        String temp1 = "<h:outputLabel style=\"font-weight:bolder\" value=\"#{msg['obj.objclass.detail.fieldname']}\"/>\n";
        String temp2 = "<h:outputLabel style=\"font-weight:bolder\" value=\"#{msg['obj.objclass.detail.fieldname']} (*)\"/>\n";
        if (this.required) {
            ret = temp2;
        } else {
            ret = temp1;
        }
        ret = ret.replace("objclass", ObjClass);
        ret = ret.replace("fieldname", this.getName());
        return ret;
    }

    public String getOutputColumn(String displayObject, String objclass) {
        String ret = "<p:column style=\"text-align: left;\" filterBy=\"#{obj.fieldname}\" filterMatchMode=\"contains\" filterStyle=\"width:95%\">\n"
                + "            <f:facet name=\"header\">\n"
                + "                #{msg['obj.objclass.detail.fieldname']}\n"
                + "            </f:facet>\n";
        ret += "         " + this.getOutputComponent(displayObject) + "\n"
                + "        </p:column>\n";
        ret = ret.replace("objclass", objclass);
        ret = ret.replace("fieldname", this.getName());
        return ret;
    }

    public String getOutputComponent(String displayObject) {
//        <h:outputText value="Owner:#{obj.owner.account.fullname}"/>
        if (this.getType().equals(Varible.BOOLEAN_TYPE)) {
            String ret = "<p:selectBooleanCheckbox disabled=\"true\" value=\"#{displayObject.fieldname}\"/>";
            ret = ret.replace("fieldname", this.getName());
            ret = ret.replace("displayObject", displayObject);
            return ret;
        }
        if (this.getType().equals(Varible.DATE_TYPE)) {
            String ret = "<h:outputText value=\"#{displayObject.fieldname}\">\n";
            ret += "  <f:convertDateTime type=\"date\" timeZone=\"GMT+7\" pattern=\"dd/MM/yyyy HH:mm:ss\"/>\n";
            ret += "  </h:outputText>";
            ret = ret.replace("fieldname", this.getName());
            ret = ret.replace("displayObject", displayObject);
            return ret;
        }
        String ret = "<h:outputText style=\"white-space: pre-wrap\" value=\"#{displayObject.fieldname}\"/>";
        ret = ret.replace("fieldname", this.getName());
        ret = ret.replace("displayObject", displayObject);
        return ret;
    }

    public String getInputComponent(String objclass, String displayObject) {
//        <h:outputText value="Owner:#{obj.owner.account.fullname}"/>
        String ret = "<p:outputPanel>\n";
        String data = "";
        String endTag = "";
        if (this.getType().equals(Varible.BOOLEAN_TYPE)) {
            data = "<p:selectBooleanCheckbox id=\"id_fieldname\" value=\"#{displayObject.fieldname}\"/>";
        }
        if (this.getType().equals(Varible.STRING_TYPE)) {
            String temp1 = "<p:inputText readonly=\"true\" id=\"id_fieldname\" style=\"width:90%\"  value=\"#{displayObject.fieldname}\">";
            String temp2 = "<p:inputText readonly=\"true\" id=\"id_fieldname\" style=\"width:90%\"  value=\"#{displayObject.fieldname}\" required=\"true\" requiredMessage=\"#{msg['obj.objclass.detail.fieldname.validateMsg']}\">";
            if (!this.required) {
                data = temp1;
            } else {
                data = temp2;
            }
            if (!this.primeKey) {
                data = data.replace("readonly=\"true\"", "");
            }
            endTag = "</p:inputText>";
        }

        if (this.getType().equals(Varible.DATE_TYPE)) {
            String temp1 = "<p:calendar readonly=\"true\" id=\"id_fieldname\" value=\"#{displayObject.fieldname}\" pattern=\"dd/MM/yyyy HH:mm:ss\">";
            String temp2 = "<p:calendar readonly=\"true\" id=\"id_fieldname\" value=\"#{displayObject.fieldname}\" required=\"true\" requiredMessage=\"#{msg['obj.objclass.detail.fieldname.validateMsg']}\" pattern=\"dd/MM/yyyy HH:mm:ss\">";
            if (!this.required) {
                data = temp1;
            } else {
                data = temp2;
            }
            if (!this.primeKey) {
                data = data.replace("readonly=\"true\"", "");
            }
            endTag = "</p:calendar>";
        }
        if (this.getType().equals(Varible.INT_TYPE) || this.getType().equals(Varible.LONG_TYPE) || this.getType().equals(Varible.BIGINT_TYPE)) {
            String temp1 = "<p:inputNumber readonly=\"true\" id=\"id_fieldname\" value=\"#{displayObject.fieldname}\" decimalPlaces=\"0\">";
            String temp2 = "<p:inputNumber readonly=\"true\" id=\"id_fieldname\" value=\"#{displayObject.fieldname}\" required=\"true\" requiredMessage=\"#{msg['obj.objclass.detail.fieldname.validateMsg']}\" decimalPlaces=\"0\">";
            if (!this.required) {
                data = temp1;
            } else {
                data = temp2;
            }
            if (!this.primeKey) {
                data = data.replace("readonly=\"true\"", "");
            }
            endTag = "</p:inputNumber>";
        }

        if (this.getType().equals(Varible.FLOAT_TYPE) || this.getType().equals(Varible.DOUBLE_TYPE) || this.getType().equals(Varible.BIGDECIMAL_TYPE)) {
            String temp1 = "<p:inputNumber readonly=\"true\" id=\"id_fieldname\" value=\"#{displayObject.fieldname}\">";
            String temp2 = "<p:inputNumber readonly=\"true\" id=\"id_fieldname\" value=\"#{displayObject.fieldname}\" required=\"true\" requiredMessage=\"#{msg['obj.objclass.detail.fieldname.validateMsg']}\">";
            if (!this.required) {
                data = temp1;
            } else {
                data = temp2;
            }
            if (!this.primeKey) {
                data = data.replace("readonly=\"true\"", "");
            }
            endTag = "</p:inputNumber>";
        }

        ret += "   " + data + "\n";
        ret += "   <h:outputLabel style=\"font-weight:bolder\" value=\"#{msg['obj.objclass.detail.fieldname.helpMsg']}\"/>\n";
        ret += "   <p:message id=\"msg_id_fieldname\" for=\"id_fieldname\"/>\n";
        ret += "   " + endTag + "\n";
        ret += "</p:outputPanel>\n";
        ret = ret.replace("fieldname", this.getName());
        ret = ret.replace("objclass", objclass);
        ret = ret.replace("displayObject", displayObject);
        return ret;
    }

    /**
     * @return the autoIncrement
     */
    public Boolean getAutoIncrement() {
        return autoIncrement;
    }

    /**
     * @param autoIncrement the autoIncrement to set
     */
    public void setAutoIncrement(Boolean autoIncrement) {
        this.autoIncrement = autoIncrement;
    }

    public String getMapPut() {
        return "map.put(\"" + this.getName() + "\", this." + this.getName() + ");\n";
    }
}
