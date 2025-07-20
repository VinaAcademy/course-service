package vn.vinaacademy.course.document;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;
import vn.vinaacademy.course.entity.Course;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(indexName = "courses")
public class CourseDocument {
    
    @Id
    private String id;
    
    @Field(type = FieldType.Text, analyzer = "standard")
    private String name;
    
    @Field(type = FieldType.Text, analyzer = "standard")
    private String description;
    
    @Field(type = FieldType.Keyword)
    private String slug;
    
    @Field(type = FieldType.Keyword)
    private String image;
    
    @Field(type = FieldType.Double)
    private BigDecimal price;
    
    @Field(type = FieldType.Keyword)
    private Course.CourseLevel level;
    
    @Field(type = FieldType.Keyword)
    private Course.CourseStatus status;
    
    @Field(type = FieldType.Keyword)
    private String language;
    
    @Field(type = FieldType.Double)
    private double rating;
    
    @Field(type = FieldType.Long)
    private long totalRating;
    
    @Field(type = FieldType.Long)
    private long totalStudent;
    
    @Field(type = FieldType.Long)
    private long totalSection;
    
    @Field(type = FieldType.Long)
    private long totalLesson;
    
    // Category information
    @Field(type = FieldType.Keyword)
    private String categoryId;
    
    @Field(type = FieldType.Keyword)
    private String categorySlug;
    
    @Field(type = FieldType.Text)
    private String categoryName;
    
    // Instructor information
    @Field(type = FieldType.Keyword)
    private List<String> instructorIds;
    
    @Field(type = FieldType.Text)
    private List<String> instructorNames;
    
    @Field(type = FieldType.Date)
    private LocalDateTime createdDate;
    
    @Field(type = FieldType.Date)
    private LocalDateTime updatedDate;
}