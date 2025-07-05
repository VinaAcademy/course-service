package vn.vinaacademy.instructor.service.impl;

import vn.vinaacademy.client.dto.UserDto;
import vn.vinaacademy.client.service.UserService;
import vn.vinaacademy.common.constant.AppConstants;
import vn.vinaacademy.common.exception.BadRequestException;
import vn.vinaacademy.event.NotificationEventSender;
import vn.vinaacademy.instructor.dto.InstructorInfoDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.vinaacademy.common.security.SecurityContextHelper;
import vn.vinaacademy.common.security.annotation.HasAnyRole;
import vn.vinaacademy.instructor.repository.CourseInstructorRepository;
import vn.vinaacademy.instructor.service.InstructorService;
import vn.vinaacademy.kafka.event.NotificationCreateEvent;

import java.util.UUID;

import static vn.vinaacademy.common.constant.AuthConstants.INSTRUCTOR_ROLE;

@Service
public class InstructorServiceImpl implements InstructorService {
    @Autowired
    private SecurityContextHelper securityHelper;
    @Autowired
    private NotificationEventSender notificationService;

    @Autowired
    private UserService userService;
    @Autowired
    private CourseInstructorRepository courseInstructorRepository;

    @Override
    @Transactional(readOnly = true)
    @HasAnyRole({INSTRUCTOR_ROLE})
    public InstructorInfoDto getInstructorInfo(UUID instructorId) {
        UserDto instructor = userService.getUserById(instructorId);
        if (instructor == null) {
            throw new IllegalArgumentException("Không tìm thấy giảng viên với ID: " + instructorId);
        }

        InstructorInfoDto dto = convertToDto(instructor);
        var courses = courseInstructorRepository.countByInstructorId(instructorId);
        dto.setTotalCourses(courses);
        return dto;
    }

    @Override
    @Transactional
    public InstructorInfoDto registerAsInstructor() {
        // Lấy thông tin người dùng hiện tại
        UUID userId = securityHelper.getCurrentUserIdAsUUID();

        if (securityHelper.isInstructor()) {
            throw BadRequestException.message("Bạn đã là giảng viên rồi");
        }

        UserDto userDto = userService.registerAsInstructor(userId);

        InstructorInfoDto instructorInfo = convertToDto(userDto);

        sendWelcomeNotification(userId);

        return instructorInfo;
    }

    private static InstructorInfoDto convertToDto(UserDto userDto) {
        InstructorInfoDto instructorInfo = new InstructorInfoDto();
        instructorInfo.setFullName(userDto.getFullName());
        instructorInfo.setUsername(userDto.getUsername());
        instructorInfo.setEmail(userDto.getEmail());
        instructorInfo.setDescription(userDto.getDescription());
        instructorInfo.setAvatarUrl(userDto.getAvatarUrl());
        return instructorInfo;
    }

    private void sendWelcomeNotification(UUID userId) {
        String title = "Chào mừng bạn đến cộng đồng giảng viên VinaAcademy";
        String message = "Chúng tôi rất vui khi bạn trở thành một phần của cộng đồng giảng viên tại VinaAcademy.";
        String targetUrl = AppConstants.FRONTEND_URL + "/instructor/dashboard";
        NotificationCreateEvent request = NotificationCreateEvent.builder()
                .userId(userId)
                .title(title)
                .content(message)
                .targetUrl(targetUrl)
                .type(NotificationCreateEvent.NotificationType.SYSTEM)
                .build();

        notificationService.createNotification(request);
    }

//    @Override
//    @Transactional(readOnly = true)
//    public boolean isInstructor(UUID userId) {
//        // Tìm người dùng trong hệ thống
//        User user = userRepository.findById(userId)
//                .orElseThrow(() -> BadRequestException.message("Không tìm thấy người dùng"));
//
//        // Kiểm tra xem người dùng có role INSTRUCTOR không
//        return user.getRoles().stream()
//                .anyMatch(role -> role.getCode().equalsIgnoreCase(AuthConstants.INSTRUCTOR_ROLE));
//    }
}