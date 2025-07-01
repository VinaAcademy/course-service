package vn.vinaacademy.category.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import vn.vinaacademy.common.dto.BaseDto;

import java.util.List;

@EqualsAndHashCode(callSuper = false)
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class CategoryDto extends BaseDto {
    private Long id;
    private String name;
    private String slug;
    private String parentSlug;
    private long coursesCount;

    private List<CategoryDto> children;
}
