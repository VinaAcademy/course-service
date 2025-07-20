package vn.vinaacademy.course.repository;

import org.springframework.data.domain.Page;
import vn.vinaacademy.course.document.CourseDocument;
import vn.vinaacademy.course.dto.CourseSearchRequest;
import vn.vinaacademy.course.dto.InstructorCourseSearchRequest;

import java.util.UUID;

public interface CourseElasticRepositoryCustom {
    Page<CourseDocument> search(CourseSearchRequest req);
    Page<CourseDocument> search(InstructorCourseSearchRequest req, UUID instructorId);
}