package vn.vinaacademy.course.dto;

import vn.vinaacademy.course.enums.CourseLevel;
import vn.vinaacademy.course.enums.CourseStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CourseSearchRequest {
    private String keyword;
    private String categorySlug;
    private CourseLevel level;
    private String language;
    private BigDecimal minPrice;
    private BigDecimal maxPrice;
    private Double minRating;
    private CourseStatus status;
}