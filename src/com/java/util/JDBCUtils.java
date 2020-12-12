package com.java.util;


import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Properties;

/**
 * 自己封装的JDBC工具类
 * 前提：列名和字段名一致；类名和表名一致（表名student 类名Student）
 */
public class JDBCUtils {

    private static String DRIVER;
    private static String URL;
    private static String USERNAME;
    private static String PASSWORD;

    static {
        try {
            Properties properties = new Properties();
            InputStream inputStream = JDBCUtils.class.getResourceAsStream("/db.properties");
            properties.load(inputStream);
            URL = properties.getProperty("URL");
            USERNAME = properties.getProperty("USERNAME");
            PASSWORD = properties.getProperty("PASSWORD");
            DRIVER = properties.getProperty("DRIVER");
            Class.forName(DRIVER);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取连接
     *
     * @return 连接对象
     */
    public static Connection getConnection() {
        Connection conn = null;
        try {
            conn = DriverManager.getConnection(URL, USERNAME, PASSWORD);
        } catch (Exception e) {
            System.out.println("数据库连接失败");
        }
        return conn;
    }

    /**
     * 释放资源
     *
     * @param obj
     */
    public static void close(Object obj) {
        try {
            if (obj instanceof ResultSet) {
                if (obj != null) {
                    ((ResultSet) obj).close();
                }
            } else if (obj instanceof Statement) {
                if (obj != null) {
                    ((Statement) obj).close();
                }
            } else if (obj instanceof Connection) {
                if (obj != null) {
                    ((Connection) obj).close();
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * 增加、删除、修改
     *
     * @param sql
     * @param params
     */
    public static void modify(String sql, Object[] params) {
        try {
            Connection connection = getConnection();
            PreparedStatement ptmt = connection.prepareStatement(sql);
            // 获取几个问号
            int paramsCount = ptmt.getParameterMetaData().getParameterCount();
            if (paramsCount != params.length) {
                throw new RuntimeException("传递的参数数量不匹配！");
            }
            for (int i = 0; i < params.length; i++) {
                ptmt.setObject(i + 1, params[i]);
            }
            ptmt.execute();
            close(ptmt);
            close(connection);
            System.out.println("操作成功");
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    /**
     * 查询
     *
     * @param sql
     * @param params
     * @param clazz
     * @param <T>
     * @return
     */
    public static <T> List<T> selectList(String sql, Object[] params, Class<T> clazz) {
        List<T> list = new ArrayList<>();
        T t = null;
        Field[] fields = null;
        try {
            fields = clazz.getDeclaredFields();
            Connection conn = getConnection();
            PreparedStatement ptmt = conn.prepareStatement(sql);
            // 获取几个问号
            int paramsCount = ptmt.getParameterMetaData().getParameterCount();
            if (paramsCount != params.length) {
                throw new RuntimeException("传递的参数数量不匹配！");
            }
            for (int i = 0; i < params.length; i++) {
                ptmt.setObject(i + 1, params[i]);
            }
            //执行
            ResultSet rs = ptmt.executeQuery();

            while (rs.next()) {
                // 获取查询列的信息，一共有几行
                ResultSetMetaData metaData = rs.getMetaData();
                int rows = metaData.getColumnCount();
                t = clazz.newInstance();
                String column = null;
                Method method = null;
                for (int i = 0; i < rows; i++) {
                    // 获取列名
                    column = metaData.getColumnName(i + 1);
                    for (Field field :
                            fields) {
                        // 如果列名和属性名相等
                        if (column.equals(field.getName())) {
                            // 执行该属性的setter方法 给属性赋值
                            method = clazz.getMethod("set" + CommonUtils.upperCaseFirst(field.getName()), field.getType());
                            method.invoke(t, rs.getObject(i + 1));
                        }
                    }
                }
                list.add(t);
            }
            close(rs);
            close(ptmt);
            close(conn);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    /**
     * 通用单表插入方法
     * @param t
     * @param <T>
     * @return
     */
    public static <T> void add(T t) {
        Class clazz = t.getClass();
        Field[] fields = clazz.getDeclaredFields();
        String tablename = CommonUtils.lowerCaseFirst(clazz.getName().substring(clazz.getName().lastIndexOf(".") + 1));
        StringBuilder sql = new StringBuilder(String.format("insert into %s ", tablename));
        sql.append("(");
        int i = 0;
        for (; i < fields.length - 1; i++) {
            sql.append(fields[i].getName());

            sql.append(",");
        }
        sql.append(fields[i].getName());
        sql.append(") values (");
        i = 0;

        Method method = null;
        String methodName = null;
        Object resultValue = null;
        try {
            for (; i < fields.length - 1; i++) {
                methodName = "get" + CommonUtils.upperCaseFirst(fields[i].getName());
                method = clazz.getMethod(methodName, new Class[]{});
                method.setAccessible(true);
                resultValue = method.invoke(t, new Object[]{});
                if(resultValue instanceof Date){
                    String date = CommonUtils.format((Date) resultValue,null);
                    sql.append(String.format("'%s'",date));
                    sql.append(",");
                }else if(resultValue instanceof String){
                    sql.append(String.format("'%s'",resultValue));
                    sql.append(",");
                }else{
                    sql.append(resultValue);
                    sql.append(",");
                }

            }

            methodName = "get" + CommonUtils.upperCaseFirst(fields[i].getName());
            method = clazz.getMethod(methodName, new Class[]{});
            method.setAccessible(true);
            resultValue = method.invoke(t, new Object[]{});
            if(resultValue instanceof Date){
                String date = CommonUtils.format((Date) resultValue,null);
                sql.append(String.format("'%s'",date));
            }else if(resultValue instanceof String){
                sql.append(String.format("'%s'",resultValue));
            }else{
                sql.append(resultValue);
            }
            sql.append(")");
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
        System.out.println(sql.toString());
        modify(sql.toString(),new Object[]{});
    }

    /**
     * 通用单表修改方法
     * @param t
     * @param idPropertyName 主键属性名称
     * @param <T>
     */
    public static <T> void update(T t,String idPropertyName) {
        Class clazz = t.getClass();
        Field[] fields = clazz.getDeclaredFields();
        String tablename = CommonUtils.lowerCaseFirst(clazz.getName().substring(clazz.getName().lastIndexOf(".") + 1));
        StringBuilder sql = new StringBuilder(String.format("update %s set ", tablename));
        int i = 0;
        Method method = null;
        String methodName = null;
        Object resultValue = null;
        try {
            for (; i < fields.length - 1; i++) {
                methodName = "get" + CommonUtils.upperCaseFirst(fields[i].getName());
                method = clazz.getMethod(methodName, new Class[]{});
                method.setAccessible(true);
                resultValue = method.invoke(t, new Object[]{});
                if(resultValue instanceof Date){
                    String date = CommonUtils.format((Date) resultValue,null);
                    sql.append(String.format("%s = '%s'",fields[i].getName(),date));
                    sql.append(",");
                }else if(resultValue instanceof String){
                    sql.append(String.format("%s = '%s'",fields[i].getName(),resultValue));
                    sql.append(",");
                }else{
                    sql.append(String.format("%s = %s",fields[i].getName(),resultValue));
                    sql.append(",");
                }

            }

            methodName = "get" + CommonUtils.upperCaseFirst(fields[i].getName());
            method = clazz.getMethod(methodName, new Class[]{});
            method.setAccessible(true);
            resultValue = method.invoke(t, new Object[]{});
            if(resultValue instanceof Date){
                String date = CommonUtils.format((Date) resultValue,null);
                sql.append(String.format("%s = '%s'",fields[i].getName(),date));
            }else if(resultValue instanceof String){
                sql.append(String.format("%s = '%s'",fields[i].getName(),resultValue));
            }else{
                sql.append(String.format("%s = %s",fields[i].getName(),resultValue));
            }


            methodName = "get" + CommonUtils.upperCaseFirst(idPropertyName);
            method = clazz.getMethod(methodName, new Class[]{});
            method.setAccessible(true);
            resultValue = method.invoke(t, new Object[]{});
            sql.append(String.format(" where %s = %s",idPropertyName,resultValue));
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
        System.out.println(sql.toString());
        modify(sql.toString(),new Object[]{});
    }

    /**
     * 通用删除方法
     * @param t
     * @param idPropertyName id的属性名
     * @param <T>
     */
    public static <T> void delete(T t, String idPropertyName) {
        Class clazz = t.getClass();
        Field[] fields = clazz.getDeclaredFields();
        String tablename = CommonUtils.lowerCaseFirst(clazz.getName().substring(clazz.getName().lastIndexOf(".") + 1));
        StringBuilder sql = new StringBuilder(String.format("delete from %s where %s =  ", tablename,idPropertyName));
        int i = 0;
        Method method = null;
        String methodName = null;
        Object resultValue = null;
        try {
            methodName = "get" + CommonUtils.upperCaseFirst(idPropertyName);
            method = clazz.getMethod(methodName, new Class[]{});
            method.setAccessible(true);
            resultValue = method.invoke(t, new Object[]{});
            if(resultValue instanceof String){
                sql.append(String.format("'%s'",resultValue));
            }else{
                sql.append(resultValue);
            }
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
        System.out.println(sql.toString());
        modify(sql.toString(),new Object[]{});
    }

    /**
     * 单表通用查询，字符串是模糊查询
     * @param t
     * @param <T>
     * @return
     */
    public static <T> List<T> findAll(T t) {
        Class clazz = t.getClass();
        Field[] fields = clazz.getDeclaredFields();
        String tablename = CommonUtils.lowerCaseFirst(clazz.getName().substring(clazz.getName().lastIndexOf(".") + 1));
        StringBuilder sql = new StringBuilder(String.format("select * from %s where 1 = 1 ", tablename));
        int i = 0;
        List<Object> params = new ArrayList<>();

        Method method = null;
        String methodName = null;
        Object resultValue = null;
        try {
            for (; i < fields.length; i++) {
                methodName = "get" + CommonUtils.upperCaseFirst(fields[i].getName());
                method = clazz.getMethod(methodName, new Class[]{});
                method.setAccessible(true);
                resultValue = method.invoke(t, new Object[]{});
                if(resultValue != null){
                    if(resultValue instanceof Date){
                        String date = CommonUtils.format((Date) resultValue,null);
                        params.add(date);
                        sql.append(String.format("and %s = ? ",fields[i].getName()));
                    }else if(resultValue instanceof String){
                        sql.append(String.format("and %s like ? ",fields[i].getName(),"%","%"));
                        params.add(String.format("%s%s%s","%",resultValue,"%"));
                    }else{
                        sql.append(String.format("and %s = ? ",fields[i].getName()));
                        params.add(resultValue);
                    }
                }
            }
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
        System.out.println(sql.toString());
        Object[] paramsObject = params.toArray();

        return selectList(sql.toString(),paramsObject,clazz);
    }

}
