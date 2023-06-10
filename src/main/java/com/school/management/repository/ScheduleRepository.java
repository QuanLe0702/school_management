package com.school.management.repository;

import java.time.LocalDate;

import org.springframework.data.jpa.repository.JpaRepository;

import com.school.management.model.Schedule;

public interface ScheduleRepository extends JpaRepository<Schedule, Long> {

	boolean existsBySemesterAndClassesId(Integer semester, Long classesId);

	boolean existsByClassesIdAndStartDateLessThanEqualAndEndDateGreaterThanEqualAndStartDateGreaterThanAndEndDateLessThan(
			Long classesId, LocalDate startDate, LocalDate endDate, LocalDate newStartDate, LocalDate newEndDate);
}
