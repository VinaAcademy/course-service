package vn.vinaacademy;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import vn.vinaacademy.kafka.KafkaProducerConfig;

@SpringBootApplication
@ComponentScan(basePackages = {
        "vn.vinaacademy.course",
        "vn.vinaacademy.category",
        "vn.vinaacademy.client",
        "vn.vinaacademy.event",
        "vn.vinaacademy.instructor",
        "vn.vinaacademy.common",
})
@Import({KafkaProducerConfig.class})
@EnableDiscoveryClient
@EnableFeignClients
public class CourseServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(CourseServiceApplication.class, args);
    }

}
