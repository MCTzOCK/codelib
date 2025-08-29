package com.bensiebert.codelib.common.conditions;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.type.AnnotatedTypeMetadata;

public class OnPackageScannedCondition implements Condition {

    private static final String PACKAGE_KEY  = "packageName";

    @Override
    public boolean matches(ConditionContext ctx, AnnotatedTypeMetadata metadata) {
        String pkg = (String) metadata
                .getAnnotationAttributes(ConditionalOnPackageScanned.class.getName())
                .get(PACKAGE_KEY);

        BeanDefinitionRegistry registry =
                (BeanDefinitionRegistry) ctx.getBeanFactory();

        for(String beanName : registry.getBeanDefinitionNames()) {
            BeanDefinition bd = registry.getBeanDefinition(beanName);
            String className = bd.getBeanClassName();
            if(className != null && className.startsWith(pkg)) {
                return true;
            }
        }

        return false;
    }

}
