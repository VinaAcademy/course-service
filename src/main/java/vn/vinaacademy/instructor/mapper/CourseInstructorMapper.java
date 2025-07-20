package vn.vinaacademy.instructor.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;
import vn.vinaacademy.instructor.entity.CourseInstructor;
import vn.vinaacademy.instructor.dto.CourseInstructorDto;

@Mapper(componentModel = "spring")
public interface CourseInstructorMapper {

    CourseInstructorMapper INSTANCE = Mappers.getMapper(CourseInstructorMapper.class);
    
    @Mapping(source = "instructorId", target = "userId")
    @Mapping(source = "course.id", target = "courseId")
    @Mapping(source = "isOwner", target = "isOwner")
    CourseInstructorDto toDto (CourseInstructor courseInstructor);
    
    
}
