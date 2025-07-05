package vn.vinaacademy.instructor.controller;

import vn.vinaacademy.common.response.ApiResponse;
import vn.vinaacademy.instructor.dto.InstructorInfoDto;
import vn.vinaacademy.instructor.service.InstructorService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import vn.vinaacademy.common.constant.AuthConstants;
import vn.vinaacademy.common.security.annotation.HasAnyRole;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/instructor")
@RequiredArgsConstructor
@Tag(name = "Instructor", description = "Instructor public APIs")
public class InstructorController {

    private final InstructorService instructorService;

    @GetMapping("/{instructorId}")
    public ApiResponse<InstructorInfoDto> getInstructorById(@PathVariable UUID instructorId) {
        InstructorInfoDto instructor = instructorService.getInstructorInfo(instructorId);
        return ApiResponse.success(instructor);
    }

    @PostMapping("/register")
    @HasAnyRole({AuthConstants.STUDENT_ROLE})
    @Operation(summary = "Đăng ký trở thành giảng viên", description = "Học viên đăng ký trở thành giảng viên")
    public ApiResponse<InstructorInfoDto> registerAsInstructor() {
        InstructorInfoDto instructor = instructorService.registerAsInstructor();
        return ApiResponse.success("Đăng ký trở thành giảng viên thành công", instructor);
    }
}