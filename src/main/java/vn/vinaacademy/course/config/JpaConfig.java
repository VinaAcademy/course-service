package vn.vinaacademy.course.config;

import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Configuration
@EnableJpaRepositories(basePackages = {
        "vn.vinaacademy.course",
        "vn.vinaacademy.category"
})
@EntityScan(basePackages = {
        "vn.vinaacademy.course",
        "vn.vinaacademy.category"
})
public class JpaConfig {
}
