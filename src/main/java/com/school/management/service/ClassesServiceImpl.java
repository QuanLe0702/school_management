package com.school.management.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.school.management.dto.ClassesDto;
import com.school.management.model.AcademicYear;
import com.school.management.model.Classes;
import com.school.management.model.Student;
import com.school.management.model.Teacher;
import com.school.management.repository.AcademicYearRespository;
import com.school.management.repository.ClassesRepository;
import com.school.management.repository.StudentRepository;
import com.school.management.repository.TeacherRepository;

@Service
public class ClassesServiceImpl implements ClassesService {
    @Autowired
    private ClassesRepository classesRepository;
    @Autowired
    private TeacherRepository teacherRepository;
    @Autowired
    private AcademicYearRespository academicYearRepository;

    @Autowired
    private StudentRepository studentRepository;

    @Override
    public Classes createClasses(ClassesDto classesDto) {
        Long teacherId = classesDto.getTeacherId();
        Long academicYearId = classesDto.getAcademicYearId();

        Teacher teacher = teacherRepository.findById(teacherId)
                .orElseThrow(() -> new IllegalArgumentException("Teacher not found with id: " + teacherId));
        AcademicYear academicYear = academicYearRepository.findById(academicYearId)
                .orElseThrow(() -> new IllegalArgumentException("AcademicYear not found with id: " + academicYearId));

        Classes classes = new Classes();
        classes.setName(classesDto.getName());
        classes.setDescription(classesDto.getDescription());
        classes.setGrade(classesDto.getGrade());
        classes.setLimitStudent(classesDto.getLimitStudent());
        classes.setTeacher(teacher);
        classes.setAcademicYear(academicYear);

        return classesRepository.save(classes);
    }

    @Override
    public Classes getClassesById(Long id) {
        return classesRepository.findById(id)
                .orElseThrow(() -> new ClassesNotFoundException("Class not found with id: " + id));
    }

    @Override
    public Classes updateClasses(Long id, ClassesDto classesDto) {
        Long teacherId = classesDto.getTeacherId();
        Long academicYearId = classesDto.getAcademicYearId();
        Classes exitingClasses = classesRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Class not found with id: " + id));
        Teacher teacher = teacherRepository.findById(teacherId)
                .orElseThrow(() -> new IllegalArgumentException("Teacher not found with id: " + teacherId));
        AcademicYear academicYear = academicYearRepository.findById(academicYearId)
                .orElseThrow(() -> new IllegalArgumentException("AcademicYear not found with id: " + academicYearId));
        exitingClasses.setName(classesDto.getName());
        exitingClasses.setDescription(classesDto.getDescription());
        exitingClasses.setGrade(classesDto.getGrade());
        exitingClasses.setLimitStudent(classesDto.getLimitStudent());
        exitingClasses.setTeacher(teacher);
        exitingClasses.setAcademicYear(academicYear);

        return classesRepository.save(exitingClasses);
    }

    @Override
    public boolean deleteClasses(Long id) {
        if (!classesRepository.existsById(id)) {
            throw new ClassesNotFoundException("Metric not found with id: " + id);
        }
        classesRepository.deleteById(id);
        return true;
    }

    @Override
    public List<Classes> getAllClasses() {
        return classesRepository.findAll();
    }

    @Override
    public List<Classes> getClassesByName(String name) {
        return classesRepository.findByNameContainingIgnoreCase(name);
    }

    public class ClassesNotFoundException extends RuntimeException {
        public ClassesNotFoundException(String message) {
            super(message);
        }
    }

    // huyen
    @Override
    public List<Classes> getClassesByTeacherId(Long teacherId) {
        return classesRepository.findAllByTeacherId(teacherId);
    }

    // @Override
    // public Classes updateClassesStatus(Long id) {
    // Classes existingClasses = classesRepository.findById(id)
    // .orElseThrow(() -> new ClassesNotFoundException("Không tìm thấy với id: " +
    // id));

    // boolean isActive = existingClasses.getIsActive();
    // existingClasses.setIsActive(!isActive);

    // return classesRepository.save(existingClasses);
    // }

    @Override
    public void addStudentToClass(Long classId, Long studentId) {
        // Kiểm tra xem lớp học có tồn tại không
        Classes classes = classesRepository.findById(classId)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy lớp học với ID: " + classId));

        // Kiểm tra xem học sinh có tồn tại không
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy học sinh với ID: " + studentId));

        // Kiểm tra xem lớp học đã đạt tới giới hạn số học sinh chưa
        if (classes.getStudents().size() >= classes.getLimitStudent()) {
            throw new IllegalStateException("Lớp học đã đạt tới giới hạn số học sinh.");
        }

        // Kiểm tra xem học sinh đã tồn tại trong lớp học hay chưa
        if (classes.getStudents().contains(student)) {
            throw new IllegalArgumentException("Học sinh đã tồn tại trong lớp học.");
        }

        // Thêm học sinh vào lớp học
        try {
            classes.getStudents().add(student);
            student.getClassName().add(classes);
            studentRepository.save(student);
            System.out.println("Đã lưu thông tin lớp học thành công.");
        } catch (Exception e) {
            // Xử lý ngoại lệ khi lưu dữ liệu
            System.err.println("Lỗi khi lưu thông tin lớp học: " + e.getMessage());
        }

        System.out.println("Đã thêm học sinh " + student.getName() + " vào lớp học " + classes.getName());
    }
}
