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
public class MySQLApiUtils {

    ObjTemplate template;
    static final org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger(MySQLApiUtils.class.getName());

    public MySQLApiUtils(ObjTemplate template) {
        this.template = template;
    }

    //MySQL API Area
    private String writeMySqlApiObjClass() {
        try {
            File ObjClassTemFile = new File(Global.getConfigValue("FILE_PROJECT_TEMPLATE") + "/mysqlobject/api/obj/ObjClass.java");
            String content = FileUtils.readFileToString(ObjClassTemFile);
            content = content.replace("com.unibro.[objclass]", "com.unibro." + template.getName().toLowerCase());
            String varible_def = "";
            for (Varible v : template.getVaribleController().getList_varible()) {
                varible_def += v.getApiVaribleDefinition();
            }
            content = content.replace("[VaribleDefinition]", varible_def);
            String varible_default = "";
            for (Varible v : template.getVaribleController().getList_varible()) {
                if (!v.getDefaultValue().equals("") && v.getDefaultValue() != null) {
                    varible_default += "instant.set" + v.getName1() + "(" + v.getDefaultValue() + ");\n";
                }
            }
            content = content.replace("[DefaultValue]", varible_default);

            String getter_setter_data = "";
            for (Varible v : template.getVaribleController().getList_varible()) {
                getter_setter_data += v.getSetter() + "\n";
                getter_setter_data += v.getGetterJson() + "\n";
            }

            content = content.replace("[SetterAndGetter]", getter_setter_data);

//            for (Varible v : template.getVaribleController().getList_varible()) {
//                if (v.getPrimeKey()) {
//                    content = content.replace("[OBJECTKEY]", v.getName1());
//                    break;
//                }
//            }
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

            content = content.replace("com.unibro.[objclass]", "com.unibro." + template.getName().toLowerCase());
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

    private String writeMySqlApiObjClassArrayResult() {
        try {
            File ObjClassTemFile = new File(Global.getConfigValue("FILE_PROJECT_TEMPLATE") + "/mysqlobject/api/obj/ObjClassArrayResult.java");
            String content = FileUtils.readFileToString(ObjClassTemFile);
            content = content.replace("com.unibro.[objclass]", "com.unibro." + template.getName().toLowerCase());
            content = content.replace("objclass", template.getName());
            content = content.replace("ObjClass", template.getClassName());

            File file_dir = new File(template.getProject().getProjectFolder() + "/src/main/java/com/unibro/" + template.getClassName().toLowerCase());
            file_dir.mkdirs();
            FileUtils.writeStringToFile(new File(file_dir.getAbsolutePath() + "/" + template.getClassName() + "ArrayResult.java"), content);
            logger.info(content);
            return content;
        } catch (IOException ex) {
            logger.error(ex);
            return "";
        }
    }

    private String writeMySqlApiObjClassDAO() {
        try {
            File ObjClassTemFile = new File(Global.getConfigValue("FILE_PROJECT_TEMPLATE") + "/mysqlobject/api/obj/ObjClassDAO.java");
            String content = FileUtils.readFileToString(ObjClassTemFile);

            content = content.replace("com.unibro.[objclass]", "com.unibro." + template.getName().toLowerCase());
            content = content.replace("[objclass]", template.getName());
            content = content.replace("[ObjClass]", template.getClassName());

            File file_dir = new File(template.getProject().getProjectFolder() + "/src/main/java/com/unibro/" + template.getClassName().toLowerCase());
            file_dir.mkdirs();
            FileUtils.writeStringToFile(new File(file_dir.getAbsolutePath() + "/" + template.getClassName() + "DAO.java"), content);
            return content;
        } catch (IOException ex) {
            logger.error(ex);
            return "";
        }
    }

    private void writeMySqlApiObjClassDAOImpl() {
        try {
            File ObjClassTemFile = new File(Global.getConfigValue("FILE_PROJECT_TEMPLATE") + "/mysqlobject/api/obj/ObjClassDAOImpl.java");
            String content = FileUtils.readFileToString(ObjClassTemFile);
            content = content.replace("com.unibro.[objclass]", "com.unibro." + template.getName().toLowerCase());
            content = content.replace("[columns]", this.template.getVaribleController().getVaribleStrList());
            content = content.replace("[columns_key]", this.template.getVaribleController().getKeyVaribleStrList());
            content = content.replace("[SPLIT_VALUES]",this.template.getVaribleController().getSplitKeyVarible());
            content = content.replace("[getOBJECTKEY]",this.template.getVaribleController().getObjectKeyData("object"));

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

//            content = content.replace("[CREATE_PARAM]", template.getVaribleController().getInsertJdbcPrepareStatement());
//            content = content.replace("[CREATE_MASK]", template.getVaribleController().getInsertJdbcPrepareStatementMask());
//            content = content.replace("[CREATE_PARAM_STR]", template.getVaribleController().getInsertJdbcPrepareStatementStr());
            content = content.replace("[CREATE_SETOBJECT]", template.getVaribleController().getInsertJdbcPrepareStatementData());

//            content = content.replace("[UPDATE_PARAM_MASK]", template.getVaribleController().getUpdateJdbcPrepareStatementMask());
//            content = content.replace("[UPDATE_PARAM_STR]", template.getVaribleController().getUpdateJdbcPrepareStatementStr());
            content = content.replace("[UPDATE_SETOBJECT]", template.getVaribleController().getUpdateJdbcPrepareStatementData());

            content = content.replace("[objclass]", template.getName());
//            content = content.replace("objclass", template.getName());
            content = content.replace("[ObjClass]", template.getClassName());

            File file_dir = new File(template.getProject().getProjectFolder() + "/src/main/java/com/unibro/" + template.getClassName().toLowerCase());
            file_dir.mkdirs();
            FileUtils.writeStringToFile(new File(file_dir.getAbsolutePath() + "/" + template.getClassName() + "DAOImpl.java"), content);
            logger.info(content);
        } catch (IOException ex) {
            logger.error(ex);
        }
    }

    private void writeMySqlApiObjClassDAOImplForAutoIncrement() {
        try {
            File ObjClassTemFile = new File(Global.getConfigValue("FILE_PROJECT_TEMPLATE") + "/mysqlobject/api/obj/ObjClassDAOImplForAutoIncrement.java");
            String content = FileUtils.readFileToString(ObjClassTemFile);
            content = content.replace("com.unibro.[objclass]", "com.unibro." + template.getName().toLowerCase());
            for (Varible v : template.getVaribleController().getList_varible()) {
                if (v.getPrimeKey()) {
                    content = content.replace("[KEYTYPE]", v.getType());
                    break;
                }
            }

            for (Varible v : template.getVaribleController().getList_varible()) {
                if (v.getPrimeKey()) {
                    content = content.replace("[KEY]", v.getName());
                    content = content.replace("[OBJECTKEY]", v.getName1());
                    break;
                }
            }

            content = content.replace("[CREATE_PARAM]", template.getVaribleController().getInsertJdbcPrepareStatement());
            content = content.replace("[CREATE_MASK]", template.getVaribleController().getInsertJdbcPrepareStatementMask());
            content = content.replace("[CREATE_PARAM_STR]", template.getVaribleController().getInsertJdbcPrepareStatementStr());
            content = content.replace("[CREATE_SETOBJECT]", template.getVaribleController().getInsertJdbcPrepareStatementData());

            content = content.replace("[UPDATE_PARAM_MASK]", template.getVaribleController().getUpdateJdbcPrepareStatementMask());
            content = content.replace("[UPDATE_PARAM_STR]", template.getVaribleController().getUpdateJdbcPrepareStatementStr());
            content = content.replace("[UPDATE_SETOBJECT]", template.getVaribleController().getUpdateJdbcPrepareStatementData());

            content = content.replace("[objclass]", template.getName());
            content = content.replace("objclass", template.getName());
            content = content.replace("ObjClass", template.getClassName());

            File file_dir = new File(template.getProject().getProjectFolder() + "/src/main/java/com/unibro/" + template.getClassName().toLowerCase());
            file_dir.mkdirs();
            FileUtils.writeStringToFile(new File(file_dir.getAbsolutePath() + "/" + template.getClassName() + "DAOImpl.java"), content);
            logger.info(content);
        } catch (IOException ex) {
            logger.error(ex);
        }
    }

    private void writeMySqlApiObjClassController() {
        try {
            File ObjClassTemFile = new File(Global.getConfigValue("FILE_PROJECT_TEMPLATE") + "/mysqlobject/api/obj/ObjClassController.java");
            String content = FileUtils.readFileToString(ObjClassTemFile);
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
            content = content.replace("[ObjClass]", template.getClassName());

            File file_dir = new File(template.getProject().getProjectFolder() + "/src/main/java/com/unibro/" + template.getClassName().toLowerCase());
            file_dir.mkdirs();
            FileUtils.writeStringToFile(new File(file_dir.getAbsolutePath() + "/" + template.getClassName() + "Controller.java"), content);
            logger.info(content);
        } catch (IOException ex) {
            logger.error(ex);
        }
    }

    private void writeMySqlApiObjClassException() {
        try {
            File ObjClassTemFile = new File(Global.getConfigValue("FILE_PROJECT_TEMPLATE") + "/mysqlobject/api/obj/ObjClassDAOException.java");
            String content = FileUtils.readFileToString(ObjClassTemFile);

            content = content.replace("com.unibro.[objclass]", "com.unibro." + template.getName().toLowerCase());
            content = content.replace("[objclass]", template.getName());
            content = content.replace("objclass", template.getName());
            content = content.replace("ObjClass", template.getClassName());

            File file_dir = new File(template.getProject().getProjectFolder() + "/src/main/java/com/unibro/" + template.getClassName().toLowerCase());
            file_dir.mkdirs();
            FileUtils.writeStringToFile(new File(file_dir.getAbsolutePath() + "/" + template.getClassName() + "DAOException.java"), content);
            logger.info(content);
        } catch (IOException ex) {
            logger.error(ex);
        }
    }

    private void writeMySqlApiObjClassMapper() {
        try {
            File ObjClassTemFile = new File(Global.getConfigValue("FILE_PROJECT_TEMPLATE") + "/mysqlobject/api/obj/ObjClassMapper.java");
            String content = FileUtils.readFileToString(ObjClassTemFile);
            content = content.replace("com.unibro.[objclass]", "com.unibro." + template.getName().toLowerCase());
            String mapper_data = "";
            for (Varible v : template.getVaribleController().getList_varible()) {
                mapper_data += v.getJdbcMapper();
            }

            content = content.replace("[MAPPER_LIST]", mapper_data);

            content = content.replace("[objclass]", template.getName());
            content = content.replace("objclass", template.getName());
            content = content.replace("ObjClass", template.getClassName());

            File file_dir = new File(template.getProject().getProjectFolder() + "/src/main/java/com/unibro/" + template.getClassName().toLowerCase());
            file_dir.mkdirs();
            FileUtils.writeStringToFile(new File(file_dir.getAbsolutePath() + "/" + template.getClassName() + "Mapper.java"), content);
            logger.info(content);
        } catch (IOException ex) {
            logger.error(ex);
        }
    }

    private void writeMySqlApiObjClassDataSource() {
        try {
            File ObjClassTemFile = new File(Global.getConfigValue("FILE_PROJECT_TEMPLATE") + "/mysqlobject/api/datasource/Spring-ObjClass.xml");
            String content = FileUtils.readFileToString(ObjClassTemFile);
            content = content.replace("com.unibro.[objclass]", "com.unibro." + template.getName().toLowerCase());
            content = content.replace("[objclass]", template.getName());
            content = content.replace("objclass", template.getName());
            content = content.replace("ObjClass", template.getClassName());

            File file_dir = new File(template.getProject().getProjectFolder() + "/src/main/resources/database/");
            file_dir.mkdirs();
            FileUtils.writeStringToFile(new File(file_dir.getAbsolutePath() + "/Spring-" + template.getClassName() + ".xml"), content);
            logger.info(content);
        } catch (IOException ex) {
            logger.error(ex);
        }
    }

    public void generateCode() {
        this.writeMySqlApiObjClass();
        this.writeMySqlApiObjClassArrayResult();
        this.writeMySqlApiObjClassDAO();
        this.writeMySqlApiObjClassDAOImpl();
//        if (!template.checkAutoIncrement()) {
//            
//        } else {
//            this.writeMySqlApiObjClassDAOImplForAutoIncrement();
//        }
        this.writeMySqlApiObjClassController();
        this.writeMySqlApiObjClassException();
        this.writeMySqlApiObjClassMapper();
        this.writeMySqlApiObjClassDataSource();
    }

}
