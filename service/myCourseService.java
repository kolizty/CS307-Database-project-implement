package me.impl.service;

import cn.edu.sustech.cs307.database.SQLDataSource;
import cn.edu.sustech.cs307.dto.*;
import cn.edu.sustech.cs307.dto.prerequisite.Prerequisite;
import cn.edu.sustech.cs307.exception.EntityNotFoundException;
import cn.edu.sustech.cs307.exception.IntegrityViolationException;

import javax.annotation.Nullable;
import java.sql.*;
import java.time.DayOfWeek;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class myCourseService extends cn.edu.sustech.cs307.service.CourseService {
    @Override
    public void addCourse(String courseId, String courseName, int credit, int classHour, Course.CourseGrading grading, @Nullable Prerequisite prerequisite) {
        if (courseId == null)
            throw new IntegrityViolationException("courseId is not null");
        if (courseName == null)
            throw new IntegrityViolationException("courseName is not null");
        try (Connection connection = SQLDataSource.getInstance().getSQLConnection();
             PreparedStatement stmt1 = connection.prepareStatement("insert into courses(course_id,course_name,credit,class_hour,grading)" + "values(?,?,?,?,?::grading_type)");
             PreparedStatement stmt2 = connection.prepareStatement("insert into course_prerequisites(course_id,prerequisite)" + "values(?,?)")) {
            stmt1.setString(1, courseId);
            stmt1.setString(2, courseName);
            stmt1.setInt(3, credit);
            stmt1.setInt(4, classHour);
            stmt1.setString(5, grading.toString());
            stmt1.execute();
            stmt2.setString(1, courseId);
            if (prerequisite==null)
                stmt2.setString(2,null);
            else {

            }

                //todo: insert prerequisites
            stmt2.execute();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public int addCourseSection(String courseId, int semesterId, String sectionName, int totalCapacity) {
        if (courseId == null)
            throw new IntegrityViolationException("courseId is not null");
        if (sectionName == null)
            throw new IntegrityViolationException("sectionName is not null");
        ResultSet rs;
        int id = 0;
        try (Connection connection = SQLDataSource.getInstance().getSQLConnection();
             PreparedStatement stmt = connection.prepareStatement("insert into courseSection(course_id,semester_id,section_name,total_capacity,left_capacity)" + "values(?,?,?,?,?)", Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, courseId);
            stmt.setInt(2, semesterId);
            stmt.setString(3, sectionName);
            stmt.setInt(4, totalCapacity);
            stmt.setInt(5, totalCapacity);
            stmt.execute();
            rs = stmt.getGeneratedKeys();
            while (rs.next()) {
                id = rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return id;
    }

    @Override
    public int addCourseSectionClass(int sectionId, int instructorId, DayOfWeek dayOfWeek, Set<Short> weekList, short classStart, short classEnd, String location) {
        if (dayOfWeek == null)
            throw new IntegrityViolationException("dayOfWeek is not null");
        if (weekList == null)
            throw new IntegrityViolationException("weekList is not null");
        if (location == null)
            throw new IntegrityViolationException("location is not null");
        ResultSet rs;
        int id = 0;
        try (Connection connection = SQLDataSource.getInstance().getSQLConnection();
             PreparedStatement stmt = connection.prepareStatement("insert into course_section_class(section_id,instructor_id,dayofweek,week_list,classstart,classend,location)" + "values(?,?,?::days_of_week,?::weeklist,?,?,?)", Statement.RETURN_GENERATED_KEYS)) {
            stmt.setInt(1, sectionId);
            stmt.setInt(2, instructorId);
            stmt.setString(3, dayOfWeek.toString());
            stmt.setString(4, weekList.toString());
            stmt.setShort(5, classStart);
            stmt.setShort(6, classEnd);
            stmt.setString(7, location);
            stmt.execute();
            rs = stmt.getGeneratedKeys();
            while (rs.next()) {
                id = rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return id;
    }

    @Override
    public void removeCourse(String courseId) {
        if (courseId == null)
            throw new IntegrityViolationException("courseId is not null");
        ResultSet rs_course, rs_section;
        try (Connection connection = SQLDataSource.getInstance().getSQLConnection();
             PreparedStatement stmt0 = connection.prepareStatement("select * from courses where course_id=?");
             PreparedStatement stmt1 = connection.prepareStatement("select section_id from course_section where course_id=?");
             PreparedStatement stmt2 = connection.prepareStatement("delete from course_section_class where section_id=?");
             PreparedStatement stmt3 = connection.prepareStatement("delete from student_courses where section_id=?");
             PreparedStatement stmt4 = connection.prepareStatement("delete from course_section where section_id=?");
             PreparedStatement stmt5 = connection.prepareStatement("delete from major_compulsory where course_id=?");
             PreparedStatement stmt = connection.prepareStatement("delete from courses where course_id=?")) {
            stmt0.setString(1, courseId);
            rs_course = stmt0.executeQuery();
            if (!rs_course.next())
                throw new EntityNotFoundException("courseId not found");
            stmt1.setString(1, courseId);
            rs_section = stmt1.executeQuery();
            while (rs_section.next()) {
                int sectionId = rs_section.getInt(1);
                stmt2.setInt(1, sectionId);
                stmt3.setInt(1, sectionId);
                stmt4.setInt(1, sectionId);
                stmt2.execute();
                stmt3.execute();
                stmt4.execute();
            }
            stmt5.setString(1, courseId);
            stmt5.execute();
            stmt.setString(1, courseId);
            stmt.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void removeCourseSection(int sectionId) {
        ResultSet rs;
        try (Connection connection = SQLDataSource.getInstance().getSQLConnection();
             PreparedStatement stmt0 = connection.prepareStatement("select * from courses where course_id=?");
             PreparedStatement stmt1 = connection.prepareStatement("delete from course_section_class where section_id=?");
             PreparedStatement stmt2 = connection.prepareStatement("delete from student_courses where section_id=?");
             PreparedStatement stmt = connection.prepareStatement("delete from course_section where section_id=?")) {
            stmt0.setInt(1, sectionId);
            rs = stmt0.executeQuery();
            if (!rs.next())
                throw new EntityNotFoundException("sectionId not found");
            stmt1.setInt(1, sectionId);
            stmt2.setInt(1, sectionId);
            stmt1.execute();
            stmt2.execute();
            stmt.setInt(1, sectionId);
            stmt.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void removeCourseSectionClass(int classId) {
        ResultSet rs;
        try (Connection connection = SQLDataSource.getInstance().getSQLConnection();
             PreparedStatement stmt0 = connection.prepareStatement("select * from course_section_class where course_id=?");
             PreparedStatement stmt = connection.prepareStatement("delete from course_section_class where class_id=?")) {
            stmt0.setInt(1, classId);
            rs = stmt0.executeQuery();
            if (!rs.next())
                throw new EntityNotFoundException("classId not found");
            stmt.setInt(1, classId);
            stmt.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public List<Course> getAllCourses() {
        ResultSet rs;
        List<Course> courses = new ArrayList<>();
        try (Connection connection = SQLDataSource.getInstance().getSQLConnection();
             PreparedStatement stmt = connection.prepareStatement("select * from courses")) {
            rs = stmt.executeQuery();
            while (rs.next()) {
                String id = rs.getString(1);
                String name = rs.getString(2);
                int credit = rs.getInt(3);
                int hour = rs.getInt(4);
                Course.CourseGrading grade = Course.CourseGrading.valueOf(rs.getString(5));
                Course course = new Course();
                course.id = id;
                course.name = name;
                course.credit = credit;
                course.classHour = hour;
                course.grading = grade;
                courses.add(course);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return courses;
    }

    @Override
    public List<CourseSection> getCourseSectionsInSemester(String courseId, int semesterId) {
        if (courseId == null)
            throw new IntegrityViolationException("courseId is not null");
        ResultSet rs;
        List<CourseSection> courseSection = new ArrayList<>();
        try (Connection connection = SQLDataSource.getInstance().getSQLConnection();
             PreparedStatement stmt = connection.prepareStatement("select * from course_section where course_id=? and semester_id=?")) {
            stmt.setString(1, courseId);
            stmt.setInt(2, semesterId);
            rs = stmt.executeQuery();
            if (!rs.next())
                throw new EntityNotFoundException("id not found");
            rs.previous();
            while (rs.next()) {
                int id = rs.getInt(1);
                String name = rs.getString(4);
                int total = rs.getInt(5);
                int left = rs.getInt(6);
                CourseSection section = new CourseSection();
                section.id = id;
                section.name = name;
                section.totalCapacity = total;
                section.leftCapacity = left;
                courseSection.add(section);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return courseSection;
    }

    @Override
    public Course getCourseBySection(int sectionId) {
        ResultSet rs_section, rs_course;
        Course course = new Course();
        try (Connection connection = SQLDataSource.getInstance().getSQLConnection();
             PreparedStatement stmt = connection.prepareStatement("select course_id from course_section where section_id=?");
             PreparedStatement stmt1 = connection.prepareStatement("select * from courses where course_id=?")) {
            stmt.setInt(1, sectionId);
            rs_section = stmt.executeQuery();
            if (!rs_section.next())
                throw new EntityNotFoundException("sectionId not found");
            rs_section.previous();
            while (rs_section.next()) {
                String courseId = rs_section.getString(1);
                stmt1.setString(1, courseId);
                rs_course = stmt1.executeQuery();
                if (!rs_course.next())
                    throw new EntityNotFoundException("course not found");
                rs_course.previous();
                while (rs_course.next()) {
                    String name = rs_course.getString(2);
                    int credit = rs_course.getInt(3);
                    int hour = rs_course.getInt(4);
                    Course.CourseGrading grade = Course.CourseGrading.valueOf(rs_course.getString(5));
                    course.id = courseId;
                    course.name = name;
                    course.credit = credit;
                    course.classHour = hour;
                    course.grading = grade;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return course;
    }

    @Override
    public List<CourseSectionClass> getCourseSectionClasses(int sectionId) {
        ResultSet rs;
        List<CourseSectionClass> courseSectionClassList = new ArrayList<>();
        try (Connection connection = SQLDataSource.getInstance().getSQLConnection();
             PreparedStatement stmt = connection.prepareStatement(
                 "select class_id,c.instructor_id,first_name,last_name,dayofweek," +
                 "week_list,classstart,classend,location from course_section_class c join " +
                 "instructors i on i.instructor_id = c.instructor_id where section_id=?")) {
            stmt.setInt(1, sectionId);
            rs = stmt.executeQuery();
            if (!rs.next())
                throw new EntityNotFoundException("sectionId not found");
            rs.previous();
            while (rs.next()) {
                int classId = rs.getInt(1);
                int instructorId = rs.getInt(2);
                String firstName=rs.getString(3);
                String lastName=rs.getString(4);
                DayOfWeek dayOfWeek = (DayOfWeek) rs.getObject(5);
                String week=rs.getString(6);
                Set<Short> weekList=new HashSet<Short>();
                //todo: weekList
                short begin=rs.getShort(7);
                short end=rs.getShort(8);
                String location=rs.getString(9);
                Instructor instructor=new Instructor();
                instructor.id=instructorId;
                String fullName="";
                String regex="^[a-zA-Z ]+$";
                if (firstName.matches(regex) && lastName.matches(regex))
                    fullName=firstName+" "+lastName;
                else
                    fullName=firstName+lastName;
                instructor.fullName=fullName;
                CourseSectionClass courseSectionClass=new CourseSectionClass();
                courseSectionClass.id=classId;
                courseSectionClass.instructor=instructor;
                courseSectionClass.dayOfWeek=dayOfWeek;
                courseSectionClass.weekList=weekList;
                courseSectionClass.classBegin=begin;
                courseSectionClass.classEnd=end;
                courseSectionClass.location=location;
                courseSectionClassList.add(courseSectionClass);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return courseSectionClassList;
    }

    @Override
    public CourseSection getCourseSectionByClass(int classId) {
        ResultSet rs_section, rs_class;
        CourseSection courseSection = new CourseSection();
        try (Connection connection = SQLDataSource.getInstance().getSQLConnection();
             PreparedStatement stmt = connection.prepareStatement("select section_id from course_section_class where class_id=?");
             PreparedStatement stmt1 = connection.prepareStatement("select section_name,total_capacity,left_capacity from course_section where section_id=?")) {
            stmt.setInt(1, classId);
            rs_class = stmt.executeQuery();
            if (!rs_class.next())
                throw new EntityNotFoundException("sectionId not found");
            rs_class.previous();
            while (rs_class.next()) {
                int sectionId = rs_class.getInt(1);
                stmt1.setInt(1, sectionId);
                rs_section = stmt1.executeQuery();
                while (rs_section.next()) {
                    String name = rs_section.getString(1);
                    int total=rs_section.getInt(2);
                    int left=rs_section.getInt(3);
                    courseSection.id=sectionId;
                    courseSection.name=name;
                    courseSection.totalCapacity=total;
                    courseSection.leftCapacity=left;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return courseSection;
    }

    @Override
    public List<Student> getEnrolledStudentsInSemester(String courseId, int semesterId) {
        return null;
    }
}
