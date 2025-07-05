package vn.vinaacademy.instructor.controller;

import vn.vinaacademy.common.response.ApiResponse;
import vn.vinaacademy.instructor.dto.CourseInstructorDto;
import vn.vinaacademy.instructor.dto.CourseInstructorDtoRequest;
import vn.vinaacademy.instructor.service.CourseInstructorService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import vn.vinaacademy.common.constant.AuthConstants;
import vn.vinaacademy.common.security.annotation.HasAnyRole;

@RestController
@RequestMapping("/api/v1/courseinstructor")
@RequiredArgsConstructor
@Slf4j
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "InstructorCourse", description = "Quản lý instructor")
public class CourseInstructorController {

    private final CourseInstructorService service;
    
    @HasAnyRole({AuthConstants.INSTRUCTOR_ROLE, AuthConstants.STAFF_ROLE, AuthConstants.ADMIN_ROLE})
    @PostMapping
    public ApiResponse<CourseInstructorDto> create(@RequestBody @Valid CourseInstructorDtoRequest req) {
    	log.debug("tạo instructor cho course "+req.getCourseId());
        CourseInstructorDto dto = service.createCourseInstructor(req);
        
        return ApiResponse.success(dto);
    }
}
