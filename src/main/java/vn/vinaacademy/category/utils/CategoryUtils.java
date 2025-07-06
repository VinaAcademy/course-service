package vn.vinaacademy.category.utils;

import lombok.experimental.UtilityClass;
import vn.vinaacademy.category.dto.CategoryDto;
import vn.vinaacademy.category.entity.Category;
import vn.vinaacademy.category.mapper.CategoryMapper;

import java.util.List;

@UtilityClass
public class CategoryUtils {
    public static CategoryDto buildCategoryHierarchy(Category category, CategoryMapper mapper) {
        CategoryDto categoryDto = mapper.toDto(category);

        if (category.getChildren() != null && !category.getChildren().isEmpty()) {
            List<CategoryDto> childrenDto = category.getChildren().stream()
                    .map(v -> CategoryUtils.buildCategoryHierarchy(v, mapper))
                    .toList();

            long coursesCount = childrenDto.stream()
                    .mapToLong(CategoryDto::getCoursesCount)
                    .sum();
            coursesCount += category.getCourses().size();
            categoryDto.setChildren(childrenDto);
            categoryDto.setCoursesCount(coursesCount);
        } else {
            categoryDto.setChildren(List.of());
            categoryDto.setCoursesCount(category.getCourses().size());
        }

        return categoryDto;
    }

    public static boolean isAncestor(Category parent, Category child) {
        if (child.getParent() == null) {
            return false;
        }

        if (child.getParent().getId().equals(parent.getId())) {
            return true;
        }

        return isAncestor(parent, child.getParent());
    }
}
