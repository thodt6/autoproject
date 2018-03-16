/*
 * To change template license header, choose License Headers in Project Properties.
 * To change template template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.unibro.objtemplate.utils;

import com.unibro.objtemplate.ObjTemplate;
import com.unibro.objtemplate.Varible;
import com.unibro.utils.Global;
import java.io.File;
import java.io.IOException;
import org.apache.commons.io.FileUtils;

/**
 *
 * @author THOND
 */
public class ElasticApiUtils {

    ObjTemplate template;
    static final org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger(ElasticApiUtils.class.getName());

    public ElasticApiUtils(ObjTemplate template) {
        this.template = template;
    }

    private String writeElasticApiObjClass() {
        try {
            File ObjClassTemFile = new File(Global.getConfigValue("FILE_PROJECT_TEMPLATE") + "/elasticobject/ObjClass.java");
            String content = FileUtils.readFileToString(ObjClassTemFile);
            //Generate package name
            content = content.replace("com.unibro.[objclass]", "com.unibro." + template.getName().toLowerCase());
            String varible_def = "";
            for (Varible v : template.getVaribleController().getList_varible()) {
                varible_def += v.getApiVaribleDefinition();
            }
            //Generate Varible
            content = content.replace("[VaribleDefinition]", varible_def);
            String varible_default = "";
            for (Varible v : template.getVaribleController().getList_varible()) {
                if (!v.getDefaultValue().equals("") && v.getDefaultValue() != null) {
                    varible_default += "instant.set" + v.getName1() + "(" + v.getDefaultValue() + ");\n";
                }
            }
            content = content.replace("[DefaultValue]", varible_default);

            //Generate getter and setter
            String getter_setter_data = "";
            for (Varible v : template.getVaribleController().getList_varible()) {
                getter_setter_data += v.getSetter() + "\n";
                getter_setter_data += v.getGetterJson() + "\n";
            }
            content = content.replace("[SetterAndGetter]", getter_setter_data);

            //Generate map put
            String map_put_data = "";
            for (Varible v : template.getVaribleController().getList_varible()) {
                map_put_data += v.getMapPut();
            }

            content = content.replace("[MapPut]", map_put_data);
            //Generate unique key
            String uniqueKey = "";
            for (Varible v : template.getVaribleController().getList_varible()) {
                if (v.getPrimeKey()) {
                    if (v.getType().equals("String")) {
                        uniqueKey += "+" + "\"_\"" + "+" + v.getName();
                    } else {
                        uniqueKey += "+" + "\"_\"" + "+" + v.getName() + ".toString()";
                    }
                }
            }
            if (uniqueKey.length() > 0) {
                uniqueKey = uniqueKey.substring(5).trim();
            }
            content = content.replace("[uniqueKey]", uniqueKey);

            content = content.replace("[objclass]", template.getName());
            content = content.replace("[ObjClass]", template.getClassName());

            File file_dir = new File(template.getProject().getProjectFolder() + "/src/main/java/com/unibro/" + template.getClassName().toLowerCase());
            file_dir.mkdirs();
            FileUtils.writeStringToFile(new File(file_dir.getAbsolutePath() + "/" + template.getClassName() + ".java"), content);
//            logger.info(content);
            return content;
        } catch (IOException ex) {
            logger.error(ex);
            return "";
        }
    }

    private String writeElasticApiObjClassDAO() {
        try {
            File ObjClassTemFile = new File(Global.getConfigValue("FILE_PROJECT_TEMPLATE") + "/elasticobject/ObjClassDAO.java");
            String content = FileUtils.readFileToString(ObjClassTemFile);
            content = content.replace("com.unibro.[objclass]", "com.unibro." + template.getName().toLowerCase());
            for (Varible v : template.getVaribleController().getList_varible()) {
                if (v.getPrimeKey()) {
                    content = content.replace("[KEYTYPE]", v.getType());
                    break;
                }
            }

            content = content.replace("[objclass]", template.getName());
            content = content.replace("[ObjClass]", template.getClassName());

            File file_dir = new File(template.getProject().getProjectFolder() + "/src/main/java/com/unibro/" + template.getClassName().toLowerCase());
            file_dir.mkdirs();
            FileUtils.writeStringToFile(new File(file_dir.getAbsolutePath() + "/" + template.getClassName() + "DAO.java"), content);
            logger.info(content);
            return content;
        } catch (IOException ex) {
            logger.error(ex);
            return "";
        }
    }

    private String writeElasticApiObjClassDAOImpl() {
        try {
            File ObjClassTemFile = new File(Global.getConfigValue("FILE_PROJECT_TEMPLATE") + "/elasticobject/ObjClassDAOImpl.java");
            String content = FileUtils.readFileToString(ObjClassTemFile);
            content = content.replace("com.unibro.[objclass]", "com.unibro." + template.getName().toLowerCase());
//            for (Varible v : template.getVaribleController().getList_varible()) {
//                if (v.getPrimeKey()) {
//                    content = content.replace("[KEYTYPE]", v.getType());
//                    break;
//                }
//            }

//            for (Varible v : template.getVaribleController().getList_varible()) {
//                if (v.getPrimeKey()) {
//                    content = content.replace("[KEY]", v.getName());
//                    content = content.replace("[OBJECTKEY]", v.getName1());
//                    break;
//                }
//            }
            content = content.replace("[objclass]", template.getName());
//            content = content.replace("objclass", template.getName());
            content = content.replace("[ObjClass]", template.getClassName());

            File file_dir = new File(template.getProject().getProjectFolder() + "/src/main/java/com/unibro/" + template.getClassName().toLowerCase());
            file_dir.mkdirs();
            FileUtils.writeStringToFile(new File(file_dir.getAbsolutePath() + "/" + template.getClassName() + "DAOImpl.java"), content);
            logger.info(content);
            return content;
        } catch (IOException ex) {
            logger.error(ex);
            return "";
        }
    }

    private String writeElasticApiObjClassController() {
        try {
            File ObjClassTemFile = new File(Global.getConfigValue("FILE_PROJECT_TEMPLATE") + "/elasticobject/ObjClassController.java");
            String content = FileUtils.readFileToString(ObjClassTemFile);
            content = content.replace("com.unibro.[objclass]", "com.unibro." + template.getName().toLowerCase());
            String authorization_data = "String[] params = {authorization, id};";

            for (Varible v : template.getVaribleController().getList_varible()) {
                if (v.getPrimeKey()) {
                    content = content.replace("[KEYTYPE]", v.getType());
                    if (!v.getType().equals(Varible.STRING_TYPE)) {
                        authorization_data = "String[] params = {authorization, String.valueOf(id)};";
                    }
                    break;
                }
            }
            content = content.replace("[authorization_data]", authorization_data);
            content = content.replace("[objclass]", template.getName());
//            content = content.replace("objclass", template.getName());
            content = content.replace("[ObjClass]", template.getClassName());

            File file_dir = new File(template.getProject().getProjectFolder() + "/src/main/java/com/unibro/" + template.getClassName().toLowerCase());
            file_dir.mkdirs();
            FileUtils.writeStringToFile(new File(file_dir.getAbsolutePath() + "/" + template.getClassName() + "Controller.java"), content);
            logger.info(content);
            return content;
        } catch (IOException ex) {
            logger.error(ex);
            return "";
        }
    }

    private String writeElasticApiObjClassException() {
        try {
            File ObjClassTemFile = new File(Global.getConfigValue("FILE_PROJECT_TEMPLATE") + "/elasticobject/ObjClassDAOException.java");
            String content = FileUtils.readFileToString(ObjClassTemFile);
            content = content.replace("com.unibro.[objclass]", "com.unibro." + template.getName().toLowerCase());
            content = content.replace("[objclass]", template.getName());
            content = content.replace("objclass", template.getName());
            content = content.replace("ObjClass", template.getClassName());

            File file_dir = new File(template.getProject().getProjectFolder() + "/src/main/java/com/unibro/" + template.getClassName().toLowerCase());
            file_dir.mkdirs();
            FileUtils.writeStringToFile(new File(file_dir.getAbsolutePath() + "/" + template.getClassName() + "DAOException.java"), content);
            logger.info(content);
            return content;
        } catch (IOException ex) {
            logger.error(ex);
            return "";
        }
    }

    public void generateCode() {
        this.writeElasticApiObjClass();
        this.writeElasticApiObjClassController();
        this.writeElasticApiObjClassDAO();
        this.writeElasticApiObjClassDAOImpl();
        this.writeElasticApiObjClassException();
    }

}
