package myimplement.service;

import cn.edu.sustech.cs307.database.SQLDataSource;
import cn.edu.sustech.cs307.dto.*;
import cn.edu.sustech.cs307.dto.grade.Grade;
import cn.edu.sustech.cs307.dto.grade.HundredMarkGrade;
import cn.edu.sustech.cs307.dto.grade.PassOrFailGrade;
import cn.edu.sustech.cs307.service.StudentService;
import com.zaxxer.hikari.HikariDataSource;

import javax.annotation.Nullable;
import java.sql.*;
import java.sql.Date;
import java.time.DayOfWeek;
import java.util.*;

public class myStudentService implements StudentService {

    @Override
    public void addStudent(int userId, int majorId, String firstName, String lastName, Date enrolledDate) {
        try {
            PreparedStatement prstAddStu;
            Connection con = SQLDataSource.getInstance().getSQLConnection();
            String inSql = "insert into students (sid, major_id, first_name, last_name,  enrolled_date) values (?,?,?,?,?) on conflict do nothing";
            prstAddStu = con.prepareStatement(inSql);
            prstAddStu.setInt(1, userId);
            prstAddStu.setInt(2, majorId);
            prstAddStu.setString(3, firstName);
            prstAddStu.setString(4, lastName);
            prstAddStu.setDate(5, enrolledDate);

            prstAddStu.execute();
            prstAddStu.close();
            con.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public List<CourseSearchEntry> searchCourse(int studentId,
                                                int semesterId,
                                                @Nullable String searchCid,
                                                @Nullable String searchName,
                                                @Nullable String searchInstructor,
                                                @Nullable DayOfWeek searchDayOfWeek,
                                                @Nullable Short searchClassTime,
                                                @Nullable List<String> searchClassLocations,
                                                CourseType searchCourseType,
                                                boolean ignoreFull, boolean ignoreConflict,
                                                boolean ignorePassed, boolean ignoreMissingPrerequisites,
                                                int pageSize, int pageIndex) {
        String sql = "select * from ";


        return null;
    }

    @Override
    public EnrollResult enrollCourse(int studentId, int sectionId) {
        //别忘了容量减少

        return null;
    }


    @Override
    public void dropCourse(int studentId, int sectionId) throws IllegalStateException {

        try (
                Connection conn = SQLDataSource.getInstance().getSQLConnection();
                PreparedStatement stmt1 = conn.prepareStatement("select * from drop_course(?,?)");

        ) {
            stmt1.setInt(1, studentId);
            stmt1.setInt(2, sectionId);
            stmt1.execute();
            stmt1.close();
            conn.close();
        } catch (SQLException e) {
            throw new IllegalStateException();
            // e.printStackTrace();
        }
    }

    @Override
    public void addEnrolledCourseWithGrade(int studentId, int sectionId, @Nullable Grade grade) {
        String sql = "insert into student_courses (sid, section_id, course_state, mark) VALUES (?,?, cast(? as states) ,?) on conflict do nothing";
        try {
            PreparedStatement prst;
            Connection con = SQLDataSource.getInstance().getSQLConnection();
            prst = con.prepareStatement(sql);
            prst.setInt(1, studentId);
            prst.setInt(2, sectionId);
            if (grade == null) {
                prst.setString(3, "SELECTED");
                prst.setNull(4, Types.NULL);
            } else if (grade.equals(PassOrFailGrade.FAIL)) {
                prst.setString(3, "FAILED");
                prst.setNull(4, Types.NULL);
            } else if (grade.equals(PassOrFailGrade.PASS)) {
                prst.setString(3, "PASSED");
                prst.setNull(4, Types.NULL);
            } else {
                prst.setString(3, "MARKED");
                prst.setShort(4, ((HundredMarkGrade) grade).mark);
            }
            prst.execute();
            prst.close();
            con.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void setEnrolledCourseGrade(int studentId, int sectionId, Grade grade) {
//        HundredMarkGrade hundredMarkGrade = new HundredMarkGrade();
//        Grade grade1 = new Grade() {
//            @Override
//            public <R> R when(Cases<R> cases) {
//                return null;
//            }
//        };
//        Cases<> cases = new Grade.Cases<>();
//        grade1.when(PassOrFailGrade)
        try {
            Connection connection = SQLDataSource.getInstance().getSQLConnection();
            String sql = "";
        } catch (SQLException r) {

        }
    }


    @Override
    public Map<Course, Grade> getEnrolledCoursesAndGrades(int studentId, @Nullable Integer semesterId) {
        Map<Course, Grade> map = new HashMap<>();
        try {
            Connection con = SQLDataSource.getInstance().getSQLConnection();
            String sql = "select b.course_id,course_name,credit,class_hour,  case  when grading = 'PASS_OR_FAIL' then true " +
                    " else false end as is_PF, cast(course_state as text) as course_state,  case  when mark is null then -1  else mark  end as mark " +
                    "from (select course_state,mark,course_id from (select * from student_courses where sid = ?)a join course_section se on a.section_id = se.section_id";

            if (semesterId != null) {
                sql = sql + "where semester_id = ?";
            }
            sql = sql + ") b join courses co on b.course_id = co.course_id";
            PreparedStatement ps1;
            ps1 = con.prepareStatement(sql);
            ps1.setInt(1, studentId);
            if (semesterId != null) {
                ps1.setInt(2, semesterId);
            }

            //todo “预”编译??
            ResultSet rs = ps1.executeQuery();
            while (rs.next()) {
                Course course = new Course();
                course.classHour = rs.getInt("class_hour");
                course.credit = rs.getInt("credit");
                if (rs.getBoolean("is_PF")) {
                    course.grading = Course.CourseGrading.PASS_OR_FAIL;
                } else {
                    course.grading = Course.CourseGrading.HUNDRED_MARK_SCORE;
                }

                Grade grade;
                short mark = rs.getShort("mark");
                if (mark != -1) {
                    grade = new HundredMarkGrade((short) mark);
                } else {// mark == -1(null)
                    String courseState = rs.getString("course_state");
                    switch (courseState) {
                        case "SELECTED": //未判分
                            grade = null;
                            break;
                        case "PASSED": // PF 的课 且过了
                            grade = PassOrFailGrade.PASS;
                            break;
                        case "FAILED":
                            grade = PassOrFailGrade.FAIL;
                            break;
                        default:
                            System.out.println(" ");
                            grade = null;
                            break;
                    }
                }
                if (!map.containsKey(course)) {//之前记录没有这课
                    map.put(course, grade);
                } else if (grade == null) {  // 未设置成绩，必然最新
                    map.put(course, null);
                } else if (mark >= 60) {
                    map.put(course, grade);
                } else if (grade.equals(grade = PassOrFailGrade.PASS)) {
                    map.put(course, grade);
                } else { //本条记录不及格
                    Grade prev = map.get(course);
//                    if(prev==null){   //有未设成绩的记录，prev最新
//                        //map.put(course,grade);
//                    }else
                    if (prev.when(
                            new Grade.Cases<Boolean>() {
                                @Override
                                public Boolean match(PassOrFailGrade self) {

                                    return self.equals(PassOrFailGrade.FAIL);
                                }

                                @Override
                                public Boolean match(HundredMarkGrade self) {

                                    return self.mark < 60;
                                }
                            }
                    )) {// prev 不及格
                        map.put(course, grade);
                    }
                }
            }
            rs.close();
            ps1.close();
            con.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return map;
    }

    @Override
    public CourseTable getCourseTable(int studentId, Date date) {
        CourseTable courseTable = new CourseTable();
        courseTable.table = new HashMap<>();
        Instructor instructor = new Instructor();
        CourseTable.CourseTableEntry courseTableEntry = new CourseTable.CourseTableEntry();
        Set<CourseTable.CourseTableEntry>[] sets = new Set[7];
        for (int i = 0; i < 7; i++) {
            sets[i] = new HashSet<>();
        }
        try {
            Connection connection = SQLDataSource.getInstance().getSQLConnection();
            //查找当前date所在的学期,好根据学期开始时间确定date属于在第几周
            String sql_date = "select begin_time from semester where begin_time<(?) and end_time>(?);";
            PreparedStatement preparedStatement = connection.prepareStatement(sql_date);
            preparedStatement.setDate(1, date);
            preparedStatement.setDate(2, date);
            preparedStatement.execute();
            ResultSet resultSet = preparedStatement.getResultSet();
            if (resultSet.next()) {
                Date dateBegin = resultSet.getDate(1);

                //查找data到begin有多少天
                String sql_days = "select date_part('day',(?)::timestamp-(?)::timestamp);";
                preparedStatement = connection.prepareStatement(sql_days);
                preparedStatement.setDate(1, date);
                preparedStatement.setDate(2, dateBegin);
                preparedStatement.execute();
                resultSet = preparedStatement.getResultSet();
                if (resultSet.next()) {
                    int countDay = resultSet.getInt(1);
                    int weekNumber = countDay / 7 + 1;

                    //正式查找：
                    String sql_select = "select c.course_name,section_name,i.instructor_id,i.first_name,i.last_name,classstart,classend,location,cast(dayofweek as text)\n" +
                            "from course_section_class csc join course_section cs on csc.section_id = cs.section_id join courses c on c.course_id = cs.course_id\n" +
                            "join instructors i on csc.instructor_id = i.instructor_id where (cs.section_id in (select section_id from student_courses where sid = (?)))and (?)=ANY (csc.weeklist);";
                    preparedStatement = connection.prepareStatement(sql_select);
                    preparedStatement.setInt(1, studentId);
                    preparedStatement.setInt(2, weekNumber);
                    preparedStatement.execute();
                    resultSet = preparedStatement.getResultSet();

                    while (resultSet.next()) {
                        courseTableEntry.courseFullName = String.format("%s[%s]", resultSet.getString(1), resultSet.getString(2));
                        instructor.id = resultSet.getInt(3);
                        //判断中英文名
                        String firstName = resultSet.getString(4);
                        String lastName = resultSet.getString(5);

                        String regex = "^[a-zA-Z ]+$";
                        if (firstName.matches(regex) && lastName.matches(regex)) {
                            instructor.fullName = firstName + " " + lastName;
                        } else {
                            instructor.fullName = firstName + lastName;
                        }
                        courseTableEntry.instructor = instructor;
                        courseTableEntry.classBegin = resultSet.getShort(6);
                        courseTableEntry.classEnd = resultSet.getShort(7);
                        courseTableEntry.location = resultSet.getString(8);
                        //    DayOfWeek dayOfWeek = (DayOfWeek) resultSet.getObject(9);
                        String dayOfWeek = resultSet.getString(9);
                        if (dayOfWeek.equals("MONDAY")) {
                            sets[0].add(courseTableEntry);
                        } else if (dayOfWeek.equals("TUESDAY")) {
                            sets[1].add(courseTableEntry);
                        } else if (dayOfWeek.equals("WEDNESDAY")) {
                            sets[2].add(courseTableEntry);
                        } else if (dayOfWeek.equals("THURSDAY")) {
                            sets[3].add(courseTableEntry);
                        } else if (dayOfWeek.equals("FRIDAY")) {
                            sets[4].add(courseTableEntry);
                        } else if (dayOfWeek.equals("SATURDAY")) {
                            sets[5].add(courseTableEntry);
                        } else if (dayOfWeek.equals("SUNDAY")) {
                            sets[6].add(courseTableEntry);
                        }
                    }
                }
            }
            if (sets[0].isEmpty()) {
                courseTable.table.put(DayOfWeek.MONDAY, Set.of());
            } else {
                courseTable.table.put(DayOfWeek.MONDAY, sets[0]);
            }
            if (sets[1].isEmpty()) {
                courseTable.table.put(DayOfWeek.TUESDAY, Set.of());
            } else {
                courseTable.table.put(DayOfWeek.TUESDAY, sets[1]);
            }
            if (sets[2].isEmpty()) {
                courseTable.table.put(DayOfWeek.WEDNESDAY, Set.of());
            } else {
                courseTable.table.put(DayOfWeek.WEDNESDAY, sets[2]);

            }
            if (sets[3].isEmpty()) {
                courseTable.table.put(DayOfWeek.THURSDAY, Set.of());
            } else {
                courseTable.table.put(DayOfWeek.THURSDAY, sets[3]);

            }
            if (sets[4].isEmpty()) {
                courseTable.table.put(DayOfWeek.FRIDAY, Set.of());
            } else {
                courseTable.table.put(DayOfWeek.FRIDAY, sets[4]);
            }
            if (sets[5].isEmpty()) {
                courseTable.table.put(DayOfWeek.SATURDAY, Set.of());
            } else {
                courseTable.table.put(DayOfWeek.SATURDAY, sets[5]);
            }
            if (sets[6].isEmpty()) {
                courseTable.table.put(DayOfWeek.SUNDAY, Set.of());
            } else {
                courseTable.table.put(DayOfWeek.SUNDAY, sets[6]);
            }

            resultSet.close();
            preparedStatement.close();
            connection.close();
        } catch (SQLException e) {

        }

        return courseTable;
    }

    @Override
    public boolean passedPrerequisitesForCourse(int studentId, String courseId) {


        return false;
    }


    @Override
    public Major getStudentMajor(int studentId) {
        Major major = new Major();
        try {
            PreparedStatement prstMajor;
            Connection con = SQLDataSource.getInstance().getSQLConnection();
            prstMajor = con.prepareStatement("select major_id,major_name,b.dept_id,dept_name from ( select a.major_id,major_name,dept_id from ( select major_id from students where sid = " + studentId +
                    " )a join majors m on a.major_id = m.major_id) b join department d on b.dept_id = d.dept_id");
            //todo “预编译??????
            ResultSet rs = prstMajor.executeQuery();
            rs.next();
            major.id = rs.getInt("major_id");
            major.name = rs.getString("major_name");
            major.department = new Department();
            major.department.id = rs.getInt("dept_id");
            major.department.name = rs.getString("dept_name");
            rs.close();
            prstMajor.close();
            con.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return major;
    }
}
