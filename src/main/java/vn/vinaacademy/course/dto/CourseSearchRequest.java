package vn.vinaacademy.course.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import vn.vinaacademy.course.entity.Course;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CourseSearchRequest {
    // Search keyword
    private String keyword;
    
    // Filter by category
    private String categorySlug;
    
    // Filter by level
    private Course.CourseLevel level;
    
    // Filter by language
    private String language;
    
    // Filter by price range
    private BigDecimal minPrice;
    private BigDecimal maxPrice;
    
    // Filter by rating
    private Double minRating;
    
    // Filter by status (for admin/instructor)
    private Course.CourseStatus status;
    
    // Pagination
    private int page = 0;
    private int size = 10;
    
    // Sorting
    private String sortBy = "createdDate";
    private String sortDirection = "desc";
}