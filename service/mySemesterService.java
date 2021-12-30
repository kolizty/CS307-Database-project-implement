package myimplement.service;

import cn.edu.sustech.cs307.database.SQLDataSource;
import cn.edu.sustech.cs307.dto.Semester;
import cn.edu.sustech.cs307.exception.EntityNotFoundException;
import cn.edu.sustech.cs307.exception.IntegrityViolationException;
import cn.edu.sustech.cs307.service.SemesterService;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class mySemesterService implements SemesterService {

    static String sql;
//    static PreparedStatement preparedStatement;
//    static ResultSet resultSet;

    @Override
    public int addSemester(String name, Date begin, Date end) {
        int id = 0;
        try {
            Connection connection = SQLDataSource.getInstance().getSQLConnection();
            sql = "insert into semester(semester_id, semester_name, begin_time, end_time)\n" +
                    "values (default, ?, ?, ?)\n" +
                    "on conflict do nothing\n";
            PreparedStatement preparedStatement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            preparedStatement.setString(1, name);
            preparedStatement.setDate(2, begin);
            preparedStatement.setDate(3, end);
            preparedStatement.execute();
            ResultSet resultSet = preparedStatement.getGeneratedKeys();
            if (resultSet.next())
                id = resultSet.getInt(1);
            resultSet.close();
            preparedStatement.close();
            connection.close();
//            preparedStatement = connection.prepareStatement(sql_addSemester);
//            preparedStatement.setString(1, name);
//            preparedStatement.setDate(2, begin);
//            preparedStatement.setDate(3, end);
//            preparedStatement.execute();
//
//            //返回id
//            String sql1 = "select semester_id from semester where semester_name=(?);";
//            preparedStatement = connection.prepareStatement(sql1);
//            preparedStatement.setString(1, name);
//            preparedStatement.execute();
//            resultSet = preparedStatement.getResultSet();
//            resultSet.next();
//            return resultSet.getInt(1);
        } catch (SQLException e) {
            //           throw new IntegrityViolationException();
        }
        return id;
    }

    @Override
    public void removeSemester(int semesterId) {
        try {
            Connection connection = SQLDataSource.getInstance().getSQLConnection();
            String sql_delete_semester = "delete from semester where semester_id=(?);";
            PreparedStatement preparedStatement = connection.prepareStatement(sql_delete_semester);
            preparedStatement.setInt(1, semesterId);
            preparedStatement.execute();
            preparedStatement.close();
            connection.close();
        } catch (Exception e) {

        }
//        try {
//            Connection connection = SQLDataSource.getInstance().getSQLConnection();
//            //删除section_id对应course_section_class中的内容
//            String sql_delete_class = "delete\n" +
//                    "from course_section_class\n" +
//                    "where section_id in (select section_id from course_section where semester_id=(?));";
//            PreparedStatement preparedStatement = connection.prepareStatement(sql_delete_class);
//            preparedStatement.setInt(1, semesterId);
//            preparedStatement.execute();
//
//            //删除section_id对应student_courses中的内容
//            String sql_delete_student_courses = "delete\n" +
//                    "from student_courses\n" +
//                    "where section_id in (select section_id from course_section where semester_id = (?));";
//            preparedStatement = connection.prepareStatement(sql_delete_student_courses);
//            preparedStatement.setInt(1, semesterId);
//            preparedStatement.execute();
//
//            //删除semesterId对应course_section中的内容
//            String sql_delete_course_section = "delete from course_section where semester_id=(?);";
//            preparedStatement = connection.prepareStatement(sql_delete_course_section);
//            preparedStatement.setInt(1, semesterId);
//            preparedStatement.execute();
//
//            //删除semesterId对应semester表中的内容
//            String sql_delete_semester = "delete from semester where semester_id=(?);";
//            preparedStatement = connection.prepareStatement(sql_delete_semester);
//            preparedStatement.setInt(1, semesterId);
//            preparedStatement.execute();
//
//            preparedStatement.close();
//            connection.close();
//
//        } catch (SQLException e) {
//            throw new EntityNotFoundException();
//        }
    }

    @Override
    public List<Semester> getAllSemesters() {
        List<Semester> list = new ArrayList<>();
        try {
            Connection connection = SQLDataSource.getInstance().getSQLConnection();
            sql = "select * from semester;";
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.execute();
            ResultSet resultSet = preparedStatement.getResultSet();
            while (resultSet.next()) {
                Semester semester = new Semester();
                semester.id = resultSet.getInt(1);
                semester.name = resultSet.getString(2);
                semester.begin = resultSet.getDate(3);
                semester.end = resultSet.getDate(4);
                list.add(semester);
            }
            resultSet.close();
            preparedStatement.close();
            connection.close();
        } catch (Exception e) {
//            throw new EntityNotFoundException();
        }
        if (list.size() == 0) {
            return List.of();
        } else {
            return list;
        }
    }

    @Override
    public Semester getSemester(int semesterId) {
        Semester semester = new Semester();
        try {
            Connection connection = SQLDataSource.getInstance().getSQLConnection();
            String sql = "select * from semester where semester_id=(?);";
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setInt(1, semesterId);
            preparedStatement.execute();
            ResultSet resultSet = preparedStatement.getResultSet();
            resultSet.next();
            semester.id = resultSet.getInt(1);
            semester.name = resultSet.getString(2);
            semester.begin = resultSet.getDate(3);
            semester.end = resultSet.getDate(4);
            resultSet.close();
            preparedStatement.close();
            connection.close();
        } catch (Exception e) {
//            throw new EntityNotFoundException();
        }
        return semester;
    }
}
