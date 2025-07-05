package vn.vinaacademy.instructor.service;

import vn.vinaacademy.instructor.dto.InstructorInfoDto;

import java.util.UUID;

public interface InstructorService {
    InstructorInfoDto getInstructorInfo(UUID instructorId);
    InstructorInfoDto registerAsInstructor();
//    boolean isInstructor(UUID userId);

}
