package com.aewinformatica.seguranca.config;

import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.web.config.EnableSpringDataWebSupport;
import org.springframework.scheduling.annotation.EnableAsync;

@Configuration
@EnableSpringDataWebSupport
@EnableAsync
@EnableCaching
public class WebConfig  {

}
