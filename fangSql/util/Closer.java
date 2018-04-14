package javadev.core.fangSql.util;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

/**
 * ������
 *
 * @author zuojie
 */
public class Closer {
    public static void close(ResultSet r) {
        if (r != null) try {
            r.close();
        } catch (Exception e) {
        }
    }

    public static void close(Statement s) {
        if (s != null) try {
            s.close();
        } catch (Exception e) {
        }
    }

    public static void close(Connection c) {
        if (c != null) try {
            c.close();
        } catch (Exception e) {
        }
    }
}
