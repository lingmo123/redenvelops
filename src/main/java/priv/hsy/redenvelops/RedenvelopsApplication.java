package priv.hsy.redenvelops;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@SpringBootApplication
@MapperScan("priv.hsy.redenvelops.mapper")
@EnableSwagger2
public class RedenvelopsApplication {

    public static void main(String[] args) {
        SpringApplication.run(RedenvelopsApplication.class, args);
    }

}
