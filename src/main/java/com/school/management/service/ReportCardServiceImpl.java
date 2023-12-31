package com.school.management.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.school.management.dto.ReportCardDto;
import com.school.management.model.AcademicYear;
import com.school.management.model.ReportCard;
import com.school.management.model.Student;
import com.school.management.repository.AcademicYearRespository;
import com.school.management.repository.ReportCardRepository;
import com.school.management.repository.StudentRepository;

@Service
public class ReportCardServiceImpl implements ReportCardService{

    @Autowired 
    private ReportCardRepository reportCardRepository;
    @Autowired
    private StudentRepository studentRepository;
    @Autowired
    private AcademicYearRespository academicYearRespository;

    @Override
    public ReportCard createReportCard(ReportCardDto reportCardDto) {
        Long studentId = reportCardDto.getStudentId();
        Long academicYearId = reportCardDto.getAcademicYearId();

        Student student = studentRepository.findById(studentId)
            .orElseThrow(() -> new IllegalArgumentException("Student not found with id: " + studentId));
        AcademicYear academicYear = academicYearRespository.findById(academicYearId)
            .orElseThrow(() -> new IllegalArgumentException("Academic Year not found with id: " + academicYearId));
        
        ReportCard reportCard = new ReportCard();
        reportCard.setStudent(student);
        reportCard.setViolate(reportCardDto.getViolate());
        reportCard.setDescription(reportCardDto.getDescription());
        reportCard.setDate(reportCardDto.getDate());
        reportCard.setAcademicYear(academicYear);

        return reportCardRepository.save(reportCard);
    }

    @Override
    public ReportCard updateReportCard(Long id, ReportCardDto reportCardDto) {
        ReportCard existingReportCard = reportCardRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Report Card not found with id: " + id));
        existingReportCard.setViolate(reportCardDto.getViolate());
        existingReportCard.setDescription(reportCardDto.getDescription());
        existingReportCard.setDate(reportCardDto.getDate());

        return reportCardRepository.save(existingReportCard);
    }

    @Override
    public ReportCard getReportCardById(Long id) {
        return reportCardRepository.findById(id)
				.orElseThrow(() -> new IllegalArgumentException("Report Card not found with id: " + id));
    }

    @Override
    public List<ReportCard> getReportCardByStudentId(Long studentId) {
        return reportCardRepository.findByStudentId(studentId);
    }

    @Override
    public void deleteReportCard(Long id) {
        if (!reportCardRepository.existsById(id)) {
			throw new IllegalArgumentException("Report Card not found with id: " + id);
		}
		reportCardRepository.deleteById(id);
    }

    @Override
    public List<ReportCard> getReportCard() {
        return reportCardRepository.findAll();
    }

    public class ReportCardNotFoundException extends RuntimeException {
        public ReportCardNotFoundException(String message) {
            super(message);
        }
    }

    @Override
    public List<ReportCard> searchReportCardsByViolate(String violate) {
        return reportCardRepository.findByViolateContainingIgnoreCase(violate);
    }

    @Override
    public List<ReportCard> searchReportCards(String violate) {
        if (violate == null || violate.trim().isEmpty()) {
			return getReportCard();
		} else {
			return searchReportCardsByViolate(violate);
		}
    }   
}
