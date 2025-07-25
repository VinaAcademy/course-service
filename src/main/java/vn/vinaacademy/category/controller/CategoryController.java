package vn.vinaacademy.category.controller;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import vn.vinaacademy.category.dto.CategoryDto;
import vn.vinaacademy.category.dto.CategoryRequest;
import vn.vinaacademy.category.service.CategoryService;
import vn.vinaacademy.common.constant.AuthConstants;
import vn.vinaacademy.common.response.ApiResponse;
import vn.vinaacademy.common.security.annotation.HasAnyRole;

import java.util.List;

@RestController
@RequestMapping("/api/v1/categories")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
public class CategoryController {
    private final CategoryService categoryService;

    @GetMapping
    public ApiResponse<List<CategoryDto>> getCategories() {
        return ApiResponse.success(categoryService.getCategories());
    }

    @GetMapping("/{slug}")
    public ApiResponse<CategoryDto> getCategory(@PathVariable String slug) {
        return ApiResponse.success(categoryService.getCategory(slug));
    }

    @HasAnyRole({AuthConstants.ADMIN_ROLE})
    @PostMapping
    public ApiResponse<CategoryDto> createCategory(@RequestBody @Valid CategoryRequest request) {
        return ApiResponse.success(categoryService.createCategory(request));
    }

    @HasAnyRole({AuthConstants.ADMIN_ROLE})
    @PutMapping("/{slug}")
    public ApiResponse<CategoryDto> updateCategory(@PathVariable String slug, @RequestBody @Valid CategoryRequest request) {
        return ApiResponse.success(categoryService.updateCategory(slug, request));
    }

    @HasAnyRole({AuthConstants.ADMIN_ROLE})
    @DeleteMapping("/{slug}")
    public ApiResponse<Void> deleteCategory(@PathVariable String slug) {
        categoryService.deleteCategory(slug);
        return ApiResponse.success("Xóa danh mục thành công");
    }
}
