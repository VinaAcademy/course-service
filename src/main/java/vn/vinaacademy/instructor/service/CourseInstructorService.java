package vn.vinaacademy.instructor.service;

import vn.vinaacademy.instructor.dto.CourseInstructorDto;
import vn.vinaacademy.instructor.dto.CourseInstructorDtoRequest;

public interface CourseInstructorService {
    CourseInstructorDto createCourseInstructor(CourseInstructorDtoRequest dto);
}
