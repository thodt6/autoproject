package com.unibro.language;

import com.unibro.utils.Global;
import java.util.List;
import jdbchelper.MappingBatchFeeder;
import org.apache.log4j.Logger;

/**
 *
 * @author Nguyen Duc Tho
 */
public class LanguageDAO {

    static final Logger logger = Logger.getLogger(LanguageDAO.class.getName());

    public LanguageDAO() {
        //super(); 
        //this.setConnectionPool(Global.getDbConnectionPool());
    }

    public long getTotalObject(String condition, Object... params) {
        String SQL = "SELECT count(*) as total FROM " + Global.getDBName() + ".language " + condition;
        return Global.getJDBCHelper().queryForLong(SQL, params);
    }

    public Language getObjectByKey(String languageid) {
        return Global.getJDBCHelper().queryForObject("SELECT * FROM " + Global.getDBName() + ".language WHERE languageid=?", Language.beanCreator, languageid);
    }

    public List<Language> load(String conditionClause, Object... conditionValue) {
        String SQL = "SELECT * FROM " + Global.getDBName() + ".language " + conditionClause;
        SQL = SQL.trim();
        if (conditionValue != null) {
            return Global.getJDBCHelper().queryForList(SQL, Language.beanCreator, conditionValue);
        } else {
            return Global.getJDBCHelper().queryForList(SQL, Language.beanCreator);
        }
    }

    public boolean create(Language obj) {
        int rows = Global.getJDBCHelper().execute("INSERT INTO " + Global.getDBName() + ".language(languageid,name) values(?,?)", obj, Language.statementMapper);
        return rows > 0;
    }

    public int create(List<Language> list) {
        if (list == null || list.isEmpty()) {
            return 0;
        }
        int[] rows = Global.getJDBCHelper().executeBatch("INSERT INTO " + Global.getDBName() + ".language(languageid,name) values(?,?)", new MappingBatchFeeder<Language>(list.iterator(), Language.statementMapper));
        return rows.length;
    }

    public boolean edit(Language obj) {
        int rows = Global.getJDBCHelper().execute("UPDATE " + Global.getDBName() + ".language SET languageid = ?,name = ? WHERE languageid=?", obj.getLanguageid(), obj.getName(), obj.getLanguageid());
        return rows > 0;
    }

    public boolean delete(Language obj) {
        int rows = Global.getJDBCHelper().execute("DELETE FROM " + Global.getDBName() + ".language WHERE languageid=?", obj.getLanguageid());
        return rows > 0;
    }

    public int delete(List<Language> list) {
        if (list == null || list.isEmpty()) {
            return 0;
        }
        int sum = 0;
        for (Language list1 : list) {
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

    public Language queryForObject(String whereClause, Object... conditionValue) {
        return Global.getJDBCHelper().queryForObject("SELECT * FROM " + Global.getDBName() + ".language " + whereClause, Language.beanCreator, conditionValue);
    }

}
