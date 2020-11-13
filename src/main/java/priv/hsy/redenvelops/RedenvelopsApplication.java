package priv.hsy.redenvelops;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("priv.hsy.redenvelops.mapper")
public class RedenvelopsApplication {

    public static void main(String[] args) {
        SpringApplication.run(RedenvelopsApplication.class, args);
    }

}
