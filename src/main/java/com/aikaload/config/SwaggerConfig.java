package com.aikaload.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.ParameterBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.schema.ModelRef;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.service.Parameter;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger.web.DocExpansion;
import springfox.documentation.swagger.web.UiConfiguration;
import springfox.documentation.swagger.web.UiConfigurationBuilder;
import springfox.documentation.swagger2.annotations.EnableSwagger2;
import java.util.Collections;
import java.util.List;
import java.util.function.Predicate;


@Configuration
@EnableSwagger2
public class SwaggerConfig {


    @Bean
    public Docket api() {
        return new Docket(DocumentationType.SWAGGER_2)
                .groupName("default")
                .forCodeGeneration(true)
                .globalOperationParameters(globalParameterList())
                .select()
                .apis(RequestHandlerSelectors.basePackage("com.aikaload.controller"))
                .paths(PathSelectors.any())
                .build()
                .apiInfo(apiInfo());
    }

    @Bean
    public UiConfiguration uiConfig() {
        return UiConfigurationBuilder.builder()
                .docExpansion(DocExpansion.NONE) // or DocExpansion.NONE or DocExpansion.FULL
                .build();
    }


   /* @Bean
    public Docket authApiDoc() {
        return new Docket(DocumentationType.SWAGGER_2).groupName("auth-api")
                .apiInfo(apiInfo()).select().paths(authApiPaths()).build();
    }

    @Bean
    public Docket userApiDoc() {
        return new Docket(DocumentationType.SWAGGER_2).groupName("user-api")
                .forCodeGeneration(true)
                .globalOperationParameters(globalParameterList())
                .apiInfo(apiInfo()).select().paths(userApiPaths()).build();
    }


    @Bean
    public Docket jobApiDoc() {
        return new Docket(DocumentationType.SWAGGER_2).groupName("job-api")
                .forCodeGeneration(true)
                .globalOperationParameters(globalParameterList())
                .apiInfo(apiInfo()).select().paths(jobPaths()).build();
    }

    @Bean
    public Docket truckApiDoc() {
        return new Docket(DocumentationType.SWAGGER_2).groupName("truck-api")
                .forCodeGeneration(true)
                .globalOperationParameters(globalParameterList())
                .apiInfo(apiInfo()).select().paths(truckPaths()).build();
    }

    @Bean
    public Docket userRoleApiDoc() {
        return new Docket(DocumentationType.SWAGGER_2).groupName("user-role-api")
                .forCodeGeneration(true)
                .globalOperationParameters(globalParameterList())
                .apiInfo(apiInfo()).select().paths(userRolePaths()).build();
    }



    private Predicate<String> userApiPaths() {
        return or(regex("/user/.*"));
    }

    private Predicate<String> userRolePaths() {
        return or(regex("/user-role/.*"));
    }

    private Predicate<String> jobPaths() {
        return or(regex("/job/.*"));
    }

    private Predicate<String> truckPaths() {
        return or(regex("/truck/.*"));
    }*/



    private List<Parameter> globalParameterList() {
        Parameter authTokenHeader =
                (Parameter) new ParameterBuilder()
                        .name("Authorization") // name of the header
                        .modelRef(new ModelRef("string")) // data-type of the header
                        .required(false) // required/optional
                        .parameterType("header") // for query-param, this value can be 'query'
                        .description("Bearer access_token e.g Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.TUlObCQp9OtZecJ8-gOsOouN9e0-k........")
                        .build();

        return Collections.singletonList(authTokenHeader);
    }


    private ApiInfo apiInfo() {
        return new ApiInfoBuilder().title("Aikaload API")
                .description("Aikaload API reference for developers")
                .contact(new Contact("Ashaolu Tolu","",""))
                .licenseUrl("").version("1.0").build();
    }
}
