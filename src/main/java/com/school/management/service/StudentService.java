package com.school.management.service;

import java.util.List;
import java.util.Optional;

import com.school.management.dto.StudentDTO;
import com.school.management.model.Classes;
import com.school.management.model.Student;

public interface StudentService {
    StudentDTO GetStudent(Long id);

    StudentDTO UpdateProfile(StudentDTO student);

    List<StudentDTO> GetAllStudent();

    String DeleteStudent(Long id);

    StudentDTO AddStudent(StudentDTO student);

    // Student GiveAccessAccount(String email,Student student);
    // Long generateAccount();
    String ConfirmStudent(StudentDTO studentDTO);

    String upgradeClass(String className, String email);

    // huyen
    List<Classes> getAllClassesByStudentId(Long studentId);

    List<Student> getStudentsByClassId(Long classId);

    Student getStudentsById(Long studentId);
}
