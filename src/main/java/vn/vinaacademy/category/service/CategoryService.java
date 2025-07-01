package vn.vinaacademy.category.service;


import vn.vinaacademy.category.dto.CategoryDto;
import vn.vinaacademy.category.dto.CategoryRequest;

import java.util.List;

public interface CategoryService {
    List<CategoryDto> getCategories();

    CategoryDto getCategory(String slug);

    CategoryDto createCategory(CategoryRequest request);

    CategoryDto updateCategory(String slug, CategoryRequest request);

    void deleteCategory(String slug);
}
