package javadev.core.fangSql.util;

import java.util.HashMap;
import java.util.Map;

public class BeanUtils {
    /**
     * SQL到Java转换 hot_document_id --> hotDocumentId
     */
    public static String sqlToJava(String field) {
        String[] a = field.split("_");
        StringBuffer sb = new StringBuffer(a[0]);
        for (int i = 1; i < a.length; i++) {
            String b = a[i];
            if (b.length() == 0) continue;
            sb.append(Character.toUpperCase(b.charAt(0)));
            sb.append(b.substring(1));
        }

        return sb.toString();
    }

    /**
     * Map到Bean的转换
     * 使用org.apache.commons.beanutils进行转换
     */
    public static <T> T mapToBean(Map<String, Object> map, Class<T> beanClass) {
        if (map == null)
            return null;

        try {
            T obj = beanClass.newInstance();

            // 对map遍历一遍，如果存在下划线的key，自动添加一个驼峰式命名的key->value
            // user_name -> "fang"  转换为 userName -> "fang"
            Map<String, Object> newMap = new HashMap<>();
            for (String key : map.keySet()) {
                if (key.contains("_")) {
                    newMap.put(sqlToJava(key), map.get(key));
                }
            }

            if (!newMap.isEmpty()) {
                map.putAll(newMap);
            }

            org.apache.commons.beanutils.BeanUtils.populate(obj, map);

            return obj;
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Bean到map的转换
     * 使用org.apache.commons.beanutils进行转换
     */
    public static Map<?, ?> objectToMap(Object obj) {
        if (obj == null)
            return null;

        return new org.apache.commons.beanutils.BeanMap(obj);
    }

    public static void main(String[] args) throws Exception {
//        Map<String, Object> map = new HashMap<>();
//        map.put("id", "11");
//        map.put("user_name", "fang");
//        map.put("userName", "123");

//        User user = mapToBean(map, User.class);
    }

}
