package myService;

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
    static Connection connection;
    static String sql;
    static PreparedStatement preparedStatement;
    static ResultSet resultSet;

    @Override
    public int addMajor(String name, int departmentId) {
        try {
            connection = SQLDataSource.getInstance().getSQLConnection();
            sql = "insert into majors(major_name,dept_id)values (?,?);";
            preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, name);
            preparedStatement.setInt(2, departmentId);
            preparedStatement.execute();

            //返回id
            String sql1 = "select major_id from majors where major_name=(?);";
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
    public void removeMajor(int majorId) {
        try {
            connection = SQLDataSource.getInstance().getSQLConnection();
//            //找到semesterId对应section_id
//            int sid;
//            String sql_select_sid = "select sid from students where major_id=(?);";
//            preparedStatement = connection.prepareStatement(sql_select_sid);
//            preparedStatement.setInt(1, majorId);
//            preparedStatement.execute();
//            resultSet = preparedStatement.getResultSet();
//            resultSet.next();
//            sid = resultSet.getInt(1);

            //删除majorId对应student_courses中的内容
            String sql_delete_student_courses = "delete\n" +
                    "from student_courses\n" +
                    "where sid in (select sid from students where major_id = (?));";
            preparedStatement = connection.prepareStatement(sql_delete_student_courses);
            preparedStatement.setInt(1, majorId);
            preparedStatement.execute();

            //删除majorId对应students中的内容
            String sql_delete_students = "delete from students where major_id=(?);";
            preparedStatement = connection.prepareStatement(sql_delete_students);
            preparedStatement.setInt(1, majorId);
            preparedStatement.execute();

            //删除majorId对应major_compulsory中的内容
            String sql_delete_major_compulsory = "delete from major_compulsory where major_id=(?);";
            preparedStatement = connection.prepareStatement(sql_delete_major_compulsory);
            preparedStatement.setInt(1, majorId);
            preparedStatement.execute();

            //删除majorId对应major_elective表中的内容
            String sql_delete_major_elective = "delete from major_elective where major_id=(?);";
            preparedStatement = connection.prepareStatement(sql_delete_major_elective);
            preparedStatement.setInt(1, majorId);
            preparedStatement.execute();

            //删除majorId对应majors表中的内容
            String sql_delete_majors = "delete from majors where major_id=(?);";
            preparedStatement = connection.prepareStatement(sql_delete_majors);
            preparedStatement.setInt(1, majorId);
            preparedStatement.execute();

        } catch (SQLException e) {
            throw new EntityNotFoundException();
        }
    }

    @Override
    public List<Major> getAllMajors() {
        List<Major> list = new ArrayList<>();
        try {
            connection = SQLDataSource.getInstance().getSQLConnection();
            sql = "select major_id,major_name,d.dept_id,dept_name\n" +
                    "from majors join department d on d.dept_id = majors.dept_id;";
            preparedStatement = connection.prepareStatement(sql);
            preparedStatement.execute();
            resultSet = preparedStatement.getResultSet();
            while (resultSet.next()) {
                Major major = new Major();
                major.id = resultSet.getInt(1);
                major.name = resultSet.getString(2);
                major.department.id = resultSet.getInt(3);
                major.department.name = resultSet.getString(4);
                list.add(major);
            }
        } catch (Exception e) {
            throw new EntityNotFoundException();
        }
        return list;
    }

    @Override
    public Major getMajor(int majorId) {
        Major major = new Major();
        try {
            connection = SQLDataSource.getInstance().getSQLConnection();
            sql = "select major_id,major_name,d.dept_id,dept_name\n" +
                    "from majors join department d on d.dept_id = majors.dept_id where major_id=(?);";
            preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setInt(1, majorId);
            preparedStatement.execute();
            resultSet = preparedStatement.getResultSet();
            resultSet.next();
            major.id = resultSet.getInt(1);
            major.name = resultSet.getString(2);
            major.department.id = resultSet.getInt(3);
            major.department.name = resultSet.getString(4);
        } catch (Exception e) {
            throw new EntityNotFoundException();
        }
        return major;
    }

    @Override
    public void addMajorCompulsoryCourse(int majorId, String courseId) {
        try {
            connection = SQLDataSource.getInstance().getSQLConnection();
            sql = "insert into major_compulsory(major_id, course_id)values (?,?);";
            preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setInt(1, majorId);
            preparedStatement.setString(2, courseId);
            preparedStatement.execute();
        } catch (Exception e) {
            throw new IntegrityViolationException();
        }
    }

    @Override
    public void addMajorElectiveCourse(int majorId, String courseId) {
        try {
            connection = SQLDataSource.getInstance().getSQLConnection();
            sql = "insert into major_elective(major_id, course_id)values (?,?);";
            preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setInt(1, majorId);
            preparedStatement.setString(2, courseId);
            preparedStatement.execute();
        } catch (Exception e) {
            throw new IntegrityViolationException();
        }
    }
}
