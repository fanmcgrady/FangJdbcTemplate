# 封装说明
对Spring中jdbcTemplate进行封装，方便对Bean实体的insert和update操作  
1. org.springframework.jdbc.core.JdbcTemplate本身对Bean的插入和更新不方便，进行了重写
2. 重写JdbcTemplate，当queryForObject查询结果为0时，返回null而不是抛出异常

# xml配置
只需要在配置文件中注入如下配置即可

```xml
    <bean id="jdbcTemplate" class="javadev.core.fangSql.FangJdbcTemplate">
        <property name="dataSource" ref="dataSource"/>
    </bean>
    <!--<bean id="jdbcTemplate" class="org.springframework.jdbc.core.JdbcTemplate">-->
    <!--<property name="dataSource" ref="dataSource"/>-->
    <!--</bean>-->
```
# java调用
java service中使用如下方式调用，简化操作

```java
    // 插入一条数据
    public void insert(HuaxiData huaxiData) {
//        String sql = "INSERT INTO t_huaxi_data (" +
//                "dabh, hbsag_qualitative, hbsab_qualitative, hbsag, hbsab, hbeag, hbeab, hbcab, hbvdna) " +
//                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
//        jt.update(sql, new PreparedStatementSetter() {
//            public void setValues(PreparedStatement ps) throws SQLException {
//                ps.setString(1, huaxiData.getDabh());
//                ps.setString(2, huaxiData.getHbsagQualitative());
//                ps.setString(3, huaxiData.getHbsabQualitative());
//                ps.setString(4, huaxiData.getHbsag());
//                ps.setString(5, huaxiData.getHbsab());
//                ps.setString(6, huaxiData.getHbeag());
//                ps.setString(7, huaxiData.getHbeab());
//                ps.setString(8, huaxiData.getHbcab());
//                ps.setString(9, huaxiData.getHbvdna());
//            }
//        });

        jt.helper.insert(huaxiData, "t_huaxi_data", null, "id");
    }

    // 更新一条数据
    public void update(HuaxiData huaxiData) {
//        String sql = "UPDATE t_huaxi_data SET " +
//                "  hbsag_qualitative = ?," +
//                "  hbsab_qualitative = ?," +
//                "  hbsag = ?," +
//                "  hbsab = ?," +
//                "  Hbeag = ?," +
//                "  Hbeab = ?," +
//                "  Hbcab = ?," +
//                "  hbvdna = ? " +
//                "WHERE dabh = ?";
//
//        jt.update(sql, new PreparedStatementSetter() {
//            public void setValues(PreparedStatement ps) throws SQLException {
//                ps.setString(1, huaxiData.getHbsagQualitative());
//                ps.setString(2, huaxiData.getHbsabQualitative());
//                ps.setString(3, huaxiData.getHbsag());
//                ps.setString(4, huaxiData.getHbsab());
//                ps.setString(5, huaxiData.getHbeag());
//                ps.setString(6, huaxiData.getHbeab());
//                ps.setString(7, huaxiData.getHbcab());
//                ps.setString(8, huaxiData.getHbvdna());
//                ps.setString(9, huaxiData.getDabh());
//            }
//        });

        jt.helper.update(huaxiData, "t_huaxi_data", null, "id, dabh", "dabh = ?", huaxiData.getDabh());
    }
```
# 参数说明
具体参数查看源码，其中include，exclude介绍如下

```java
    /**
     * 插入
     * include: 只插入哪些字段
     * exclude: 不插入哪些字段
     */
    public int insert(Object bean, String table, String include, String exclude) {...}
    
    /**
     * 更新
     * include: 只更新哪些字段
     * exclude: 不更新哪些字段
     */
    public int update(Object bean, String table, String include, String exclude, String where, Object... parameters) {...}
```
