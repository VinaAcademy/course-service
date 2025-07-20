package vn.vinaacademy.course.service;

import org.springframework.data.domain.Page;
import vn.vinaacademy.course.document.CourseDocument;
import vn.vinaacademy.course.dto.CourseSearchRequest;
import vn.vinaacademy.course.dto.InstructorCourseSearchRequest;

public interface CourseSearchService {

    Page<CourseDocument> searchPublishedCourses(CourseSearchRequest searchRequest);

    Page<CourseDocument> searchInstructorCourses(InstructorCourseSearchRequest searchRequest);
}
