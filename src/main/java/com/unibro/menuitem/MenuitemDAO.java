package com.unibro.menuitem;

import com.unibro.utils.Global;
import java.util.List;
import jdbchelper.MappingBatchFeeder;
import org.apache.log4j.Logger;

/**
 *
 * @author Nguyen Duc Tho
 */
public class MenuitemDAO {

    static final Logger logger = Logger.getLogger(MenuitemDAO.class.getName());

    public MenuitemDAO() {
        //super(); 
        //this.setConnectionPool(Global.getDbConnectionPool());
    }

    public long getTotalObject(String condition, Object... params) {
        String SQL = "SELECT count(*) as total FROM " + Global.getDBName() + ".menuitem " + condition;
        return Global.getJDBCHelper().queryForLong(SQL, params);
    }

    public Menuitem getObjectByKey(String id) {
        return Global.getJDBCHelper().queryForObject("SELECT * FROM " + Global.getDBName() + ".menuitem WHERE id=?", Menuitem.beanCreator, id);
    }

    public List<Menuitem> load(String conditionClause, Object... conditionValue) {
        String SQL = "SELECT * FROM " + Global.getDBName() + ".menuitem " + conditionClause;
        SQL = SQL.trim();
        if (conditionValue != null) {
            return Global.getJDBCHelper().queryForList(SQL, Menuitem.beanCreator, conditionValue);
        } else {
            return Global.getJDBCHelper().queryForList(SQL, Menuitem.beanCreator);
        }
    }

    public boolean create(Menuitem obj) {
        try {
            int rows = Global.getJDBCHelper().execute("INSERT INTO " + Global.getDBName() + ".menuitem(id,name,outcome,submenu,parent_id,createdtime,projectid,orderindex,icon) values(?,?,?,?,?,?,?,?,?)", obj, Menuitem.statementMapper);
            return rows > 0;
        } catch (Exception ex) {
            logger.info(ex);
            return false;
        }
    }

    public int create(List<Menuitem> list) {
        if (list == null || list.isEmpty()) {
            return 0;
        }
        int[] rows = Global.getJDBCHelper().executeBatch("INSERT INTO " + Global.getDBName() + ".menuitem(id,name,outcome,submenu,parent_id,createdtime,projectid,orderindex,icon) values(?,?,?,?,?,?,?,?,?)", new MappingBatchFeeder<Menuitem>(list.iterator(), Menuitem.statementMapper));
        return rows.length;
    }

    public boolean edit(Menuitem obj) {
        try {
            int rows = Global.getJDBCHelper().execute("UPDATE " + Global.getDBName() + ".menuitem SET id = ?,name = ?,outcome = ?,submenu = ?,parent_id = ?,createdtime = ?,projectid = ?,orderindex=?,icon=? WHERE id=?", obj.getId(), obj.getName(), obj.getOutcome(), obj.getSubmenu(), obj.getParentId(), obj.getCreatedtime(), obj.getProjectid(), obj.getOrderindex(),obj.getIcon(), obj.getId());
            return rows > 0;
        } catch (Exception ex) {
            return false;
        }
    }

    public boolean delete(Menuitem obj) {
        int rows = Global.getJDBCHelper().execute("DELETE FROM " + Global.getDBName() + ".menuitem WHERE id=?", obj.getId());
        return rows > 0;
    }

    public int delete(List<Menuitem> list) {
        if (list == null || list.isEmpty()) {
            return 0;
        }
        int sum = 0;
        for (Menuitem list1 : list) {
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

    public Menuitem queryForObject(String whereClause, Object... conditionValue) {
        return Global.getJDBCHelper().queryForObject("SELECT * FROM " + Global.getDBName() + ".menuitem " + whereClause, Menuitem.beanCreator, conditionValue);
    }

}
