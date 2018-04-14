package javadev.core.fangSql.helper;

import javadev.core.fangSql.FangJdbcTemplate;
import javadev.core.fangSql.util.Utils;
import org.springframework.jdbc.core.BeanPropertyRowMapper;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


/**
 * 一个使用Bean来进行数据库操作的工具帮助类
 *
 * @author fangzhiyang
 */
public class FangSqlHelper {
    private FangJdbcTemplate jt;

    private Map<String, TableInfo> tables = new ConcurrentHashMap<>();
    private Map<Class<?>, ClassInfo> classes = new ConcurrentHashMap<>();

    public FangSqlHelper(FangJdbcTemplate jt) {
        this.jt = jt;
    }

    /**
     * 通过include, exclude来过滤参数
     * include: 只查询哪些字段
     * exclude: 不查询哪些字段
     */
    private static List<String> filterColumns(TableInfo ti, String include, String exclude) {
        if (include != null) include = include.trim();
        if (exclude != null) exclude = exclude.trim();

        if (include != null && !include.equals("")) {
            return Arrays.asList(include.split("[ \t]*,[ \t]*"));
        }

        if (exclude != null && !exclude.equals("")) {
            String[] ss = exclude.split("[ \t]*,[ \t]*");

            List<String> list = new ArrayList<String>();
            outter:
            for (String s1 : ti.getColumns()) {
                for (String s2 : ss) {
                    if (s1.equals(s2)) continue outter;
                }

                list.add(s1);
            }

            return list;
        }

        return ti.getColumns();
    }

    /**
     * 查询一条
     */
    public <T> T query(Class<T> type, String table, String include, String exclude, String where, Object... parameters) {
        try {
            String sql = prepareSqlForSelect(table, include, exclude, null, where);
            List<T> list = jt.query(sql, parameters, new BeanPropertyRowMapper<>(type));
            if (list == null || list.size() == 0)
                return null;
            return list.get(0);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 查询多条
     */
    public <T> List<T> list(Class<T> type, String table, String include, String exclude, String order, String where, Object... parameters) {
        try {
            String sql = prepareSqlForSelect(table, include, exclude, order, where);
            return jt.query(sql, parameters, new BeanPropertyRowMapper<>(type));
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 插入
     */
    public int insert(Object bean, String table, String include, String exclude) {
        StringBuilder sql = new StringBuilder();
        List<Object> values = new ArrayList<Object>();

        try {
            TableInfo ti = getTableInfo(table);
            ClassInfo ci = getClassInfo(bean);

            List<String> columns = filterColumns(ti, include, exclude);
            Map<String, Method> getters = ci.getGetters();

            sql.append("insert into ");
            sql.append(table);
            sql.append("(");
            String d = "";
            for (String column : columns) {
                sql.append(d);
                sql.append(column);
                d = ", ";
            }
            sql.append(") values (");
            d = "";
            for (String column : columns) {
                sql.append(d);
                sql.append("?");

                Method getter = getters.get(Utils.sqlToJava(column));
                values.add(getter.invoke(bean));

                d = ", ";
            }
            sql.append(")");

            return jt.update(sql.toString(), values.toArray());
        } catch (Exception e) {
            return 0;
        }
    }

    /**
     * 更新
     */
    public int update(Object bean, String table, String include, String exclude, String where, Object... parameters) {
        List<Object> values = new ArrayList<Object>();
        StringBuilder sql = new StringBuilder();

        try {
            TableInfo ti = getTableInfo(table);
            ClassInfo ci = getClassInfo(bean);

            List<String> columns = filterColumns(ti, include, exclude);
            Map<String, Method> getters = ci.getGetters();

            sql.append("update ");
            sql.append(table);
            sql.append(" set ");
            String d = "";
            for (String column : columns) {
                sql.append(d);
                sql.append(column);
                sql.append(" = ?");

                Method getter = getters.get(Utils.sqlToJava(column));
                values.add(getter.invoke(bean));

                d = ", ";
            }
            if (where != null) {
                sql.append(" where ");
                sql.append(where);
            }

            values.addAll(Arrays.asList(parameters));
            return jt.update(sql.toString(), values.toArray());
        } catch (Exception e) {
            return 0;
        }
    }

    // ==================================

    /**
     * 删除
     */
    public int delete(String table, String where, Object... parameters) {
        StringBuilder sql = new StringBuilder();
        sql.append("delete from ");
        sql.append(table);
        if (where != null) {
            sql.append(" where ");
            sql.append(where);
        }

        return jt.update(sql.toString(), parameters);
    }

    /**
     * 准备select语句
     */
    private String prepareSqlForSelect(String table, String include, String exclude, String order, String where) throws Exception {
        TableInfo ti = getTableInfo(table);

        List<String> columns = filterColumns(ti, include, exclude);

        StringBuilder sql = new StringBuilder();
        sql.append("select ");
        String d = "";
        for (String column : columns) {
            sql.append(d);
            sql.append(column);
            d = ", ";
        }

        sql.append(" from ");
        sql.append(table);

        if (where != null) {
            sql.append(" where ");
            sql.append(where);
        }

        if (order != null) {
            sql.append(" order by ");
            sql.append(order);
        }

        return sql.toString();
    }

    private synchronized TableInfo getTableInfo(String table) throws Exception {
        TableInfo ti = tables.get(table);
        if (ti == null) {
            ti = TableInfo.create(jt, table);
            tables.put(table, ti);
        }
        return ti;
    }

    private synchronized ClassInfo getClassInfo(Object bean) throws Exception {
        Class<?> type = bean.getClass();
        ClassInfo ci = classes.get(type);
        if (ci == null) {
            ci = ClassInfo.create(type);
            classes.put(type, ci);
        }
        return ci;
    }
}
