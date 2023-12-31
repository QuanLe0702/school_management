package com.school.management.service;

import java.security.SecureRandom;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.school.management.dto.RoleDto;
import com.school.management.dto.TeacherDto;
import com.school.management.dto.UserDto;
import com.school.management.model.Classes;
import com.school.management.model.RefreshToken;
import com.school.management.model.Subject;
import com.school.management.model.Teacher;
import com.school.management.model.User;
import com.school.management.repository.ClassesRepository;
import com.school.management.repository.RefreshTokenRepository;
import com.school.management.repository.TeacherRepository;
import com.school.management.repository.UserRepository;

@Service
public class TeacherServiceImpl implements TeacherService {
    @Autowired
    private TeacherRepository teacherRepository;

    @Autowired
    private ClassesRepository classesRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private RefreshTokenRepository refreshTokenReposity;

    @Autowired
    private EmailService emailService;

    @Autowired
    private UserRepository userRepository;

    @Override
    public Teacher createTeacher(TeacherDto teacherDto) {
        // Long userId = teacherDto.getUserId();
        // User user = userRepository.findById(userId)
        // .orElseThrow(() -> new IllegalArgumentException("User not found with id: " +
        // userId));

        Teacher teacher = new Teacher();
        teacher.setName(teacherDto.getName());
        teacher.setGender(teacherDto.getGender());
        teacher.setDob(teacherDto.getDob());
        teacher.setEmail(teacherDto.getEmail());
        teacher.setAddress(teacherDto.getAddress());
        teacher.setPhone(teacherDto.getPhone());
        teacher.setIsActive(teacherDto.getIsActive());
                    char[] possibleCharacters = (new String(
                    "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789"))
                    .toCharArray();
            String randomStr = RandomStringUtils.random(6, 0, possibleCharacters.length - 1, false, false,
                    possibleCharacters, new SecureRandom());

            userService.signup(new UserDto(teacher.getEmail(), randomStr, new RoleDto("TEACHER")));
            User user = userRepository.findByEmail(teacher.getEmail()).get();
            teacher.setUser(user);
            try {
                emailService.sendUsernamePassword(user.getEmail(),randomStr);
            } catch (Exception e) {
                throw new  IllegalArgumentException(e.getMessage());
            }
        return teacherRepository.save(teacher);
    }

    @Override
    public Teacher updateTeacher(Long id, TeacherDto teacherDto) {
        Teacher exitingTeacher = teacherRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Teacher not found with id: " + id));
        User user = userRepository.findByEmail(exitingTeacher.getUser().getEmail()).get();
        if(user !=null){
            user.setEmail(teacherDto.getEmail());
            userRepository.save(user);
        }
        exitingTeacher.setName(teacherDto.getName());
        exitingTeacher.setGender(teacherDto.getGender());
        exitingTeacher.setDob(teacherDto.getDob());
        exitingTeacher.setEmail(teacherDto.getEmail());
        exitingTeacher.setAddress(teacherDto.getAddress());
        exitingTeacher.setPhone(teacherDto.getPhone());
        exitingTeacher.setIsActive(teacherDto.getIsActive());
        // exitingTeacher.setUser(user);

        return teacherRepository.save(exitingTeacher);
    }

    @Override
    public boolean deleteTeacher(Long id) {
        if (!teacherRepository.existsById(id)) {
            throw new TeacherNotFoundException("Teacher not found with id: " + id);
        }
        Optional<Teacher> teacher = teacherRepository.findById(id);
         User user = teacher.get().getUser();
        
        if (!teacher.isPresent()) {
            throw new TeacherNotFoundException("Teacher " + teacher.get().getEmail() + " cant be found");

        }
        List<Classes> classes = classesRepository.findAllByTeacherId(id);
            if(classes.size()>0){
                classes.forEach((oneclass)->{
                oneclass.setTeacher(null);
                classesRepository.save(oneclass);
            });
            }
        teacherRepository.deleteById(id);
        if(user != null) {
            List<RefreshToken> list= refreshTokenReposity.findByUser(user);
            if(list.size()>0){
                list.forEach((token)->{
                refreshTokenReposity.delete(token);
            });
            }
            userRepository.delete(user);

        }
        return true;
    }

    @Override
    public Teacher getTeacherById(Long id) {
        return teacherRepository.findById(id)
                .orElseThrow(() -> new TeacherNotFoundException("Không tìm thấy giáo viên với id: " + id));
    }

    @Override
    public List<Teacher> getAllTeacher() {
        return teacherRepository.findAll();
    }

    @Override
    public List<Teacher> getTeacherByName(String name) {
        return teacherRepository.findByNameContainingIgnoreCase(name);
    }

    public class TeacherNotFoundException extends RuntimeException {
        public TeacherNotFoundException(String message) {
            super(message);
        }
    }

    @Override
    public Teacher updateTeacherStatus(Long id) {
        Teacher existingTeacher = teacherRepository.findById(id)
                .orElseThrow(() -> new TeacherNotFoundException("Không tìm thấy với id: " + id));

        boolean isActive = existingTeacher.getIsActive();
        existingTeacher.setIsActive(!isActive);

        return teacherRepository.save(existingTeacher);
    }

    // huyen
    @Override
    public List<Subject> getAllSubjectsByTeacherId(Long teacherId) {
        Teacher teacher = teacherRepository.findById(teacherId)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy giáo viên"));

        return teacherRepository.findSubjectsByTeacherId(teacherId);
    }

    public Set<Teacher> getTeachersBySubjectId(Long subjectId) {
        // Lấy tất cả giáo viên của môn học dựa vào ID môn học
        return teacherRepository.findTeachersBySubjectId(subjectId);
    }
}
