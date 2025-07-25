package vn.vinaacademy.course.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import vn.vinaacademy.common.dto.BaseDto;
import vn.vinaacademy.course.entity.Course;

import java.math.BigDecimal;
import java.util.UUID;

@EqualsAndHashCode(callSuper = false)
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class CourseDto extends BaseDto {

    private UUID id;

    private String image;

    private String name;

    private String description;

    private String slug;

    private BigDecimal price;

     private Course.CourseLevel level;

    private Course.CourseStatus status;

    private String language;

    private String categoryName;

    private double rating;

    private long totalRating;

    private long totalStudent;

    private long totalSection;

    private long totalLesson;

//    private EnrollmentProgressDto progress;
//
//    private List<SectionDto> sections;

}
