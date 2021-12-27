package myService;

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
    static Connection connection;
    static String sql;
    static PreparedStatement preparedStatement;
    static ResultSet resultSet;

    @Override
    public int addDepartment(String name) {
        try {
            connection = SQLDataSource.getInstance().getSQLConnection();
            sql = "insert into department(dept_name) values (?)";
            preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, name);
            preparedStatement.execute();

            //返回id
            String sql1 = "select dept_id from department where dept_name=(?);";
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
    public void removeDepartment(int departmentId) {
        try {
            connection = SQLDataSource.getInstance().getSQLConnection();
//            String sql_delete_student_courses = "delete from student_courses where sid in\n" +
//                    "(select sid from students where major_id in \n" +
//                    "(select major_id from department where dept_id=(?)));";
//            String sql_delete_students = "delete\n" +
//                    "from students\n" +
//                    "where major_id in (select major_id from department where dept_id = (?));";

            //选出departmentId对应哪些majorId
            sql = "select major_id from majors where dept_id=(?);";
            preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setInt(1, departmentId);
            preparedStatement.execute();
            List<Integer> list = new ArrayList<>();
            while (resultSet.next()) {
                list.add(resultSet.getInt(1));
            }
            //调用majors中的major方法；删除major
            myMajorService m1 = new myMajorService();
            for (int i = 0; i < list.size(); i++) {
                m1.removeMajor(list.get(i));
            }

            //删除department
            sql="delete from department where dept_id=(?);";
            preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setInt(1, departmentId);
            preparedStatement.execute();
        } catch (SQLException e) {
            throw new EntityNotFoundException();
        }
    }


    @Override
    public List<Department> getAllDepartments() {
        List<Department> list = new ArrayList<>();
        try {
            connection = SQLDataSource.getInstance().getSQLConnection();
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
        } catch (Exception e) {
            throw new EntityNotFoundException();
        }
        return list;
    }

    @Override
    public Department getDepartment(int departmentId) {
        Department department = new Department();
        try {
            connection = SQLDataSource.getInstance().getSQLConnection();
            sql = "select * from department where dept_id=(?);";
            preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setInt(1, departmentId);
            preparedStatement.execute();
            resultSet = preparedStatement.getResultSet();
            resultSet.next();
            department.id = resultSet.getInt(1);
            department.name = resultSet.getString(2);
        } catch (Exception e) {
            throw new EntityNotFoundException();
        }
        return department;
    }
}
