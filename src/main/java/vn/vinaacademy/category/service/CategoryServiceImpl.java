package vn.vinaacademy.category.service;

import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;
import vn.vinaacademy.category.dto.CategoryDto;
import vn.vinaacademy.category.dto.CategoryRequest;
import vn.vinaacademy.category.entity.Category;
import vn.vinaacademy.category.mapper.CategoryMapper;
import vn.vinaacademy.category.repository.CategoryRepository;
import vn.vinaacademy.category.utils.CategoryUtils;
import vn.vinaacademy.common.constant.CacheConstants;
import vn.vinaacademy.common.exception.BadRequestException;
import vn.vinaacademy.common.utils.SlugUtils;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {
    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;

    @Override
    @Cacheable(value = CacheConstants.CATEGORIES)
    public List<CategoryDto> getCategories() {
        // Root categories
        List<Category> categories = categoryRepository.findAllRootCategoriesWithChildren();

        return categories.stream()
                .map(v -> CategoryUtils.buildCategoryHierarchy(v, categoryMapper))
                .toList();
    }

    @Override
    @Cacheable(value = CacheConstants.CATEGORY, key = "#slug")
    public CategoryDto getCategory(String slug) {
        Category category = categoryRepository.findBySlug(slug)
                .orElseThrow(() -> BadRequestException.message("Danh mục không tồn tại"));

        return CategoryUtils.buildCategoryHierarchy(category, categoryMapper);
    }

    @Override
    @CacheEvict(value = CacheConstants.CATEGORIES, allEntries = true)
    @CachePut(value = CacheConstants.CATEGORY, key = "#result.slug")
    public CategoryDto createCategory(CategoryRequest request) {
        String slug = StringUtils.isNotBlank(request.getSlug())
                ? request.getSlug() : SlugUtils.toSlug(request.getName());

        if (categoryRepository.existsBySlug(slug)) {
            throw BadRequestException.message("Slug đã tồn tại");
        }

        Category parent = null;
        if (StringUtils.isNotBlank(request.getParentSlug())) {
            parent = categoryRepository.findBySlug(request.getParentSlug())
                    .orElseThrow(() -> BadRequestException.message("Danh mục cha không tồn tại"));
        }

        Category category = Category.builder()
                .name(request.getName())
                .slug(slug)
                .parent(parent)
                .build();

        categoryRepository.save(category);

        return categoryMapper.toDto(category);
    }

    @Override
    @Caching(
            evict = {
                    @CacheEvict(value = CacheConstants.CATEGORIES, allEntries = true),
                    @CacheEvict(value = CacheConstants.CATEGORY, key = "#slug")
            },
            put = {
                    @CachePut(value = CacheConstants.CATEGORY, key = "#slug")
            }
    )
    public CategoryDto updateCategory(String slug, CategoryRequest request) {
        Category category = categoryRepository.findBySlug(slug)
                .orElseThrow(() -> BadRequestException.message("Danh mục không tồn tại"));

        String newSlug = StringUtils.isNotBlank(request.getSlug())
                ? request.getSlug() : SlugUtils.toSlug(request.getName());

        if (!slug.equals(newSlug) && categoryRepository.existsBySlug(newSlug)) {
            throw BadRequestException.message("Slug đã tồn tại");
        }

        Category parent = null;
        if (StringUtils.isNotBlank(request.getParentSlug())) {
            parent = categoryRepository.findBySlug(request.getParentSlug())
                    .orElseThrow(() -> BadRequestException.message("Danh mục cha không tồn tại"));

            // Check if parent is child of category
            if (CategoryUtils.isAncestor(category, parent)) {
                throw BadRequestException.message("Danh mục cha không hợp lệ");
            }
        }

        category.setName(request.getName());
        category.setSlug(newSlug);
        category.setParent(parent);

        categoryRepository.save(category);

        return categoryMapper.toDto(category);
    }

    @Override
    @Caching(evict = {
            @CacheEvict(value = CacheConstants.CATEGORIES, allEntries = true),
            @CacheEvict(value = CacheConstants.CATEGORY, key = "#slug")
    })
    public void deleteCategory(String slug) {
        Category category = categoryRepository.findBySlug(slug)
                .orElseThrow(() -> BadRequestException.message("Danh mục không tồn tại"));
        if (categoryRepository.existsByParent(category)) {
            throw BadRequestException.message("Danh mục có danh mục con");
        }
        if (categoryRepository.existsByCourses(category)) {
            throw BadRequestException.message("Danh mục có khóa học");
        }

        categoryRepository.delete(category);
    }
}
