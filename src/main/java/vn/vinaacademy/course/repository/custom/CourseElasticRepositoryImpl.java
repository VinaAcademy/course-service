package vn.vinaacademy.course.repository.custom;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.elasticsearch.core.SearchHitSupport;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.query.Criteria;
import org.springframework.data.elasticsearch.core.query.CriteriaQuery;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.stereotype.Repository;
import vn.vinaacademy.course.document.CourseDocument;
import vn.vinaacademy.course.dto.CourseSearchRequest;
import vn.vinaacademy.course.dto.InstructorCourseSearchRequest;
import vn.vinaacademy.course.repository.CourseElasticRepositoryCustom;

import java.util.UUID;

@Repository
@Slf4j
@RequiredArgsConstructor
public class CourseElasticRepositoryImpl implements CourseElasticRepositoryCustom {
    private final ElasticsearchOperations ops;

    @Override
    public Page<CourseDocument> search(CourseSearchRequest req) {
        Criteria criteria = CourseCriteriaBuilder.build(req);
        return searchByCriteria(criteria, req.getPage(), req.getSize(), req.getSortBy(), req.getSortDirection());
    }


    @Override
    public Page<CourseDocument> search(InstructorCourseSearchRequest req, UUID instructorId) {
        Criteria criteria = new Criteria("instructorId").is(instructorId.toString());

        if (StringUtils.isNotBlank(req.getTitle())) {
            criteria = criteria.and(new Criteria("title").contains(req.getTitle()));
        }

        if (req.getStatus() != null) {
            criteria = criteria.and(new Criteria("status").is(req.getStatus().name()));
        }

        return searchByCriteria(criteria, req.getPage(), req.getSize(), req.getSortBy(), req.getSortDirection());
    }

    private Page<CourseDocument> searchByCriteria(Criteria criteria, int page, int size, String sortBy, String direction) {
        CriteriaQuery query = buildCriteriaQuery(criteria, page, size, sortBy, direction);
        SearchHits<CourseDocument> hits = ops.search(query, CourseDocument.class);
        return SearchHitSupport.searchPageFor(hits, query.getPageable()).map(SearchHit::getContent);
    }

    private CriteriaQuery buildCriteriaQuery(Criteria criteria, int page, int size, String sortBy, String direction) {
        CriteriaQuery query = new CriteriaQuery(criteria)
                .setPageable(PageRequest.of(page, size));
        Sort.Direction dir = "desc".equalsIgnoreCase(direction) ? Sort.Direction.DESC : Sort.Direction.ASC;
        query.addSort(Sort.by(dir, sortBy));
        return query;
    }

}
