package vn.vinaacademy.category.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import vn.vinaacademy.category.Category;
import vn.vinaacademy.category.dto.CategoryDto;
import vn.vinaacademy.category.dto.CategoryRequest;

import java.util.List;

@Mapper(componentModel = "spring")
public interface CategoryMapper {

    @Mapping(target = "id", expression = "java(category.getId())")
    @Mapping(target = "parentSlug", expression = "java(category.getParent() != null ? category.getParent().getSlug() : null)")
    @Mapping(target = "children", ignore = true)
    CategoryDto toDto(Category category);

    List<CategoryDto> toDtoList(List<Category> categories);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "parent", ignore = true)
    @Mapping(target = "children", ignore = true)
    Category toEntity(CategoryRequest request);
}
