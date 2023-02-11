package com.roc.rest;

import com.roc.rest.adapter.in.web.EntitiesController;
import com.roc.rest.adapter.in.web.RestControllerExceptionHandler;
import com.roc.rest.annotation.RestEntity;
import com.roc.rest.application.in.RestService;
import com.roc.rest.application.in.impl.RestServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.boot.autoconfigure.AutoConfigurationPackages;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.util.StringUtils;

import java.util.List;


@Slf4j
@Configuration(
        proxyBeanMethods = false
)
@EnableConfigurationProperties(RestProperties.class)
public class RestAutoConfiguration {
    @Autowired
    ApplicationContext applicationContext;

    @Bean("restService")
    @ConditionalOnMissingBean(RestService.class)
    public RestService restService() {
        return new RestServiceImpl(applicationContext);
    }

    @Bean
    EntitiesController entitiesController() {
        return new EntitiesController(restService());
    }

    @Bean
    RestControllerExceptionHandler exceptionHandler() {
        return new RestControllerExceptionHandler();
    }

    @Configuration
    @Import({AutoConfiguredRestScannerRegistrar.class})
    @ConditionalOnMissingBean({RestScannerConfigurer.class})
    public static class RestScannerRegistrarNotFoundConfiguration implements InitializingBean {
        public RestScannerRegistrarNotFoundConfiguration() {
        }

        public void afterPropertiesSet() {
            log.debug("Not found configuration for registering rest entity bean using @RestEntityScan,  RestScannerConfigurer.");
        }
    }

    @Slf4j
    public static class AutoConfiguredRestScannerRegistrar implements BeanFactoryAware, ImportBeanDefinitionRegistrar {
        private BeanFactory beanFactory;

        public AutoConfiguredRestScannerRegistrar() {
        }

        @Override
        public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry) {
            if (!AutoConfigurationPackages.has(this.beanFactory)) {
                log.warn("Could not determine auto-configuration package, automatic rest entity scanning disabled.");
            } else {
                List<String> packages = AutoConfigurationPackages.get(this.beanFactory);

                BeanDefinitionBuilder builder = BeanDefinitionBuilder.genericBeanDefinition(RestScannerConfigurer.class);

                builder.addPropertyValue("processPropertyPlaceHolders", true);
                builder.addPropertyValue("annotationClass", RestEntity.class);
                builder.addPropertyValue("basePackage", StringUtils.collectionToCommaDelimitedString(packages));

                builder.setRole(2);
                registry.registerBeanDefinition(RestScannerConfigurer.class.getName(), builder.getBeanDefinition());
            }
        }

        @Override
        public void setBeanFactory(@NotNull BeanFactory beanFactory) throws BeansException {
            this.beanFactory = beanFactory;
        }
    }
}
