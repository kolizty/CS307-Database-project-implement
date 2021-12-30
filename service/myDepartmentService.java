package myimplement.service;

import cn.edu.sustech.cs307.database.SQLDataSource;
import cn.edu.sustech.cs307.dto.Department;

import cn.edu.sustech.cs307.dto.Major;
import cn.edu.sustech.cs307.exception.EntityNotFoundException;
import cn.edu.sustech.cs307.exception.IntegrityViolationException;
import cn.edu.sustech.cs307.service.DepartmentService;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class myDepartmentService implements DepartmentService {
    static String sql;
    static PreparedStatement preparedStatement;
    static ResultSet resultSet;

    @Override
    public int addDepartment(String name) {
        try {
            Connection connection = SQLDataSource.getInstance().getSQLConnection();
            sql = "insert into department(dept_id, dept_name)\n" +
                    "values (default, ?)\n" +
                    "on conflict do nothing\n" +
                    "returning dept_id;";
            preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, name);
            preparedStatement.execute();
            resultSet = preparedStatement.getResultSet();
            if (resultSet.next()) {
                connection.close();
                throw new IntegrityViolationException();
            } else {
                connection.close();
                return resultSet.getInt(1);
            }
//            sql = "insert into department(dept_name) values (?)";
//            preparedStatement = connection.prepareStatement(sql);
//            preparedStatement.setString(1, name);
//            preparedStatement.execute();
//
//            //返回id
//            String sql1 = "select dept_id from department where dept_name=(?);";
//            preparedStatement = connection.prepareStatement(sql1);
//            preparedStatement.setString(1, name);
//            preparedStatement.execute();
//            resultSet = preparedStatement.getResultSet();
//            resultSet.next();
//            connection.close();
//            return resultSet.getInt(1);
        } catch (SQLException e) {
            throw new IntegrityViolationException();
        }
    }

    @Override
    public void removeDepartment(int departmentId) {
        try {
            Connection connection = SQLDataSource.getInstance().getSQLConnection();
            sql = "select major_id from majors where dept_id=(?);";
            preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setInt(1, departmentId);
            preparedStatement.execute();
            List<Integer> list = new ArrayList<>();
            while (resultSet.next()) {
                list.add(resultSet.getInt(1));
            }
            //按majors中的major方法；删除major
            myMajorService m1 = new myMajorService();
            for (int i = 0; i < list.size(); i++) {
                int majorId = list.get(i);
                ////删除majorId对应student_courses中的内容
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

            }
            //删除department
            sql = "delete from department where dept_id=(?);";
            preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setInt(1, departmentId);
            preparedStatement.execute();
            connection.close();
        } catch (SQLException e) {
            throw new EntityNotFoundException();
        }
    }


    @Override
    public List<Department> getAllDepartments() {
        List<Department> list = new ArrayList<>();
        try {
            Connection connection = SQLDataSource.getInstance().getSQLConnection();
            sql = "select * from department;";
            preparedStatement = connection.prepareStatement(sql);
            preparedStatement.execute();
            resultSet = preparedStatement.getResultSet();
            while (resultSet.next()) {
                Department department = new Department();
                department.id = resultSet.getInt(1);
                department.name = resultSet.getString(2);
                list.add(department);
            }
            connection.close();
        } catch (Exception e) {
            throw new EntityNotFoundException();
        }
        return list;
    }

    @Override
    public Department getDepartment(int departmentId) {
        Department department = new Department();
        try {
            Connection connection = SQLDataSource.getInstance().getSQLConnection();
            sql = "select * from department where dept_id=(?);";
            preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setInt(1, departmentId);
            preparedStatement.execute();
            resultSet = preparedStatement.getResultSet();
            if (resultSet.next()) {
                department.id = resultSet.getInt(1);
                department.name = resultSet.getString(2);
                connection.close();
                return department;
            } else {
                connection.close();
                throw new EntityNotFoundException();
            }
        } catch (Exception e) {
            throw new EntityNotFoundException();
        }
    }
}
