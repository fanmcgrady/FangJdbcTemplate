package javadev.core.fangSql.helper;

import javadev.core.fangSql.FangJdbcTemplate;
import javadev.core.fangSql.util.Closer;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 获取某张table的字段列表
 *
 * @author fangzhiyang
 */
class TableInfo {
    private List<String> columns;

    public static TableInfo create(FangJdbcTemplate jt, String table) throws Exception {
        String sql = jt.getTableInfoSql(table);

        Connection db = jt.getDataSource().getConnection();
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            ps = db.prepareStatement(sql);
            rs = ps.executeQuery();

            boolean id = false;
            boolean parentId = false;

            ResultSetMetaData md = rs.getMetaData();
            List<String> columns = new ArrayList<String>();
            for (int i = 1; i <= md.getColumnCount(); i++) {
                String column = md.getColumnName(i).toLowerCase();
                if (column.equals("id")) {
                    id = true;
                    continue;
                }
                if (column.equals("parent_id")) {
                    parentId = true;
                    continue;
                }

                columns.add(column);
            }

            Collections.sort(columns);

            if (parentId) columns.add(0, "parent_id");
            if (id) columns.add(0, "id");

            TableInfo ti = new TableInfo();
            ti.setColumns(columns);

            return ti;
        } finally {
            Closer.close(rs);
            Closer.close(ps);
        }
    }

    public List<String> getColumns() {
        return columns;
    }

    public void setColumns(List<String> columns) {
        this.columns = columns;
    }
}
