package vn.vinaacademy.course.service.impl;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.vinaacademy.category.entity.Category;
import vn.vinaacademy.category.repository.CategoryRepository;
import vn.vinaacademy.client.service.UserService;
import vn.vinaacademy.common.constant.AppConstants;
import vn.vinaacademy.common.constant.AuthConstants;
import vn.vinaacademy.common.exception.BadRequestException;
import vn.vinaacademy.common.exception.ResourceNotFoundException;
import vn.vinaacademy.common.helpers.SlugGeneratorHelper;
import vn.vinaacademy.common.security.SecurityContextHelper;
import vn.vinaacademy.common.utils.SlugUtils;
import vn.vinaacademy.course.dto.CourseDetailsResponse;
import vn.vinaacademy.course.dto.CourseDto;
import vn.vinaacademy.course.dto.CourseRequest;
import vn.vinaacademy.course.dto.CourseStatusRequest;
import vn.vinaacademy.course.entity.Course;
import vn.vinaacademy.course.mapper.CourseMapper;
import vn.vinaacademy.course.repository.CourseRepository;
import vn.vinaacademy.course.service.CourseService;
import vn.vinaacademy.event.NotificationEventSender;
import vn.vinaacademy.instructor.entity.CourseInstructor;
import vn.vinaacademy.instructor.repository.CourseInstructorRepository;
import vn.vinaacademy.kafka.event.NotificationCreateEvent;

import java.util.List;
import java.util.UUID;

@Service

public class CourseServiceImpl implements CourseService {

    @Autowired
    private CourseRepository courseRepository;
    @Autowired
    private CourseInstructorRepository courseInstructorRepository;
    @Autowired
    private UserService userService;
    @Autowired
    private CategoryRepository categoryRepository;


    @Autowired
    private CourseMapper courseMapper;
    @Autowired
    private SecurityContextHelper securityContextHelper;
    @Autowired
    private SlugGeneratorHelper slugGeneratorHelper;
    @Autowired
    private NotificationEventSender notificationEventSender;

    @Override
    @Transactional(readOnly = true)
    public CourseDetailsResponse getCourse(String slug) {
        Course course = courseRepository.findBySlug(slug)
                .orElseThrow(() -> BadRequestException.message("Khóa học không tồn tại"));

        // Use CourseMapper to create the base course details
        CourseDetailsResponse response = courseMapper.toCourseDetailsResponse(course);        // Fetch and set instructors
        List<CourseInstructor> courseInstructors = courseInstructorRepository.findByCourse(course);
        response.setInstructors(courseInstructors.stream()
                .map(ci -> userService.getUserById(ci.getInstructorId()))
                .toList());        // Find the owner instructor specifically
        courseInstructorRepository.findByCourseAndIsOwnerTrue(course)
                .ifPresent(owner -> response.setOwnerInstructor(userService.getUserById(owner.getInstructorId())));

        // Process sections with lessons using the common method
//        List<SectionDto> sectionDtos = processSectionsAndLessons(course.getSections());
//        response.setSections(sectionDtos);

        // Fetch course reviews
//        if (course.getCourseReviews() != null && !course.getCourseReviews().isEmpty()) {
//            List<CourseReviewDto> reviewDtos = course.getCourseReviews().stream()
//                    .map(CourseReviewMapper.INSTANCE::toDto)
//                    .toList();
//            response.setReviews(reviewDtos);
//        }

        return response;
    }

//    @Override
//    @Transactional(readOnly = true)
//    public Page<CourseDetailsResponse> searchCourseDetails(CourseSearchRequest searchRequest, int page, int size,
//                                                           String sortBy, String sortDirection) {
//        Pageable pageable = createPageable(page, size, sortBy, sortDirection);
//
//        Specification<Course> spec = Specification.where(CourseSpecification.hasKeyword(searchRequest.getKeyword()))
//                .and(CourseSpecification.hasStatus(
//                        searchRequest.getStatus() != null ? searchRequest.getStatus() : null))
//                .and(CourseSpecification.dontHasStatus(Course.CourseStatus.DRAFT))
//                .and(CourseSpecification.hasCategory(searchRequest.getCategorySlug()))
//                .and(CourseSpecification.hasLevel(searchRequest.getLevel()))
//                .and(CourseSpecification.hasLanguage(searchRequest.getLanguage()))
//                .and(CourseSpecification.hasMinPrice(searchRequest.getMinPrice()))
//                .and(CourseSpecification.hasMaxPrice(searchRequest.getMaxPrice()))
//                .and(CourseSpecification.hasMinRating(searchRequest.getMinRating()));
//
//        Page<Course> coursePage = courseRepository.findAll(spec, pageable);
//
//        return coursePage.map(course -> {
//            CourseDetailsResponse response = courseMapper.toCourseDetailsResponse(course);            // Set instructors
//            List<CourseInstructor> courseInstructors = courseInstructorRepository.findByCourse(course);
//            response.setInstructors(courseInstructors.stream()
//                    .map(ci -> userService.getUserById(ci.getInstructorId()))
//                    .toList());
//
//            // Set owner instructor
//            courseInstructorRepository.findByCourseAndIsOwnerTrue(course)
//                    .ifPresent(owner -> response.setOwnerInstructor(userService.getUserById(owner.getInstructorId())));
//
//            // Set sections and lessons

    /// /            List<SectionDto> sectionDtos = processSectionsAndLessons(course.getSections());
    /// /            response.setSections(sectionDtos);
    /// /
    /// /            // Set reviews
    /// /            if (course.getCourseReviews() != null && !course.getCourseReviews().isEmpty()) {
    /// /                List<CourseReviewDto> reviewDtos = course.getCourseReviews().stream()
    /// /                        .map(CourseReviewMapper.INSTANCE::toDto)
    /// /                        .toList();
    /// /                response.setReviews(reviewDtos);
    /// /            }
//
//            return response;
//        });
//    }
    @Override
    public CourseDto createCourse(CourseRequest request) {
        String baseSlug = StringUtils.isBlank(request.getSlug()) ?
                SlugUtils.toSlug(request.getName()) :
                request.getSlug();
        String slug = slugGeneratorHelper.generateSlug(baseSlug, s -> !courseRepository.existsBySlug(s));

        Category category = categoryRepository.findBySlug(request.getCategorySlug())
                .orElseThrow(() -> BadRequestException.message("Không tìm thấy danh mục"));
        Course course = Course.builder()
                .name(request.getName())
                .category(category)
                .description(request.getDescription())
                .image(request.getImage())
                .language(request.getLanguage())
                .level(request.getLevel())
                .price(request.getPrice())
                .rating(0)
                .totalLesson(0)
                .totalRating(0)
                .totalSection(0)
                .totalLesson(0)
                .slug(slug)
                .status(Course.CourseStatus.DRAFT)
                .build();

        courseRepository.save(course);

        return courseMapper.toDTO(course);
    }

    @Override
    public CourseDto updateCourse(String slug, CourseRequest request) {
        Course course = courseRepository.findBySlug(slug)
                .orElseThrow(() -> BadRequestException.message("Khóa học không tồn tại"));
        UUID currentUserId = securityContextHelper.getCurrentUserIdAsUUID();
        if (!securityContextHelper.hasAnyRole(AuthConstants.ADMIN_ROLE, AuthConstants.STAFF_ROLE)
                && course.getInstructors().stream()
                .noneMatch(courseInstructor -> courseInstructor.getInstructorId().equals(currentUserId))) {
            throw BadRequestException.message("Người dùng không có quyền sửa khóa học này");
        }

        String oldSlug = course.getSlug();
        String newSlug = StringUtils.isBlank(request.getSlug()) ? oldSlug
                : SlugUtils.toSlug(request.getName());

        if (!oldSlug.equals(newSlug) && courseRepository.existsBySlug(newSlug)) {
            throw BadRequestException.message("Slug đã tồn tại");
        }

        Category category = categoryRepository.findBySlug(request.getCategorySlug())
                .orElseThrow(() -> BadRequestException.message("Không tìm thấy danh mục"));
        course.setName(request.getName());
        course.setSlug(newSlug);
        course.setCategory(category);
        course.setDescription(request.getDescription());
        course.setImage(request.getImage());
        course.setLanguage(request.getLanguage());
        course.setLevel(request.getLevel());
        course.setPrice(request.getPrice());
//        course.setStatus(request.getStatus());
        courseRepository.save(course);
        return courseMapper.toDTO(course);
    }

    @Override
    public void deleteCourse(String slug) {
        Course course = courseRepository.findBySlug(slug)
                .orElseThrow(() -> BadRequestException.message("Khóa học không tồn tại"));

        if (course.getTotalStudent() > 0) {
            throw BadRequestException.message("Khóa học đã có người đăng ký không thể xóa");
        }
        UUID currentUserId = securityContextHelper.getCurrentUserIdAsUUID();
        if (!securityContextHelper.hasAnyRole(AuthConstants.ADMIN_ROLE, AuthConstants.STAFF_ROLE, AuthConstants.INSTRUCTOR_ROLE)
        ) {
            throw BadRequestException.message("Người dùng không có quyền xóa khóa học này");
        }

        if (!securityContextHelper.hasAnyRole(AuthConstants.ADMIN_ROLE)) {
            if (course.getInstructors().stream()
                    .noneMatch(courseInstructor -> courseInstructor.getInstructorId().equals(currentUserId))) {
                throw BadRequestException.message("Người dùng không phải chủ sở hữu khóa học này");
            }
        }

        courseRepository.delete(course);
    }

//    @Override
//    public List<CourseDto> getCoursesByCategory(String slug) {

    /// /        categoryRepository.findBySlug(slug).orElseThrow(() -> BadRequestException.message("Danh mục không tồn tại"));
//        return courseRepository.findAllCourseByCategory(slug).stream().map(courseMapper::toDTO)
//                .toList();
//    }

//    @Override
//    public Page<CourseDto> getCoursesByInstructor(UUID instructorId, int page, int size,
//                                                  String sortBy, String sortDirection) {
//        Pageable pageable = createPageable(page, size, sortBy, sortDirection);
//
//        // Verify instructor exists
//        userService.getUserById(instructorId);
//
//        Page<Course> coursePage = courseRepository.findByInstructorId(instructorId, pageable);
//        return coursePage.map(courseMapper::toDTO);
//    }

//    @Override
//    @Transactional(readOnly = true)
//    public Page<CourseDto> searchInstructorCourses(
//            UUID instructorId,
//            CourseSearchRequest searchRequest,
//            int page,
//            int size,
//            String sortBy,
//            String sortDirection) {
//
//        Pageable pageable = createPageable(page, size, sortBy, sortDirection);
//
//        // Tạo specification cho việc tìm kiếm
//        Specification<Course> spec = Specification.where(CourseSpecification.hasInstructor(instructorId))
//                .and(CourseSpecification.hasKeyword(searchRequest.getKeyword()))
//                .and(CourseSpecification.hasStatus(searchRequest.getStatus()))
//                .and(CourseSpecification.hasCategory(searchRequest.getCategorySlug()))
//                .and(CourseSpecification.hasLevel(searchRequest.getLevel()))
//                .and(CourseSpecification.hasLanguage(searchRequest.getLanguage()))
//                .and(CourseSpecification.hasMinPrice(searchRequest.getMinPrice()))
//                .and(CourseSpecification.hasMaxPrice(searchRequest.getMaxPrice()))
//                .and(CourseSpecification.hasMinRating(searchRequest.getMinRating()));
//
//        Page<Course> coursePage = courseRepository.findAll(spec, pageable);
//        return coursePage.map(courseMapper::toDTO);
//    }
    @Override
    @Transactional(readOnly = true)
    public CourseDto getCourseLearning(String slug) {
        Course course = courseRepository.findBySlug(slug)
                .orElseThrow(() -> BadRequestException.message("Khóa học không tồn tại"));
        CourseDto courseDto = courseMapper.toDTO(course);
        if (course.getStatus() != Course.CourseStatus.PUBLISHED) {
            throw BadRequestException.message("Khóa học chưa được công khai");
        }

        UUID currentUserId = securityContextHelper.getCurrentUserIdAsUUID();
        // enrollment progress
        List<UUID> instructors = course.getInstructors().stream()
                .map(CourseInstructor::getInstructorId)
                .toList();
//        if (!securityContextHelper.hasAnyRole(AuthConstants.ADMIN_ROLE, AuthConstants.STAFF_ROLE)
//                && !instructors.contains(currentUserId)) {
//            Enrollment courseEnrollment = enrollmentRepository.findByCourseAndUserId(course, currentUserId)
//                    .orElseThrow(() -> BadRequestException.message("Người dùng không có quyền truy cập khóa học này"));
//            courseDto.setProgress(EnrollmentMapper.INSTANCE.toDto2(courseEnrollment));
//        } else {
//            courseDto.setProgress(new EnrollmentProgressDto());
//        }

        // sections + lessons
//        List<Section> sections = course.getSections();
//
//        // 1. First collect all lessons from all sections
//        List<Lesson> allLessons = sections.stream()
//                .flatMap(section -> section.getLessons().stream())
//                .toList();
//
//        // 2. Fetch all user progress records in a single query
//        List<UserProgress> allUserProgress = lessonProgressRepository.findByUserIdAndLessonIn(currentUserId, allLessons);
//
//        // 3. Create a map for quick lookup: lessonId -> UserProgress
//        Map<UUID, UserProgress> progressMap = allUserProgress.stream()
//                .collect(Collectors.toMap(
//                        progress -> progress.getLesson().getId(),
//                        progress -> progress));
//
//        // 4. Get sorted sections and lessons using the common method
//        List<SectionDto> sectionDtos = processSectionsAndLessons(sections);
//
//        // 5. Add user progress to each lesson
//        for (SectionDto sectionDto : sectionDtos) {
//            for (LessonDto lesson : sectionDto.getLessons()) {
//                // Use the map to look up user progress instead of making individual queries
//                UserProgress userProgress = progressMap.getOrDefault(
//                        lesson.getId(),
//                        new UserProgress());
//                lesson.setCurrentUserProgress(userProgress);
//            }
//        }
//
//        courseDto.setSections(sectionDtos);
        return courseDto;
    }
//    private List<SectionDto> processSectionsAndLessons(List<Section> sections) {
//        if (sections == null || sections.isEmpty()) {
//            return List.of();
//        }
//
//        return sections.stream()
//                .sorted(java.util.Comparator.comparing(Section::getOrderIndex))
//                .map(section -> {
//                    SectionDto sectionDto = sectionMapper.toDto(section);
//                    // Fetch and map lessons for each section
//                    sectionDto.setLessons(section.getLessons().stream()
//                            .sorted(java.util.Comparator.comparing(Lesson::getOrderIndex))
//                            .map(lessonMapper::lessonToLessonDto)
//                            .toList());
//                    return sectionDto;
//                })
//                .toList();
//    }

    /**
     * Create a pageable object based on sort parameters
     *
     * @param page          Page number
     * @param size          Page size
     * @param sortBy        Field to sort by
     * @param sortDirection Sort direction (asc/desc)
     * @return Configured Pageable object
     */
    private Pageable createPageable(int page, int size, String sortBy, String sortDirection) {
        Direction direction = sortDirection.equalsIgnoreCase("desc") ? Direction.DESC : Direction.ASC;
        return PageRequest.of(page, size, Sort.by(direction, sortBy));
    }

    @Override
    @Transactional(readOnly = true)
    public CourseDto getCourseById(UUID id) {
        Course course = courseRepository.findById(id)
                .orElseThrow(() -> BadRequestException.message("Khóa học không tồn tại"));
        return courseMapper.toDTO(course);
    }

    @Override
    public Boolean updateStatusCourse(CourseStatusRequest courseStatusRequest) {
        Course course = courseRepository.findBySlug(courseStatusRequest.getSlug())
                .orElseThrow(() -> BadRequestException.message("Khóa học không tồn tại"));
        if (courseStatusRequest.getStatus() == null)
            throw BadRequestException.message("Thiếu dữ liệu cần thiết");
        course.setStatus(courseStatusRequest.getStatus());
        if (course.getInstructors().isEmpty()) {
            throw BadRequestException.message("Khóa học không có giảng viên");
        }
        sendCourseNotification(course, course.getInstructors().get(0).getInstructorId());
        courseRepository.save(course);
        return true;
    }

    @Override
    public void submitCourseForReview(UUID courseId) {
        // Lấy thông tin người dùng hiện tại
        UUID currentUserId = securityContextHelper.getCurrentUserIdAsUUID();

        // Kiểm tra sự tồn tại của khóa học
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy khóa học với ID: " + courseId));

        // Kiểm tra quyền của người dùng (giảng viên của khóa học)
        boolean isInstructor = isInstructorOfCourse(course.getId(), currentUserId);
        if (!isInstructor) {
            throw BadRequestException.message("Bạn không có quyền cập nhật khóa học này");
        }

        // Chuyển trạng thái khóa học sang PENDING
        course.setStatus(Course.CourseStatus.PENDING);
        courseRepository.save(course);
    }

    private void sendCourseNotification(Course course, UUID userId) {
        String title = String.format("Khóa học %s đã được cập nhật trạng thái", course.getName());
        String message = String.format("Khóa học %s đã được cập nhật trạng thái thành %s",
                course.getName(), course.getStatus().getValue());
        String url = String.format("%s%s%s%s",
                AppConstants.FRONTEND_URL,
                "/instructor/courses/",
                course.getId(),
                "/content");
        NotificationCreateEvent request = NotificationCreateEvent.builder()
                .title(title)
                .content(message)
                .targetUrl(url)
                .userId(userId)
                .type(NotificationCreateEvent.NotificationType.COURSE_APPROVAL)
                .build();

        notificationEventSender.createNotification(request);
    }

//    @Override
//    @Transactional(readOnly = true)
//    public Page<CourseDto> getPublishedCoursesByInstructor(
//            UUID instructorId,
//            int page,
//            int size,
//            String sortBy,
//            String sortDirection) {
//        Pageable pageable = createPageable(page, size, sortBy, sortDirection);        // Kiểm tra user có phải là instructor không
//        UserDto instructor = userService.getUserById(instructorId);
//
//        // Kiểm tra xem user có role INSTRUCTOR không
//        boolean isInstructor = instructor.getRoles().stream()
//                .anyMatch(role -> role.getCode().equalsIgnoreCase(AuthConstants.INSTRUCTOR_ROLE));
//
//        if (!isInstructor) {
//            throw BadRequestException.message("Người dùng không phải là giảng viên");
//        }
//
//        // Lọc theo instructor và status = PUBLISHED
//        Specification<Course> spec = Specification.where(CourseSpecification.hasInstructor(instructorId))
//                .and(CourseSpecification.hasStatus(Course.CourseStatus.PUBLISHED));
//
//        Page<Course> coursePage = courseRepository.findAll(spec, pageable);
//        return coursePage.map(courseMapper::toDTO);
//    }


    private Boolean isInstructorOfCourse(UUID courseId, UUID instructorId) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> BadRequestException.message("Khóa học không tồn tại"));

        return course.getInstructors().stream()
                .anyMatch(courseInstructor -> courseInstructor.getInstructorId().equals(instructorId));
    }

}
