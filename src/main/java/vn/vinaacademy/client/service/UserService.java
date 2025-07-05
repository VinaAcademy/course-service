package vn.vinaacademy.client.service;

import lombok.extern.slf4j.Slf4j;
import vn.vinaacademy.client.UserClient;
import vn.vinaacademy.client.dto.UserDto;
import vn.vinaacademy.common.exception.BadRequestException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserClient userClient;

    public UserDto getUserById(UUID userId) {
        try {
            return userClient.getUserByIdAsDto(userId).getData();
        } catch (Exception e) {
            throw BadRequestException.message("Không tìm thấy ID người dùng này");
        }
    }

    public UserDto registerAsInstructor(UUID userId) {
        try {
            return userClient.registerAsInstructor(userId).getData();
        } catch (Exception e) {
            log.error("Đăng ký làm giảng viên không thành công cho userId: {}", userId, e);
            throw BadRequestException.message("Đăng ký làm giảng viên không thành công");
        }
    }
}
