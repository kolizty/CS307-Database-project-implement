package myService;
import cn.edu.sustech.cs307.database.SQLDataSource;
import cn.edu.sustech.cs307.dto.Semester;
import cn.edu.sustech.cs307.exception.EntityNotFoundException;
import cn.edu.sustech.cs307.exception.IntegrityViolationException;
import cn.edu.sustech.cs307.service.SemesterService;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class mySemesterService implements SemesterService {
    static Connection connection;

    static {
        try {
            connection = SQLDataSource.getInstance().getSQLConnection();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }
    static String  sql_addSemester = "insert into semester(semester_name, begin_time, end_time)values (?, ?, ?);";
    static String sql;
    static PreparedStatement preparedStatement;
    static ResultSet resultSet;

    @Override
    public int addSemester(String name, Date begin, Date end) {
        try {

            preparedStatement = connection.prepareStatement(sql_addSemester);
            preparedStatement.setString(1, name);
            preparedStatement.setDate(2, begin);
            preparedStatement.setDate(3, end);
            preparedStatement.execute();

            //返回id
            String sql1 = "select semester_id from semester where semester_name=(?);";
            preparedStatement = connection.prepareStatement(sql1);
            preparedStatement.setString(1, name);
            preparedStatement.execute();
            resultSet = preparedStatement.getResultSet();
            resultSet.next();
            return resultSet.getInt(1);
        } catch (SQLException e) {
            throw new IntegrityViolationException();
        }
    }

    @Override
    public void removeSemester(int semesterId) {
        try {

            //删除section_id对应course_section_class中的内容
            String sql_delete_class = "delete\n" +
                    "from course_section_class\n" +
                    "where section_id in (select section_id from course_section where semester_id=(?));";
            preparedStatement = connection.prepareStatement(sql_delete_class);
            preparedStatement.setInt(1, semesterId);
            preparedStatement.execute();

            //删除section_id对应student_courses中的内容
            String sql_delete_student_courses = "delete\n" +
                    "from student_courses\n" +
                    "where section_id in (select section_id from course_section where semester_id = (?));";
            preparedStatement = connection.prepareStatement(sql_delete_student_courses);
            preparedStatement.setInt(1, semesterId);
            preparedStatement.execute();

            //删除semesterId对应course_section中的内容
            String sql_delete_course_section = "delete from course_section where semester_id=(?);";
            preparedStatement = connection.prepareStatement(sql_delete_course_section);
            preparedStatement.setInt(1, semesterId);
            preparedStatement.execute();

            //删除semesterId对应semester表中的内容
            String sql_delete_semester = "delete from semester where semester_id=(?);";
            preparedStatement = connection.prepareStatement(sql_delete_semester);
            preparedStatement.setInt(1, semesterId);
            preparedStatement.execute();

        } catch (SQLException e) {
            throw new EntityNotFoundException();
        }
    }

    @Override
    public List<Semester> getAllSemesters() {
        List<Semester> list = new ArrayList<>();
        try {
            sql = "select * from semester;";
            preparedStatement = connection.prepareStatement(sql);
            preparedStatement.execute();
            resultSet = preparedStatement.getResultSet();
            while (resultSet.next()) {
                Semester semester = new Semester();
                semester.id = resultSet.getInt(1);
                semester.name = resultSet.getString(2);
                semester.begin = resultSet.getDate(3);
                semester.end = resultSet.getDate(4);
                list.add(semester);
            }
        } catch (Exception e) {
            throw new EntityNotFoundException();
        }
        return list;
    }

    @Override
    public Semester getSemester(int semesterId) {
        Semester semester = new Semester();
        try {
            connection = SQLDataSource.getInstance().getSQLConnection();
            String sql= "select * from semester where semester_id=(?);";
            preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setInt(1, semesterId);
            preparedStatement.execute();
            resultSet = preparedStatement.getResultSet();
            resultSet.next();
            semester.id = resultSet.getInt(1);
            semester.name = resultSet.getString(2);
            semester.begin = resultSet.getDate(3);
            semester.end = resultSet.getDate(4);
        } catch (Exception e) {
            throw new EntityNotFoundException();
        }
        return semester;

    }
}