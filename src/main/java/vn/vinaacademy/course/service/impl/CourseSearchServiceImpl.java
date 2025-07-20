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
import vn.vinaacademy.common.security.SecurityContextHelper;
import vn.vinaacademy.course.document.CourseDocument;
import vn.vinaacademy.course.dto.CourseSearchRequest;
import vn.vinaacademy.course.dto.InstructorCourseSearchRequest;
import vn.vinaacademy.course.repository.CourseElasticRepository;
import vn.vinaacademy.course.service.CourseSearchService;

import java.io.IOException;
import java.util.List;
import java.util.UUID;
import java.util.function.Function;

@Service
@Slf4j
@RequiredArgsConstructor
public class CourseSearchServiceImpl implements CourseSearchService {
    @Autowired
    private CourseElasticRepository courseElasticRepository;
    @Autowired
    private SecurityContextHelper securityContextHelper;

    @Override
    public Page<CourseDocument> searchPublishedCourses(CourseSearchRequest req) {
        return courseElasticRepository.search(req);
    }

    @Override
    public Page<CourseDocument> searchInstructorCourses(InstructorCourseSearchRequest searchRequest) {
        UUID instructorId = securityContextHelper.getCurrentUserIdAsUUID();

        return courseElasticRepository.search(searchRequest, instructorId);
    }
}
