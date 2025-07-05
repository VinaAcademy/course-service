package vn.vinaacademy.course.dto;


import vn.vinaacademy.course.enums.CourseStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CourseStatusRequest {
    
    private String slug;
    
    private CourseStatus status;

   
}