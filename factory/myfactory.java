package myimplement.factory;

import cn.edu.sustech.cs307.factory.ServiceFactory;
import cn.edu.sustech.cs307.service.*;
import myimplement.service.*;

import java.util.List;

public class myfactory extends ServiceFactory {
    public myfactory() {
        super();
        registerService(CourseService.class, new myCourseService());
        registerService(DepartmentService.class, new myDepartmentService());
        registerService(InstructorService.class, new myInstructorService());
        registerService(MajorService.class, new myMajorService());
        registerService(SemesterService.class, new mySemesterService());
        registerService(StudentService.class, new myStudentService());
        registerService(UserService.class, new myUserService());
    }
    @Override
    public List<String> getUIDs() {
        return List.of("12012925","12012825","12011425");
    }
}
