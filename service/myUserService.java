package myimplement.service;

import cn.edu.sustech.cs307.database.SQLDataSource;
import cn.edu.sustech.cs307.dto.User;
import cn.edu.sustech.cs307.exception.EntityNotFoundException;
import cn.edu.sustech.cs307.service.UserService;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class myUserService implements UserService {
    static Connection connection;
    static String sql1_delete_instructor = "delete from instructors where instructor_id=(?);";
    static String sql2_delete_student = "delete from students where sid=(?);";
    static String sql3_select_all_student = "select sid,first_name,last_name from students;";
    static String sql4_select_all_instructor = "select * from instructors;";
    static String sql5_select_student = "select sid,first_name,last_name from students where sid=(?);";
    static String sql6_select_instructor = "select * from instructors where instructor_id=(?);";

    static {
        try {
            connection = SQLDataSource.getInstance().getSQLConnection();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    static PreparedStatement preparedStatement1;
    static PreparedStatement preparedStatement2;
    static PreparedStatement preparedStatement3;
    static PreparedStatement preparedStatement4;
    static PreparedStatement preparedStatement5;
    static PreparedStatement preparedStatement6;

    static {
        try {
            preparedStatement1 = connection.prepareStatement(sql1_delete_instructor);
            preparedStatement2 = connection.prepareStatement(sql2_delete_student);
            preparedStatement3 = connection.prepareStatement(sql3_select_all_student);
            preparedStatement4 = connection.prepareStatement(sql4_select_all_instructor);
            preparedStatement5 = connection.prepareStatement(sql5_select_student);
            preparedStatement6 = connection.prepareStatement(sql6_select_instructor);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }


    static ResultSet resultSet;

    @Override
    public void removeUser(int userId) {
        try {

            preparedStatement1.setInt(1, userId);
            preparedStatement1.execute();
            //      resultSet=preparedStatement1.getResultSet();

        } catch (SQLException e) {
            throw new EntityNotFoundException();
        }
    }

    @Override
    public List<User> getAllUsers() {
        List<User> list = new ArrayList<>();
        try {

            preparedStatement3.execute();
            resultSet = preparedStatement3.getResultSet();
            while (resultSet.next()) {
                User user = new User() {
                    @Override
                    public boolean equals(Object o) {
                        return super.equals(o);
                    }

                    @Override
                    public int hashCode() {
                        return super.hashCode();
                    }
                };
                user.id = resultSet.getInt(1);
                //判断是英文名还是中文名
                String firstName = resultSet.getString(2).trim();
                if (firstName.charAt(0) >= 'A' && firstName.charAt(0) <= 'z') {
                    //英文名
                    user.fullName = resultSet.getString(2) + " " + resultSet.getString(3);
                } else {
                    //中文名
                    user.fullName = resultSet.getString(2) + resultSet.getString(3);
                }
                list.add(user);
            }
            preparedStatement4.execute();
            resultSet = preparedStatement4.getResultSet();
            while (resultSet.next()) {
                User user = new User() {
                    @Override
                    public boolean equals(Object o) {
                        return super.equals(o);
                    }

                    @Override
                    public int hashCode() {
                        return super.hashCode();
                    }
                };
                user.id = resultSet.getInt(1);
                //判断是英文名还是中文名
                String firstName = resultSet.getString(2).trim();
                if (firstName.charAt(0) >= 'A' && firstName.charAt(0) <= 'z') {
                    //英文名
                    user.fullName = resultSet.getString(2) + " " + resultSet.getString(3);
                } else {
                    //中文名
                    user.fullName = resultSet.getString(2) + resultSet.getString(3);
                }
                list.add(user);
            }
        } catch (Exception e) {
            throw new EntityNotFoundException();
        }
        return list;
    }

    @Override
    public User getUser(int userId) {
        User user = new User() {
            @Override
            public boolean equals(Object o) {
                return super.equals(o);
            }

            @Override
            public int hashCode() {
                return super.hashCode();
            }
        };
        try {
            String sql;
            if (userId >= 30000000) {
                sql = "select * from instructors where instructor_id=(?);";
            } else {
                sql = "select sid,first_name,last_name from students where sid=(?);";
            }
            preparedStatement5 = connection.prepareStatement(sql);
            preparedStatement5.setInt(1, userId);
            preparedStatement5.execute();
            resultSet = preparedStatement5.getResultSet();
            resultSet.next();
            user.id = resultSet.getInt(1);
            //判断中英文名
            String firstName = resultSet.getString(2).trim();
            if (firstName.charAt(0) >= 'A' && firstName.charAt(0) <= 'z') {
                //英文名
                user.fullName = resultSet.getString(2) + " " + resultSet.getString(3);
            } else {
                //中文名
                user.fullName = resultSet.getString(2) + resultSet.getString(3);
            }
        } catch (SQLException e) {
            throw new EntityNotFoundException();
        }
        return user;
    }
}
