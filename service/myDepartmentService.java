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

    @Override
    public int addDepartment(String name) {
        int id = 0;
        try {
            Connection connection = SQLDataSource.getInstance().getSQLConnection();
            sql = "insert into department(dept_id, dept_name)\n" +
                    "values (default, ?)\n" +
                    "on conflict do nothing\n" +
                    "returning dept_id;";
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, name);
            preparedStatement.execute();
            ResultSet resultSet = preparedStatement.getResultSet();

            if (resultSet.next()) {
                id = resultSet.getInt(1);
                resultSet.close();
                preparedStatement.close();
                connection.close();
            } else {
                resultSet.close();
                preparedStatement.close();
                connection.close();
                //               throw new IntegrityViolationException();
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
//            resultSet = preparedStatement.executeQuery();
//            resultSet.next();
//            connection.close();
//            return resultSet.getInt(1);
        } catch (SQLException e) {

        }
        return id;

    }

    @Override
    public void removeDepartment(int departmentId) {
        try {
            Connection connection = SQLDataSource.getInstance().getSQLConnection();
            sql = "delete from department where dept_id=(?);";
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setInt(1, departmentId);
            preparedStatement.execute();
            ResultSet resultSet = preparedStatement.getResultSet();
            resultSet.close();
            preparedStatement.close();
            connection.close();
        } catch (SQLException e) {

        }
        try {
            Connection connection = SQLDataSource.getInstance().getSQLConnection();
//            sql = "select major_id from majors where dept_id=(?);";
//            PreparedStatement preparedStatement = connection.prepareStatement(sql);
//            preparedStatement.setInt(1, departmentId);
//            preparedStatement.execute();
//            List<Integer> list = new ArrayList<>();
//            ResultSet resultSet = preparedStatement.executeQuery();
//            while (resultSet.next()) {
//                list.add(resultSet.getInt(1));
//            }
//            //按majors中的major方法；删除major
//
//            for (int i = 0; i < list.size(); i++) {
//                int majorId = list.get(i);
//                ////删除majorId对应student_courses中的内容
//                String sql_delete_student_courses = "delete\n" +
//                        "from student_courses\n" +
//                        "where sid in (select sid from students where major_id = (?));";
//                preparedStatement = connection.prepareStatement(sql_delete_student_courses);
//                preparedStatement.setInt(1, majorId);
//                preparedStatement.execute();
//
//                //删除majorId对应students中的内容
//                String sql_delete_students = "delete from students where major_id=(?);";
//                preparedStatement = connection.prepareStatement(sql_delete_students);
//                preparedStatement.setInt(1, majorId);
//                preparedStatement.execute();
//
//                //删除majorId对应major_compulsory中的内容
//                String sql_delete_major_compulsory = "delete from major_compulsory where major_id=(?);";
//                preparedStatement = connection.prepareStatement(sql_delete_major_compulsory);
//                preparedStatement.setInt(1, majorId);
//                preparedStatement.execute();
//
//                //删除majorId对应major_elective表中的内容
//                String sql_delete_major_elective = "delete from major_elective where major_id=(?);";
//                preparedStatement = connection.prepareStatement(sql_delete_major_elective);
//                preparedStatement.setInt(1, majorId);
//                preparedStatement.execute();
//
//                //删除majorId对应majors表中的内容
//                String sql_delete_majors = "delete from majors where major_id=(?);";
//                preparedStatement = connection.prepareStatement(sql_delete_majors);
//                preparedStatement.setInt(1, majorId);
//                preparedStatement.execute();
//
//            }
//            //删除department
//            sql = "delete from department where dept_id=(?);";
//            preparedStatement = connection.prepareStatement(sql);
//            preparedStatement.setInt(1, departmentId);
//            preparedStatement.execute();
//            resultSet.close();
//            preparedStatement.close();
//            connection.close();
            //删除department
            sql = "delete from department where dept_id=(?);";
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setInt(1, departmentId);
            preparedStatement.execute();
            ResultSet resultSet = preparedStatement.getResultSet();
            resultSet.close();
            preparedStatement.close();
            connection.close();
        } catch (SQLException e) {

        }
    }


    @Override
    public List<Department> getAllDepartments() {
        List<Department> list = new ArrayList<>();
        try {
            Connection connection = SQLDataSource.getInstance().getSQLConnection();
            sql = "select * from department;";
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.execute();
            ResultSet resultSet = preparedStatement.getResultSet();
            while (resultSet.next()) {
                Department department = new Department();
                department.id = resultSet.getInt(1);
                department.name = resultSet.getString(2);
                list.add(department);
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
    public Department getDepartment(int departmentId) {
        Department department = new Department();
        try {
            Connection connection = SQLDataSource.getInstance().getSQLConnection();
            sql = "select * from department where dept_id=(?);";
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setInt(1, departmentId);
            preparedStatement.execute();
            ResultSet resultSet = preparedStatement.getResultSet();
            if (resultSet.next()) {
                department.id = resultSet.getInt(1);
                department.name = resultSet.getString(2);
                resultSet.close();
                preparedStatement.close();
                connection.close();
            } else {
                resultSet.close();
                preparedStatement.close();
                connection.close();
                //               throw new EntityNotFoundException();
            }
        } catch (Exception e) {
            //           throw new EntityNotFoundException();
        }
        return department;
    }
}
