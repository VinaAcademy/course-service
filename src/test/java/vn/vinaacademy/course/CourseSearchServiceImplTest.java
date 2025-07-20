package vn.vinaacademy.course;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import vn.vinaacademy.common.security.SecurityContextHelper;
import vn.vinaacademy.course.document.CourseDocument;
import vn.vinaacademy.course.dto.CourseSearchRequest;
import vn.vinaacademy.course.dto.InstructorCourseSearchRequest;
import vn.vinaacademy.course.repository.CourseElasticRepository;
import vn.vinaacademy.course.service.impl.CourseSearchServiceImpl;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CourseSearchServiceImplTest {

    @Mock
    private CourseElasticRepository courseElasticRepository;

    @Mock
    private SecurityContextHelper securityContextHelper;

    @InjectMocks
    private CourseSearchServiceImpl courseSearchService;

    private CourseDocument sampleCourse1;
    private CourseDocument sampleCourse2;
    private UUID instructorId;

    @BeforeEach
    void setUp() {
        instructorId = UUID.randomUUID();
        
        sampleCourse1 = new CourseDocument();
        sampleCourse1.setId("1");
        sampleCourse1.setName("Java Programming");
        sampleCourse1.setInstructorIds(Arrays.asList(instructorId.toString()));
        
        sampleCourse2 = new CourseDocument();
        sampleCourse2.setId("2");
        sampleCourse2.setName("Spring Boot");
        sampleCourse2.setInstructorIds(Arrays.asList(instructorId.toString()));
    }

    @Test
    void searchPublishedCourses_ShouldReturnCourses() {
        // Given
        CourseSearchRequest request = new CourseSearchRequest();
        request.setPage(0);
        request.setSize(10);
        request.setSortBy("title");
        request.setSortDirection("asc");
        
        List<CourseDocument> courses = Arrays.asList(sampleCourse1, sampleCourse2);
        Page<CourseDocument> expectedPage = new PageImpl<>(courses, PageRequest.of(0, 10), 2);
        
        when(courseElasticRepository.search(request)).thenReturn(expectedPage);

        // When
        Page<CourseDocument> result = courseSearchService.searchPublishedCourses(request);

        // Then
        assertNotNull(result);
        assertEquals(2, result.getTotalElements());
        assertEquals(2, result.getContent().size());
        assertEquals("Java Programming", result.getContent().get(0).getName());
        assertEquals("Spring Boot", result.getContent().get(1).getName());
        
        verify(courseElasticRepository, times(1)).search(request);
    }

    @Test
    void searchPublishedCourses_WithEmptyResult_ShouldReturnEmptyPage() {
        // Given
        CourseSearchRequest request = new CourseSearchRequest();
        Page<CourseDocument> emptyPage = new PageImpl<>(Arrays.asList(), PageRequest.of(0, 10), 0);
        
        when(courseElasticRepository.search(request)).thenReturn(emptyPage);

        // When
        Page<CourseDocument> result = courseSearchService.searchPublishedCourses(request);

        // Then
        assertNotNull(result);
        assertEquals(0, result.getTotalElements());
        assertTrue(result.getContent().isEmpty());
        
        verify(courseElasticRepository, times(1)).search(request);
    }

    @Test
    void searchInstructorCourses_ShouldReturnInstructorCourses() {
        // Given
        InstructorCourseSearchRequest request = new InstructorCourseSearchRequest();
        request.setPage(0);
        request.setSize(5);
        request.setTitle("Java");
        
        List<CourseDocument> instructorCourses = Arrays.asList(sampleCourse1);
        Page<CourseDocument> expectedPage = new PageImpl<>(instructorCourses, PageRequest.of(0, 5), 1);
        
        when(securityContextHelper.getCurrentUserIdAsUUID()).thenReturn(instructorId);
        when(courseElasticRepository.search(request, instructorId)).thenReturn(expectedPage);

        // When
        Page<CourseDocument> result = courseSearchService.searchInstructorCourses(request);

        // Then
        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals(1, result.getContent().size());
        assertEquals("Java Programming", result.getContent().get(0).getName());
        assertEquals(instructorId.toString(), result.getContent().get(0).getInstructorIds().get(0));
        
        verify(securityContextHelper, times(1)).getCurrentUserIdAsUUID();
        verify(courseElasticRepository, times(1)).search(request, instructorId);
    }

    @Test
    void searchInstructorCourses_WithNoResults_ShouldReturnEmptyPage() {
        // Given
        InstructorCourseSearchRequest request = new InstructorCourseSearchRequest();
        Page<CourseDocument> emptyPage = new PageImpl<>(Arrays.asList(), PageRequest.of(0, 10), 0);
        
        when(securityContextHelper.getCurrentUserIdAsUUID()).thenReturn(instructorId);
        when(courseElasticRepository.search(request, instructorId)).thenReturn(emptyPage);

        // When
        Page<CourseDocument> result = courseSearchService.searchInstructorCourses(request);

        // Then
        assertNotNull(result);
        assertEquals(0, result.getTotalElements());
        assertTrue(result.getContent().isEmpty());
        
        verify(securityContextHelper, times(1)).getCurrentUserIdAsUUID();
        verify(courseElasticRepository, times(1)).search(request, instructorId);
    }

    @Test
    void searchInstructorCourses_ShouldUseCurrentUserContext() {
        // Given
        InstructorCourseSearchRequest request = new InstructorCourseSearchRequest();
        UUID differentInstructorId = UUID.randomUUID();
        Page<CourseDocument> mockPage = new PageImpl<>(Arrays.asList());
        
        when(securityContextHelper.getCurrentUserIdAsUUID()).thenReturn(differentInstructorId);
        when(courseElasticRepository.search(request, differentInstructorId)).thenReturn(mockPage);

        // When
        courseSearchService.searchInstructorCourses(request);

        // Then
        verify(securityContextHelper, times(1)).getCurrentUserIdAsUUID();
        verify(courseElasticRepository, times(1)).search(request, differentInstructorId);
        verify(courseElasticRepository, never()).search(request, instructorId);
    }
}