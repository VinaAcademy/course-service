package vn.vinaacademy.course.service;

import vn.vinaacademy.course.dto.CourseDetailsResponse;
import vn.vinaacademy.course.dto.CourseDto;
import vn.vinaacademy.course.dto.CourseRequest;
import vn.vinaacademy.course.dto.CourseStatusRequest;

import java.util.UUID;

public interface CourseService {

    CourseDto createCourse(CourseRequest request);

    CourseDto updateCourse(String slug, CourseRequest request);

    void submitCourseForReview(UUID courseId);

    Boolean updateStatusCourse(CourseStatusRequest courseStatusRequest);

    void deleteCourse(String slug);

    CourseDetailsResponse getCourse(String slug);

    CourseDto getCourseById(UUID id);

    CourseDto getCourseLearning(String slug);

}
