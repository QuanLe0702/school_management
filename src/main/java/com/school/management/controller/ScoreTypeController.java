package com.school.management.controller;

import com.school.management.model.ScoreType;
import com.school.management.service.ScoreTypeServiceImpl;
import com.school.management.service.ScoreTypeServiceImpl.ScoreTypeNotFoundException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/score-types")
public class ScoreTypeController {

	@Autowired
	private ScoreTypeServiceImpl scoreTypeServiceImpl;

	@GetMapping
	public ResponseEntity<List<ScoreType>> getScoreTypes(@RequestParam(required = false) String name) {
		List<ScoreType> scoreTypes = scoreTypeServiceImpl.getAllScoreTypes(name);
		return ResponseEntity.ok(scoreTypes);
	}

	@GetMapping("/{id}")
	public ResponseEntity<?> getScoreTypeById(@PathVariable("id") Long id) {
		try {
			ScoreType scoreType = scoreTypeServiceImpl.getScoreTypeById(id);
			return ResponseEntity.ok(scoreType);
		} catch (ScoreTypeNotFoundException e) {
			return ResponseEntity.badRequest().body(e.getMessage());
		}
	}

	@PostMapping
	public ResponseEntity<?> createScoreType(@RequestBody ScoreType scoreType) {
		try {
			return ResponseEntity.ok(scoreTypeServiceImpl.createScoreType(scoreType));
		} catch (IllegalArgumentException e) {
			return ResponseEntity.badRequest().body(e.getMessage());
		}
	}

	@PutMapping("/{id}")
	public ResponseEntity<?> updateScoreType(@PathVariable("id") Long id,
			@RequestBody ScoreType scoreType) {
		try {
			ScoreType updatedScoreType = scoreTypeServiceImpl.updateScoreType(id, scoreType);
			return ResponseEntity.ok(updatedScoreType);
		} catch (IllegalArgumentException e) {
			return ResponseEntity.badRequest().body(e.getMessage());
		} catch (ScoreTypeNotFoundException e) {
			return ResponseEntity.badRequest().body(e.getMessage());
		}
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<?> deleteScoreType(@PathVariable("id") Long id) {
		try {
			scoreTypeServiceImpl.deleteScoreType(id);
			return ResponseEntity.ok().build();
		} catch (ScoreTypeNotFoundException e) {
			return ResponseEntity.badRequest().body(e.getMessage());
		}
	}

}