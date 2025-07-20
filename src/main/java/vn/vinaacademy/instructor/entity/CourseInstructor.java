package vn.vinaacademy.instructor.entity;

import jakarta.persistence.*;
import lombok.*;
import vn.vinaacademy.common.entity.BaseEntity;
import vn.vinaacademy.course.entity.Course;

import java.util.UUID;

@Data
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "course_instructor", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"course_id", "is_owner"}),
        @UniqueConstraint(columnNames = {"user_id", "course_id"})
})
public class CourseInstructor extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private UUID instructorId;

    @Column(name = "course_instructor_full_name")
    private String instructorFullName;

    @Column(name = "course_instructor_avatar")
    private String instructorAvatar;

    @ManyToOne
    @JoinColumn(name = "course_id", nullable = false)
    private Course course;

    @Column(name = "is_owner", nullable = false)
    private Boolean isOwner;
}
