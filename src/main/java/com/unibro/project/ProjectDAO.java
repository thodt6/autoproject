package com.unibro.project;

import com.unibro.utils.Global;
import java.util.List;
import jdbchelper.MappingBatchFeeder;
import org.apache.log4j.Logger;

/**
 *
 * @author Nguyen Duc Tho
 */
public class ProjectDAO {

    static final Logger logger = Logger.getLogger(ProjectDAO.class.getName());

    public ProjectDAO() {
        //super(); 
        //this.setConnectionPool(Global.getDbConnectionPool());
    }

    public long getTotalObject(String condition, Object... params) {
        String SQL = "SELECT count(*) as total FROM " + Global.getDBName() + ".project " + condition;
        return Global.getJDBCHelper().queryForLong(SQL, params);
    }

    public Project getObjectByKey(String projectid) {
        return Global.getJDBCHelper().queryForObject("SELECT * FROM " + Global.getDBName() + ".project WHERE projectid=?", Project.beanCreator, projectid);
    }

    public List<Project> load(String conditionClause, Object... conditionValue) {
        String SQL = "SELECT * FROM " + Global.getDBName() + ".project " + conditionClause;
        SQL = SQL.trim();
        if (conditionValue != null) {
            return Global.getJDBCHelper().queryForList(SQL, Project.beanCreator, conditionValue);
        } else {
            return Global.getJDBCHelper().queryForList(SQL, Project.beanCreator);
        }
    }

    public boolean create(Project obj) {
        try {
            int rows = Global.getJDBCHelper().execute("INSERT INTO " + Global.getDBName() + ".project(projectid,name,createdtime,language_list,project_folder,project_type,dataAccess_type) values(?,?,?,?,?,?,?)", obj, Project.statementMapper);
            return rows > 0;
        } catch (Exception ex) {
            return false;
        }
    }

    public int create(List<Project> list) {
        if (list == null || list.isEmpty()) {
            return 0;
        }
        int[] rows = Global.getJDBCHelper().executeBatch("INSERT INTO " + Global.getDBName() + ".project(projectid,name,createdtime,language_list,project_folder,project_type,dataAccess_type) values(?,?,?,?,?,?,?)", new MappingBatchFeeder<Project>(list.iterator(), Project.statementMapper));
        return rows.length;
    }

    public boolean edit(Project obj) {
        int rows = Global.getJDBCHelper().execute("UPDATE " + Global.getDBName() + ".project SET projectid = ?,name = ?,createdtime = ?,language_list = ?,project_folder = ?, project_type=?, dataAccess_type=? WHERE projectid=?", obj.getProjectid(), obj.getName(), obj.getCreatedtime(), obj.getLanguageList(), obj.getProjectFolder(), obj.getProjectType(), obj.getDataAccessType(), obj.getProjectid());
        return rows > 0;
    }

    public boolean delete(Project obj) {
        int rows = Global.getJDBCHelper().execute("DELETE FROM " + Global.getDBName() + ".project WHERE projectid=?", obj.getProjectid());
        return rows > 0;
    }

    public int delete(List<Project> list) {
        if (list == null || list.isEmpty()) {
            return 0;
        }
        int sum = 0;
        for (Project list1 : list) {
            if (delete(list1)) {
                sum++;
            }
        }
        return sum;
    }

    public int excuteQuery(String SQL, Object... params) {
        int rows = Global.getJDBCHelper().execute(SQL, params);
        return rows;
    }

    public Project queryForObject(String whereClause, Object... conditionValue) {
        return Global.getJDBCHelper().queryForObject("SELECT * FROM " + Global.getDBName() + ".project " + whereClause, Project.beanCreator, conditionValue);
    }

}
