package javadev.core.fangSql.util;

public class Utils {
    /**
     * ��SQL���ת����Java��� hot_document_id --> hotDocumentId
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
}
