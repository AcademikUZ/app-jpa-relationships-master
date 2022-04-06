package uz.pdp.appjparelationships.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;
import uz.pdp.appjparelationships.entity.Address;
import uz.pdp.appjparelationships.entity.Group;
import uz.pdp.appjparelationships.entity.Student;
import uz.pdp.appjparelationships.entity.Subject;
import uz.pdp.appjparelationships.payload.StudentDto;
import uz.pdp.appjparelationships.repository.AddressRepository;
import uz.pdp.appjparelationships.repository.GroupRepository;
import uz.pdp.appjparelationships.repository.StudentRepository;
import uz.pdp.appjparelationships.repository.SubjectRepository;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/student")
public class StudentController {
    @Autowired
    StudentRepository studentRepository;

    @Autowired
    AddressRepository addressRepository;

    @Autowired
    SubjectRepository subjectRepository;

    @Autowired
    GroupRepository groupRepository;


    //1. VAZIRLIK
    @GetMapping("/forMinistry")
    public Page<Student> getStudentListForMinistry(@RequestParam int page) {
        //1-1=0     2-1=1    3-1=2    4-1=3
        //select * from student limit 10 offset (0*10)
        //select * from student limit 10 offset (1*10)
        //select * from student limit 10 offset (2*10)
        //select * from student limit 10 offset (3*10)
        Pageable pageable = PageRequest.of(page, 10);
        Page<Student> studentPage = studentRepository.findAll(pageable);
        return studentPage;
    }

    //2. UNIVERSITY
    @GetMapping("/forUniversity/{universityId}")
    public Page<Student> getStudentListForUniversity(@PathVariable Integer universityId,
                                                     @RequestParam int page) {
        Pageable pageable = PageRequest.of(page, 10);
        Page<Student> studentPage = studentRepository.findAllByGroup_Faculty_UniversityId(universityId, pageable);
        return studentPage;
    }

    //3. FACULTY DEKANAT
    @GetMapping("/forFoculty/{focultuid}")
    public Page<Student> getStudentListForFoculty(@PathVariable Integer focultuid,
                                                  @RequestParam int page) {

        Pageable pageable = PageRequest.of(page, 10);
        Page<Student> studentPage = studentRepository.findAllByGroup_FacultyId(focultuid, pageable);
        return studentPage;
    }


    //4. GROUP OWNER
    @GetMapping("/forGroup/{groupid}")
    public Page<Student> getStudentListForGroup(@PathVariable Integer groupid,
                                                @RequestParam int page) {

        Pageable pageable = PageRequest.of(page, 10);
        Page<Student> studentPage = studentRepository.findAllByGroupId(groupid, pageable);
        return studentPage;
    }

    //5. ID orqali olish
    @GetMapping("/onestudent/{id}")
    public Page<Student> getStudent(@PathVariable Integer id,
                                    @RequestParam int page) {

        Pageable pageable = PageRequest.of(page, 10);
        Page<Student> studentPage = studentRepository.findById(id, pageable);
        return studentPage;
    }

    @PostMapping("/addStudent")
    public String addStudent(@RequestBody StudentDto studentDto) {
        if (studentDto == null)
            return "Qo'shishga qiymat mavjud emas";
        Address address = new Address();
        address.setCity(studentDto.getCity());
        address.setDistrict(studentDto.getDistrict());
        address.setStreet(studentDto.getStreet());
        Address savedAddress = addressRepository.save(address);

        Group group = groupRepository.findById(studentDto.getGroupID()).get();

        Student student = new Student();
        student.setFirstName(studentDto.getFirstName());
        student.setLastName(studentDto.getLastName());
        student.setAddress(savedAddress);
        student.setGroup(group);
        studentRepository.save(student);

        return "student saqlandi";
    }

    @PostMapping("/addStudentSubject/{studentID}")
    public String addStudent(@RequestBody StudentDto studentDto, @PathVariable Integer studentID) {
        if (studentDto == null)
            return "Qo'shishga qiymat mavjud emas";

        Optional<Student> byIdStudent = studentRepository.findById(studentID);
        if(!byIdStudent.isPresent())
            return "Bunday idlik student yo'q";
        Student student = byIdStudent.get();
        List<Subject> subjectList = student.getSubjects();
        Subject subject = subjectRepository.findById(studentDto.getSubjectsID()).get();
        subjectList.add(subject);
        student.setSubjects(subjectList);
        studentRepository.save(student);

        return "student subjecti saqlandi";
    }


    @PutMapping("/editStudent/{studentID}")
    public String editStudent(@RequestBody StudentDto studentDto,
                              @PathVariable Integer studentID) {

        if (studentDto == null)
            return "Qo'shishga qiymat mavjud emas";

        Optional<Student> byIdStudent = studentRepository.findById(studentID);
        if(!byIdStudent.isPresent())
            return "Bunday idlik student yo'q";
        Student student = byIdStudent.get();
        Address address = addressRepository.findById(student.getAddress().getId()).get();
        address.setCity(studentDto.getCity());
        address.setDistrict(studentDto.getDistrict());
        address.setStreet(studentDto.getStreet());
        Address savedAddress = addressRepository.save(address);
        Group group = groupRepository.findById(student.getGroup().getId()).get();

        student.setFirstName(studentDto.getFirstName());
        student.setLastName(studentDto.getLastName());
        student.setAddress(savedAddress);
        student.setGroup(group);
        studentRepository.save(student);
        return "student o'zgartirildi";
    }

    @DeleteMapping("/deleteStudentSubject/{studentID}")
    public String deleteStudentSubject(@RequestBody StudentDto studentDto, @PathVariable Integer studentID) {
        if (studentDto == null)
            return "Qo'shishga qiymat mavjud emas";
        Optional<Student> byIdStudent = studentRepository.findById(studentID);
        if(!byIdStudent.isPresent())
            return "Bunday idlik student yo'q";
        Student student = byIdStudent.get();
        List<Subject> subjectList = student.getSubjects();
        Subject subject = subjectRepository.findById(studentDto.getSubjectsID()).get();
        subjectList.remove(subject);
        student.setSubjects(subjectList);
        studentRepository.save(student);
        return "studentdan subject o'chirildi";
    }

    @DeleteMapping("/deleteStudent/{studentID}")
    public String deleteStudent(@PathVariable Integer studentID) {
        Optional<Student> byIdStudent = studentRepository.findById(studentID);
        if(!byIdStudent.isPresent())
            return "Bunday idlik student yo'q";
        Student student = byIdStudent.get();
        studentRepository.delete(student);
        return "student o'chirildi";
    }



}
