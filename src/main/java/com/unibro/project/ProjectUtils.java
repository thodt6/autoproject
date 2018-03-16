/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.unibro.project;

import com.google.common.io.Files;
import com.unibro.utils.Global;
import java.io.File;
import java.io.IOException;
import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;

/**
 *
 * @author THOND
 */
public class ProjectUtils {

    private static final Logger logger = Logger.getLogger(ProjectUtils.class.getName());

    protected static void copyProjectCMSAbstract(Project p) {
        try {
            File src = new File(Global.getConfigValue("FILE_PROJECT_TEMPLATE") + "autoproject_cms_abstract_template");
            File dst = new File(Global.getConfigValue("FILE_PROJECT_PATH"));
            FileUtils.copyDirectoryToDirectory(src, dst);
            File current_dst = new File(Global.getConfigValue("FILE_PROJECT_PATH") + "autoproject_cms_abstract_template");
            Files.move(current_dst, new File(Global.getConfigValue("FILE_PROJECT_PATH") + p.getName()));
            //Replace project name in log4j
            File log4j = new File(Global.getConfigValue("FILE_PROJECT_PATH") + p.getName() + "/src/main/resources/config/log4j.properties");
            Global.replaceString(log4j, "projectname", p.getName());
            File config4j = new File(Global.getConfigValue("FILE_PROJECT_PATH") + p.getName() + "/src/main/resources/config/config.properties");
            Global.replaceString(config4j, "projectname", p.getName());
            File meta_inf = new File(Global.getConfigValue("FILE_PROJECT_PATH") + p.getName() + "/src/main/webapp/META-INF/context.xml");
            Global.replaceString(meta_inf, "projectname", p.getName());
            File template = new File(Global.getConfigValue("FILE_PROJECT_PATH") + p.getName() + "/src/main/webapp/WEB-INF/template.xhtml");
            Global.replaceString(template, "projectname", p.getName());
            File footer = new File(Global.getConfigValue("FILE_PROJECT_PATH") + p.getName() + "/src/main/webapp/WEB-INF/footer.xhtml");
            Global.replaceString(footer, "projectname", p.getName());
            File pomFile = new File(Global.getConfigValue("FILE_PROJECT_PATH") + p.getName() + "/pom.xml");
            Global.replaceString(pomFile, "autoprojecttemplate", p.getProjectid());
        } catch (IOException ex) {
            logger.error(ex);
        }
    }

    protected static void copyApiMySQLProject(Project p) {
        try {
            File src = new File(Global.getConfigValue("FILE_PROJECT_TEMPLATE") + "autoproject_api_template");
            logger.info(src.getAbsolutePath());
            File dst = new File(Global.getConfigValue("FILE_PROJECT_PATH"));
            logger.info(dst.getAbsolutePath());
            FileUtils.copyDirectoryToDirectory(src, dst);
            File current_dst = new File(Global.getConfigValue("FILE_PROJECT_PATH") + "autoproject_api_template");
            Files.move(current_dst, new File(Global.getConfigValue("FILE_PROJECT_PATH") + p.getName()));
            //Replace project name in log4j
            File log4j = new File(Global.getConfigValue("FILE_PROJECT_PATH") + p.getName() + "/src/main/resources/log4j.properties");
            File pomFile = new File(Global.getConfigValue("FILE_PROJECT_PATH") + p.getName() + "/pom.xml");
            Global.replaceString(log4j, "projectname", p.getName());
            Global.replaceString(pomFile, "autoprojectapitemplate", p.getProjectid());
        } catch (IOException ex) {
            logger.error(ex);
        }
    }

    protected static void copyApiOracleProject(Project p) {
        try {
            File src = new File(Global.getConfigValue("FILE_PROJECT_TEMPLATE") + "autoproject_api_oracle_template");
            logger.info(src.getAbsolutePath());
            File dst = new File(Global.getConfigValue("FILE_PROJECT_PATH"));
            logger.info(dst.getAbsolutePath());
            FileUtils.copyDirectoryToDirectory(src, dst);
            File current_dst = new File(Global.getConfigValue("FILE_PROJECT_PATH") + "autoproject_api_oracle_template");
            Files.move(current_dst, new File(Global.getConfigValue("FILE_PROJECT_PATH") + p.getName()));
            //Replace project name in log4j
            File log4j = new File(Global.getConfigValue("FILE_PROJECT_PATH") + p.getName() + "/src/main/resources/log4j.properties");
            File pomFile = new File(Global.getConfigValue("FILE_PROJECT_PATH") + p.getName() + "/pom.xml");
            Global.replaceString(log4j, "projectname", p.getName());
            Global.replaceString(pomFile, "autoprojectapitemplate", p.getProjectid());
        } catch (IOException ex) {
            logger.error(ex);
        }
    }

    protected static void copyApiElasticProject(Project p) {
        try {
            File src = new File(Global.getConfigValue("FILE_PROJECT_TEMPLATE") + "autoproject_api_elastic_template");
            logger.info(src.getAbsolutePath());
            File dst = new File(Global.getConfigValue("FILE_PROJECT_PATH"));
            logger.info(dst.getAbsolutePath());
            FileUtils.copyDirectoryToDirectory(src, dst);
            File current_dst = new File(Global.getConfigValue("FILE_PROJECT_PATH") + "autoproject_api_elastic_template");
            Files.move(current_dst, new File(Global.getConfigValue("FILE_PROJECT_PATH") + p.getName()));
            //Replace project name in log4j
            File log4j = new File(Global.getConfigValue("FILE_PROJECT_PATH") + p.getName() + "/src/main/resources/log4j.properties");
            File pomFile = new File(Global.getConfigValue("FILE_PROJECT_PATH") + p.getName() + "/pom.xml");
            Global.replaceString(log4j, "projectname", p.getName());
            Global.replaceString(pomFile, "autoprojectapitemplate", p.getProjectid());
        } catch (IOException ex) {
            logger.error(ex);
        }
    }

    protected static void copyProjectCMSApi(Project p) {
        try {
            logger.info("Start copy api cms project");
            File src = new File(Global.getConfigValue("FILE_PROJECT_TEMPLATE") + "autoproject_cms_api_template");
            File dst = new File(Global.getConfigValue("FILE_PROJECT_PATH"));
            FileUtils.copyDirectoryToDirectory(src, dst);
            File current_dst = new File(Global.getConfigValue("FILE_PROJECT_PATH") + "autoproject_cms_api_template");
            Files.move(current_dst, new File(Global.getConfigValue("FILE_PROJECT_PATH") + p.getName()));
            //Replace project name in log4j
            File log4j = new File(Global.getConfigValue("FILE_PROJECT_PATH") + p.getName() + "/src/main/resources/config/log4j.properties");
            Global.replaceString(log4j, "projectname", p.getName());
            File config4j = new File(Global.getConfigValue("FILE_PROJECT_PATH") + p.getName() + "/src/main/resources/config/config.properties");
            Global.replaceString(config4j, "projectname", p.getName());
            File meta_inf = new File(Global.getConfigValue("FILE_PROJECT_PATH") + p.getName() + "/src/main/webapp/META-INF/context.xml");
            Global.replaceString(meta_inf, "projectname", p.getName());
            File template = new File(Global.getConfigValue("FILE_PROJECT_PATH") + p.getName() + "/src/main/webapp/WEB-INF/template.xhtml");
            Global.replaceString(template, "projectname", p.getName());
            File footer = new File(Global.getConfigValue("FILE_PROJECT_PATH") + p.getName() + "/src/main/webapp/WEB-INF/footer.xhtml");
            Global.replaceString(footer, "projectname", p.getName());
            File pomFile = new File(Global.getConfigValue("FILE_PROJECT_PATH") + p.getName() + "/pom.xml");
            Global.replaceString(pomFile, "autoprojecttemplate", p.getProjectid());
        } catch (IOException ex) {
            logger.error(ex);
        }
    }

}
