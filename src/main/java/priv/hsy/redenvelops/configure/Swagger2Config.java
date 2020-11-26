package priv.hsy.redenvelops.configure;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

/**
 * @author SongYawn
 * @date  2020/11/25 11点12分
 */
@Configuration
@EnableSwagger2
public class Swagger2Config {

    @Bean
    public Docket customDocket() {
        return new Docket(DocumentationType.SWAGGER_2)
                .pathMapping("/")
                .select()
                .apis(RequestHandlerSelectors.basePackage("priv.hsy.redenvelops.controller"))
                .paths(PathSelectors.any())
                .build()
                .apiInfo(apiInfo());
    }

    private ApiInfo apiInfo() {
        Contact contact = new Contact("SongYawn", "756207032@qq.com", "756207032@qq.com");
        return new ApiInfoBuilder()
                .title("RedEnvelop")
                .description("RedEnvelop")
                .contact(contact)
                .version("1.0.0")
                .build();
    }

}
