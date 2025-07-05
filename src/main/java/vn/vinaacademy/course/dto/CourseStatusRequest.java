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
public class CourseStatusRequest {
    
    private String slug;
    
    private Course.CourseStatus status;

   
}