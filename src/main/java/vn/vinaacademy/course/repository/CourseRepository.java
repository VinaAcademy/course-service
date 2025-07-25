package vn.vinaacademy.course.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import vn.vinaacademy.course.entity.Course;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface CourseRepository extends JpaRepository<Course, UUID>, JpaSpecificationExecutor<Course> {

    Optional<Course> findBySlug(String slug);

    boolean existsBySlug(String slug);

    @Query("SELECT c FROM Course c WHERE c.category.slug = :slug")
    List<Course> findAllCourseByCategory(@Param("slug") String slug);

    // Tìm tất cả khóa học theo trạng thái
    List<Course> findByStatus(Course.CourseStatus status);

    boolean existsById(UUID id);

    Page<Course> findAll(Pageable pageable);

    Page<Course> findByCategorySlug(String categorySlug, Pageable pageable);

    Page<Course> findByRatingGreaterThanEqual(double minRating, Pageable pageable);

    Page<Course> findByCategorySlugAndRatingGreaterThanEqual(String CategorySlug, double minRating,
                                                             Pageable pageable);

    // Tìm kiếm khóa học theo tên hoặc mô tả
    @Query("SELECT c FROM Course c WHERE " +
            "(LOWER(c.name) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(c.description) LIKE LOWER(CONCAT('%', :keyword, '%'))) " +
            "AND c.status = :status")
    Page<Course> searchCourses(
            @Param("keyword") String keyword,
            @Param("status") Course.CourseStatus status, Pageable pageable);

    // Lấy khóa học phổ biến dựa trên số lượng học viên đăng ký
    @Query("SELECT c FROM Course c WHERE c.status = :status ORDER BY c.totalStudent DESC")
    Page<Course> findPopularCourses(@Param("status") Course.CourseStatus status, Pageable pageable);

    // Lấy khóa học mới nhất dựa trên thời gian tạo
    @Query("SELECT c FROM Course c WHERE c.status = :status ORDER BY c.createdDate DESC")
    Page<Course> findNewestCourses(@Param("status") Course.CourseStatus status, Pageable pageable);

    // Lấy khóa học của giảng viên
    @Query("SELECT c FROM Course c JOIN c.instructors i WHERE i.instructorId = :instructorId")
    Page<Course> findByInstructorId(@Param("instructorId") UUID instructorId, Pageable pageable);

    // Lấy khóa học mà người dùng đã đăng ký
//    @Query("SELECT c FROM Course c JOIN c.enrollments e WHERE e.userId = :userId")
//    Page<Course> findEnrolledCoursesByUserId(@Param("userId") UUID userId, Pageable pageable);

    // Lấy khóa học dựa trên đánh giá cao
    @Query("SELECT c FROM Course c WHERE c.status = :status ORDER BY c.rating DESC")
    Page<Course> findTopRatedCourses(@Param("status") Course.CourseStatus status, Pageable pageable);

    // Đếm số lượng khóa học theo trạng thái
    long countByStatus(Course.CourseStatus status);

    // Đếm số lượng khóa học mà người dùng đã tạo
    @Query("SELECT COUNT(c) FROM Course c JOIN c.instructors i WHERE i.instructorId = :instructorId")
    long countCoursesByInstructorId(@Param("instructorId") UUID instructorId);

    // Đếm số lượng khóa học published của một instructor
    @Query("SELECT COUNT(c) FROM Course c JOIN c.instructors i WHERE i.instructorId = :instructorId AND c.status = :status")
    long countCoursesByInstructorIdAndStatus(Course.CourseStatus status, UUID instructorId);

    // Tìm kiếm khóa học theo nhiều tiêu chí
    @Query("SELECT c FROM Course c WHERE " +
            "(:keyword IS NULL OR LOWER(c.name) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(c.description) LIKE LOWER(CONCAT('%', :keyword, '%'))) " +
            "AND (:status IS NULL OR c.status = :status) " +
            "AND (:categoryId IS NULL OR c.category.id = :categoryId) " +
            "AND (:level IS NULL OR c.level = :level) " +
            "AND (:language IS NULL OR c.language = :language) " +
            "AND (:minPrice IS NULL OR c.price >= :minPrice) " +
            "AND (:maxPrice IS NULL OR c.price <= :maxPrice) " +
            "AND (:minRating IS NULL OR c.rating >= :minRating)")
    Page<Course> advancedSearchCourses(
            @Param("keyword") String keyword,
            @Param("status") Course.CourseStatus status,
            @Param("categoryId") UUID categoryId,
            @Param("level") Course.CourseLevel level,
            @Param("language") String language,
            @Param("minPrice") BigDecimal minPrice,
            @Param("maxPrice") BigDecimal maxPrice,
            @Param("minRating") Double minRating,
            Pageable pageable);

//    @Query("SELECT CASE WHEN COUNT(c) > 0 THEN true ELSE false END " +
//            "FROM Course c JOIN c.enrollments e JOIN c.sections s " +
//            "JOIN s.lessons ls" +
//            " WHERE e.userId = :studentId AND ls.id = :lessonId")
//    boolean existsByStudentAndLesson(UUID studentId, UUID lessonId);

//    @Query("SELECT c FROM Course c JOIN c.sections s " +
//            "WHERE s.id = :sectionId")
//    Optional<Course> getCourseBySectionId(UUID sectionId);

    @Query("SELECT c.status, COUNT(c) FROM Course c GROUP BY c.status")
    List<Object[]> countCoursesByStatus();

}
