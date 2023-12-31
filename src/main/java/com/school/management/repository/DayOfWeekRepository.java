package com.school.management.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.school.management.model.DayOfWeek;

@Repository
public interface DayOfWeekRepository extends JpaRepository<DayOfWeek, Long> {
	DayOfWeek findByName(String name);
}
