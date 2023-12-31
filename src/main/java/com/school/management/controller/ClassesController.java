package com.school.management.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.school.management.dto.ClassesDto;
import com.school.management.model.Classes;
import com.school.management.service.ClassesService;
import com.school.management.service.ClassesServiceImpl;
import com.school.management.service.ClassesServiceImpl.ClassesNotFoundException;

@RestController
@RequestMapping("/api/classes")
public class ClassesController {
	@Autowired
	private ClassesService classesService;

	// duy
	@Autowired
	private ClassesServiceImpl classesServiceImpl;

	@PostMapping("/create")
	public ResponseEntity<?> createClasses(@RequestBody ClassesDto classesDto) {
		try {
			Classes createdClasses = classesService.createClasses(classesDto);
			return ResponseEntity.ok().body(createdClasses);
		} catch (IllegalArgumentException e) {
			return ResponseEntity.badRequest().body(e.getMessage());
		}
	}

	@GetMapping("/findById/{id}")
	public ResponseEntity<?> getClassesById(@PathVariable Long id) {
		try {
			Classes classes = classesService.getClassesById(id);
			return ResponseEntity.ok(classes);
		} catch (ClassesServiceImpl.ClassesNotFoundException e) {
			return ResponseEntity.badRequest().body(e.getMessage());
		}
	}

	@PutMapping("/update/{id}")
	public ResponseEntity<?> updateClasses(@PathVariable Long id,
			@RequestBody ClassesDto classesDto) {
		try {
			Classes updatedClasses = classesService.updateClasses(id, classesDto);
			return ResponseEntity.ok(updatedClasses);
		} catch (ClassesServiceImpl.ClassesNotFoundException e) {
			return ResponseEntity.badRequest().body(e.getMessage());
		} catch (IllegalArgumentException e) {
			return ResponseEntity.badRequest().body(e.getMessage());
		}
	}

	@DeleteMapping("/delete/{id}")
	public ResponseEntity<?> deleteClasses(@PathVariable Long id) {
		try {
			classesService.deleteClasses(id);
			return ResponseEntity.ok().build();
		} catch (ClassesServiceImpl.ClassesNotFoundException e) {
			return ResponseEntity.badRequest().body(e.getMessage());
		}
	}

	@GetMapping
	public ResponseEntity<List<Classes>> getAllClasses() {
		List<Classes> classes = classesService.getAllClasses();
		return ResponseEntity.ok(classes);
	}

	@GetMapping("/findByName/{name}")
	public ResponseEntity<List<Classes>> searchClassesByName(@PathVariable(value = "name") String name) {
		List<Classes> classes = classesService.getClassesByName(name);
		return ResponseEntity.ok(classes);
	}

	// @PutMapping("/isActive/{id}")
	// public ResponseEntity<?> updateIsActive(@PathVariable("id") Long id) {
	// try {
	// Classes updatedClasses = classesService.updateClassesStatus(id);
	// return ResponseEntity.ok(updatedClasses);
	// } catch (ClassesNotFoundException e) {
	// return ResponseEntity.badRequest().body(e.getMessage());
	// } catch (Exception e) {
	// return ResponseEntity.badRequest().body(e.getMessage());
	// }
	// }

	// huyen
	@GetMapping("/teachers/{teacherId}")
	public List<Classes> getClassesByTeacherId(@PathVariable Long teacherId) {
		return classesService.getClassesByTeacherId(teacherId);
	}

	// duy
	@GetMapping("/all")
	public ResponseEntity<List<?>> getAllClasses2() {
		List<Classes> classes = classesServiceImpl.getAllClasses();
		return ResponseEntity.ok(classes);
	}

	// huyen
	@PostMapping("/{classId}/students/{studentId}")
	public ResponseEntity<String> addStudentToClass(
			@PathVariable Long classId,
			@PathVariable Long studentId) {
		try {
			classesService.addStudentToClass(classId, studentId);
			return ResponseEntity.ok("Thêm học sinh vào lớp học thành công.");
		} catch (IllegalArgumentException e) {
			return ResponseEntity.badRequest().body(e.getMessage());
		} catch (IllegalStateException e) {
			return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Đã xảy ra lỗi không xác định.");
		}
	}
}
