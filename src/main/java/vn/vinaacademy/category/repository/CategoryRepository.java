package vn.vinaacademy.category.repository;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import vn.vinaacademy.category.entity.Category;

import java.util.List;
import java.util.Optional;

public interface CategoryRepository extends JpaRepository<Category, Long> {
    @EntityGraph(attributePaths = {"children"})
    Optional<Category> findBySlug(String slug);
        
    boolean existsBySlug(String slug);

    @EntityGraph(attributePaths = {"children"})
    @Query("SELECT c FROM Category c WHERE c.parent IS NULL")
    List<Category> findAllRootCategoriesWithChildren();

    boolean existsByParent(Category category);

    boolean existsByCourses(Category category);
}
