package vn.vinaacademy.course.controller;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vn.vinaacademy.common.constant.AuthConstants;
import vn.vinaacademy.common.exception.ResourceNotFoundException;
import vn.vinaacademy.common.response.ApiResponse;
import vn.vinaacademy.common.security.SecurityContextHelper;
import vn.vinaacademy.common.security.annotation.HasAnyRole;
import vn.vinaacademy.course.dto.*;
import vn.vinaacademy.course.entity.Course;
import vn.vinaacademy.course.repository.CourseRepository;
import vn.vinaacademy.course.service.CourseService;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/courses")
@Slf4j
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
public class CourseController {
    private final CourseService courseService;
    private final CourseRepository courseRepository;
    private final SecurityContextHelper securityContextHelper;

    @HasAnyRole({AuthConstants.ADMIN_ROLE, AuthConstants.INSTRUCTOR_ROLE, AuthConstants.STAFF_ROLE})
    @PostMapping
    public ApiResponse<CourseDto> createCourse(@RequestBody @Valid CourseRequest request) {
        // Only ADMIN and INSTRUCTOR can create courses
        log.debug("Course creating " + request.getName());
        return ApiResponse.success(courseService.createCourse(request));
    }



    @HasAnyRole({AuthConstants.INSTRUCTOR_ROLE})
    @PutMapping("/slug/{slug}")
    public ApiResponse<CourseDto> updateCourse(@PathVariable String slug, @RequestBody @Valid CourseRequest request) {
        // Only INSTRUCTOR can update their courses
        log.debug("Course updated");
        return ApiResponse.success(courseService.updateCourse(slug, request));
    }

    @PutMapping("/statuschange")
    @HasAnyRole({AuthConstants.ADMIN_ROLE, AuthConstants.STAFF_ROLE})
    public ApiResponse<Boolean> updateStatusCourse(@RequestBody @Valid CourseStatusRequest courseStatusRequest) {
        Boolean update = courseService.updateStatusCourse(courseStatusRequest);
        log.debug("Update status course " + courseStatusRequest.getSlug() + " => " + courseStatusRequest.getStatus()
                + " - " + update);
        return ApiResponse.success(update);
    }

    @HasAnyRole({AuthConstants.ADMIN_ROLE, AuthConstants.INSTRUCTOR_ROLE})
    @DeleteMapping("/slug/{slug}")
    public ApiResponse<Void> deleteCourse(@PathVariable String slug) {
        // Only ADMIN can delete courses
        log.debug("Course deleted");
        courseService.deleteCourse(slug);
        return ApiResponse.success("Xóa khóa học thành công");
    }

    @GetMapping("/{id}")
    public ApiResponse<CourseDto> getCourseById(@PathVariable UUID id) {
        log.debug("Getting course information for id: {}", id);
        return ApiResponse.success(courseService.getCourseById(id));
    }

    @GetMapping("/slug/{slug}")
    public ApiResponse<CourseDetailsResponse> getCourseDetails(@PathVariable String slug) {
        log.debug("Getting detailed course information for slug: {}", slug);
        return ApiResponse.success(courseService.getCourse(slug));
    }

    @GetMapping("/{slug}/learning")
    @HasAnyRole({AuthConstants.STUDENT_ROLE})
    public ApiResponse<CourseDto> getCourseLearning(@PathVariable String slug) {
        log.debug("Getting course learning information for slug: {}", slug);
        return ApiResponse.success(courseService.getCourseLearning(slug));
    }

    /**
     * Lấy ID khóa học từ slug
     */
    @GetMapping("/id-by-slug/{slug}")
    public ResponseEntity<ApiResponse<Map<String, UUID>>> getCourseIdBySlug(@PathVariable String slug) {
        try {
            Course course = courseRepository.findBySlug(slug)
                    .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy khóa học với slug: " + slug));

            Map<String, UUID> response = new HashMap<>();
            response.put("id", course.getId());

            return ResponseEntity.ok(new ApiResponse<>("success", "Lấy ID khóa học thành công", response));
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponse<>("error", e.getMessage(), null));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>("error", "Lỗi khi lấy ID khóa học: " + e.getMessage(), null));
        }
    }

//    @GetMapping("/instructor/courses")
//    @HasAnyRole({AuthConstants.INSTRUCTOR_ROLE})
//    public ApiResponse<Page<CourseDto>> getInstructorCourses(
//            @RequestParam(defaultValue = "0") int page,
//            @RequestParam(defaultValue = "10") int size,
//            @RequestParam(defaultValue = "createdDate") String sortBy,
//            @RequestParam(defaultValue = "desc") String sortDirection) {
//
//        UUID currentUserId = securityContextHelper.getCurrentUserIdAsUUID();
//
//        log.debug("Lấy danh sách khóa học của giảng viên với params: page={}, size={}, sortBy={}, sortDirection={}",
//                page, size, sortBy, sortDirection);
//
//        Page<CourseDto> coursePage = courseService.getCoursesByInstructor(
//                currentUserId, page, size, sortBy, sortDirection);
//
//        log.debug("Tìm thấy {} khóa học của giảng viên {}",
//                coursePage.getTotalElements(), currentUserId);
//
//        log.debug("Lấy danh sách khóa học của giảng viên: {}", currentUserId);
//        return ApiResponse.success(coursePage);
//    }

//    @GetMapping("/instructor/search")
//    @HasAnyRole({AuthConstants.INSTRUCTOR_ROLE})
//    public ApiResponse<Page<CourseDto>> searchInstructorCourses(
//            @ModelAttribute CourseSearchRequest searchRequest,
//            @RequestParam(defaultValue = "0") int page,
//            @RequestParam(defaultValue = "10") int size,
//            @RequestParam(defaultValue = "createdDate") String sortBy,
//            @RequestParam(defaultValue = "desc") String sortDirection) {
//
//        UUID currentUserId = securityContextHelper.getCurrentUserIdAsUUID();
//
//        Page<CourseDto> coursePage = courseService.searchInstructorCourses(
//                currentUserId, searchRequest, page, size, sortBy, sortDirection);
//
//        log.debug("Tìm kiếm khóa học của giảng viên: {}", currentUserId);
//        return ApiResponse.success(coursePage);
//    }

    /**
     * API để chuyển trạng thái khóa học sang PENDING khi thêm bài giảng mới
     */
    @PutMapping("/submit-for-review/{courseId}")
    @HasAnyRole({AuthConstants.INSTRUCTOR_ROLE})
    public ApiResponse<Boolean> submitCourseForReview(@PathVariable UUID courseId) {
        courseService.submitCourseForReview(courseId);
        return ApiResponse.success(true);
    }
}
