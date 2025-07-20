package vn.vinaacademy.course.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import vn.vinaacademy.course.entity.Course;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InstructorCourseSearchRequest {
    // Search by course title
    private String title;
    
    // Filter by status
    private Course.CourseStatus status;
    
    // Pagination
    private int page = 0;
    private int size = 10;
    
    // Sorting
    private String sortBy = "updatedDate";
    private String sortDirection = "desc";
}