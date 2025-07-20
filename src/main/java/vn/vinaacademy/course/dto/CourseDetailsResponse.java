package vn.vinaacademy.course.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import vn.vinaacademy.client.dto.UserDto;
import vn.vinaacademy.common.dto.BaseDto;
import vn.vinaacademy.course.entity.Course;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@EqualsAndHashCode(callSuper = false)
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class CourseDetailsResponse extends BaseDto {
    private UUID id;
    private String image;
    private String name;
    private String description;
    private String slug;
    private BigDecimal price;
     private Course.CourseLevel level;
    private Course.CourseStatus status;
    private String language;
    private String categorySlug;
    private String categoryName;
    private double rating;
    private long totalRating;
    private long totalStudent;
    private long totalSection;
    private long totalLesson;
    
    // Additional fields for detailed view
    private List<UserDto> instructors = new ArrayList<>();
    private UserDto ownerInstructor;
//    private List<SectionDto> sections = new ArrayList<>();
//    private List<CourseReviewDto> reviews = new ArrayList<>();
}
