package myimplement.service;

import cn.edu.sustech.cs307.database.SQLDataSource;
import cn.edu.sustech.cs307.dto.*;
import cn.edu.sustech.cs307.dto.prerequisite.Prerequisite;
import cn.edu.sustech.cs307.service.CourseService;

import javax.annotation.Nullable;
import java.sql.*;
import java.time.DayOfWeek;
import java.util.*;

public class myCourseService implements CourseService {
//    public void addPrerequisite(String courseId, Prerequisite prerequisite, StringBuffer sql) {
//        if ()
//
//    }

    @Override
    public void addCourse(String courseId, String courseName, int credit, int classHour, Course.CourseGrading grading, @Nullable Prerequisite prerequisite) {
        try {
            Connection connection = SQLDataSource.getInstance().getSQLConnection();
            PreparedStatement stmt1 = connection.prepareStatement(
                    "insert into courses(course_id,course_name,credit,class_hour,grading)" + "values(?,?,?,?,?::grading_type)");
            PreparedStatement stmt2 = connection.prepareStatement(
                    "insert into course_prerequisites(course_id,pre_course_id,prerequisite)" + "values(?,?,?)");
            stmt1.setString(1, courseId);
            stmt1.setString(2, courseName);
            stmt1.setInt(3, credit);
            stmt1.setInt(4, classHour);
            stmt1.setString(5, grading.toString());
            stmt1.execute();
//            stmt2.setString(1, courseId);
//            if (prerequisite != null) {
//
//                stmt2.execute();
//            }
            //todo: insert prerequisites
            stmt1.close();
            stmt2.close();
            connection.close();
        } catch (SQLException e) {
//            e.printStackTrace();
        }
    }

    @Override
    public int addCourseSection(String courseId, int semesterId, String sectionName, int totalCapacity) {
        int id = 0;
        try {
            Connection connection = SQLDataSource.getInstance().getSQLConnection();
            PreparedStatement stmt = connection.prepareStatement(
                    "insert into course_section(course_id,semester_id,section_name,total_capacity,left_capacity)"
                            + "values(?,?,?,?,?) ",Statement.RETURN_GENERATED_KEYS);
            stmt.setString(1, courseId);
            stmt.setInt(2, semesterId);
            stmt.setString(3, sectionName);
            stmt.setInt(4, totalCapacity);
            stmt.setInt(5, totalCapacity);
            stmt.execute();
            ResultSet rs = stmt.getGeneratedKeys();
            while (rs.next()) {
                id = rs.getInt(1);
            }
            rs.close();
            stmt.close();
            connection.close();
        } catch (SQLException e) {
//            e.printStackTrace();
        }
        return id;
    }

    @Override
    public int addCourseSectionClass(int sectionId, int instructorId, DayOfWeek dayOfWeek, Set<Short> weekList, short classStart, short classEnd, String location) {
        int id = 0;
        try {
            Connection connection = SQLDataSource.getInstance().getSQLConnection();
            PreparedStatement stmt = connection.prepareStatement(
                    "insert into course_section_class(section_id,instructor_id,"
                            + "dayofweek,weeklist,classstart,classend,location)"
                            + "values(?,?,?::days_of_week,?,?,?,?)",Statement.RETURN_GENERATED_KEYS);
            stmt.setInt(1, sectionId);
            stmt.setInt(2, instructorId);
            stmt.setString(3, dayOfWeek.toString());
            List<Short> list=new ArrayList<>();
            list.addAll(weekList);
            Array weeklist = connection.createArrayOf("smallint", list.toArray());
            stmt.setArray(4, weeklist);
            stmt.setShort(5, classStart);
            stmt.setShort(6, classEnd);
            stmt.setString(7, location);
            stmt.execute();
            ResultSet rs = stmt.getGeneratedKeys();
            while (rs.next()) {
                id = rs.getInt(1);
            }
            rs.close();
            stmt.close();
            connection.close();
        } catch (SQLException e) {
//            e.printStackTrace();
        }
        return id;
    }

    @Override
    public void removeCourse(String courseId) {
        try {
            Connection connection = SQLDataSource.getInstance().getSQLConnection();
            PreparedStatement stmt = connection.prepareStatement(
                    "delete from courses where course_id=?");
            stmt.setString(1, courseId);
            stmt.execute();
            stmt.close();
            connection.close();
        } catch (SQLException e) {
//            e.printStackTrace();
        }
    }

    @Override
    public void removeCourseSection(int sectionId) {
        try {
            Connection connection = SQLDataSource.getInstance().getSQLConnection();
            PreparedStatement stmt = connection.prepareStatement(
                    "delete from course_section where section_id=?");
            stmt.setInt(1, sectionId);
            stmt.execute();
            stmt.close();
            connection.close();
        } catch (SQLException e) {
//            e.printStackTrace();
        }
    }

    @Override
    public void removeCourseSectionClass(int classId) {
        try {
            Connection connection = SQLDataSource.getInstance().getSQLConnection();
            PreparedStatement stmt = connection.prepareStatement(
                    "delete from course_section_class where class_id=?");
            stmt.setInt(1, classId);
            stmt.execute();
            stmt.close();
            connection.close();
        } catch (SQLException e) {
//            e.printStackTrace();
        }
    }

    @Override
    public List<Course> getAllCourses() {
        List<Course> courses = new ArrayList<>();
        try {
            Connection connection = SQLDataSource.getInstance().getSQLConnection();
            PreparedStatement stmt = connection.prepareStatement(
                    "select * from courses");
            ResultSet rs = stmt.executeQuery();
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
            rs.close();
            stmt.close();
            connection.close();
        } catch (SQLException e) {
//            e.printStackTrace();
        }
        if (courses.isEmpty())
            return List.of();
        return courses;
    }

    @Override
    public List<CourseSection> getCourseSectionsInSemester(String courseId, int semesterId) {
        List<CourseSection> courseSection = new ArrayList<>();
        try {
            Connection connection = SQLDataSource.getInstance().getSQLConnection();
            PreparedStatement stmt = connection.prepareStatement(
                    "select section_id,section_name,total_capacity,left_capacity "
                            + "from course_section where course_id=? and semester_id=?");
            stmt.setString(1, courseId);
            stmt.setInt(2, semesterId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                int id = rs.getInt(1);
                String name = rs.getString(2);
                int total = rs.getInt(3);
                int left = rs.getInt(4);
                CourseSection section = new CourseSection();
                section.id = id;
                section.name = name;
                section.totalCapacity = total;
                section.leftCapacity = left;
                courseSection.add(section);
            }
            rs.close();
            stmt.close();
            connection.close();
        } catch (SQLException e) {
//            e.printStackTrace();
        }
        if (courseSection.isEmpty())
            return List.of();
        return courseSection;
    }

    @Override
    public Course getCourseBySection(int sectionId) {
        Course course = new Course();
        try {
            Connection connection = SQLDataSource.getInstance().getSQLConnection();
            PreparedStatement stmt = connection.prepareStatement(
                    "select s.course_id,course_name,credit,class_hour,grading from "
                            + "(select course_id from course_section where section_id=?) s "
                            + "join courses c on s.course_id=c.course_id");
            stmt.setInt(1, sectionId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                String courseId = rs.getString(1);
                String name = rs.getString(2);
                int credit = rs.getInt(3);
                int hour = rs.getInt(4);
                Course.CourseGrading grade = Course.CourseGrading.valueOf(rs.getString(5));
                course.id = courseId;
                course.name = name;
                course.credit = credit;
                course.classHour = hour;
                course.grading = grade;
            }
            rs.close();
            stmt.close();
            connection.close();
        } catch (SQLException e) {
//            e.printStackTrace();
        }
        return course;
    }

    @Override
    public List<CourseSectionClass> getCourseSectionClasses(int sectionId) {
        List<CourseSectionClass> courseSectionClassList = new ArrayList<>();
        try {
            Connection connection = SQLDataSource.getInstance().getSQLConnection();
            PreparedStatement stmt = connection.prepareStatement(
                    "select class_id,c.instructor_id,first_name,last_name,dayofweek,"
                            + "week_list,classstart,classend,location from course_section_class c join "
                            + "instructors i on i.instructor_id = c.instructor_id where section_id=?");
            stmt.setInt(1, sectionId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                int classId = rs.getInt(1);
                int instructorId = rs.getInt(2);
                String firstName = rs.getString(3);
                String lastName = rs.getString(4);
                DayOfWeek dayOfWeek = (DayOfWeek) rs.getObject(5);
                Array arr = rs.getArray(6);
                Set<Short> weekList = (Set<Short>) arr;
                short begin = rs.getShort(7);
                short end = rs.getShort(8);
                String location = rs.getString(9);
                Instructor instructor = new Instructor();
                instructor.id = instructorId;
                String fullName;
                String regex = "^[a-zA-Z ]+$";
                if (firstName.matches(regex) && lastName.matches(regex))
                    fullName = firstName + " " + lastName;
                else
                    fullName = firstName + lastName;
                instructor.fullName = fullName;
                CourseSectionClass courseSectionClass = new CourseSectionClass();
                courseSectionClass.id = classId;
                courseSectionClass.instructor = instructor;
                courseSectionClass.dayOfWeek = dayOfWeek;
                courseSectionClass.weekList = weekList;
                courseSectionClass.classBegin = begin;
                courseSectionClass.classEnd = end;
                courseSectionClass.location = location;
                courseSectionClassList.add(courseSectionClass);
            }
            rs.close();
            stmt.close();
            connection.close();
        } catch (SQLException e) {
//            e.printStackTrace();
        }
        if (courseSectionClassList.isEmpty())
            return List.of();
        return courseSectionClassList;
    }

    @Override
    public CourseSection getCourseSectionByClass(int classId) {
        CourseSection courseSection = new CourseSection();
        try {
            Connection connection = SQLDataSource.getInstance().getSQLConnection();
            PreparedStatement stmt = connection.prepareStatement(
                    "select cs.section_id,section_name,total_capacity,left_capacity from "
                            + "(select section_id from course_section_class where class_id=?) csc "
                            + "join course_section cs on csc.section_id=cs.section_id");
            stmt.setInt(1, classId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                int sectionId = rs.getInt(1);
                String name = rs.getString(2);
                int total = rs.getInt(3);
                int left = rs.getInt(4);
                courseSection.id = sectionId;
                courseSection.name = name;
                courseSection.totalCapacity = total;
                courseSection.leftCapacity = left;
            }
            rs.close();
            stmt.close();
            connection.close();
        } catch (SQLException e) {
//            e.printStackTrace();
        }
        return courseSection;
    }

    @Override
    public List<Student> getEnrolledStudentsInSemester(String courseId, int semesterId) {
        List<Student> studentList = new ArrayList<>();
        try {
            Connection connection = SQLDataSource.getInstance().getSQLConnection();
            PreparedStatement stmt = connection.prepareStatement(
                    "select s2.sid,first_name,last_name,enrolled_date,m.major_id,major_name,d.dept_id,dept_name from "
                            + "(select section_id from course_section where course_id=? and semester_id=?) s1 "
                            + "join student_courses sc on s1.section_id=sc.section_id join students s2 on sc.sid = s2.sid "
                            + "join majors m on s2.major_id = m.major_id join department d on m.dept_id = d.dept_id");
            stmt.setString(1, courseId);
            stmt.setInt(2, semesterId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                int sid = rs.getInt(1);
                String firstName = rs.getString(2);
                String lastName = rs.getString(3);
                java.sql.Date date = rs.getDate(4);
                int mid = rs.getInt(5);
                String mname = rs.getString(6);
                int did = rs.getInt(7);
                String dname = rs.getString(8);
                String fullName;
                String regex = "^[a-zA-Z ]+$";
                if (firstName.matches(regex) && lastName.matches(regex))
                    fullName = firstName + " " + lastName;
                else
                    fullName = firstName + lastName;
                Student student = new Student();
                student.id = sid;
                student.fullName = fullName;
                student.enrolledDate = date;
                Major major = new Major();
                major.id = mid;
                major.name = mname;
                Department department = new Department();
                department.id = did;
                department.name = dname;
                major.department = department;
                student.major = major;
                studentList.add(student);
            }
            rs.close();
            stmt.close();
            connection.close();
        } catch (SQLException e) {
//            e.printStackTrace();
        }
        if (studentList.isEmpty())
            return List.of();
        return studentList;
    }
}