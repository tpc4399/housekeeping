package com.housekeeping.common.utils;

import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.ParameterBuilder;
import springfox.documentation.schema.ModelRef;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.service.Parameter;

import java.util.ArrayList;
import java.util.List;

/**
 * @Author su
 * @create 2020/11/2 16:47
 */
public class SwaggerUtils {
    public static ApiInfo apiInfo(String description){
        return new ApiInfoBuilder().title("家政服务Swagger2 接口列表")
                .description(description)
                .contact(new Contact("housekeeping", "", "mail_sanciyuan@163.com"))
                .version("2.0.0").build();
    }

    public static List<Parameter> getPars(boolean required){
        //给header添加参数token
        ParameterBuilder tokenPar = new ParameterBuilder();
        List<Parameter> pars = new ArrayList<Parameter>();
        tokenPar.name("Authorization").description("Token").modelRef(new ModelRef("string")).parameterType("header").required(required).order(0).build();
        pars.add(tokenPar.build());
        return pars;
    }
}
