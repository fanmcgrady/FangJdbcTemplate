package javadev.core.fangSql.helper;

import java.beans.BeanInfo;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

/**
 * 获取Bean实体的成员变量
 *
 * @author fangzhiyang
 */
class ClassInfo {
    private Map<String, Method> getters;

    public static ClassInfo create(Class<?> type) throws Exception {
        Map<String, PropertyDescriptor> properties = new HashMap<String, PropertyDescriptor>();

        BeanInfo bi = Introspector.getBeanInfo(type);
        PropertyDescriptor[] pds = bi.getPropertyDescriptors();
        for (int i = 0; i < pds.length; i++) {
            PropertyDescriptor pd = pds[i];
            properties.put(pd.getName(), pd);
        }

        Map<String, Method> getters = new HashMap<String, Method>();
        for (String p : properties.keySet()) {
            PropertyDescriptor pd = properties.get(p);
            Method getter = pd.getReadMethod();
            getters.put(p, getter);
        }

        ClassInfo ci = new ClassInfo();
        ci.setGetters(getters);

        return ci;
    }


    public Map<String, Method> getGetters() {
        return getters;
    }

    public void setGetters(Map<String, Method> getters) {
        this.getters = getters;
    }
}
