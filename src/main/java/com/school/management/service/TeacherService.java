package com.school.management.service;

import java.util.List;

import com.school.management.dto.TeacherDto;
import com.school.management.model.Teacher;

public interface TeacherService {
    Teacher createTeacher(TeacherDto teacherDto);

    Teacher updateTeacher(Long id, TeacherDto teacherDto);

    boolean deleteTeacher(Long id);

    Teacher getTeacherById(Long id);

    List<Teacher> getAllTeacher();

    List<Teacher> getTeacherByName(String name);
}
