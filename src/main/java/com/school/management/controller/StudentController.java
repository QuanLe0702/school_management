package com.school.management.controller;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.school.management.dto.StudentDTO;
import com.school.management.model.Classes;
import com.school.management.model.Score;
import com.school.management.model.Student;
import com.school.management.repository.StudentRepository;
import com.school.management.service.StudentService;
import com.school.management.service.StudentServiceImpl;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PathVariable;

@RestController
@RequestMapping("/api/student")
public class StudentController {
    @Autowired
    private StudentService studentService;

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private StudentServiceImpl studentServiceImpl;

    @GetMapping("/all")
    public ResponseEntity<List<StudentDTO>> getAllStudent() {
        return ResponseEntity.ok(studentService.GetAllStudent());
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getStudent(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(studentService.GetStudent(id));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/create")
    public ResponseEntity<?> addStudent(@RequestBody StudentDTO entity) {
        try {
            return ResponseEntity.ok(studentService.AddStudent(entity));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(studentService.DeleteStudent(id));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("/updateStudent/{id}")
    public ResponseEntity<?> updateStudent(@PathVariable String id, @RequestBody StudentDTO entity) {
        try {
            return ResponseEntity.ok(studentService.UpdateProfile(entity));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // @PostMapping("/giveAccessAccount")
    // public ResponseEntity<?> giveAccessAccount() {
    // try {
    // return ResponseEntity.ok(studentService.generateAccount());
    // } catch (Exception e) {
    // return ResponseEntity.badRequest().body(e.getMessage());
    // }
    // }

    @PostMapping("/import")
    public ResponseEntity<?> mapReapExcelDatatoDB(@RequestParam("file") MultipartFile reapExcelDataFile)
            throws IOException {

        try (// List<StudentDTO> tempStudentList = new ArrayList<StudentDTO>();
                XSSFWorkbook workbook = new XSSFWorkbook(reapExcelDataFile.getInputStream())) {
            XSSFSheet worksheet = workbook.getSheetAt(0);

            for (int i = 1; i < worksheet.getPhysicalNumberOfRows(); i++) {
                XSSFRow row = worksheet.getRow(i);
                StudentDTO tempStudent = new StudentDTO();

                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("d/M/yyyy");
                String date = row.getCell(2).toString();
                LocalDate localDate = LocalDate.parse(date, formatter);
                tempStudent.setAddress(row.getCell(4).toString()).setClassName(null)
                        .setDob(localDate).setEmail(row.getCell(3).toString()).setGender(row.getCell(1).toString())
                        .setImage("student.png").setName(row.getCell(0).toString()).setPhone(row.getCell(5).toString())
                        .setStatus("pending");
                Student existStudent = studentRepository.findByEmail(tempStudent.getEmail());

                if (existStudent != null) {
                    // throw new StudentException("Student already exists,
                    // "+existStudent.getName()+" "+existStudent.getEmail());
                    continue;
                }
                // tempStudent.setId((int) row.getCell(0).getNumericCellValue());
                // tempStudent.setContent(row.getCell(1).getStringCellValue());
                // tempStudentList.add(tempStudent);
                studentService.AddStudent(tempStudent);
            }
            return ResponseEntity.ok("Student list added successfully");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Student list added failed, " + e.getMessage());
        }
    }

    @PostMapping("/confirm")
    public ResponseEntity<?> confirmStudent(@RequestBody StudentDTO studentDTO) {
        // try {
        return ResponseEntity.ok(studentService.ConfirmStudent(studentDTO));
        // } catch (Exception e) {
        // return ResponseEntity.badRequest().body(e.getMessage());
        // }
    }

    @PostMapping("/upgrade")
    public ResponseEntity<?> upgradeClass(@RequestParam("email") String email,
            @RequestParam("classname") String classname) {
        try {
            return ResponseEntity.ok(studentService.upgradeClass(classname, email));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // huyen update
    @GetMapping("/{classId}/students")
    public List<Student> getStudentsByClassId(@PathVariable Long classId) {
        return studentService.getStudentsByClassId(classId);
    }

    // huyen update
    @GetMapping("/{studentId}/classes")
    public ResponseEntity<List<Classes>> getAllClassesByStudentId(@PathVariable Long studentId) {
        List<Classes> classes = studentService.getAllClassesByStudentId(studentId);
        return ResponseEntity.ok(classes);
    }

    // huyen

    @GetMapping("/studentId/{studentId}")
    public ResponseEntity<?> getScoreById(@PathVariable("studentId") Long studentId) {
        try {
            Student student = studentService.getStudentsById(studentId);
            return ResponseEntity.ok(student);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // duy
    @GetMapping("/allStudent")
    public ResponseEntity<List<?>> getAllStudents() {
        List<Student> students = studentServiceImpl.getAllStudents();
        return ResponseEntity.ok(students);
    }

}
