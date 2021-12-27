package myService;

import cn.edu.sustech.cs307.database.SQLDataSource;
import cn.edu.sustech.cs307.dto.CourseSection;
import cn.edu.sustech.cs307.dto.Major;
import cn.edu.sustech.cs307.exception.EntityNotFoundException;
import cn.edu.sustech.cs307.exception.IntegrityViolationException;
import cn.edu.sustech.cs307.service.InstructorService;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class myInstructorService implements InstructorService {
    static Connection connection;
    static String sql;
    static PreparedStatement preparedStatement;
    static ResultSet resultSet;

    @Override
    public void addInstructor(int userId, String firstName, String lastName) {
        try {
            connection = SQLDataSource.getInstance().getSQLConnection();
            sql = "insert into instructors(instructor_id, first_name, last_name) values (?,?,?);";
            preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setInt(1, userId);
            preparedStatement.setString(2, firstName);
            preparedStatement.setString(3, lastName);
            preparedStatement.execute();
        } catch (Exception e) {
            throw new IntegrityViolationException();
        }
    }

    @Override
    public List<CourseSection> getInstructedCourseSections(int instructorId, int semesterId) {
        List<CourseSection> list = new ArrayList<>();
        try {
            connection = SQLDataSource.getInstance().getSQLConnection();
            sql = "select csc.section_id, section_name, total_capacity, left_capacity\n" +
                    "from course_section\n" +
                    "         join course_section_class csc on course_section.section_id = csc.section_id\n" +
                    "where instructor_id = (?)\n" +
                    "  and semester_id = (?);";
            preparedStatement = connection.prepareStatement(sql);
            preparedStatement.execute();
            resultSet = preparedStatement.getResultSet();
            while (resultSet.next()) {
                CourseSection courseSection = new CourseSection();
                courseSection.id = resultSet.getInt(1);
                courseSection.name = resultSet.getString(2);
                courseSection.totalCapacity = resultSet.getInt(3);
                courseSection.leftCapacity = resultSet.getInt(4);
                list.add(courseSection);
            }
        } catch (Exception e) {
            throw new EntityNotFoundException();
        }
        return list;
    }
}
