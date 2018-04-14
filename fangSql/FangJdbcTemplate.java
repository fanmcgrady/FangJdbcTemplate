package javadev.core.fangSql;

import javadev.core.fangSql.helper.FangSqlHelper;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.RowMapperResultSetExtractor;

import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * 封装后的jdbc模板类
 *
 * @author fangzhiyang
 */
public class FangJdbcTemplate extends JdbcTemplate {

    public FangSqlHelper helper = new FangSqlHelper(this);

    public String getTableInfoSql(String table) throws Exception {
        return "select * from " + table + " limit 1";
    }

    @Override
    public <T> T queryForObject(String sql, RowMapper<T> rowMapper, Object... args) throws DataAccessException {
        List<T> results = query(sql, args, new RowMapperResultSetExtractor<T>(rowMapper, 1));
        return requiredSingleResult(results);
    }

    // 重写JdbcTemplate，当queryForObject查询结果为0时，返回null而不是抛出异常
    @Override
    public Map<String, Object> queryForMap(String sql, Object... args) throws DataAccessException {
        try {
            return super.queryForMap(sql, args);
        } catch (Exception e) {
            return null;
        }
    }

    private <T> T requiredSingleResult(Collection<T> results) throws IncorrectResultSizeDataAccessException {
        int size = (results != null ? results.size() : 0);
        if (size == 0) {
            return null;
        }
        if (results.size() > 1) {
            throw new IncorrectResultSizeDataAccessException(1, size);
        }
        return results.iterator().next();
    }
}
