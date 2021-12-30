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
import java.time.DayOfWeek;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class myStudentService implements StudentService {

    @Override
    public void addStudent(int userId, int majorId, String firstName, String lastName, Date enrolledDate) {
        try {
            PreparedStatement prstAddStu;
            Connection con = SQLDataSource.getInstance().getSQLConnection();
            String inSql = "insert into students (sid, major_id, first_name, last_name,  enrolled_date) values (?,?,?,?,?) on conflict do nothing";
            prstAddStu = con.prepareStatement(inSql);
            prstAddStu.setInt(1,userId);
            prstAddStu.setInt(2,majorId);
            prstAddStu.setString(3,firstName);
            prstAddStu.setString(4,lastName);
            prstAddStu.setDate(5,enrolledDate);

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

        return null;
    }



    @Override
    public void dropCourse(int studentId, int sectionId) throws IllegalStateException {

        try {
            Connection conn =  SQLDataSource.getInstance().getSQLConnection();
//            String searchSql = "select case  when course_state = 'SELECTED' then false else true  end as already_marked " +
//                    "from student_courses where sid = " +studentId+ " and section_id = "+sectionId;
            String searchSql = "select * from drop_course("+studentId+ ","+sectionId+ ")";
            PreparedStatement stmt1 = conn.prepareStatement(searchSql);
            stmt1.execute();
//            ResultSet rs = stmt1.executeQuery();
//            if(rs.next()) {
//                if(rs.getBoolean("throw")){
//                    throw new IllegalStateException();
//                }
//            }
//            rs.close();
            stmt1.close();
            conn.close();
        } catch (SQLException e) {
            throw new IllegalStateException();
           // e.printStackTrace();
        }
    }

    @Override
    public void addEnrolledCourseWithGrade(int studentId, int sectionId, @Nullable Grade grade) {
        String sql = "";

    }

    @Override
    public void setEnrolledCourseGrade(int studentId, int sectionId, Grade grade) {

    }


    @Override
    public Map<Course, Grade> getEnrolledCoursesAndGrades(int studentId, @Nullable Integer semesterId) {
        Map<Course, Grade> map = new HashMap<>();
        try {
            Connection con = SQLDataSource.getInstance().getSQLConnection();
            String sql = "select b.course_id,course_name,credit,class_hour,  case  when grading = 'PASS_OR_FAIL' then true " +
 " else false end as is_PF, cast(course_state as text) as course_state,  case  when mark is null then -1  else mark  end as mark " +
    "from (select course_state,mark,course_id from (select * from student_courses where sid = "+studentId+")a join course_section se on a.section_id = se.section_id";
            if(semesterId!=null){
                sql = sql + "where semester_id = "+semesterId;
            }
            sql = sql + ") b join courses co on b.course_id = co.course_id";
            PreparedStatement ps1;
            ps1 = con.prepareStatement( sql );
            //todo “预”编译??
            ResultSet rs = ps1.executeQuery();
            while (rs.next()){
                Course course = new Course();
                course.classHour = rs.getInt("class_hour");
                course.credit = rs.getInt("credit");
                if(rs.getBoolean("is_PF")){
                    course.grading = Course.CourseGrading.PASS_OR_FAIL;
                }else {
                    course.grading = Course.CourseGrading.HUNDRED_MARK_SCORE;
                }

                Grade grade;
                short mark = rs.getShort("mark");
                if(mark != -1){
                    grade = new HundredMarkGrade((short) mark);
                }else {// mark ==-1(null)
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
                if(!map.containsKey(course)){//之前记录没有这课
                    map.put(course,grade);
                }else if(grade == null){  // 未设置成绩，必然最新
                    map.put(course, null);
                }else if(mark >= 60){
                    map.put(course,grade);
                }else if(grade.equals(grade = PassOrFailGrade.PASS)){
                    map.put(course,grade);
                }else { //本条记录不及格
                    Grade prev = map.get(course);
//                    if(prev==null){   //有未设成绩的记录，prev最新
//                        //map.put(course,grade);
//                    }else
                        if( prev.when(
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
                    )){// prev 不及格
                        map.put(course,grade);
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


        return null;
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
