package myimplement.service;

import cn.edu.sustech.cs307.database.SQLDataSource;
import cn.edu.sustech.cs307.dto.Department;
import cn.edu.sustech.cs307.dto.Major;
import cn.edu.sustech.cs307.exception.EntityNotFoundException;
import cn.edu.sustech.cs307.exception.IntegrityViolationException;
import cn.edu.sustech.cs307.service.MajorService;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class myMajorService implements MajorService {

    static String sql;

    @Override
    public int addMajor(String name, int departmentId) {
        int id = 0;
        try {
            Connection connection = SQLDataSource.getInstance().getSQLConnection();
            sql = "insert into majors(major_id, dept_id, major_name)\n" +
                    "values (default, ?, ?)\n" +
                    "on conflict do nothing\n" +
                    "returning major_id;";
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setInt(1, departmentId);
            preparedStatement.setString(2, name);
            preparedStatement.execute();
            ResultSet resultSet = preparedStatement.getResultSet();
            resultSet.next();
            id = resultSet.getInt(1);
            resultSet.close();
            preparedStatement.close();
            connection.close();
//            sql = "insert into majors(major_name,dept_id)values (?,?);";
//            preparedStatement = connection.prepareStatement(sql);
//            preparedStatement.setString(1, name);
//            preparedStatement.setInt(2, departmentId);
//            preparedStatement.execute();
//
//            //返回id
//            String sql1 = "select major_id from majors where major_name=(?);";
//            preparedStatement = connection.prepareStatement(sql1);
//            preparedStatement.setString(1, name);
//            preparedStatement.execute();
//            resultSet = preparedStatement.getResultSet();
//            resultSet.next();
//            return resultSet.getInt(1);
        } catch (SQLException e) {
//            throw new IntegrityViolationException();
        }
        return id;
    }

    @Override
    public void removeMajor(int majorId) {
        try {
            Connection connection = SQLDataSource.getInstance().getSQLConnection();
            String sql_delete_majors = "delete from majors where major_id=(?);";
            PreparedStatement preparedStatement = connection.prepareStatement(sql_delete_majors);
            preparedStatement.setInt(1, majorId);
            preparedStatement.execute();

            preparedStatement.close();
            connection.close();
        } catch (SQLException e) {

        }
//        try {
//            Connection connection = SQLDataSource.getInstance().getSQLConnection();
//
//            //删除majorId对应student_courses中的内容
//            String sql_delete_student_courses = "delete\n" +
//                    "from student_courses\n" +
//                    "where sid in (select sid from students where major_id = (?));";
//            PreparedStatement preparedStatement = connection.prepareStatement(sql_delete_student_courses);
//            preparedStatement.setInt(1, majorId);
//            preparedStatement.execute();
//
//            //删除majorId对应students中的内容
//            String sql_delete_students = "delete from students where major_id=(?);";
//            preparedStatement = connection.prepareStatement(sql_delete_students);
//            preparedStatement.setInt(1, majorId);
//            preparedStatement.execute();
//
//            //删除majorId对应major_compulsory中的内容
//            String sql_delete_major_compulsory = "delete from major_compulsory where major_id=(?);";
//            preparedStatement = connection.prepareStatement(sql_delete_major_compulsory);
//            preparedStatement.setInt(1, majorId);
//            preparedStatement.execute();
//
//            //删除majorId对应major_elective表中的内容
//            String sql_delete_major_elective = "delete from major_elective where major_id=(?);";
//            preparedStatement = connection.prepareStatement(sql_delete_major_elective);
//            preparedStatement.setInt(1, majorId);
//            preparedStatement.execute();
//
//            //删除majorId对应majors表中的内容
//            String sql_delete_majors = "delete from majors where major_id=(?);";
//            preparedStatement = connection.prepareStatement(sql_delete_majors);
//            preparedStatement.setInt(1, majorId);
//            preparedStatement.execute();
//
//            preparedStatement.close();
//            connection.close();
//        } catch (SQLException e) {
////            throw new EntityNotFoundException();
//        }
    }

    @Override
    public List<Major> getAllMajors() {
        List<Major> list = new ArrayList<>();
        try {
            Connection connection = SQLDataSource.getInstance().getSQLConnection();
            sql = "select major_id,major_name,d.dept_id,dept_name\n" +
                    "from majors join department d on d.dept_id = majors.dept_id;";
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.execute();
            ResultSet resultSet = preparedStatement.getResultSet();
            while (resultSet.next()) {
                Major major = new Major();
                major.id = resultSet.getInt(1);
                major.name = resultSet.getString(2);
                major.department.id = resultSet.getInt(3);
                major.department.name = resultSet.getString(4);
                list.add(major);
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
    public Major getMajor(int majorId) {
        Major major = new Major();
        Department department = new Department();
        try {
            Connection connection = SQLDataSource.getInstance().getSQLConnection();
            sql = "select major_id,major_name,d.dept_id,dept_name\n" +
                    "from majors join department d on d.dept_id = majors.dept_id where major_id=(?);";
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setInt(1, majorId);
            preparedStatement.execute();
            ResultSet resultSet = preparedStatement.getResultSet();
            if (resultSet.next()) {
                major.id = resultSet.getInt(1);
                major.name = resultSet.getString(2);
                department.id = resultSet.getInt(3);
                department.name = resultSet.getString(4);
                major.department = department;
                resultSet.close();
                preparedStatement.close();
                connection.close();
            } else {
                resultSet.close();
                preparedStatement.close();
                connection.close();
//              throw new EntityNotFoundException();
            }
        } catch (Exception e) {
//            throw new EntityNotFoundException();
        }
        return major;
    }

    @Override
    public void addMajorCompulsoryCourse(int majorId, String courseId) {
        try {
            Connection connection = SQLDataSource.getInstance().getSQLConnection();
            sql = "insert into major_compulsory(major_id, course_id)values (?,?);";
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setInt(1, majorId);
            preparedStatement.setString(2, courseId);
            preparedStatement.execute();
            preparedStatement.close();
            connection.close();
        } catch (Exception e) {
//            throw new IntegrityViolationException();
        }
    }

    @Override
    public void addMajorElectiveCourse(int majorId, String courseId) {
        try {
            Connection connection = SQLDataSource.getInstance().getSQLConnection();
            sql = "insert into major_elective(major_id, course_id)values (?,?);";
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setInt(1, majorId);
            preparedStatement.setString(2, courseId);
            preparedStatement.execute();
            preparedStatement.close();
            connection.close();
        } catch (Exception e) {
//            throw new IntegrityViolationException();
        }
    }
}
