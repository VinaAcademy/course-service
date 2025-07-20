package vn.vinaacademy.course.repository.custom;

import org.apache.commons.lang3.StringUtils;
import org.springframework.data.elasticsearch.core.query.Criteria;
import vn.vinaacademy.course.dto.CourseSearchRequest;
import vn.vinaacademy.course.entity.Course;

import java.math.BigDecimal;

public class CourseCriteriaBuilder {

    public static Criteria build(CourseSearchRequest req) {
        Criteria criteria = new Criteria("status")
                .is(req.getStatus() != null ? req.getStatus().name() : Course.CourseStatus.PUBLISHED.name());

        if (StringUtils.isNotBlank(req.getKeyword())) {
            criteria = criteria.and(keywordCriteria(req.getKeyword()));
        }

        if (StringUtils.isNotBlank(req.getCategorySlug())) {
            criteria = criteria.and(new Criteria("categorySlug").is(req.getCategorySlug()));
        }

        if (req.getLevel() != null) {
            criteria = criteria.and(new Criteria("level").is(req.getLevel().name()));
        }

        if (StringUtils.isNotBlank(req.getLanguage())) {
            criteria = criteria.and(new Criteria("language").is(req.getLanguage()));
        }

        if (req.getMinPrice() != null || req.getMaxPrice() != null) {
            criteria = criteria.and(priceCriteria(req.getMinPrice(), req.getMaxPrice()));
        }

        if (req.getMinRating() != null) {
            criteria = criteria.and(new Criteria("rating").greaterThanEqual(String.valueOf(req.getMinRating())));
        }

        return criteria;
    }

    public static Criteria keywordCriteria(String keyword) {
        return new Criteria("name").contains(keyword)
                .or(new Criteria("description").contains(keyword))
                .or(new Criteria("instructorNames").contains(keyword));
    }

    public static Criteria priceCriteria(BigDecimal min, BigDecimal max) {
        Criteria price = new Criteria("price");
        if (min != null) price = price.greaterThanEqual(String.valueOf(min));
        if (max != null) price = price.and(new Criteria("price").lessThanEqual(String.valueOf(max)));
        return price;
    }
}
