package vn.vinaacademy.client.service;

import vn.vinaacademy.client.UserClient;
import vn.vinaacademy.client.dto.UserDto;
import vn.vinaacademy.common.exception.BadRequestException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

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
}
