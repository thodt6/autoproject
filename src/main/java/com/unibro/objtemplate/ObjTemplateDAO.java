package com.unibro.objtemplate;

import com.unibro.utils.Global;
import java.util.List;
import jdbchelper.JdbcException;
import jdbchelper.MappingBatchFeeder;
import org.apache.log4j.Logger;

/**
 *
 * @author Nguyen Duc Tho
 */
public class ObjTemplateDAO {

    static final Logger logger = Logger.getLogger(ObjTemplateDAO.class.getName());

    public ObjTemplateDAO() {
        //super(); 
        //this.setConnectionPool(Global.getDbConnectionPool());
    }

    public long getTotalObject(String condition, Object... params) {
        String SQL = "SELECT count(*) as total FROM " + Global.getDBName() + ".obj_template " + condition;
        return Global.getJDBCHelper().queryForLong(SQL, params);
    }

    public ObjTemplate getObjectByKey(String id) {
        return Global.getJDBCHelper().queryForObject("SELECT * FROM " + Global.getDBName() + ".obj_template WHERE id=?", ObjTemplate.beanCreator, id);
    }

    public List<ObjTemplate> load(String conditionClause, Object... conditionValue) {
        String SQL = "SELECT * FROM " + Global.getDBName() + ".obj_template " + conditionClause;
        SQL = SQL.trim();
        if (conditionValue != null) {
            return Global.getJDBCHelper().queryForList(SQL, ObjTemplate.beanCreator, conditionValue);
        } else {
            return Global.getJDBCHelper().queryForList(SQL, ObjTemplate.beanCreator);
        }
    }

    public boolean create(ObjTemplate obj) {
        try {
            int rows = Global.getJDBCHelper().execute("INSERT INTO " + Global.getDBName() + ".obj_template(id,projectid,name,object_format,createdtime,help_format) values(?,?,?,?,?,?)", obj, ObjTemplate.statementMapper);
            return rows > 0;
        } catch (JdbcException ex) {
            logger.error(ex);
            return false;
        }
    }

    public int create(List<ObjTemplate> list) {
        if (list == null || list.isEmpty()) {
            return 0;
        }
        int[] rows = Global.getJDBCHelper().executeBatch("INSERT INTO " + Global.getDBName() + ".obj_template(id,projectid,name,object_format,createdtime,help_format) values(?,?,?,?,?,?)", new MappingBatchFeeder<ObjTemplate>(list.iterator(), ObjTemplate.statementMapper));
        return rows.length;
    }

    public boolean edit(ObjTemplate obj) {
        try {
            logger.info(obj.toJson());
            int rows = Global.getJDBCHelper().execute("UPDATE " + Global.getDBName() + ".obj_template SET id = ?,projectid = ?,name = ?,object_format = ?,createdtime=?, help_format=? WHERE id=?", obj.getId(), obj.getProjectid(), obj.getName(), obj.getObjectFormat(), obj.getCreatedtime(), obj.getHelpFormat(), obj.getId());
            return rows > 0;
        } catch (Exception ex) {
            logger.error(ex);
            return false;
        }
    }

    public boolean delete(ObjTemplate obj) {
        int rows = Global.getJDBCHelper().execute("DELETE FROM " + Global.getDBName() + ".obj_template WHERE id=?", obj.getId());
        return rows > 0;
    }

    public int delete(List<ObjTemplate> list) {
        if (list == null || list.isEmpty()) {
            return 0;
        }
        int sum = 0;
        for (ObjTemplate list1 : list) {
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

    public ObjTemplate queryForObject(String whereClause, Object... conditionValue) {
        return Global.getJDBCHelper().queryForObject("SELECT * FROM " + Global.getDBName() + ".obj_template " + whereClause, ObjTemplate.beanCreator, conditionValue);
    }

}
