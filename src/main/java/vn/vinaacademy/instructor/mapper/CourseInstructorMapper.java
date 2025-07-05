package vn.vinaacademy.instructor.mapper;

import vn.vinaacademy.instructor.CourseInstructor;
import vn.vinaacademy.instructor.dto.CourseInstructorDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface CourseInstructorMapper {

    CourseInstructorMapper INSTANCE = Mappers.getMapper(CourseInstructorMapper.class);
    
    @Mapping(source = "instructorId", target = "userId")
    @Mapping(source = "course.id", target = "courseId")
    @Mapping(source = "isOwner", target = "isOwner")
    CourseInstructorDto toDto (CourseInstructor courseInstructor);
    
    
}
