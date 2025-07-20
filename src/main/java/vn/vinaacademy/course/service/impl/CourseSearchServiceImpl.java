package vn.vinaacademy.course.service.impl;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.SortOrder;
import co.elastic.clients.elasticsearch._types.query_dsl.BoolQuery;
import co.elastic.clients.elasticsearch.core.SearchRequest;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.elasticsearch.core.search.Hit;
import co.elastic.clients.util.ObjectBuilder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.elasticsearch.core.query.Criteria;
import org.springframework.stereotype.Service;
import vn.vinaacademy.course.document.CourseDocument;
import vn.vinaacademy.course.dto.CourseSearchRequest;
import vn.vinaacademy.course.dto.InstructorCourseSearchRequest;
import vn.vinaacademy.course.service.CourseSearchService;

import java.io.IOException;
import java.util.List;
import java.util.function.Function;

@Service
@Slf4j
@RequiredArgsConstructor
public class CourseSearchServiceImpl implements CourseSearchService {
    @Autowired
    private ElasticsearchClient elasticClient;

    @Override
    public Page<CourseDocument> searchPublishedCourses(CourseSearchRequest req) {
        Criteria criteria = new Criteria("status").is("PUBLISHED");

        Function<SearchRequest.Builder, ObjectBuilder<SearchRequest>> queryBuilder = builder -> {
            BoolQuery.Builder boolQuery = new BoolQuery.Builder();
            boolQuery.must(m -> m.term(t -> t.field("status").value("PUBLISHED")));
            if (req.getKeyword() != null && !req.getKeyword().isEmpty()) {
                boolQuery.must(m -> m.match(mq -> mq.field("name").query(req.getKeyword())));
            }
            if (req.getLevel() != null) {
                boolQuery.filter(f -> f.term(t -> t.field("level").value(req.getLevel().name())));
            }
            if (req.getLanguage() != null) {
                boolQuery.filter(f -> f.term(t -> t.field("language").value(req.getLanguage())));
            }
            return builder.query(boolQuery.build()._toQuery());
        };

        SearchResponse<CourseDocument> resp = null;
        try {
            resp = elasticClient.search(
                    queryBuilder,
                    CourseDocument.class
            );
        } catch (IOException e) {
            log.error("Error searching courses: {}", e.getMessage(), e);
            throw new RuntimeException(e);
        }

        List<CourseDocument> list = resp.hits().hits().stream()
                .map(Hit::source)
                .toList();
        long total = resp.hits().total().value();
        return new PageImpl<>(list, PageRequest.of(req.getPage(), req.getSize()), total);
    }

    @Override
    public Page<CourseDocument> searchInstructorCourses(InstructorCourseSearchRequest searchRequest) {
        return null;
    }
}
