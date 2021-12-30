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

//    static String sql1_delete_instructor = "delete from instructors where instructor_id=(?);";
//    static String sql2_delete_student = "delete from students where sid=(?);";
//    static String sql3_select_all_student = "select sid,first_name,last_name from students;";
//    static String sql4_select_all_instructor = "select * from instructors;";
//    static String sql5_select_student = "select sid,first_name,last_name from students where sid=(?);";
//    static String sql6_select_instructor = "select * from instructors where instructor_id=(?);";

    static String sql;
    static PreparedStatement preparedStatement;
    static ResultSet resultSet;

    @Override
    public void removeUser(int userId) {
        try {
            Connection connection = SQLDataSource.getInstance().getSQLConnection();
            //判断是老师还是学生
            sql = "delete from course_section_class where instructor_id=(?) returning class_id;";
            preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setInt(1, userId);
            preparedStatement.execute();
            resultSet = preparedStatement.getResultSet();
            if (resultSet.next()) {//如果有返回，代表userId是老师,只要再把老师删了;
                sql = "delete from instructors where instructor_id=(?);";
                preparedStatement = connection.prepareStatement(sql);
                preparedStatement.setInt(1, userId);
                preparedStatement.execute();
                connection.close();
            } else {//如果没有返回,代表不是老师，是学生,则删除student_courses和students表中相关内容
                //删除student_courses表中相关内容
                sql = "delete from student_courses where sid=(?);";
                preparedStatement = connection.prepareStatement(sql);
                preparedStatement.setInt(1, userId);
                preparedStatement.execute();
                //删除students表中相关内容
                sql = "delete from students where sid=(?);";
                preparedStatement = connection.prepareStatement(sql);
                preparedStatement.setInt(1, userId);
                preparedStatement.execute();
                connection.close();
            }
//            preparedStatement1 = connection.prepareStatement(sql1_delete_instructor);
//            preparedStatement1.setInt(1, userId);
//            preparedStatement1.execute();
//            resultSet=preparedStatement1.getResultSet();
//            preparedStatement2 = connection.prepareStatement(sql2_delete_student);
//            preparedStatement2.setInt(1, userId);
//            preparedStatement2.execute();
//            connection.close();
        } catch (SQLException e) {
            throw new EntityNotFoundException();
        }
    }

    @Override
    public List<User> getAllUsers() {
        List<User> list = new ArrayList<>();
        try {
            Connection connection = SQLDataSource.getInstance().getSQLConnection();
            //加学生
            sql = "select sid,first_name,last_name from students;";
            preparedStatement = connection.prepareStatement(sql);
            preparedStatement.execute();
            resultSet = preparedStatement.getResultSet();
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
            //加老师
            sql = "select * from instructors;";
            preparedStatement = connection.prepareStatement(sql);
            preparedStatement.execute();
            resultSet = preparedStatement.getResultSet();
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
            connection.close();
            return list;
        } catch (Exception e) {
            throw new EntityNotFoundException();
        }
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
            Connection connection = SQLDataSource.getInstance().getSQLConnection();
            sql = "select * from instructors where instructor_id=(?);";
            preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setInt(1, userId);
            preparedStatement.execute();
            resultSet = preparedStatement.getResultSet();
            if (resultSet.next()) {
                user.id = resultSet.getInt(1);
                //判断中英文名
                String firstName = resultSet.getString(2).trim();
                String lastName = resultSet.getString(3).trim();
                if ((firstName.charAt(0) >= 'A' && firstName.charAt(0) <= 'z')&&
                        (lastName.charAt(0) >= 'A' && lastName.charAt(0) <= 'z')) {
                    //英文名
                    user.fullName = resultSet.getString(2) + " " + resultSet.getString(3);
                } else {
                    //中文名
                    user.fullName = resultSet.getString(2) + resultSet.getString(3);
                }
            } else {
                sql = "select sid,first_name,last_name from students where sid=(?);";
                preparedStatement = connection.prepareStatement(sql);
                preparedStatement.setInt(1, userId);
                preparedStatement.execute();
                resultSet = preparedStatement.getResultSet();
                resultSet.next();
                user.id = resultSet.getInt(1);
                //判断中英文名
                String firstName = resultSet.getString(2).trim();
                String lastName = resultSet.getString(3).trim();
                if ((firstName.charAt(0) >= 'A' && firstName.charAt(0) <= 'z') &&
                        (lastName.charAt(0) >= 'A' && lastName.charAt(0) <= 'z')) {
                    //英文名
                    user.fullName = resultSet.getString(2) + " " + resultSet.getString(3);
                } else {
                    //中文名
                    user.fullName = resultSet.getString(2) + resultSet.getString(3);
                }
            }
            connection.close();
            return user;
        } catch (SQLException e) {
            throw new EntityNotFoundException();
        }
    }
}
