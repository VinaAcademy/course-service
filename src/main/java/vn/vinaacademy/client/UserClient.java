package vn.vinaacademy.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import vn.vinaacademy.client.dto.UserDto;
import vn.vinaacademy.common.constant.ServiceNames;
import vn.vinaacademy.common.response.ApiResponse;

import java.util.UUID;

@FeignClient(name = ServiceNames.USER_SERVICE, path = "/api/v1/internal/users")
public interface UserClient {

    @GetMapping("/{userId}")
    ApiResponse<UserDto> getUserByIdAsDto(@PathVariable("userId") UUID userId);

    @PostMapping("/{userId}/register-instructor")
    public ApiResponse<UserDto> registerAsInstructor(@PathVariable("userId") UUID userId);
}
