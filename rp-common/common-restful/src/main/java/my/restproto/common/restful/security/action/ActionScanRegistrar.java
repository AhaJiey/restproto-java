package my.restproto.common.restful.security.action;

import my.restproto.common.restful.security.annotations.ActionScan;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.util.StringUtils;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * 读取标注类上 ActionScan 的 basePackages, 注册 ActionCollector Bean
 */
public class ActionScanRegistrar implements ImportBeanDefinitionRegistrar {

    @Override
    public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry) {
        Map<String, Object> attributes = importingClassMetadata.getAnnotationAttributes(ActionScan.class.getName());
        if (attributes == null) {
            return;
        }

        String[] basePackages = (String[]) attributes.get("basePackages");

        List<String> scanPackages = Arrays.stream(basePackages).toList();

        BeanDefinitionBuilder builder = BeanDefinitionBuilder
                .genericBeanDefinition(ActionCollector.class);

        builder.addConstructorArgValue(scanPackages);
        builder.addConstructorArgReference(StringUtils.uncapitalize(ActionCollections.class.getSimpleName()));

        registry.registerBeanDefinition(
                StringUtils.uncapitalize(ActionCollector.class.getSimpleName()), builder.getBeanDefinition());
    }
}
