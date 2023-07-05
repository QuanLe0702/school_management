package com.school.management.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.school.management.model.Lesson;

@Repository
public interface LessonRepository extends JpaRepository<Lesson, Long> {
}
