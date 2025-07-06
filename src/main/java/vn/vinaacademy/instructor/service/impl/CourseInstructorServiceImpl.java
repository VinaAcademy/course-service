package vn.vinaacademy.instructor.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import vn.vinaacademy.client.UserClient;
import vn.vinaacademy.common.exception.BadRequestException;
import vn.vinaacademy.common.security.SecurityContextHelper;
import vn.vinaacademy.course.entity.Course;
import vn.vinaacademy.course.repository.CourseRepository;
import vn.vinaacademy.instructor.CourseInstructor;
import vn.vinaacademy.instructor.dto.CourseInstructorDto;
import vn.vinaacademy.instructor.dto.CourseInstructorDtoRequest;
import vn.vinaacademy.instructor.mapper.CourseInstructorMapper;
import vn.vinaacademy.instructor.repository.CourseInstructorRepository;
import vn.vinaacademy.instructor.service.CourseInstructorService;

import java.util.UUID;

import static vn.vinaacademy.common.constant.AuthConstants.ADMIN_ROLE;
import static vn.vinaacademy.common.constant.AuthConstants.STAFF_ROLE;

@Service
@RequiredArgsConstructor
public class CourseInstructorServiceImpl implements CourseInstructorService {

    private final CourseInstructorRepository instructorRepository;
    private final CourseRepository courseRepository;
    private final UserClient userClient;
    @Autowired
    private SecurityContextHelper securityContextHelper;

    @Override
    public CourseInstructorDto createCourseInstructor(CourseInstructorDtoRequest request) {
        Course course = courseRepository.findById(request.getCourseId())
                .orElseThrow(() -> BadRequestException.message("Không tìm thấy khóa học đó"));

        UUID currentUserId = securityContextHelper.getCurrentUserIdAsUUID();
        boolean isOwner = instructorRepository.existsByCourseIdAndInstructorId(course.getId(), currentUserId);

        if (!isOwner && !securityContextHelper.hasAnyRole(ADMIN_ROLE, STAFF_ROLE)) {
            throw BadRequestException.message("Bạn không phải là người sở hữu khóa học này");
        }

        CourseInstructor instructor = CourseInstructor.builder()
                .course(course)
                .instructorId(request.getUserId())
                .build();

        instructorRepository.save(instructor);
        return CourseInstructorMapper.INSTANCE.toDto(instructor);
    }
}
