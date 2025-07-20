package vn.vinaacademy.course.repository;

import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import vn.vinaacademy.course.document.CourseDocument;

public interface CourseElasticRepository extends ElasticsearchRepository<CourseDocument, String>,
        CourseElasticRepositoryCustom {
}
